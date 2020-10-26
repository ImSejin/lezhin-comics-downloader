package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.model.Product;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.*;

public class ChromeBrowserTest {

    @Test
    public void getJson() {
        // given
        ChromeDriver driver = ChromeBrowser.getDriver();

        // when
        driver.get("https://www.lezhin.com/ko/comic/snail");
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");
        String product = driver.getLocalStorage().getItem("product");

        // then
        assertThat(product)
                .as("The item 'product' of local storage must be json string")
                .startsWith("{\"display\":{\"title\":\"");
        assertThat(JsonUtils.toObject(product, Product.class))
                .as("The json string can be parsed to Product")
                .isExactlyInstanceOf(Product.class);
        System.out.println(product);
    }

}
