/*
 * Copyright 2020 Sejin Im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.imsejin.lzcodl.common;

import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Episode;
import lombok.SneakyThrows;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @since 2.0.0
 */
public final class URLFactory {

    /**
     * CDN 서버의 origin URL
     *
     * @see <a href="http://cdn.lezhin.com">CDN URL</a>
     */
    private static final String cdnUrl = "http://cdn.lezhin.com";

    /**
     * 각 회차의 정보를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI to obtain information for each episode
     *
     * @see <a href="http://cdn.lezhin.com/episodes/">Episode Info URL</a>
     */
    private static final String episodeInfoUrl = cdnUrl + "/episodes/";

    /**
     * 이미지 URI의 접두사<br>
     * The prefix of image URI
     *
     * @see <a href="http://cdn.lezhin.com/v2/comics/">Image Prefix URL</a>
     */
    private static final String imgUrl = cdnUrl + "/v2/comics/";

    private URLFactory() {
    }

    /**
     * @see <a href="https://cdn.lezhin.com/v2/comics/5651768999542784/episodes/6393378955722752/contents/scrolls/1.webp?access_token=5be30a25-a044-410c-88b0-19a1da968a64&purchased=false">
     * Image URL
     * </a>
     */
    @SneakyThrows(MalformedURLException.class)
    public static synchronized URL image(long comicId, long episodeId, int filename, String accessToken, boolean purchased) {
        StringBuilder sb = new StringBuilder();

        sb.append(imgUrl);
        sb.append(comicId);
        sb.append("/episodes/");
        sb.append(episodeId);
        sb.append("/contents/scrolls/");
        sb.append(filename);
        sb.append(".webp?access_token=");
        sb.append(accessToken);
        sb.append("&purchased=").append(purchased); // If not append though you paid this episode, width of image decreases. (1080px => 720px)
        sb.append("&q=30"); // I don't know what this means, but if not append, width of image decreases. (1080px => 1024px)

        return new URL(sb.toString());
    }

    /**
     * @since 2.6.2
     */
    public static URL image(Arguments args, Episode episode, int filename, boolean purchased) {
        return image(args.getProduct().getId(), episode.getId(), filename, args.getAccessToken(), purchased);
    }

    /**
     * @see <a href="http://cdn.lezhin.com/episodes/snail/1.json?access_token=5be30a25-a044-410c-88b0-19a1da968a64">A episode API</a>
     */
    @SneakyThrows(MalformedURLException.class)
    public static URL oneEpisodeAPI(String comicName, String episodeName, String accessToken) {
        StringBuilder sb = new StringBuilder();

        sb.append(episodeInfoUrl);
        sb.append(comicName);
        sb.append('/');
        sb.append(episodeName);
        sb.append(".json?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

    /**
     * @since 2.6.2
     */
    public static URL oneEpisodeAPI(Arguments args, Episode episode) {
        return oneEpisodeAPI(args.getProduct().getAlias(), episode.getName(), args.getAccessToken());
    }

    /**
     * @see <a href="http://cdn.lezhin.com/episodes/snail?access_token=5be30a25-a044-410c-88b0-19a1da968a64">All episode API</a>
     */
    @SneakyThrows(MalformedURLException.class)
    public static URL allEpisodeAPI(String comicName, String accessToken) {
        StringBuilder sb = new StringBuilder();

        sb.append(episodeInfoUrl);
        sb.append(comicName);
        sb.append("?access_token=");
        sb.append(accessToken);

        return new URL(sb.toString());
    }

}
