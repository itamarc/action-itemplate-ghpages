package io.github.itamarc.tmplpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class responsible to get data from GitHub API and put the values formatted
 * in the data map that will be used to fill the templates.
 */
public class GitHubApiHandler {
    private int maxIssues = 5;
    private int maxCollaborators = 20;

    /**
     * Connect to the GitHub API, get the data and feed the map.
     * @param valuesMap HashMap where the collected data will be inserted.
     */
    public void getRepositoryData(HashMap<String, String> valuesMap) {
        ActionLogger.info("Getting data from GitHub API.");
        // Get the data from the API
        JSONObject responseJson = callingGraph(valuesMap);
        JSONObject dataJson;
        if (responseJson.has("data")) {
            dataJson = responseJson.getJSONObject("data").getJSONObject("repository");
            // Process the received data and insert into the map
            processJsonObject(dataJson, valuesMap, "repository");
        } else {
            ActionLogger.severe("Error getting data from GitHub API: " + responseJson.toString());
        }
    }

    private void processJsonObject(JSONObject dataJson, HashMap<String, String> dataMap, String prefix) {
        for (String key : dataJson.keySet()) {
            Object obj = dataJson.get(key);
            String currkey = prefix + "_" + key;
            if (isBaseType(obj.getClass())) {
                String objStr = obj.toString();
                // if objStr contains a new line, replace it with <br>
                objStr = objStr.replaceAll("(\r\n|\n\r|\r|\n)", "<br>");
                dataMap.put(currkey, objStr);
                ActionLogger.finer(currkey + " = " + obj.toString());
            } else if (obj instanceof JSONArray) {
                ActionLogger.finer("Processing JSONArray: '" + currkey + "'");
                if (key.equals("nodes") || key.equals("edges")) {
                    dataMap.put(prefix, processJsonArray((JSONArray) obj, dataMap, prefix));
                } else {
                    dataMap.put(currkey, processJsonArray((JSONArray) obj, dataMap, currkey));
                }
            } else if (obj instanceof JSONObject) {
                ActionLogger.finer("Processing JSONObject: '" + currkey + "'");
                if (key.equals("node") || key.equals("nodes")) {
                    processJsonObject((JSONObject) obj, dataMap, prefix);
                } else {
                    processJsonObject((JSONObject) obj, dataMap, currkey);
                }
            } else {
                ActionLogger.fine("Processing unknown type: '" + currkey + "' " + obj.getClass().getName());
            }
        }
    }

    private String processJsonArray(JSONArray array, HashMap<String, String> dataMap, String prefix) {
        StringBuffer buffer = new StringBuffer();
        if (prefix.equals("repository_licenseInfo_conditions")) {
            // [
            //     {
            //       "label": "License and copyright notice"
            //     },
            buffer.append("[\"");
            for (int i = 0; i < array.length(); i++) {
                if (buffer.length() > 2) {
                    buffer.append("\",\"");
                }
                JSONObject jso = array.getJSONObject(i);
                String cond = jso.getString("label");
                buffer.append(cond);
            }
            buffer.append("\"]");
        } else if (prefix.equals("repository_languages")) {
            // [
            //   {
            //     "node": {
            //       "color": "#3572A5",
            //       "name": "Python"
            //     },
            //     "size": 106
            //   }
            // ],
            buffer.append("[");
            for (int i = 0; i < array.length(); i++) {
                if (buffer.length() > 1) {
                    buffer.append(",");
                }
                JSONObject jso = array.getJSONObject(i);
                JSONObject node = jso.getJSONObject("node");
                node.put("size", jso.getInt("size"));
                buffer.append(node.toString());
            }
            buffer.append("]");
        } else if (prefix.equals("repository_repositoryTopics")) {
            //     [
            //       {
            //         "topic": {
            //           "name": "github"
            //         },
            //         "url": "https://github.com/topics/github"
            //       },
            buffer.append("[");
            for (int i = 0; i < array.length(); i++) {
                if (buffer.length() > 1) {
                    buffer.append(",");
                }
                JSONObject jso = array.getJSONObject(i);
                JSONObject node = jso.getJSONObject("topic");
                node.put("url", jso.getString("url"));
                buffer.append(node.toString());
            }
            buffer.append("]");
        } else if (prefix.equals("repository_issues")) {
            // [
            //   {
            //     "number": 4,
            //     "titleHTML": "Test issue 1 with enhancement",
            //     "url": "https://github.com/itamarc/githubtest/issues/4",
            //     "createdAt": "2021-07-03T13:36:19Z",
            //     "comments": {
            //       "totalCount": 0
            //     },
            //     "author": {
            //       "login": "itamarc",
            //       "url": "https://github.com/itamarc"
            //     }
            //   },
            buffer.append("[");
            for (int i = 0; i < array.length(); i++) {
                if (buffer.length() > 1) {
                    buffer.append(",");
                }
                JSONObject jso = array.getJSONObject(i);
                JSONObject commObj = jso.getJSONObject("comments");
                int commCnt = commObj.getInt("totalCount");
                jso.remove("comments");
                jso.put("comments_totalCount", commCnt);
                buffer.append(jso.toString());
            }
            buffer.append("]");
        } else { // repository_collaborators
            // [
            //     {
            //         "login": "mylogin",
            //         "url": "https://github.com/mylogin",
            //         "name": "John Constantine"
            //     }
            buffer.append(array.toString());
        }
        ActionLogger.finer("JSONArray (prefix '"+prefix+"'):\n"+buffer.toString());
        return buffer.toString();
    }

