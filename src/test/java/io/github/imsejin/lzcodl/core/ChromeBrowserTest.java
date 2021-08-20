package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Product;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openqa.selenium.chrome.ChromeDriver;

import java.lang.reflect.Method;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChromeBrowser")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChromeBrowserTest {

    @AfterAll
    static void quitDriver() {
        ChromeBrowser.softQuit();
    }

    @Test
    @DisplayName("method 'getVersion'")
    void getVersion() {
        assertThat(ChromeBrowser.getVersion()).isNotNull().isEqualTo("92.0.4515.159");
    }

    @Nested
    @DisplayName("Gets json using chrome driver")
    class GetJson {
        @Test
        @EnabledOnOs(OS.LINUX)
        @DisplayName("when on linux")
        void test0() {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/linux/chromedriver");
            assertThatGettingJson(url);
        }

        @Test
        @EnabledOnOs(OS.MAC)
        @DisplayName("when on mac")
        void test1() {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/mac/chromedriver");
            assertThatGettingJson(url);
        }

        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("when on windows")
        void test2() {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("chrome-driver/92.0.4515.107/windows/chromedriver.exe");
            assertThatGettingJson(url);
        }

        @SneakyThrows
        private void assertThatGettingJson(URL url) {
            // Pre-initializes class "ChromeBrowser".
            Class.forName(ChromeBrowser.class.getName());

            // given
            System.setProperty("webdriver.chrome.driver", url.toURI().getPath());
            ChromeDriver driver = ChromeBrowser.getDriver();

            // when
            driver.get(URIs.COMIC.get("ko", "snail").toString());
            driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");
            String json = driver.getLocalStorage().getItem("product");

            // then
            assertThat(json)
                    .as("The item 'product' of local storage must be json string")
                    .matches("\\{\"\\w.*\":.+}");
            Product product = JsonUtils.toObject(json, Product.class);
            assertThat(product)
                    .as("The json string can be parsed to Product")
                    .isExactlyInstanceOf(Product.class)
                    .returns("아가씨와 우렁총각", it -> it.getDisplay().getTitle());
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
        try {
            ChromeBrowser.getDriver().quit();
        } catch (Throwable ignored) {
        }
        Class<?> loadedClass = (Class<?>) findLoadedClass.invoke(classLoader, className);
        // then: 2
        assertThat(loadedClass)
                .isNotNull()
                .isEqualTo(Class.forName(className));
    }

}
