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

package io.github.imsejin.lzcodl;

import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.constants.EpisodeRange;
import io.github.imsejin.lzcodl.core.ChromeBrowser;
import io.github.imsejin.lzcodl.core.Crawler;
import io.github.imsejin.lzcodl.core.Downloader;
import io.github.imsejin.lzcodl.core.LoginHelper;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Artist;
import io.github.imsejin.lzcodl.model.Episode;
import io.github.imsejin.lzcodl.model.Product;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        // Validates and parses options and arguments.
        CommandLine cmd = validate(args);

        // Sets up the arguments.
        final Arguments arguments = Arguments.builder()
                .language(cmd.getOptionValue('l'))
                .comicName(cmd.getOptionValue('n'))
                .episodeRange(cmd.getOptionValue('r', null))
                .build();

        // Login with username and password and gets a token.
        arguments.setAccessToken(LoginHelper.login(arguments));

        // Crawls the webtoon page so that gets the information on the episode as JSON string.
        String jsonText = Crawler.getJson(arguments);

        // Parses JSON string and converts it to object.
        Product product = JsonUtils.toObject(jsonText, Product.class);
        arguments.setProduct(product);

        // To download, pre-processes the data and creates a directory to save episodes.
        preprocess(product);
        File comicDir = makeDirectory(product);
        arguments.setComicPathname(comicDir.getPath());

        // Downloads images.
        download(arguments);

        // Quits the downloader.
        ChromeBrowser.getDriver().quit();
        System.exit(0);
    }

    private static CommandLine validate(String[] args) {
        // Option: language
        Option lang = Option.builder("l")
                .longOpt("lang")
                .desc("language of lezhin platform you want to see")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // Option: comicName
        Option name = Option.builder("n")
                .longOpt("name")
                .desc("webtoon name you want to download")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // Option: episodeRange
        Option range = Option.builder("r")
                .longOpt("range")
                .desc("range of episodes you want to download")
                .hasArg()
                .valueSeparator()
                .build();

        Options options = new Options().addOption(lang).addOption(name).addOption(range);

        try {
            // Parses options and arguments.
            return new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            // Without required options or arguments, the program will exit.
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
        final String separator = EpisodeRange.SEPARATOR.value();

        String regex;
        if (StringUtils.isNullOrEmpty(episodeRange)) {
            // 모든 에피소드를 다운로드한다.
            Downloader.downloadAll(arguments);

        } else if (episodeRange.matches(regex = "([0-9]+)" + separator)) {
            // 지정한 에피소드부터 끝까지 다운로드한다.
            int from = Integer.parseInt(StringUtils.find(episodeRange, regex, 1));
            Downloader.downloadFrom(arguments, from);

        } else if (episodeRange.matches(regex = separator + "([0-9]+)")) {
            // 처음부터 지정한 에피소드까지 다운로드한다.
            int to = Integer.parseInt(StringUtils.find(episodeRange, regex, 1));
            Downloader.downloadTo(arguments, to);

        } else if (episodeRange.matches(regex = "([0-9]+)" + separator + "([0-9]+)")) {
            // 지정한 에피소드들만 다운로드한다.
            Map<Integer, String> map = StringUtils.find(episodeRange, regex, Pattern.MULTILINE, 1, 2);
            int from = Integer.parseInt(map.get(1));
            int to = Integer.parseInt(map.get(2));
            Downloader.downloadSome(arguments, from, to);
        }
    }

}