    private JSONObject callingGraph(HashMap<String, String> valuesMap) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(valuesMap.get("GITHUB_GRAPHQL_URL"));

        httpPost.addHeader("Authorization", "Bearer " + valuesMap.get("GITHUB_TOKEN"));
        httpPost.addHeader("Accept", "application/json");

        // GITHUB_REPOSITORY comes in the format "user/repository"
        String[] usrRepo = valuesMap.get("GITHUB_REPOSITORY").split("\\/");

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ repository(owner: \"" + usrRepo[0] + "\", name: \"" + usrRepo[1]
                + "\")" + """
                {
                    name createdAt updatedAt description shortDescriptionHTML homepageUrl forkCount
                    nameWithOwner owner { login avatarUrl url } stargazerCount url watchers { totalCount }
                    repositoryTopics(first: 100) {
                        nodes { topic { name } url }
                    }
                    issues(last: """ + maxIssues + """
                    , filterBy: {states: OPEN}) {
                        nodes { number titleHTML url createdAt comments { totalCount } author { login url } }
                    }
                    licenseInfo {
                        name nickname url conditions { label }
                    }
                    latestRelease {
                        name description createdAt tagName isPrerelease url author { name login }
                    }
                    collaborators(first: """ + maxCollaborators + """
                    ) {
                        nodes { login url name }
                    }
                    languages(last: 100) {
                        edges { node { color name } size } totalSize
                    }
                }
            }
            """);
        ActionLogger.finer("GraphQL Query:\n" + jsonObj.get("query").toString());

        try {
            StringEntity entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            response = client.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            ActionLogger.severe("Error on GraphQL connection", e);
        } catch (ClientProtocolException e) {
            ActionLogger.severe("Error on GraphQL connection", e);
        } catch (IOException e) {
            ActionLogger.severe("Error on GraphQL connection", e);
        }

        JSONObject responseJson = new JSONObject();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            ActionLogger.finer("GraphQL result:\n"+builder.toString());
            responseJson = new JSONObject(builder.toString());
        } catch (IOException e) {
            ActionLogger.warning("Error on JSON reading", e);
        }
        return responseJson;
    }

    private static boolean isBaseType(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        } else if (type.isEnum()) {
            return true;
        } else if (type.getTypeName().equals("java.lang.String")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.StringBuffer")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.StringBuilder")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Integer")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Long")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Short")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Byte")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Character")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Double")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Float")) {
            return true;
        } else if (type.getTypeName().equals("java.math.BigDecimal")) {
            return true;
        } else if (type.getTypeName().equals("java.math.BigInteger")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Byte")) {
            return true;
        } else if (type.getTypeName().equals("java.lang.Boolean")) {
            return true;
        } else if (type.getTypeName().equals("java.util.Date")) {
            return true;
        } else if (type.getTypeName().equals("java.io.InputStream")) {
            return true;
        }
        return false;
    }

    public void setMaxIssues(String maxIssuesStr) {
        try {
            int i = Integer.parseInt(maxIssuesStr);
            if (i < 0 || i > 100) {
                throw new NumberFormatException("Number out of range (0-100).");
            } else {
                maxIssues = i;
            }
        } catch (NumberFormatException e) {
            ActionLogger.warning("Invalid input max_issues (should be an integer number on range 0 to 100): " + maxIssuesStr);
        }
    }

    public void setMaxCollaborators(String maxCollabsStr) {
        try {
            int i = Integer.parseInt(maxCollabsStr);
            if (i < 0 || i > 100) {
                throw new NumberFormatException("Number out of range (0-100).");
            } else {
                maxCollaborators = i;
            }
        } catch (NumberFormatException e) {
            ActionLogger.warning("Invalid input max_collaborators (should be an integer number on range 0 to 100): " + maxCollabsStr);
        }
    }
}
