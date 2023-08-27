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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.concurrent.ThreadSafe;

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

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.util.ClassUtils;
import io.github.imsejin.dl.lezhin.exception.WebBrowserNotRunningException;

/**
 * @since 2.0.0
 */
@ThreadSafe
public final class WebBrowser {

    public static final long DEFAULT_TIMEOUT_SECONDS = 15;

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS);

    public static final String INITIAL_URL = "data:,";

    private static final List<String> arguments = ChromeOption.getArguments();

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
        Stream.of(ChromeOption.HEADLESS, ChromeOption.NO_SANDBOX, ChromeOption.DISABLE_GPU)
                .map(ChromeOption::getArgument).forEach(arguments::remove);
    }

    // Initialization ----------------------------------------------------------------------------------

    @ExcludeFromGeneratedJacocoReport
    private WebBrowser() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    private static final class SingletonLazyHolder {
        static {
            ChromeOptions options = new ChromeOptions().addArguments(WebBrowser.arguments);
            DRIVER = new ChromeDriver(options);
            WebBrowser.initialized = true;
        }

        private static final RemoteWebDriver DRIVER;
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
    public static void run(Path chromeDriverPath) {
        if (isRunning()) {
            return;
        }

        // Sets up pathname of web driver.
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriverPath.toString());
        // See: https://github.com/SeleniumHQ/selenium/issues/11750
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        // Invokes any instance method of driver to initialize this field by classloader.
        // `WebBrowser.initialized` will be updated after the invocation.
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

    /**
     * Goes to the uri.
     *
     * @param uri absolute or relative uri
     * @since 3.0.0
     */
    public static void request(URI uri) {
        String uriString = uri.normalize().toString();
        request(uriString);
    }

    /**
     * Goes to the uri.
     *
     * @param uri absolute or relative uri
     * @since 3.0.0
     */
    public static void request(String uri) {
        CHECK_INITIALIZATION.run();

        URI u;
        try {
            String currentUrl = getCurrentUrl();
            if (INITIAL_URL.equals(currentUrl)) {
                currentUrl = uri;
            }

            u = new URL(currentUrl).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        String url = u.resolve(uri).normalize().toString();
        SingletonLazyHolder.DRIVER.get(url);
    }

    public static String getCurrentUrl() {
        CHECK_INITIALIZATION.run();

        return SingletonLazyHolder.DRIVER.getCurrentUrl();
    }

    // Waiting -----------------------------------------------------------------------------------------

    /**
     * Waits for the presence of element.
     *
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
     * Waits for the visibility of element.
     *
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

}
