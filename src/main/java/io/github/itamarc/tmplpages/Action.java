package io.github.itamarc.tmplpages;

import java.util.Map;

public class Action {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        TemplateProcessor proc = new TemplateProcessor(
                env.get("INPUT_TEMPLATES_FOLDER"),
                env.get("INPUT_TEMPLATES_BRANCH"),
                env.get("INPUT_PAGES_FOLDER"),
                env.get("INPUT_PAGES_BRANCH"),
                env.get("INPUT_SNIPPETS_FOLDER"));

        System.out.println("Run result: "+proc.run(env.get("RUNNER_WORKSPACE")));
        printEnvironment(env); // only for testing
    }

    // This is only for testing fase
    private static void printEnvironment(Map<String, String> env) {
        System.out.println(">>> Environment:");
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n", envName, env.get(envName));
        }
    }
}
