package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Product;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChromeBrowserTest {

    @Test
    @Order(1)
    public void getJson() {
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

}
