package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.EPISODE_INFO_URI_PREFIX;
import static io.github.imsejin.common.Constants.IMG_URI_PARAM;
import static io.github.imsejin.common.Constants.IMG_URI_PREFIX;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class URLBuilder {

    private final StringBuilder $ = new StringBuilder();

    /**
     * StringBuilder의 버퍼를 지운다.<br>
     * Clears the buffer of StringBuilder.
     */
    private void init() {
        $.setLength(0);
    }

    @SneakyThrows(MalformedURLException.class)
    public synchronized URL imageURL(long comicId, long episodeId, int fileName, String accessToken) {
        init();

        $.append(IMG_URI_PREFIX)
        .append(comicId)
        .append("/episodes/")
        .append(episodeId)
        .append("/contents/scrolls/")
        .append(fileName)
        .append(".webp?access_token=")
        .append(accessToken)
        .append(IMG_URI_PARAM);

        return new URL($.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public synchronized URL oneEpisodeURL(String comicName, String episodeName, String accessToken) {
        init();

        $.append(EPISODE_INFO_URI_PREFIX)
        .append(comicName)
        .append("/")
        .append(episodeName)
        .append(".json?access_token=")
        .append(accessToken);

        return new URL($.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public synchronized URL allEpisodeURL(String comicName, String accessToken) {
        init();

        $.append(EPISODE_INFO_URI_PREFIX)
        .append(comicName)
        .append("?access_token=")
        .append(accessToken);

        return new URL($.toString());
    }

}
