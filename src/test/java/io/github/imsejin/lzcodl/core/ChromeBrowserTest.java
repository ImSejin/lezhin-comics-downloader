package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Product;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChromeBrowser")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChromeBrowserTest {

    @Nested
    @DisplayName("Gets version of google chrome installed")
    class GetChromeBrowserVersion {
        private static final String VERSION = "92.0.4515.159";

        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("when on windows")
        void test0() {
            assertThatChromeBrowserVersion("powershell.exe",
                    "(Get-Item 'C:/Program Files/Google/Chrome/Application/chrome.exe').VersionInfo", "|",
                    "Select-String '(\\d{1,3}\\.[\\d.]+\\d)'", "|",
                    "ForEach-Object {$_.Matches[0].Groups[0].Value}");
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        @DisplayName("when on linux")
        void test1() {
            assertThatChromeBrowserVersion("google-chrome", "--version");
        }

        @Test
        @EnabledOnOs(OS.MAC)
        @DisplayName("when on mac")
        void test2() {
            assertThatChromeBrowserVersion("/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome", "--version");
        }

        @SneakyThrows
        private void assertThatChromeBrowserVersion(String... command) {
            Process process = Runtime.getRuntime().exec(command);

            @Cleanup
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.lines().collect(joining());

            assertThat(version).isNotNull().isEqualTo(VERSION);
        }
    }

    @Nested
    @DisplayName("Gets json using chrome driver")
    class GetJson {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("when on windows")
        void test0() {
            // Sets up pathname of web driver.
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/windows/chromedriver.exe");

            assertThatGettingJson(url);
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        @DisplayName("when on linux")
        void test1() {
            // Sets up pathname of web driver.
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/windows/chromedriver.exe");

            assertThatGettingJson(url);
        }

        @Test
        @EnabledOnOs(OS.MAC)
        @DisplayName("when on mac")
        void test2() {
            // Sets up pathname of web driver.
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/windows/chromedriver.exe");

            assertThatGettingJson(url);
        }

        @Test
        @SneakyThrows
        private void assertThatGettingJson(URL url) {
            // Pre-initializes class "ChromeBrowser".
            Class.forName(ChromeBrowser.class.getName());

            try {
                // given
                System.setProperty("webdriver.chrome.driver", url.toURI().getPath());
                ChromeDriver driver = ChromeBrowser.getDriver();

                // when
                driver.get(URIs.COMIC.get("ko", "snail").toString());
                driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");
                String product = driver.getLocalStorage().getItem("product");

                // then
                assertThat(product)
                        .as("The item 'product' of local storage must be json string")
                        .matches("\\{\"\\w.*\":.+}");
                assertThat(JsonUtils.toObject(product, Product.class))
                        .as("The json string can be parsed to Product")
                        .isExactlyInstanceOf(Product.class);
                System.out.println(product);
            } finally {
                ChromeBrowser.softQuit();
            }
        }
    }

    @Test
    @Disabled
    void findLoadedClass() throws Exception {
        // given
        Method findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        findLoadedClass.setAccessible(true);
        String className = ChromeBrowser.class.getName() + "$SingletonLazyHolder";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // when: 1
        Class<?> clazz = (Class<?>) findLoadedClass.invoke(classLoader, className);
        // then: 1
        assertThat(clazz).isNull();

        // when: 2
        ChromeBrowser.getDriver().quit();
        Class<?> loadedClass = (Class<?>) findLoadedClass.invoke(classLoader, className);
        // then: 2
        assertThat(loadedClass)
                .isNotNull()
                .isEqualTo(Class.forName(className));
    }

}
