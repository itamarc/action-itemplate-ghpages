package io.github.itamarc.tmplpages;

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
        ActionLogger.setUpLogSys(System.getenv("INPUT_LOG_LEVEL"));
        var runner = new ActionRunner();
        runner.run();
    }
}
