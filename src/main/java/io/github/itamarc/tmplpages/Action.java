package io.github.itamarc.tmplpages;

import java.util.Map;

public class Action {
    public static void main(String[] args) {
        TemplateProcessor proc = new TemplateProcessor(args[0], args[1]);
        System.out.println(proc.run());
        printEnvironment();
    }

    private static void printEnvironment() {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                              envName,
                              env.get(envName));
        }
    }
}
