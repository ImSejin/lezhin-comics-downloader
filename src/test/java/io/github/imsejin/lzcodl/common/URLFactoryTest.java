package io.github.imsejin.lzcodl.common;

import io.github.imsejin.common.tool.Stopwatch;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class URLFactoryTest {

    private Stopwatch stopwatch;

    @SneakyThrows
    private static URL getImageURL(long comicId, long episodeId, int filename, String accessToken, boolean purchased) {
        String str = "http://cdn.lezhin.com/v2/comics/" + comicId + "/episodes/" + episodeId + "/contents/scrolls/" + filename +
                ".webp?access_token=" + accessToken + "&purchased=" + purchased + "&q=30";
        return new URL(str);
    }

    @BeforeEach
    void beforeEach() {
        this.stopwatch = new Stopwatch(TimeUnit.MILLISECONDS);
        this.stopwatch.start("Get URLs");
    }

    @AfterEach
    void afterEach() {
        this.stopwatch.stop();
        System.out.println(this.stopwatch.getStatistics());
    }

    @Test
    @DisplayName("Appends to StringBuilder")
    void imageWithStringBuilder() {
        // given
        long comicId = 5651768999542784L;
        long episodeId = 6393378955722752L;
        String accessToken = "5be30a25-a044-410c-88b0-19a1da968a64";

        // when
        IntStream.range(1, 1_000_000).parallel()
                .forEach(i -> URLFactory.image(comicId, episodeId, i, accessToken, (i & 1) == 1));
    }

    @Test
    @DisplayName("Concatenates strings")
    void imageWithString() {
        // given
        long comicId = 5651768999542784L;
        long episodeId = 6393378955722752L;
        String accessToken = "5be30a25-a044-410c-88b0-19a1da968a64";

        // when
        IntStream.range(1, 1_000_000).parallel()
                .forEach(i -> getImageURL(comicId, episodeId, i, accessToken, (i & 1) == 1));
    }

}
