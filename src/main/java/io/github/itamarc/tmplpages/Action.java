package io.github.itamarc.tmplpages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the main action class, responsible for the general processing.
 */
public class Action {
    /**
     * The main processing flow.
     * @param args will be ignored.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Create the map that will hold the values to be used in the templates
        HashMap<String, String> valuesMap = new HashMap<>();
        // Get environment variables and feed in the map of values
        feedEnvironmentToMap(valuesMap);
        // Insert the current date and time as LASTUPDATE field
        insertLastUpdate(valuesMap);
        // Get values from GitHub API and insert them in the map
        GitHubApiHandler handler = new GitHubApiHandler();
        handler.getRepositoryData(valuesMap);
        // Process the templates with the values map
        TemplateProcessor proc = new TemplateProcessor(
                valuesMap.get("GITHUB_WORKSPACE"),
                valuesMap.get("INPUT_TEMPLATES_FOLDER"),
                valuesMap.get("INPUT_PAGES_FOLDER"),
                allowRecursion(valuesMap));
        proc.setSnippetsPath(valuesMap.get("INPUT_SNIPPETS_FOLDER"));
        if (proc.run(valuesMap) != 0) {
            System.out.println("Some error occurred in the TemplateProcessor.");
        }

        // TODO remove print test code
        printMap("Values Map", valuesMap); // only for testing
        printMap("Environment", System.getenv()); // only for testing
    }

    private static boolean allowRecursion(HashMap<String, String> valuesMap) throws Exception {
        boolean recursion = false;
        String input = valuesMap.get("INPUT_ALLOW_TEMPLATES_SUBFOLDERS");
        if (input != null && "true".equals(input)) {
            String snptsFolder = valuesMap.get("INPUT_SNIPPETS_FOLDER");
            String tmplsFolder = valuesMap.get("INPUT_TEMPLATES_FOLDER");
            // It's not an accurate test, but it's enough and it's simple
            if (snptsFolder.startsWith(tmplsFolder)) {
                throw new Exception("Snippets folder can't be inside templates folder with recursion on.");
            }
            recursion = true;
        }
        return recursion;
    }

    private static void insertLastUpdate(HashMap<String, String> valuesMap) {
        String timezone = valuesMap.get("INPUT_TIMEZONE");
        if (timezone == null) {
            timezone = "America/Sao_Paulo";
        }
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), zoneId );
        valuesMap.put("TMPL_LASTUPDATE", ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private static void feedEnvironmentToMap(Map<String, String> valuesMap) {
        Map<String, String> env = System.getenv();
        String[] relevantKeys = {
            "INPUT_PAGES_BRANCH", // ex: gh-pages
            "INPUT_PAGES_FOLDER", // ex: docs
            "INPUT_SNIPPETS_FOLDER", // ex: docs/templates/snippets
            "INPUT_ALLOW_TEMPLATES_SUBFOLDERS", // ex: 'false'
            "INPUT_TEMPLATES_FOLDER", // ex: docs/templates
            "INPUT_TIMEZONE", // ex: America/Sao_Paulo
            "GITHUB_WORKSPACE", // ex: /github/workspace
            "GITHUB_EVENT_PATH", // ex: /github/workflow/event.json
            "GITHUB_GRAPHQL_URL", // ex: https://api.github.com/graphql
            "GITHUB_SERVER_URL", // ex: https://github.com
            "GITHUB_REPOSITORY_OWNER", // ex: itamarc
            "GITHUB_REPOSITORY", // ex: itamarc/githubtest
            "GITHUB_ACTOR", // ex: itamarc
            "GITHUB_REF", // ex: refs/heads/master
            "GITHUB_TOKEN" // needed to publish
        };

        for (String key : relevantKeys) {
            String envVal = env.get(key);
            if (envVal != null) {
                valuesMap.put(key, envVal);
            }
        }
    }

    // TODO remove print test code
    // This is only for testing fase
    private static void printMap(String description, Map<String, String> valuesMap) {
        System.out.println(">>> "+description+":");
        for (String envName : valuesMap.keySet()) {
            System.out.format("%s=%s%n", envName, valuesMap.get(envName));
        }
    }
}
