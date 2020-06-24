package io.github.imsejin.common.util;

public final class OSDetector {

    private OSDetector() {}

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

    public static String getOS() {
        if (isWindows()) return "windows";
        else if (isMac()) return "mac";
        else if (isUnix()) return "unix";
        else if (isSolaris()) return "solaris";
        else return null;
    }

}
