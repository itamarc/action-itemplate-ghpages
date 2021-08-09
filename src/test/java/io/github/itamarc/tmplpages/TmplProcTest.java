package io.github.itamarc.tmplpages;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TmplProcTest {
    private static String baseDir;
    
    public static void main(String[] args) {
        ActionLogger.setUpLogSys("FINER");
        // Set this environment variable on your system or your IDE:
        // (this needs to point to the local root of action-itemplate-ghpages)
        baseDir = System.getenv("ACTION_ROOT_DIR");
        if (baseDir == null || !new File(baseDir).exists()) {
            ActionLogger.severe("Base directory not found: " + baseDir);
            System.exit(1);
        }
        HashMap<String,String> valuesMap = getValuesMap();
        boolean syntaxHighlightEnabled = "true".equals(valuesMap.get("INPUT_SYNTAX_HIGHLIGHT_ENABLE"));
        TemplateProcessor proc = new TemplateProcessor(
            baseDir, ":dark:", "target\\docs", false, syntaxHighlightEnabled);
        proc.configPublishReadme(valuesMap.get("INPUT_PUBLISH_README_MD"));
        proc.setContentToCopy(valuesMap.get("INPUT_CONTENT_TO_COPY"));
        if (syntaxHighlightEnabled) {
            ActionLogger.info("Syntax highlighting enabled");
            proc.setSyntaxHighlightTheme(valuesMap.get("INPUT_SYNTAX_HIGHLIGHT_THEME"));
        }
        proc.run(valuesMap);
    }

    private static HashMap<String, String> getValuesMap() {
        HashMap<String,String> valuesMap = new HashMap<String,String>();
        valuesMap.put("THEMES_PATH", baseDir + "\\themes");
        valuesMap.put("GITHUB_ACTOR", "itamarc");
        valuesMap.put("GITHUB_GRAPHQL_URL", "https://api.github.com/graphql");
        // valuesMap.put("GITHUB_REPOSITORY", "itamarc/action-itemplate-ghpages");
        valuesMap.put("GITHUB_REPOSITORY", "itamarc/githubtest");
        valuesMap.put("GITHUB_REPOSITORY_OWNER", "itamarc");
        valuesMap.put("GITHUB_SERVER_URL", "https://github.com");
        valuesMap.put("INPUT_TIMEZONE", "America/Sao_Paulo");
        valuesMap.put("INPUT_PUBLISH_README_MD", "true");
        valuesMap.put("INPUT_CONTENT_TO_COPY", "images");
        valuesMap.put("INPUT_SYNTAX_HIGHLIGHT_ENABLE", "true");
        valuesMap.put("INPUT_SYNTAX_HIGHLIGHT_THEME", "tomorrow");
        valuesMap.put("repository_collaborators", "[{\"name\":\"John Constantine\",\"login\":\"myuser\",\"url\":\"https://github.com/myuser\"}]");
        valuesMap.put("repository_createdAt", "2021-06-13T19:54:12Z");
        valuesMap.put("repository_description", "Repository to test GitHub functionality.");
        valuesMap.put("repository_forkCount", "0");
        valuesMap.put("repository_homepageUrl", "https://itamarc.github.io/githubtest/");
        valuesMap.put("repository_issues", "[{\"number\":\"4\", \"createdAt\":\"2021-07-03T13:36:19Z\",\"comments_totalCount\":0,\"author\":{\"login\":\"myuser\",\"url\":\"https://github.com/myuser\"},\"titleHTML\":\"Test issue 1 with enhancement\",\"url\":\"https://github.com/itamarc/githubtest/issues/4\"},{\"number\":\"5\", \"createdAt\":\"2021-07-03T13:37:31Z\",\"comments_totalCount\":0,\"author\":{\"login\":\"myuser\",\"url\":\"https://github.com/myuser\"},\"titleHTML\":\"Test issue 2 with a bug\",\"url\":\"https://github.com/itamarc/githubtest/issues/5\"}]");
        // valuesMap.put("repository_languages", "[{\"color\":\"#3572A5\",\"size\":106,\"name\":\"Python\"}]");
        // valuesMap.put("repository_languages_totalSize", "106");
        valuesMap.put("repository_languages", "[{\"color\":\"#384d54\",\"size\":741,\"name\":\"Dockerfile\"},{\"color\":\"#b07219\",\"size\":29604,\"name\":\"Java\"},{\"color\":\"#89e051\",\"size\":824,\"name\":\"Shell\"},{\"color\":\"#f1e05a\",\"size\":4822,\"name\":\"JavaScript\"},{\"color\":\"#e34c26\",\"size\":6203,\"name\":\"HTML\"},{\"color\":\"#563d7c\",\"size\":3296,\"name\":\"CSS\"}]");
        valuesMap.put("repository_languages_totalSize", "45490");
        valuesMap.put("repository_latestRelease_author_login", "myuser");
        valuesMap.put("repository_latestRelease_author_name", "John Constantine");
        valuesMap.put("repository_latestRelease_createdAt", "2021-07-03T20:32:55Z");
        valuesMap.put("repository_latestRelease_isPrerelease", "false");
        valuesMap.put("repository_latestRelease_tagName", "v1test");
        valuesMap.put("repository_latestRelease_url", "https://github.com/itamarc/githubtest/releases/tag/v1test");
        valuesMap.put("repository_licenseInfo_conditions", "[\"License and copyright notice\",\"State changes\",\"Disclose source\",\"Same license\"]");
        valuesMap.put("repository_licenseInfo_name", "GNU General Public License v3.0");
        valuesMap.put("repository_licenseInfo_nickname", "GNU GPLv3");
        valuesMap.put("repository_licenseInfo_url", "http://choosealicense.com/licenses/gpl-3.0/");
        valuesMap.put("repository_name", "githubtest");
        valuesMap.put("repository_nameWithOwner", "itamarc/githubtestwithalongname");
        valuesMap.put("repository_owner_avatarUrl", "https://avatars.githubusercontent.com/u/19577272?u=2bf4a3411aae650b4a5ac645845ae87ddbaad593&v=4");
        valuesMap.put("repository_owner_login", "myuser");
        valuesMap.put("repository_owner_url", "https://github.com/myuser");
        valuesMap.put("repository_repositoryTopics", "[{\"name\":\"github\",\"url\":\"https://github.com/topics/github\"},{\"name\":\"testing\",\"url\":\"https://github.com/topics/testing\"},{\"name\":\"repository\",\"url\":\"https://github.com/topics/repository\"}]");
        valuesMap.put("repository_shortDescriptionHTML", "Repository to test GitHub functionality.");
        valuesMap.put("repository_stargazerCount", "1");
        valuesMap.put("repository_updatedAt", "2021-07-04T02:17:15Z");
        valuesMap.put("repository_url", "https://github.com/itamarc/githubtest");
        valuesMap.put("repository_watchers_totalCount", "1");
        insertLastUpdate(valuesMap);
        return valuesMap;
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
}
