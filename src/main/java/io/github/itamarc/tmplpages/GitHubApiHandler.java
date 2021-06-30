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

public class GitHubApiHandler {
    public void getRepositoryData(HashMap<String, String> valuesMap) {
        HashMap<String, String> dataMap = new HashMap<>();
        JSONObject dataJson = callingGraph(valuesMap).getJSONObject("data").getJSONObject("repository");

        processJsonObject(dataJson, dataMap, "repository_");

        // Insert the data received into the existing valuesMap
        for (String key : dataMap.keySet()) {
            valuesMap.put(key, dataMap.get(key));
        }
    }

    private void processJsonObject(JSONObject dataJson, HashMap<String, String> dataMap, String prefix) {
        for (String key : dataJson.keySet()) {
            Object obj = dataJson.get(key);
            String currkey = prefix + key;
            if (isBaseType(obj.getClass())) {
                dataMap.put(currkey, obj.toString());
                System.out.println(currkey + " = " + obj.toString());
            } else if (obj instanceof JSONArray) {
                if (key.equals("nodes") || key.equals("edges")) {
                    processJsonArray((JSONArray) obj, dataMap, prefix);
                } else {
                    processJsonArray((JSONArray) obj, dataMap, currkey + "_");
                }
                System.out.println(currkey + ": JSONArray");
            } else if (obj instanceof JSONObject) {
                if (key.equals("node")) {
                    processJsonObject((JSONObject) obj, dataMap, prefix);
                } else {
                    processJsonObject((JSONObject) obj, dataMap, currkey + "_");
                }
                System.out.println(currkey + ": JSONObject");
            } else {
                System.out.println("!!!>" + currkey + ": " + obj.getClass().getName());
            }
        }
    }

    private void processJsonArray(JSONArray array, HashMap<String, String> dataMap, String prefix) {
        for (int i = 0; i < array.length(); i++) {
            Object obj = array.get(i);
            String currkey = prefix + i;
            if (isBaseType(obj.getClass())) {
                dataMap.put(prefix + i, obj.toString());
                System.out.println(currkey + " = " + obj.toString());
            } else if (obj instanceof JSONArray) {
                processJsonArray((JSONArray) obj, dataMap, currkey + "_");
                System.out.println(currkey + ": JSONArray");
            } else if (obj instanceof JSONObject) {
                processJsonObject((JSONObject) obj, dataMap, currkey + "_");
                System.out.println(currkey + ": JSONObject");
            } else {
                System.out.println("!!!>" + currkey + ": " + obj.getClass().getName());
            }
        }
    }

    public JSONObject callingGraph(HashMap<String, String> valuesMap) {
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
                + "\") { name createdAt issues(last: 5, filterBy: {states: OPEN}) { nodes { id titleHTML url lastEditedAt createdAt comments { totalCount } author { login url } } } licenseInfo { name nickname url conditions { label } } latestRelease { createdAt tagName isPrerelease url author { name login } } collaborators(first: 100) { nodes { login url name } } languages(last: 100) { edges { node { color name } size } totalSize } nameWithOwner owner { login avatarUrl url } stargazerCount url watchers { totalCount } } }");

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
