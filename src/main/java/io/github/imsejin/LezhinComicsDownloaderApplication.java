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
import java.io.IOException;
import java.nio.file.Paths;
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
import lombok.SneakyThrows;

public class LezhinComicsDownloaderApplication {

    public static void main(String[] args) {
        // 실행에 필요한 인자를 넣었는지 확인한다.
        if (args == null || args.length == 0) {
            System.err.println("Must input arguments {comicName} {accessToken}.");
            System.exit(1);
        } else if (args.length < 2) {
            System.err.println("Must input argument {accessToken}.");
            System.exit(1);
        }

        final String comicName = args[0]; // "snail"
        final String accessToken = args[1]; // "5be30a25-a044-410c-88b0-19a1da968a64"

        // 해당 웹툰 페이지를 크롤링하여 회차별 정보를 JSON으로 얻어온다.
        String jsonText = Crawler.getJson(comicName);

        // JSON을 파싱하여 객체로 변환한다.
        Product product = JsonUtil.toObject(jsonText, Product.class);

        // 다운로드를 위해, 데이터를 가공하고 웹툰 폴더를 생성한다.
        File comicDir = preprocess(product);

        // 웹툰을 다운로드한다.
        Downloader.downloadAll(product, accessToken, comicDir);

        // 애플리케이션을 정상 종료한다.
        System.exit(0);
    }

    @SneakyThrows(IOException.class)
    private static File preprocess(Product product) {
        List<Episode> episodes = product.getEpisodes();

        // 해당 웹툰의 에피소드; 순서가 거꾸로 되어 있어 정렬한다.
        Collections.reverse(episodes);

        // 디렉터리명에 허용되지 않는 문자열을 치환한다.
        product.getDisplay().setTitle(StringUtil.toSafeFileName(product.getDisplay().getTitle()));
        episodes.forEach(
                episode -> episode.getDisplay().setTitle(StringUtil.toSafeFileName(episode.getDisplay().getTitle())));

        // 웹툰 이름으로 디렉터리를 생성한다.
        String comicDirName = "L_" + product.getDisplay().getTitle() + " - "
                + product.getArtists().stream()
                    .map(Artist::getName)
                    .map(StringUtil::toSafeFileName)
                    .collect(Collectors.joining(", "));
        String currentPathName = Paths.get(".").toRealPath().toString();
        File comicDir = new File(currentPathName, comicDirName);
        if (!comicDir.exists()) comicDir.mkdirs();

        return comicDir;
    }

}
