package io.github.itamarc.tmplpages;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionLogger {
    private static Logger logger = null;
    private static Handler handler = null;
    public static void setUpLogSys(String logLevel) {
        logger = Logger.getLogger(Action.class.getPackageName());
        handler = new ConsoleHandler();
        Level level = Level.WARNING;
        if (logLevel != null && !"".equals(logLevel)) {
            try {
                Level newlev = Level.parse(logLevel);
                level = newlev;
            } catch (Exception e) {
                level = Level.WARNING;
            }
        }
        handler.setLevel(level);
        logger.setLevel(level);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public static void severe(String msg) {
        logger.severe(formatMsg(msg));
    }

    public static void warning(String msg) {
        logger.warning(formatMsg(msg));
    }

    public static void info(String msg) {
        logger.info(formatMsg(msg));
    }

    public static void fine(String msg) {
        logger.fine(formatMsg(msg));
    }

    public static void finer(String msg) {
        logger.finer(formatMsg(msg));
    }

    public static void severe(String msg, Throwable thrown) {
        logger.log(Level.SEVERE, formatMsg(msg), thrown);
    }

    public static void warning(String msg, Throwable thrown) {
        logger.log(Level.WARNING, formatMsg(msg), thrown);
    }

    private static String formatMsg(String msg) {
        String classname = Thread.currentThread().getStackTrace()[3].getClassName();
        classname = classname.substring(classname.lastIndexOf(".")+1);
        return classname + ": " + msg;
    }
}
