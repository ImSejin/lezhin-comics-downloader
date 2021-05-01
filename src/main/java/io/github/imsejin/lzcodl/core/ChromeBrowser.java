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

import io.github.imsejin.common.util.PathnameUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @since 2.0.0
 */
public final class ChromeBrowser {

    private static final String CHROME_DRIVER_PATHNAME;

    private static ChromeOptions options = new ChromeOptions().addArguments(ChromeOption.getArguments());

    static {
        // Assigns chrome driver pathname.
        final String currentPathname = PathnameUtils.getCurrentPathname();
        String filename = "chromedriver.exe";                                                 // for Microsoft Windows
        if (Files.notExists(Paths.get(currentPathname, filename))) filename = "chromedriver"; // for Linux and macOS
        CHROME_DRIVER_PATHNAME = Paths.get(currentPathname, filename).toString();

        // Sets up pathname of web driver.
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATHNAME);
    }

    private ChromeBrowser() {
    }

    /**
     * @since 2.6.2
     */
    public static void debugging() {
        List<String> arguments = ChromeOption.getArguments();
        arguments.remove(ChromeOption.HEADLESS.argument);
        options = new ChromeOptions().addArguments(arguments);
    }

    public static ChromeDriver getDriver() {
        return SingletonLazyHolder.DRIVER;
    }

    private static class SingletonLazyHolder {
        private static final ChromeDriver DRIVER = new ChromeDriver(options);
    }

    /**
     * @since 2.6.2
     */
    @Getter
    @RequiredArgsConstructor
    public enum ChromeOption {

        /**
         * 전체화면으로 실행한다.
         */
        START_MAXIMIZED("--start_maximized"),

        /**
         * 팝업화면을 무시한다.
         */
        DISABLE_POPUP_BLOCKING("--disable_popup_blocking"),

        /**
         * 기본 앱을 사용하지 않는다.
         */
        DISABLE_DEFAULT_APPS("--disable_default_apps"),

        /**
         * 인증 오류를 무시한다.
         */
        IGNORE_CERTIFICATE_ERRORS("--ignore_certificate_errors"),

        /**
         * CLI 환경에서도 실행할 수 있게 한다.
         */
        HEADLESS("--headless"),

        /**
         * 프라이빗 모드로 실행한다.
         */
        INCOGNITO("--incognito");

        private final String argument;

        public static List<String> getArguments() {
            return Arrays.stream(values()).map(it -> it.argument).collect(toList());
        }
    }

}
