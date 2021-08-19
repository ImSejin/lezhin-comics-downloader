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

package io.github.imsejin.lzcodl.core;

import com.google.gson.JsonObject;
import io.github.imsejin.common.util.FileUtils;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.lzcodl.common.Loggers;
import io.github.imsejin.lzcodl.common.URLFactory;
import io.github.imsejin.lzcodl.common.constant.EpisodeRange;
import io.github.imsejin.lzcodl.common.constant.Languages;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Artist;
import io.github.imsejin.lzcodl.model.Episode;
import io.github.imsejin.lzcodl.model.Product;
import me.tongfei.progressbar.ConsoleProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

/**
 * @since 1.0.0
 */
public final class Downloader {

    /**
     * @since 2.8.0
     */
    private final Arguments args;

    /**
     * @since 2.8.0
     */
    private final URLFactory urlFactory;

    /**
     * @since 2.8.2
     */
    private final Path comicDir;

    public Downloader(Arguments args) {
        this(args, createDirectory(args.getProduct()));
    }

    /**
     * @since 2.8.2
     */
    public Downloader(Arguments args, Path comicDir) {
        this.args = args;
        this.urlFactory = new URLFactory(args);
        this.comicDir = comicDir;
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

    public void download() throws IOException {
        EpisodeRange episodeRange = EpisodeRange.from(this.args.getEpisodeRange());

        List<Episode> episodes = this.args.getProduct().getEpisodes();
        for (int i : episodeRange.getArray(this.args)) {
            Episode episode = episodes.get(i);
            downloadEpisode(this.args, episode, i + 1);
        }
    }

    private void downloadEpisode(Arguments arguments, Episode episode, int num) throws IOException {
        // Cannot download paid episode.
        if (!episode.isFree()) return;

        // 한국이 아닌 다른 국가의 플랫폼은 에피소드 API를 찾을 수 없어, 직접 크롤링한다.
        final int numOfImages = arguments.getLanguage() == Languages.KOREAN
                ? getNumOfImagesInEpisode(arguments, episode)
                : Crawler.getNumOfImagesInEpisode(arguments, episode);

        // If episode has no image, skips this episode.
        if (numOfImages < 1) return;

        // Creates directory with the name of episode.
        String episodeDirName = String.format("%04d - %s", num, episode.getDisplay().getTitle());
        Path episodeDir = this.comicDir.resolve(episodeDirName);
        Files.createDirectories(episodeDir);

        try (ProgressBar progressBar = getDefaultProgressBar(arguments.getProduct().getAlias(), num, numOfImages)) {
            // Downloads all images of the episode.
            IntStream.rangeClosed(1, numOfImages).parallel().forEach(i -> {
                String filename = String.format("%03d.%s", i, this.args.getImageFormat());
                File file = new File(episodeDir.toFile(), filename);

                // Tries to download high-resolution image for only paid users.
                URL url = this.urlFactory.image(episode, i, true);
                boolean success = downloadImage(url, file);

                // Try to download low-resolution image for all users.
                if (!success) {
                    url = this.urlFactory.image(episode, i, false);
                    success = downloadImage(url, file);

                    // If failed to download, skips this image.
                    if (!success) return;
                }

                progressBar.stepBy(1);
            });
        }
    }

    /**
     * Creates a image file with the image URL. Returns {@code true} if success or {@code false}.
     */
    private static boolean downloadImage(URL url, File dest) {
        try {
            return FileUtils.download(url.openStream(), dest);
        } catch (IOException e) {
            return false;
        }
    }

    private static int getNumOfImagesInEpisode(Arguments arguments, Episode episode) {
        URL url = URLFactory.oneEpisodeAPI(arguments, episode);
        JsonObject json = JsonUtils.readJsonFromUrl(url);

        return json.get("cut").getAsInt();
    }

    /**
     * Returns default progress bar.
     *
     * <pre>
     *     i_have_a_baby ep.1 100% [=======] 80/80 imgs (0:00:03 / 0:00:00) | 26.7 imgs/s
     *     i_have_a_baby ep.2 100% [=======] 70/70 imgs (0:00:02 / 0:00:00) | 35.0 imgs/s
     *     i_have_a_baby ep.3 100% [=======] 56/56 imgs (0:00:01 / 0:00:00) | 56.0 imgs/s
     * </pre>
     *
     * @param episodeName name of the episode
     * @param episodeNo   order of the episode
     * @param numOfImages number of images in the episode
     * @return default progress bar
     */
    private static ProgressBar getDefaultProgressBar(String episodeName, int episodeNo, int numOfImages) {
        String taskName = String.format("%s ep.%d", episodeName, episodeNo);

        ProgressBarBuilder builder = new ProgressBarBuilder();
        builder.setTaskName(taskName);
        builder.setInitialMax(numOfImages);
        builder.setUpdateIntervalMillis(250);
        builder.setConsumer(new ConsoleProgressBarConsumer(System.out));
        builder.setStyle(ProgressBarStyle.ASCII);
        builder.setUnit(" imgs", 1);
        builder.showSpeed(new DecimalFormat("| #.0"));
        builder.setSpeedUnit(ChronoUnit.SECONDS);
        builder.startsFrom(0L, Duration.ZERO);

        return builder.build();
    }

}
