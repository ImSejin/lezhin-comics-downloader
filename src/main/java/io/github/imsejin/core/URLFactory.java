package io.github.imsejin.core;

import io.github.imsejin.common.constants.URIs;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

public final class URLFactory {

    private static final StringBuilder sb = new StringBuilder();

    private URLFactory() {
    }

    /**
     * StringBuilder의 버퍼를 지운다.<br>
     * Clears the buffer of StringBuilder.
     */
    private static void init() {
        sb.setLength(0);
    }

    /**
     * <pre>
     * https://cdn.lezhin.com/v2/comics/5651768999542784/episodes/6393378955722752/contents/scrolls/1.webp?access_token=5be30a25-a044-410c-88b0-19a1da968a64&purchased=false
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL image(long comicId, long episodeId, int filename, String accessToken) {
        init();

        sb.append(URIs.IMG.value());
        sb.append(comicId);
        sb.append("/episodes/");
        sb.append(episodeId);
        sb.append("/contents/scrolls/");
        sb.append(filename);
        sb.append(".webp?access_token=");
        sb.append(accessToken);
        sb.append("&purchased=false"); // 구매한 유료 에피소드라면 true로 변경한다.

        return new URL(sb.toString());
    }

    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeViewer(String language, String comicName, String episodeName) {
        init();

        sb.append(URIs.HOME.value());
        sb.append(language);
        sb.append(URIs.COMIC.value());
        sb.append(comicName);
        sb.append('/');
        sb.append(episodeName);

        return new URL(sb.toString());
    }

    /**
     * <pre>
     * http://cdn.lezhin.com/episodes/snail/1.json?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeAPI(String comicName, String episodeName, String accessToken) {
        init();

        sb.append(URIs.EPISODE_INFO.value());
        sb.append(comicName);
        sb.append('/');
        sb.append(episodeName);
        sb.append(".json?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

    /**
     * <pre>
     * http://cdn.lezhin.com/episodes/snail?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * </pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL allEpisodeAPI(String comicName, String accessToken) {
        init();

        sb.append(URIs.EPISODE_INFO.value());
        sb.append(comicName);
        sb.append("?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

}
