package io.github.imsejin.core;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CrawlerTest {

    @Test
    public void getNumOfImagesInEpisode() throws InterruptedException {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";

        // when
        ChromeDriver driver = ChromeBrowser.getDriver();
        driver.get(URLFactory.oneEpisodeViewer(language, comicName, episodeName).toString());

        // DOM 렌더링을 기다린다
        TimeUnit.SECONDS.sleep(2);

        WebElement scrollList = driver.findElementById("scroll-list");
        List<WebElement> images = scrollList.findElements(By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        int actual = images.size();

        // then
        assertEquals(11, actual);
    }

}