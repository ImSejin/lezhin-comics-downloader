package io.github.imsejin.core;

import io.github.imsejin.common.constants.URIs;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

public final class URLFactory {

    private URLFactory() {}

    private static final StringBuilder $ = new StringBuilder();

    /**
     * StringBuilder의 버퍼를 지운다.<br>
     * Clears the buffer of StringBuilder.
     */
    private static void init() {
        $.setLength(0);
    }

    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL imageURL(long comicId, long episodeId, int fileName, String accessToken) {
        init();

        $.append(URIs.IMG.value())
        .append(comicId)
        .append("/episodes/")
        .append(episodeId)
        .append("/contents/scrolls/")
        .append(fileName)
        .append(".webp?access_token=")
        .append(accessToken)
        .append("&purchased=false"); // 구매한 유료 에피소드라면 true로 변경한다.

        return new URL($.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeURL(String comicName, String episodeName, String accessToken) {
        init();

        $.append(URIs.EPISODE_INFO.value())
        .append(comicName)
        .append("/")
        .append(episodeName)
        .append(".json?access_token=")
        .append(accessToken);

        return new URL($.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL allEpisodeURL(String comicName, String accessToken) {
        init();

        $.append(URIs.EPISODE_INFO.value())
        .append(comicName)
        .append("?access_token=")
        .append(accessToken);

        return new URL($.toString());
    }

}
