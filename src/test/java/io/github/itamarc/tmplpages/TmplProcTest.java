package io.github.itamarc.tmplpages;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TmplProcTest extends ActionRunner {
    private static String baseDir;
    
    public static void main(String[] args) throws Exception {
        ActionLogger.setUpLogSys("FINER");
        // Set this environment variable on your system or your IDE:
        // (this needs to point to the local root of action-itemplate-ghpages)
        baseDir = System.getenv("ACTION_ROOT_DIR");
        if (baseDir == null || !new File(baseDir).exists()) {
            ActionLogger.severe("Base directory not found: " + baseDir);
            System.exit(1);
        }
        var runner = new TmplProcTest();
        runner.run();
    }

    @Override
    protected void feedEnvironmentToMap(Map<String, String> valuesMap) {
        // Values more changed for tests
        valuesMap.put("GITHUB_WORKSPACE", baseDir);
        // ------- THEMES -------
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":reference:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":light:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":dark:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":bluish:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":greenish:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":purplish:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":grayish:");
        valuesMap.put("INPUT_TEMPLATES_FOLDER", ":hellish:");
        // valuesMap.put("INPUT_TEMPLATES_FOLDER", ":greenscreen:");
        // ----------------------
        valuesMap.put("INPUT_PAGES_FOLDER", "target\\docs");
        valuesMap.put("INPUT_SYNTAX_HIGHLIGHT_ENABLE", "true");
        valuesMap.put("INPUT_SYNTAX_HIGHLIGHT_THEME", "tomorrow");
        // valuesMap.put("INPUT_SYNTAX_HIGHLIGHT_THEME", "default");
        valuesMap.put("GITHUB_REPOSITORY", "itamarc/action-itemplate-ghpages");
        // valuesMap.put("GITHUB_REPOSITORY", "itamarc/githubtest");
        valuesMap.put("INPUT_PUBLISH_README_MD", "true");
        // valuesMap.put("INPUT_PUBLISH_README_MD", "inline");
        valuesMap.put("INPUT_CONVERT_MD_TO_HTML", "true");

        valuesMap.put("THEMES_PATH", baseDir + "\\themes");
        valuesMap.put("GITHUB_ACTOR", "itamarc");
        valuesMap.put("GITHUB_GRAPHQL_URL", "https://api.github.com/graphql");
        valuesMap.put("GITHUB_REPOSITORY_OWNER", "itamarc");
        valuesMap.put("GITHUB_SERVER_URL", "https://github.com");
        valuesMap.put("INPUT_TIMEZONE", "America/Sao_Paulo");
        valuesMap.put("INPUT_CONTENT_TO_COPY", "images Themes.md");
    }

    @Override
    protected void getGHApiData(HashMap<String, String> valuesMap) {
        valuesMap.put("repository_collaborators", "[{\"name\":\"Itamar Carvalho\",\"login\":\"itamarc\",\"url\":\"https://github.com/itamarc\"},{\"login\":\"gaaeus\",\"url\":\"https://github.com/gaaeus\",\"name\":\"Hélio Silva\"}]");
        valuesMap.put("repository_createdAt", "2021-06-23T22:02:31Z");
        valuesMap.put("repository_description", "Action to publish GitHub Pages automatically using themes or custom templates (using ITemplate)");
        valuesMap.put("repository_shortDescriptionHTML", "Action to publish GitHub Pages automatically using themes or custom templates (using ITemplate)");
        valuesMap.put("repository_forkCount", "0");
        valuesMap.put("repository_homepageUrl", "https://itamarc.github.io/action-itemplate-ghpages/");
        // valuesMap.put("repository_homepageUrl", "https://itamarc.github.io/githubtest/");
        valuesMap.put("repository_issues", "[{\"number\":10,\"createdAt\":\"2021-07-04T02:00:38Z\",\"comments_totalCount\":0,\"author\":{\"login\":\"itamarc\",\"url\":\"https://github.com/itamarc\"},\"titleHTML\":\"Create some initial template sets\",\"url\":\"https://github.com/itamarc/action-itemplate-ghpages/issues/10\"},{\"number\":12,\"createdAt\":\"2021-07-04T02:04:03Z\",\"comments_totalCount\":1,\"author\":{\"login\":\"itamarc\",\"url\":\"https://github.com/itamarc\"},\"titleHTML\":\"Complete documentation for v1\",\"url\":\"https://github.com/itamarc/action-itemplate-ghpages/issues/12\"},{\"number\":23,\"createdAt\":\"2021-09-17T14:36:58Z\",\"comments_totalCount\":0,\"author\":{\"login\":\"itamarc\",\"url\":\"https://github.com/itamarc\"},\"titleHTML\":\"Create screenshots of the themes\",\"url\":\"https://github.com/itamarc/action-itemplate-ghpages/issues/23\"}]");
        // valuesMap.put("repository_languages", "[{\"color\":\"#3572A5\",\"size\":106,\"name\":\"Python\"}]");
        // valuesMap.put("repository_languages_totalSize", "106");
        valuesMap.put("repository_languages", "[{\"color\":\"#384d54\",\"name\":\"Dockerfile\",\"size\":741},{\"color\":\"#b07219\",\"name\":\"Java\",\"size\":52167},{\"color\":\"#89e051\",\"name\":\"Shell\",\"size\":824},{\"color\":\"#f1e05a\",\"name\":\"JavaScript\",\"size\":6356},{\"color\":\"#e34c26\",\"name\":\"HTML\",\"size\":66546},{\"color\":\"#563d7c\",\"name\":\"CSS\",\"size\":63250}]");
        valuesMap.put("repository_languages_totalSize", "189884");
        valuesMap.put("repository_latestRelease_name", "Version 1.0");
        valuesMap.put("repository_latestRelease_description", "This is the first version");
        valuesMap.put("repository_latestRelease_author_login", "itamarc");
        valuesMap.put("repository_latestRelease_author_name", "Itamar Carvalho");
        valuesMap.put("repository_latestRelease_createdAt", "2021-09-19T12:34:56Z");
        valuesMap.put("repository_latestRelease_isPrerelease", "false");
        valuesMap.put("repository_latestRelease_tagName", "v1");
        valuesMap.put("repository_latestRelease_url", "https://github.com/itamarc/githubtest/releases/tag/v1");
        valuesMap.put("repository_licenseInfo_conditions", "[\"License and copyright notice\",\"State changes\",\"Disclose source\",\"Same license\"]");
        valuesMap.put("repository_licenseInfo_name", "GNU General Public License v3.0");
        valuesMap.put("repository_licenseInfo_nickname", "GNU GPLv3");
        valuesMap.put("repository_licenseInfo_url", "http://choosealicense.com/licenses/gpl-3.0/");
        valuesMap.put("repository_name", "action-itemplate-ghpages");
        valuesMap.put("repository_nameWithOwner", "itamarc/action-itemplate-ghpages");
        valuesMap.put("repository_owner_avatarUrl", "https://avatars.githubusercontent.com/u/19577272?u=2bf4a3411aae650b4a5ac645845ae87ddbaad593&v=4");
        valuesMap.put("repository_owner_login", "itamarc");
        valuesMap.put("repository_owner_url", "https://github.com/itamarc");
        valuesMap.put("repository_repositoryTopics", "[{\"name\":\"github\",\"url\":\"https://github.com/topics/github\"},{\"name\":\"github-actions\",\"url\":\"https://github.com/topics/github-actions\"},{\"name\":\"github-pages\",\"url\":\"https://github.com/topics/github-pages\"},{\"name\":\"markdown\",\"url\":\"https://github.com/topics/markdown\"},{\"name\":\"java\",\"url\":\"https://github.com/topics/java\"},{\"name\":\"docker\",\"url\":\"https://github.com/topics/docker\"},{\"name\":\"graphql-client\",\"url\":\"https://github.com/topics/graphql-client\"},{\"name\":\"gfm\",\"url\":\"https://github.com/topics/gfm\"}]");
        valuesMap.put("repository_stargazerCount", "1");
        valuesMap.put("repository_updatedAt", "2021-09-16T19:21:38Z");
        valuesMap.put("repository_url", "https://github.com/itamarc/action-itemplate-ghpages");
        valuesMap.put("repository_watchers_totalCount", "1");
    }
}
