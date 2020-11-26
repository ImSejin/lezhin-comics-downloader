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
import io.github.imsejin.lzcodl.common.CommandParser;
import io.github.imsejin.lzcodl.common.constant.EpisodeRange;
import io.github.imsejin.lzcodl.core.*;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Episode;
import io.github.imsejin.lzcodl.model.Product;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Application {

    private Application() {
    }

    public static void main(String[] arguments) {
        // Validates and parses options and arguments.
        CommandLine cmd = CommandParser.parse(arguments);

        // Sets up the arguments.
        final Arguments args = Arguments.builder()
                .language(cmd.getOptionValue('l'))
                .comicName(cmd.getOptionValue('n'))
                .episodeRange(cmd.getOptionValue('r', null))
                .debugging(cmd.hasOption('d'))
                .build();

        // Activates debug mode.
        if (args.isDebugging()) ChromeBrowser.debugging();

        // Login with username and password and gets a token.
        args.setAccessToken(LoginHelper.login(args));

        // Crawls the webtoon page so that gets the information on the episode as JSON string.
        String jsonText = Crawler.getJson(args);

        // Converts JSON string to java object.
        Product product = JsonUtils.toObject(jsonText, Product.class);
        args.setProduct(product);

        // To download, pre-processes the data and creates a directory to save episodes.
        preprocess(product);
        Path comicDir = createDirectory(product);
        args.setComicPath(comicDir);

        // Downloads images.
        download(args);

        // Quits the downloader.
        ChromeBrowser.getDriver().quit();
        System.exit(0);
    }

    private static void preprocess(Product product) {
        List<Episode> episodes = product.getEpisodes();

        // 해당 웹툰의 에피소드; 순서가 거꾸로 되어 있어 정렬한다.
        Collections.reverse(episodes);

        // 에피소드 이름 중 디렉터리명에 허용되지 않는 문자열을 치환한다.
        episodes.stream().map(Episode::getDisplay)
                .forEach(it -> it.setTitle(FilenameUtils.replaceUnallowables(it.getTitle())));
    }

    /**
     * Creates a directory and returns its path.
     *
     * <p> Make a directory named after the comic title.
     *
     * @param product product
     * @return path of comic directory
     */
    private static Path createDirectory(Product product) {
        String comicTitle = FilenameUtils.replaceUnallowables(product.getDisplay().getTitle());
        String artists = product.getArtists().stream()
                .map(it -> FilenameUtils.replaceUnallowables(it.getName()))
                .collect(Collectors.joining(", "));
        String dirName = String.format("L_%s - %s", comicTitle, artists);

        Path path = Paths.get(PathnameUtils.getCurrentPathname(), dirName);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a directory: " + path, e);
        }

        return path;
    }

    private static void download(Arguments args) {
        final String episodeRange = args.getEpisodeRange();
        final String separator = EpisodeRange.SEPARATOR.value();

        System.out.println();
        String regex;
        if (StringUtils.isNullOrEmpty(episodeRange)) {
            // 모든 에피소드를 다운로드한다.
            Downloader.all(args);

        } else if (episodeRange.matches(regex = "([0-9]+)" + separator)) {
            // 지정한 에피소드부터 끝까지 다운로드한다.
            int from = Integer.parseInt(StringUtils.find(episodeRange, regex, 1));
            Downloader.startTo(args, from);

        } else if (episodeRange.matches(regex = separator + "([0-9]+)")) {
            // 처음부터 지정한 에피소드까지 다운로드한다.
            int to = Integer.parseInt(StringUtils.find(episodeRange, regex, 1));
            Downloader.endTo(args, to);

        } else if (episodeRange.matches(regex = "([0-9]+)" + separator + "([0-9]+)")) {
            // 지정한 에피소드들만 다운로드한다.
            Map<Integer, String> map = StringUtils.find(episodeRange, regex, Pattern.MULTILINE, 1, 2);
            int from = Integer.parseInt(map.get(1));
            int to = Integer.parseInt(map.get(2));
            Downloader.some(args, from, to);
        }
    }

}
