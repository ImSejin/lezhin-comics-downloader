/*
 * Copyright 2020 Sejin Im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.common.constant.OperatingSystem;
import io.github.imsejin.common.tool.OSDetector;
import io.github.imsejin.common.util.PathnameUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @since 2.0.0
 */
public final class ChromeBrowser {

    private static final String CHROME_DRIVER_PATHNAME;

    private static ChromeOptions options = new ChromeOptions().addArguments(ChromeOption.getArguments());

    private static boolean initialized;

    static {
        // Assigns chrome driver pathname.
        final String currentPathname = PathnameUtils.getCurrentPathname();
        String filename = "chromedriver.exe";                                                 // for Microsoft Windows
        if (Files.notExists(Paths.get(currentPathname, filename))) filename = "chromedriver"; // for Linux and macOS
        CHROME_DRIVER_PATHNAME = Paths.get(currentPathname, filename).toString();

        // Sets up pathname of web driver.
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATHNAME);
    }

    @ExcludeFromGeneratedJacocoReport
    private ChromeBrowser() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    public static boolean isRunning() {
        return ChromeBrowser.initialized;
    }

    /**
     * @since 2.6.2
     */
    public static void debugging() {
        List<String> arguments = ChromeOption.getArguments();
        arguments.remove(ChromeOption.HEADLESS.argument);
        arguments.remove(ChromeOption.NO_SANDBOX.argument);
        arguments.remove(ChromeOption.DISABLE_GPU.argument);

        options = new ChromeOptions().addArguments(arguments);
    }

    public static ChromeDriver getDriver() {
        return SingletonLazyHolder.DRIVER;
    }

    public static void softQuit() {
        if (ChromeBrowser.initialized) SingletonLazyHolder.DRIVER.quit();
    }

    /**
     * Returns version of Google Chrome installed.
     *
     * @return version of Google Chrome
     * @since 2.8.2
     */
    @SneakyThrows
    public static String getVersion() {
        OperatingSystem os = OSDetector.getOS();
        String[] command;

        if (os == OperatingSystem.UNIX) {
            command = new String[]{"/bin/sh", "-c", "google-chrome", "--version", "|", "grep", "-oE", "'[0-9][0-9.]+[0-9]'"};
        } else if (os == OperatingSystem.MAC) {
            command = new String[]{"/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome", "--version"};
        } else if (os == OperatingSystem.WINDOWS) {
            command = new String[]{"powershell.exe",
                    "(Get-Item 'C:/Program Files/Google/Chrome/Application/chrome.exe').VersionInfo", "|",
                    "Select-String '(\\d{1,3}\\.[\\d.]+\\d)'", "|",
                    "ForEach-Object {$_.Matches[0].Groups[0].Value}"};
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os.name().toLowerCase());
        }

        Process process = Runtime.getRuntime().exec(command);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.lines().collect(joining()).trim();
        }
    }

    private static class SingletonLazyHolder {
        static {
            ChromeBrowser.initialized = true;
        }

        private static final ChromeDriver DRIVER = new ChromeDriver(options);
    }

    /**
     * @since 2.6.2
     */
    @Getter
    @RequiredArgsConstructor
    public enum ChromeOption {
        /**
         * Opens browser in maximized mode.
         */
        START_MAXIMIZED("--start-maximized"),

        /**
         * Opens browser on private mode.
         */
        INCOGNITO("--incognito"),

        /**
         * Runs browser using CLI.
         */
        HEADLESS("--headless"),

        /**
         * Bypasses OS security model.
         *
         * @since 2.8.2
         */
        NO_SANDBOX("--no-sandbox"),

        /**
         * Disables GPU computation (applicable to Windows OS only).
         *
         * @since 2.8.2
         */
        DISABLE_GPU("--disable-gpu"),

        /**
         * Ignores certificate errors.
         */
        IGNORE_CERTIFICATE_ERRORS("--ignore-certificate-errors"),

        /**
         * Disables to check if Google Chrome is default browser on your device.
         *
         * @since 2.8.2
         */
        NO_DEFAULT_BROWSER_CHECK("--no-default-browser-check"),

        /**
         * Disables popup blocking.
         */
        DISABLE_POPUP_BLOCKING("--disable-popup-blocking"),

        /**
         * Disables installed extensions(plugins) of Google Chrome.
         *
         * @since 2.8.2
         */
        DISABLE_EXTENSIONS("--disable-extensions"),

        /**
         * Disables default web apps on Google Chrome’s new tab page
         * <p>
         * Chrome Web Store, Google Drive, Gmail, YouTube, Google Search, etc.
         */
        DISABLE_DEFAULT_APPS("--disable-default-apps"),

        /**
         * Disables Google translate feature.
         *
         * @since 2.8.2
         */
        DISABLE_TRANSLATE("--disable-translate"),

        /**
         * Disables detection for client side phishing.
         *
         * @since 2.8.2
         */
        DISABLE_CLIENT_SIDE_PHISHING_DETECTION("--disable-client-side-phishing-detection"),

        /**
         * Overcomes limited resource problems.
         *
         * @since 2.8.2
         */
        DISABLE_DEV_SHM_USAGE("--disable-dev-shm-usage");

        private final String argument;

        public static List<String> getArguments() {
            return Arrays.stream(values()).map(it -> it.argument).collect(toList());
        }
    }

}
