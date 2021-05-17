package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Product;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.chrome.ChromeDriver;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChromeBrowserTest {

    @Test
    @Order(1)
    void getJson() {
        // given
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
    }

    @Test
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
