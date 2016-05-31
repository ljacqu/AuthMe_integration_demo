package ch.jalu.authme.integrationdemo;

import java.util.logging.Logger;

/**
 * Static logger for the plugin.
 */
public class SampleLogger {

    private static Logger logger;

    private SampleLogger() {
        // Class only has static methods and should not be instantiated
    }

    public static void setLogger(Logger logger) {
        SampleLogger.logger = logger;
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }
}
