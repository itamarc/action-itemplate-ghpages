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
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public static void severe(String msg) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).severe(msg);
    }

    public static void warning(String msg) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).warning(msg);
    }

    public static void info(String msg) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).info(msg);
    }

    public static void fine(String msg) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).fine(msg);
    }

    public static void finer(String msg) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).finer(msg);
    }

    public static void severe(String msg, Throwable thrown) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).log(Level.SEVERE, msg, thrown);
    }

    public static void warning(String msg, Throwable thrown) {
        Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName()).log(Level.WARNING, msg, thrown);
    }
}
