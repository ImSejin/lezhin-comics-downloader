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

package io.github.imsejin.dl.lezhin.browser;

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.constant.OS;
import io.github.imsejin.common.util.ClassUtils;
import io.github.imsejin.dl.lezhin.exception.WebBrowserNotRunningException;
import io.github.imsejin.dl.lezhin.util.PathUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @since 2.0.0
 */
@ThreadSafe
public final class WebBrowser {

    public static final long DEFAULT_TIMEOUT_SECONDS = 15;

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS);

    private static ChromeOptions options = new ChromeOptions().addArguments(ChromeOption.getArguments());

    private static final Runnable CHECK_INITIALIZATION = () -> {
        if (!isRunning()) {
            throw new WebBrowserNotRunningException("WebBrowser is not initialized yet");
        }
    };

    private static final Set<Class<?>> SUPPORTED_EVALUATED_TYPES = Set.of(
            Boolean.class, Long.class, Double.class, String.class, List.class, Map.class);

    private static boolean initialized;

    // Preparation -------------------------------------------------------------------------------------

    /**
     * @since 2.6.2
     */
    public static void debugging() {
        List<String> arguments = ChromeOption.getArguments();
        Stream.of(ChromeOption.HEADLESS, ChromeOption.NO_SANDBOX, ChromeOption.DISABLE_GPU)
                .map(ChromeOption::getArgument).forEach(arguments::remove);

        options = new ChromeOptions().addArguments(arguments);
    }

    // Initialization ----------------------------------------------------------------------------------

    static {
        // Assigns chrome driver pathname.
        String fileName;
        if (OS.WINDOWS.isCurrentOS()) {
            fileName = "chromedriver.exe"; // for Microsoft Windows
        } else {
            fileName = "chromedriver"; // for Linux and macOS
        }

        Path currentPath = PathUtils.getCurrentPath();
        Path chromeDriverPath = currentPath.resolve(fileName);

        // Sets up pathname of web driver.
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriverPath.toString());
    }

    @ExcludeFromGeneratedJacocoReport
    private WebBrowser() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    private static final class SingletonLazyHolder {
        static {
            WebBrowser.initialized = true;
        }

        private static final RemoteWebDriver DRIVER = new ChromeDriver(options);
    }

    public static RemoteWebDriver getDriver() {
        return SingletonLazyHolder.DRIVER;
    }

    /**
     * Runs web browser by chromedriver file.
     *
     * @since 3.0.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void run() {
        // Invokes any instance method of driver to initialize this field by classloader.
        SingletonLazyHolder.DRIVER.hashCode();
    }

    // -------------------------------------------------------------------------------------------------

    public static boolean isRunning() {
        return WebBrowser.initialized;
    }

    public static void quitIfInitialized() {
        if (isRunning()) {
            getDriver().quit();
        }
    }

    // Waiting -----------------------------------------------------------------------------------------

    /**
     * @param locator xpath used to find element
     * @return found element
     * @throws TimeoutException if timeout expires
     * @since 3.0.0
     */
    public static WebElement waitForPresenceOfElement(By locator) throws TimeoutException {
        CHECK_INITIALIZATION.run();
        WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * @param locator xpath used to find element
     * @return found element
     * @throws TimeoutException if timeout expires
     * @since 3.0.0
     */
    public static WebElement waitForVisibilityOfElement(By locator) throws TimeoutException {
        CHECK_INITIALIZATION.run();
        WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Javascript --------------------------------------------------------------------------------------

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T evaluate(String javascript, Class<T> expected) {
        expected = ClassUtils.wrap(expected);

        Asserts.that(SUPPORTED_EVALUATED_TYPES)
                .describedAs("Type of evaluated expression cannot be {0}; supported types: {1}",
                        expected, SUPPORTED_EVALUATED_TYPES)
                .contains(expected);

        CHECK_INITIALIZATION.run();

        Object evaluated = SingletonLazyHolder.DRIVER.executeScript("return " + javascript);

        for (Class<?> type : SUPPORTED_EVALUATED_TYPES) {
            if (type.isInstance(evaluated) && type == expected) {
                return (T) evaluated;
            }
        }

        // When not matching between evaluated type and expected type.
        return null;
    }

    // Driver Options ----------------------------------------------------------------------------------

    /**
     * @since 2.6.2
     */
    @Getter
    @RequiredArgsConstructor
    private enum ChromeOption {
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
