package com.sweeky.message.handler.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final String SPLIT = " | ";

    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static boolean isDebugEnabled(Class clazz) {
        return getLogger(clazz).isDebugEnabled();
    }

    public static void debug(Class clazz, String title, String msg) {
        getLogger(clazz).debug(title + SPLIT + msg);
    }

    public static void debug(Class clazz, String title, String format, Object... args) {
        getLogger(clazz).debug(title + SPLIT + format, args);
    }

    public static void info(Class clazz, String title, String msg) {
        getLogger(clazz).info(title + SPLIT + msg);
    }

    public static void info(Class clazz, String title, String format, Object... args) {
        getLogger(clazz).info(title + SPLIT + format, args);
    }

    public static void warn(Class clazz, String title, String msg) {
        getLogger(clazz).warn(title + SPLIT + msg);
    }

    public static void error(Class clazz, String title, String msg) {
        getLogger(clazz).error(title + SPLIT + msg);
    }

    public static void error(Class clazz, String title, String format, Object... args) {
        getLogger(clazz).error(title + SPLIT + format, args);
    }
}
