package io.github.imsejin.lzcodl.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {

    private static final Logger logger = LoggerFactory.getLogger("NormalLogger");

    private static final Logger debugger = LoggerFactory.getLogger("DebugLogger");

    private static boolean debugging;

    public static void debugging() {
        debugging = true;
    }

    public static Logger getLogger() {
        return debugging ? debugger : logger;
    }

}
