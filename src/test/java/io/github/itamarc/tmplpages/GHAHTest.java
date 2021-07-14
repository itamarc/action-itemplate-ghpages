package io.github.itamarc.tmplpages;

import java.util.HashMap;
import java.util.Map;

public class GHAHTest {
    public static void main(String[] args) {
        ActionLogger.setUpLogSys("FINER");
        GitHubApiHandler handler = new GitHubApiHandler();
        HashMap<String, String> values = new HashMap<>();
        // You need to get your own token from https://github.com/settings/tokens
        values.put("GITHUB_TOKEN", System.getenv("GITHUB_TOKEN"));
        values.put("GITHUB_GRAPHQL_URL", "https://api.github.com/graphql");
        // values.put("GITHUB_REPOSITORY", "itamarc/action-itemplate-ghpages");
        // values.put("GITHUB_REPOSITORY", "itamarc/itemplate");
        values.put("GITHUB_REPOSITORY", "itamarc/githubtest");
        handler.getRepositoryData(values);
        logMap("GitHubApiHandler", values);
    }

    private static void logMap(String description, Map<String, String> valuesMap) {
        StringBuffer buf = new StringBuffer(">>> "+description+":");
        for (String key : valuesMap.keySet()) {
            if (key.equals("GITHUB_TOKEN")) {
                buf.append(String.format("%s=%s%n", key, "***"));
            } else {
                buf.append(String.format("%s=%s%n", key, valuesMap.get(key)));
            }
        }
        ActionLogger.finer(buf.toString());
    }
}
