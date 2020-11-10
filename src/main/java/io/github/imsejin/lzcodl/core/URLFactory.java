package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Episode;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

public final class URLFactory {

    private static final StringBuilder sb = new StringBuilder();

    /**
     * CDN 서버의 origin URL
     *
     * <pre>{@code
     *     http://cdn.lezhin.com/
     * }</pre>
     */
    private static final String cdnUrl = "http://cdn.lezhin.com";

    /**
     * 각 회차의 정보를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI to obtain information for each episode
     *
     * <pre>{@code
     *     http://cdn.lezhin.com/episodes/
     * }</pre>
     */
    private static final String episodeInfoUrl = cdnUrl + "/episodes/";

    /**
     * 이미지 URI의 접두사<br>
     * The prefix of image URI
     *
     * <pre>{@code
     *     http://cdn.lezhin.com/v2/comics/
     * }</pre>
     */
    private static final String ImgUrl = cdnUrl + "/v2/comics/";

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
     * <pre>{@code
     *     https://cdn.lezhin.com/v2/comics/5651768999542784/episodes/6393378955722752/contents/scrolls/1.webp?access_token=5be30a25-a044-410c-88b0-19a1da968a64&purchased=false
     * }</pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL image(long comicId, long episodeId, int filename, String accessToken) {
        init();

        sb.append(ImgUrl);
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

    public static synchronized URL image(Arguments arguments, Episode episode, int filename) {
        return image(arguments.getProduct().getId(), episode.getId(), filename, arguments.getAccessToken());
    }

    /**
     * <pre>{@code
     *     http://cdn.lezhin.com/episodes/snail/1.json?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * }</pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL oneEpisodeAPI(String comicName, String episodeName, String accessToken) {
        init();

        sb.append(episodeInfoUrl);
        sb.append(comicName);
        sb.append('/');
        sb.append(episodeName);
        sb.append(".json?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

    public static synchronized URL oneEpisodeAPI(Arguments arguments, Episode episode) {
        return oneEpisodeAPI(arguments.getProduct().getAlias(), episode.getName(), arguments.getAccessToken());
    }

    /**
     * <pre>{@code
     *     http://cdn.lezhin.com/episodes/snail?access_token=5be30a25-a044-410c-88b0-19a1da968a64
     * }</pre>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL allEpisodeAPI(String comicName, String accessToken) {
        init();

        sb.append(episodeInfoUrl);
        sb.append(comicName);
        sb.append("?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

}
