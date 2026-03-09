package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Utility class to provide centralized access to Log4j2 loggers.
 * Simplifies logger retrieval across classes.
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class LoggerUtils {

    /**
     * Returns a logger instance for the specified class using Log4j2.
     *
     * @param clazz Class for which the logger is to be created
     * @return Logger instance tied to the class
     */

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
