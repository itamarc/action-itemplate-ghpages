package io.github.itamarc.tmplpages;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ActionRunner {
    protected void run() throws Exception {
        // Create the map that will hold the values to be used in the templates
        HashMap<String, String> valuesMap = new HashMap<>();
        // Get environment variables and feed in the map of values
        feedEnvironmentToMap(valuesMap);
        logMap("Environment", System.getenv());
        // Insert the current date and time as LASTUPDATE field
        insertLastUpdate(valuesMap);
        // Get values from GitHub API and insert them in the map
        getGHApiData(valuesMap);
        logMap("Values Map", valuesMap);
        processTemplates(valuesMap);
    }

    protected void feedEnvironmentToMap(Map<String, String> valuesMap) {
        Map<String, String> env = System.getenv();
        String[] relevantKeys = {
                "INPUT_PAGES_BRANCH", // ex: gh-pages
                "INPUT_PAGES_FOLDER", // ex: docs
                "INPUT_SNIPPETS_FOLDER", // ex: docs/templates/snippets
                "INPUT_ALLOW_TEMPLATES_SUBFOLDERS", // ex: 'false'
                "INPUT_TEMPLATES_FOLDER", // ex: templates or :light:
                "INPUT_TIMEZONE", // ex: America/Sao_Paulo
                "INPUT_PUBLISH_README_MD", // 'true', 'false' or 'inline'
                "INPUT_CONTENT_TO_COPY", // ex: 'images'
                "INPUT_CONVERT_MD_TO_HTML", // ex: 'true'
                "INPUT_LOG_LEVEL", // ex: 'INFO'
                "INPUT_SYNTAX_HIGHLIGHT_ENABLE", // ex: 'true'
                "INPUT_SYNTAX_HIGHLIGHT_THEME", // ex: 'default'
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

    protected void insertLastUpdate(HashMap<String, String> valuesMap) {
        String timezone = valuesMap.get("INPUT_TIMEZONE");
        if (timezone == null) {
            timezone = "America/Sao_Paulo";
        }
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        valuesMap.put("TMPL_LASTUPDATE", zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss OOOO")));
    }

    protected void getGHApiData(HashMap<String, String> valuesMap) {
        GitHubApiHandler handler = new GitHubApiHandler();
        handler.setMaxIssues(valuesMap.get("INPUT_MAX_ISSUES"));
        handler.setMaxCollaborators(valuesMap.get("INPUT_MAX_COLLABORATORS"));
        handler.getRepositoryData(valuesMap);
    }

    protected void processTemplates(HashMap<String, String> valuesMap) throws Exception {
        // Process the templates with the values map
        boolean syntaxHighlightEnabled = "true".equals(valuesMap.get("INPUT_SYNTAX_HIGHLIGHT_ENABLE"));
        TemplateProcessor proc = new TemplateProcessor(
                valuesMap.get("GITHUB_WORKSPACE"),
                valuesMap.get("INPUT_TEMPLATES_FOLDER"),
                valuesMap.get("INPUT_PAGES_FOLDER"),
                allowRecursion(valuesMap),
                syntaxHighlightEnabled);
        proc.configPublishReadme(valuesMap.get("INPUT_PUBLISH_README_MD"));
        proc.setContentToCopy(valuesMap.get("INPUT_CONTENT_TO_COPY"));
        proc.setConvertMdToHtml(valuesMap.get("INPUT_CONVERT_MD_TO_HTML"));
        proc.setSnippetsPath(valuesMap.get("INPUT_SNIPPETS_FOLDER"));
        if (syntaxHighlightEnabled) {
            ActionLogger.fine("Syntax highlighting enabled");
            proc.setSyntaxHighlightTheme(valuesMap.get("INPUT_SYNTAX_HIGHLIGHT_THEME"));
        }
        if (proc.run(valuesMap) != 0) {
            ActionLogger.warning("Some error occurred in the TemplateProcessor.");
        }
    }

    protected boolean allowRecursion(HashMap<String, String> valuesMap) throws Exception {
        boolean recursion = false;
        String input = valuesMap.get("INPUT_ALLOW_TEMPLATES_SUBFOLDERS");
        if (input != null && "true".equals(input)) {
            String snptsFolder = valuesMap.get("INPUT_SNIPPETS_FOLDER");
            String tmplsFolder = valuesMap.get("INPUT_TEMPLATES_FOLDER");
            // It's not an accurate test, but it's enough and it's simple
            if (snptsFolder.startsWith(tmplsFolder)) {
                ActionLogger.severe("Snippets folder can't be inside templates folder with recursion on.");
                throw new Exception("Snippets folder can't be inside templates folder with recursion on.");
            }
            recursion = true;
        }
        return recursion;
    }

    protected void logMap(String description, Map<String, String> valuesMap) {
        StringBuffer buf = new StringBuffer(">>> " + description + ":");
        for (String key : valuesMap.keySet()) {
            buf.append(String.format("%s=%s%n", key, valuesMap.get(key)));
        }
        ActionLogger.finer(buf.toString());
    }
}
