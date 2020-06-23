package io.github.imsejin.core;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeBrowserTest {

    @Test
    public void testGetJson() {
        // given
        ChromeDriver driver = ChromeBrowser.getDriver();

        // when
        driver.get("https://www.lezhin.com/ko/comic/snail");
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");
        String product = driver.getLocalStorage().getItem("product");
        System.out.println(product);

        // then
        assertTrue(product.startsWith("{\"display\":{\"title\":\""));
    }

    @AfterClass
    public static void quitDriver() {
        // ChromeDriver를 닫고 해당 프로세스를 종료한다.
        ChromeBrowser.getDriver().quit();
    }

}
