// Copyright (C) king.com Ltd 2019
// https://github.com/king/results-cache-plugin
// License: Apache 2.0, https://raw.githubusercontent.com/king/results-cache-plugin/master/LICENSE-APACHE

package hudson.plugins.resultscache.util;

import hudson.model.TaskListener;
import hudson.plugins.resultscache.Constants;

/**
 * Logger Util utility
 */
public class LoggerUtil {

    /**
     * Writes an info log trace
     * @param listener job task listener
     * @param format log trace format
     * @param args log trace arguments
     */
    public static void info(TaskListener listener, String format, Object... args) {
        logMessage(listener, " " + format, args);
    }

    /**
     * Writes a warning log trace
     * @param listener job task listener
     * @param format log trace format
     * @param args log trace arguments
     */
    public static void warn(TaskListener listener, String format, Object... args) {
        logMessage(listener, "[WARNING] " + format, args);
    }

    /**
     * Writes an error log trace
     * @param listener job task listener
     * @param format log trace format
     * @param args log trace arguments
     */
    public static void error(TaskListener listener, String format, Object... args) {
        logMessage(listener, "[ERROR] " + format, args);
    }

    /**
     * Writes a log message
     * @param listener job task listener
     * @param format log trace format
     * @param args log trace arguments
     */
    public static void logMessage(TaskListener listener, String format, Object... args) {
        String logMessageF = buildPluginLog(format);
        listener.getLogger().printf(logMessageF, args);
    }

    private static String buildPluginLog(String format) {
        return Constants.LOG_LINE_HEADER + format;
    }
}
