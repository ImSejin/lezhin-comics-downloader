package io.github.imsejin.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathnameUtils {

    @SneakyThrows(IOException.class)
    public String currentPathname() {
        return Paths.get(".").toRealPath().toString();
    }

    public String chromeDriverPathname() {
        final String currentPathname = currentPathname();

        String filename = "chromedriver.exe";                                                 // for Windows
        if (Files.notExists(Paths.get(currentPathname, filename))) filename = "chromedriver"; // for Linux and Mac

        return Paths.get(currentPathname, filename).toString();
    }

}
