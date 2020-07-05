package io.github.imsejin.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrawlerTest {

    @Test
    public void getNumOfImagesInEpisode() throws InterruptedException {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";

        // when
        int actual = Crawler.getNumOfImagesInEpisode(language, comicName, episodeName);

        // then
        assertEquals(11, actual);
    }

}