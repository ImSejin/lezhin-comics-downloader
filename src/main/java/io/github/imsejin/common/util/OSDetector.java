package io.github.imsejin.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OSDetector {

    private final String OS = System.getProperty("os.name").toLowerCase();

    public boolean isWindows() {
        return OS.contains("win");
    }

    public boolean isMac() {
        return OS.contains("mac");
    }

    public boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public boolean isSolaris() {
        return OS.contains("sunos");
    }

    public String getOS() {
        if (isWindows()) return "windows";
        else if (isMac()) return "mac";
        else if (isUnix()) return "unix";
        else if (isSolaris()) return "solaris";
        else return null;
    }

}
