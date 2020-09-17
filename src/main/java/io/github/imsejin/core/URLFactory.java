package io.github.imsejin.core;

import io.github.imsejin.common.constants.URIs;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

public final class URLFactory {

    private static final StringBuilder $ = new StringBuilder();

    private URLFactory() {
    }

    /**
     * StringBuilder의 버퍼를 지운다.<br>
     * Clears the buffer of StringBuilder.
     */
    private static void init() {
        $.setLength(0);
    }

    /**
     * <pre>
     * https://cdn.lezhin.com/v2/comics/5651768999542784/episodes/6393378955722752/contents/scrolls/1.webp?access_token=5be30a25-a044-410c-88b0-19a1da968a64&purchased=false
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL image(long comicId, long episodeId, int filename, String accessToken) {
        init();

        $.append(URIs.IMG.value())
                .append(comicId)
                .append("/episodes/")
                .append(episodeId)
                .append("/contents/scrolls/")
                .append(filename)
                .append(".webp?access_token=")
                .append(accessToken)
                .append("&purchased=false"); // 구매한 유료 에피소드라면 true로 변경한다.

        return new URL($.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeViewer(String language, String comicName, String episodeName) {
        init();

        $.append(URIs.HOME.value())
                .append(language)
                .append(URIs.COMIC.value())
                .append(comicName)
                .append('/')
                .append(episodeName);

        return new URL($.toString());
    }

    /**
     * <pre>
     * http://cdn.lezhin.com/episodes/snail/1.json?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeAPI(String comicName, String episodeName, String accessToken) {
        init();

        $.append(URIs.EPISODE_INFO.value())
                .append(comicName)
                .append('/')
                .append(episodeName)
                .append(".json?access_token=")
                .append(accessToken);

        return new URL($.toString());
    }

    /**
     * <pre>
     * http://cdn.lezhin.com/episodes/snail?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL allEpisodeAPI(String comicName, String accessToken) {
        init();

        $.append(URIs.EPISODE_INFO.value())
                .append(comicName)
                .append("?access_token=")
                .append(accessToken);

        return new URL($.toString());
    }

}
