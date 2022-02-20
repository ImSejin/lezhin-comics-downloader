package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.TestUtils;
import io.github.imsejin.lzcodl.common.constant.URIs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class CrawlerTest {

    private static ChromeDriver driver;

    @BeforeAll
    static void beforeAll() {
        String path = TestUtils.getDriverPath().getPath();
        System.setProperty("webdriver.chrome.driver", path);
        new File(path).setExecutable(true);
        driver = new ChromeDriver(new ChromeOptions().addArguments(ChromeBrowser.ChromeOption.getArguments()));
        // driver = ChromeBrowser.getDriver();
    }

    @AfterAll
    static void afterAll() {
        driver.quit();
    }

    @Test
    void getNumOfImagesInEpisode() {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";
        URI episodeUrl = URIs.EPISODE.get(language, comicName, episodeName);

        // when
        driver.get(episodeUrl.toString());

        WebElement scrollList = driver.findElement(By.id("scroll-list"));

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfAllElements(scrollList));

        List<WebElement> images = scrollList.findElements(
                By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        int actual = images.size();

        // then
        assertThat(actual).isEqualTo(11);
    }

}
