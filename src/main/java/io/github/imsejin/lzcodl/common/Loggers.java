package io.github.imsejin.lzcodl.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {

    private static String loggerName = "NormalLogger";

    private Loggers() {
    }

    public static void debugging() {
        loggerName = "DebugLogger";
    }

    public static Logger getLogger() {
        return SingletonLazyHolder.LOGGER;
    }

    private static class SingletonLazyHolder {
        private static final Logger LOGGER;

        static {
            LOGGER = LoggerFactory.getLogger(loggerName);
        }
    }

}
