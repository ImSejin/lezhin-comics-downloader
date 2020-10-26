package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.common.constants.URIs;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CrawlerTest {

    @Test
    public void getNumOfImagesInEpisode() {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";
        String episodeUrl = URIs.EPISODE.get(language, comicName, episodeName);

        // when
        ChromeDriver driver = ChromeBrowser.getDriver();
        driver.get(episodeUrl);

        WebElement scrollList = driver.findElementById("scroll-list");

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfAllElements(scrollList));

        List<WebElement> images = scrollList.findElements(
                By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        int actual = images.size();

        // then
        assertThat(actual).isEqualTo(11);
    }

}