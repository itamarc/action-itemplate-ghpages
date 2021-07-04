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
    /**
     * Connect to the GitHub API, get the data and feed the map.
     * @param valuesMap HashMap where the collected data will be inserted.
     */
    public void getRepositoryData(HashMap<String, String> valuesMap) {
        // Get the data from the API
        JSONObject dataJson = callingGraph(valuesMap).getJSONObject("data").getJSONObject("repository");

        // Process the received data and insert into the map
        processJsonObject(dataJson, valuesMap, "repository");
    }

    private void processJsonObject(JSONObject dataJson, HashMap<String, String> dataMap, String prefix) {
        for (String key : dataJson.keySet()) {
            Object obj = dataJson.get(key);
            String currkey = prefix + "_" + key;
            if (isBaseType(obj.getClass())) {
                dataMap.put(currkey, obj.toString());
                System.out.println(currkey + " = " + obj.toString());
            } else if (obj instanceof JSONArray) {
                System.out.println(currkey + ": JSONArray");
                if (key.equals("nodes") || key.equals("edges")) {
                    dataMap.put(prefix, processJsonArray((JSONArray) obj, dataMap, prefix));
                } else {
                    dataMap.put(currkey, processJsonArray((JSONArray) obj, dataMap, currkey));
                }
            } else if (obj instanceof JSONObject) {
                System.out.println(currkey + ": JSONObject");
                if (key.equals("node") || key.equals("nodes")) {
                    processJsonObject((JSONObject) obj, dataMap, prefix);
                } else {
                    processJsonObject((JSONObject) obj, dataMap, currkey);
                }
            } else {
                System.out.println("!!!>" + currkey + ": " + obj.getClass().getName());
            }
        }
    }

    private String processJsonArray(JSONArray array, HashMap<String, String> dataMap, String prefix) {
        StringBuffer buffer = new StringBuffer();
        if (prefix.equals("repository_licenseInfo_conditions")) {
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
        } else { // repository_issues, repository_collaborators
            buffer.append(array.toString());
        }
        // TODO: remove prints only for testing
        System.out.println("JSONArray (prefix '"+prefix+"'):");
        System.out.println(buffer.toString());
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
                + "\") { name createdAt updatedAt description shortDescriptionHTML homepageUrl forkCount repositoryTopics(first: 20) { nodes { topic { name } url } } issues(last: 5, filterBy: {states: OPEN}) { nodes { titleHTML url createdAt comments { totalCount } author { login url } } } licenseInfo { name nickname url conditions { label } } latestRelease { createdAt tagName isPrerelease url author { name login } } collaborators(first: 100) { nodes { login url name } } languages(last: 100) { edges { node { color name } size } totalSize } nameWithOwner owner { login avatarUrl url } stargazerCount url watchers { totalCount } } }");

        try {
            StringEntity entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            response = client.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject responseJson = new JSONObject();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            System.out.println(">>> GraphQL result:");
            System.out.println(builder.toString());
            responseJson = new JSONObject(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
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
}
