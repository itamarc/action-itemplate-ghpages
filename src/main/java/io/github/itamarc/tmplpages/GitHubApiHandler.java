package io.github.itamarc.tmplpages;

import java.util.HashMap;

public class GitHubApiHandler {
    public void getRepositoryData(HashMap<String, String> valuesMap) {
        HashMap<String, String> data = new HashMap<>();
        // TODO: get the repository data
        for (String key : data.keySet()) {
            valuesMap.put(key, data.get(key));
        }
    }
}
