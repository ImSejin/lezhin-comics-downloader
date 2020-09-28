package io.github.imsejin.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CrawlerTest {

    @Test
    public void getNumOfImagesInEpisode() {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";

        // when
        ChromeDriver driver = ChromeBrowser.getDriver();
        driver.get(URLFactory.oneEpisodeViewer(language, comicName, episodeName).toString());

        WebElement scrollList = driver.findElementById("scroll-list");

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfAllElements(scrollList));

        List<WebElement> images = scrollList.findElements(By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        int actual = images.size();

        // then
        assertEquals(11, actual);
    }

}