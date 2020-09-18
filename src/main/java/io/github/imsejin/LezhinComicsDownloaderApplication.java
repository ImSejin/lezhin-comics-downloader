/*
 * MIT License
 *
 * Copyright (c) 2020 Im Sejin
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

import io.github.imsejin.common.constants.EpisodeRange;
import io.github.imsejin.core.ChromeBrowser;
import io.github.imsejin.core.Crawler;
import io.github.imsejin.core.Downloader;
import io.github.imsejin.core.LoginHelper;
import io.github.imsejin.model.Arguments;
import io.github.imsejin.model.Artist;
import io.github.imsejin.model.Episode;
import io.github.imsejin.model.Product;
import io.github.imsejin.util.FilenameUtils;
import io.github.imsejin.util.JsonUtils;
import io.github.imsejin.util.PathnameUtils;
import io.github.imsejin.util.StringUtils;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class LezhinComicsDownloaderApplication {

    private static final String SEPARATOR = EpisodeRange.SEPARATOR.value();

    private LezhinComicsDownloaderApplication() {
    }

    public static void main(String[] args) {
        CommandLine cmd = validate(args);

        final Arguments arguments = Arguments.builder()
                .language(cmd.getOptionValue('l'))
                .comicName(cmd.getOptionValue('n'))
                .episodeRange(cmd.getOptionValue('r', null))
                .build();

        // 아이디와 비밀번호를 입력받아 로그인하고, 토큰을 가져온다.
        arguments.setAccessToken(LoginHelper.login(arguments));

        // 해당 웹툰 페이지를 크롤링하여 회차별 정보를 JSON으로 얻어온다.
        String jsonText = Crawler.getJson(arguments);

        // JSON을 파싱하여 객체로 변환한다.
        Product product = JsonUtils.toObject(jsonText, Product.class);
        arguments.setProduct(product);

        // 다운로드를 위해, 데이터를 가공하고 웹툰 폴더를 생성한다.
        preprocess(product);
        File comicDir = makeDirectory(product);
        arguments.setComicPathname(comicDir.getPath());

        // 이미지를 다운로드한다.
        download(arguments);

        // 애플리케이션을 정상 종료한다.
        ChromeBrowser.getDriver().quit();
        System.exit(0);
    }

    private static CommandLine validate(String[] args) {
        // language 옵션
        Option lang = Option.builder("l")
                .longOpt("lang")
                .desc("language of lezhin platform you want to see")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // comicName 옵션
        Option name = Option.builder("n")
                .longOpt("name")
                .desc("webtoon name you want to download")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // episodeRange 옵션
        Option range = Option.builder("r")
                .longOpt("range")
                .desc("range of episodes you want to download")
                .hasArg()
                .valueSeparator()
                .build();

        Options options = new Options().addOption(lang).addOption(name).addOption(range);

        try {
            // 옵션 및 인자를 분석한다.
            return new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            // 필요한 옵션 및 인자가 없다면, 프로그램을 종료한다.
            new HelpFormatter().printHelp(" ", null, options, "", true);
            System.exit(1);
            return null;
        }
    }

    private static void preprocess(Product product) {
        List<Episode> episodes = product.getEpisodes();

        // 해당 웹툰의 에피소드; 순서가 거꾸로 되어 있어 정렬한다.
        Collections.reverse(episodes);

        // 에피소드 이름 중 디렉터리명에 허용되지 않는 문자열을 치환한다.
        episodes.forEach(episode -> episode.getDisplay().setTitle(
                FilenameUtils.replaceUnallowables(episode.getDisplay().getTitle())));
    }

    private static File makeDirectory(Product product) {
        // 웹툰 이름으로 디렉터리를 생성한다.
        String comicTitle = FilenameUtils.replaceUnallowables(product.getDisplay().getTitle());
        String artists = product.getArtists().stream()
                .map(Artist::getName)
                .map(FilenameUtils::replaceUnallowables)
                .collect(Collectors.joining(", "));
        String dirName = "L_" + comicTitle + " - " + artists;

        File comicDir = new File(PathnameUtils.getCurrentPathname(), dirName);
        if (!comicDir.exists()) comicDir.mkdirs();

        return comicDir;
    }

    private static void download(Arguments arguments) {
        final String episodeRange = arguments.getEpisodeRange();

        if (StringUtils.isNullOrEmpty(episodeRange)) {
            // 모든 에피소드를 다운로드한다.
            Downloader.downloadAll(arguments);

        } else if (episodeRange.matches("[0-9]+" + SEPARATOR)) {
            // 지정한 에피소드부터 끝까지 다운로드한다.
            int from = Integer.parseInt(StringUtils.match("([0-9]+)" + SEPARATOR, episodeRange, 1));
            Downloader.downloadFrom(arguments, from);

        } else if (episodeRange.matches(SEPARATOR + "[0-9]+")) {
            // 처음부터 지정한 에피소드까지 다운로드한다.
            int to = Integer.parseInt(StringUtils.match(SEPARATOR + "([0-9]+)", episodeRange, 1));
            Downloader.downloadTo(arguments, to);

        } else if (episodeRange.matches("[0-9]+" + SEPARATOR + "[0-9]+")) {
            // 지정한 에피소드들만 다운로드한다.
            int from = Integer.parseInt(StringUtils.match("([0-9]+)" + SEPARATOR + "[0-9]+", episodeRange, 1));
            int to = Integer.parseInt(StringUtils.match("[0-9]+" + SEPARATOR + "([0-9]+)", episodeRange, 1));
            Downloader.downloadSome(arguments, from, to);
        }
    }

}
