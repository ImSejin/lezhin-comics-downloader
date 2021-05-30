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

package io.github.imsejin.lzcodl;

import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.lzcodl.common.CommandParser;
import io.github.imsejin.lzcodl.common.Loggers;
import io.github.imsejin.lzcodl.common.UsagePrinter;
import io.github.imsejin.lzcodl.common.exception.ConfigParseException;
import io.github.imsejin.lzcodl.common.exception.EpisodeRangeParseException;
import io.github.imsejin.lzcodl.common.exception.InvalidLanguageException;
import io.github.imsejin.lzcodl.core.ChromeBrowser;
import io.github.imsejin.lzcodl.core.Crawler;
import io.github.imsejin.lzcodl.core.Downloader;
import io.github.imsejin.lzcodl.core.LoginHelper;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Artist;
import io.github.imsejin.lzcodl.model.Episode;
import io.github.imsejin.lzcodl.model.Product;
import org.apache.commons.cli.CommandLine;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static java.util.stream.Collectors.joining;

public final class Application {

    private Application() {
    }

    public static void main(String[] arguments) {
        try (FileReader reader = new FileReader("./pom.xml")) {
            // Validates and parses options and arguments.
            CommandLine cmd = CommandParser.parse(arguments);

            // Sets up the arguments.
            final Arguments args = Arguments.builder()
                    .language(cmd.getOptionValue('l'))
                    .comicName(cmd.getOptionValue('n'))
                    .episodeRange(cmd.getOptionValue('r', null))
                    .jpg(cmd.hasOption('j'))
                    .debugging(cmd.hasOption('d'))
                    .build();

            // Activates debug mode.
            if (args.isDebugging()) {
                Loggers.debugging();
                ChromeBrowser.debugging();
            }

            // Notices.
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model pom = mavenReader.read(reader);
            Loggers.getLogger().info("{} v{}", pom.getName(), pom.getVersion());
            Loggers.getLogger().info("If you have any questions, contact me by '{}/issues'", pom.getUrl());
            Loggers.getLogger().debug("Argument: {}", args);

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
            new Downloader(args).download();

            // Terminates the application.
            ChromeBrowser.getDriver().quit();
            System.exit(0);

        } catch (ConfigParseException e) {
            ChromeBrowser.softQuit();
            Loggers.getLogger().error("ConfigParseException occurs", e);
            System.exit(1);

        } catch (InvalidLanguageException e) {
            ChromeBrowser.softQuit();
            Loggers.getLogger().error("InvalidLanguageException occurs", e);
            UsagePrinter.printLanguageAndQuit();

        } catch (EpisodeRangeParseException e) {
            ChromeBrowser.softQuit();
            Loggers.getLogger().error("EpisodeRangeParseException occurs", e);
            UsagePrinter.printEpisodeRangeAndQuit();

        } catch (Exception e) {
            ChromeBrowser.softQuit();
            Loggers.getLogger().error("Exception occurs", e);
            System.exit(1);
        }
    }

    private static void preprocess(Product product) {
        // 웹툰 이름 중 디렉터리명에 허용되지 않는 문자열을 치환한다.
        product.getDisplay().setTitle(FilenameUtils.replaceUnallowables(product.getDisplay().getTitle()));

        // 작가 이름 중 디렉터리명에 허용되지 않는 문자열을 치환한다.
        product.getArtists().forEach(it -> it.setName(FilenameUtils.replaceUnallowables(it.getName())));

        // 에피소드 이름 중 디렉터리명에 허용되지 않는 문자열을 치환한다.
        product.getEpisodes().stream().map(Episode::getDisplay)
                .forEach(it -> it.setTitle(FilenameUtils.replaceUnallowables(it.getTitle())));

        // 해당 웹툰의 에피소드; 순서가 거꾸로 되어 있어 정렬한다.
        Collections.reverse(product.getEpisodes());
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
        String comicTitle = product.getDisplay().getTitle();
        String artists = product.getArtists().stream().map(Artist::getName)
                .collect(joining(", "));
        String dirName = String.format("L_%s - %s", comicTitle, artists);

        Path path = Paths.get(PathnameUtils.getCurrentPathname(), dirName);
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory()) return path;

        try {
            Loggers.getLogger().debug("Create directory: {}", path);
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + path, e);
        }

        return path;
    }

}
