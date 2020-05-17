/**
 * MIT License
 * 
 * Copyright (c) 2019-2020 Im Sejin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.imsejin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.imsejin.common.util.JsonUtil;
import io.github.imsejin.common.util.StringUtil;
import io.github.imsejin.core.Crawler;
import io.github.imsejin.core.Downloader;
import io.github.imsejin.model.Artist;
import io.github.imsejin.model.Episode;
import io.github.imsejin.model.Product;

public class LezhinComicsDownloaderApplication {

    public static void main(String[] args) {
        final String comicName = args[0]; // "snail"
        final String accessToken = args[1]; // "5be30a25-a044-410c-88b0-19a1da968a64"

        // 해당 웹툰 페이지를 크롤링하여 회차별 정보를 JSON으로 얻어온다.
        String jsonText = Crawler.getJson(comicName);

        // JSON을 파싱하여 객체로 변환한다.
        Product product = JsonUtil.toObject(jsonText, Product.class);

        // 디렉터리명에 허용되지 않는 문자열을 치환한다
        product.getDisplay().setTitle(StringUtil.toSafeFileName(product.getDisplay().getTitle()));
        product.getEpisodes().stream().forEach(
                episode -> episode.getDisplay().setTitle(StringUtil.toSafeFileName(episode.getDisplay().getTitle())));

        // 웹툰 이름으로 디렉터리를 생성한다.
        String comicDirName = "L_" + product.getDisplay().getTitle() + " - "
                + product.getArtists().stream()
                    .map(Artist::getName)
                    .map(StringUtil::toSafeFileName)
                    .collect(Collectors.joining(", "));
        File comicDir = new File(Downloader.getCurrentPathName(), comicDirName);
        if (!comicDir.exists()) comicDir.mkdirs();

        // 해당 웹툰의 에피소드; 순서가 거꾸로 되어 있어 정렬한다.
        List<Episode> episodes = product.getEpisodes();
        Collections.reverse(episodes);

        // 웹툰을 다운로드한다.
        long comicId = product.getId();
        int size = episodes.size();
        for (int i = 0; i < size; i++) {
            Episode episode = episodes.get(i);
            String title = episode.getDisplay().getTitle();
            System.out.print("> episode #" + (i + 1) + " `" + title + "` : ");
            String episodeDirName = StringUtil.lPad(String.valueOf(i + 1), 4, '0') + " - " + title;

            Downloader.download(comicId, episode, comicName, accessToken, comicDir, episodeDirName);
        }

        // 애플리케이션을 종료한다.
        System.exit(0);
    }

}
