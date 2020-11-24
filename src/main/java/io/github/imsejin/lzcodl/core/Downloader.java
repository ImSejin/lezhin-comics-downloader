package io.github.imsejin.lzcodl.core;

import com.google.gson.JsonObject;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.lzcodl.common.constant.Languages;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Episode;
import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

public final class Downloader {

    private static final String IMG_FORMAT_EXTENSION = "webp"; // or jpg

    private Downloader() {
    }

    public static void all(Arguments arguments) {
        int to = arguments.getProduct().getEpisodes().size();
        some(arguments, 1, to);
    }

    public static void startTo(Arguments arguments, int from) {
        int to = arguments.getProduct().getEpisodes().size();
        some(arguments, from, to);
    }

    public static void endTo(Arguments arguments, int to) {
        some(arguments, 1, to);
    }

    public static void some(Arguments arguments, int from, int to) {
        List<Episode> episodes = arguments.getProduct().getEpisodes();

        // 에피소드 번호를 인덱스에 맞게 변경한다.
        from = from <= 0 ? 0 : from - 1;

        // 해당 웹툰의 마지막 에피소드 번호를 초과하는 에피소드 번호를 지정하면, 마지막 에피소드까지 다운로드하는 것으로 변경한다.
        to = Math.min(to, episodes.size());

        for (int i = from; i < to; i++) {
            Episode episode = episodes.get(i);
            one(arguments, episode, i + 1);
        }
    }

    @SneakyThrows
    public static void one(Arguments arguments, Episode episode, int num) {
        // Cannot download paid episode.
        if (!episode.isFree()) return;

        // 한국이 아닌 다른 국가의 플랫폼은 에피소드 API를 찾을 수 없어, 직접 크롤링한다.
        final int numOfImages = arguments.getLanguage().equals(Languages.KOREAN.value())
                ? getNumOfImagesInEpisode(arguments, episode)
                : Crawler.getNumOfImagesInEpisode(arguments, episode);

        // If episode has no image, skips this episode.
        if (numOfImages < 1) return;

        // Creates directory with the name of episode.
        String episodeDirName = String.format("%04d - %s", num, episode.getDisplay().getTitle());
        Path episodeDir = Paths.get(arguments.getComicPath().toString(), episodeDirName);
        Files.createDirectories(episodeDir);

        try (ProgressBar progressBar = getDefaultProgressBar(arguments.getProduct().getAlias(), num, numOfImages)) {
            // `ProgressBar.step()`이 step 1부터 시작하기 때문에 step 0로 초기화한다.
            progressBar.stepTo(0);

            // Downloads all images of the episode.
            IntStream.rangeClosed(1, numOfImages).parallel().forEach(i -> {
                String filename = String.format("%03d.%s", i, IMG_FORMAT_EXTENSION);
                File file = new File(episodeDir.toFile(), filename);

                // Tries to download high-resolution image for only paid users.
                URL url = URLFactory.image(arguments, episode, i, true);
                int result = createImage(url, file);

                // Try to download low-resolution image for all users.
                if (result == 0) {
                    url = URLFactory.image(arguments, episode, i, false);
                    result = createImage(url, file);

                    // If failed to download, skips this image.
                    if (result == 0) return;
                }

                progressBar.stepBy(1);
            });
        }
    }

    /**
     * 이미지 URL로 이미지 파일을 생성한다. 성공하면 1을, 실패하면 0을 반환한다.<br>
     * Creates a image file with the image URL. Returns 1 if success, 0 else.
     */
    private static int createImage(URL url, File file) {
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(file)) {
            // Creates a image file.
            ReadableByteChannel readChannel = Channels.newChannel(in);
            out.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);

            // Success
            return 1;
        } catch (IOException ex) {
            // Fail
            return 0;
        }
    }

    @SneakyThrows
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
        return new ProgressBar(taskName, numOfImages, 250, System.out, ProgressBarStyle.ASCII,
                " imgs", 1, true, new DecimalFormat("| #.0"), ChronoUnit.SECONDS,
                0L, Duration.ZERO);
    }

}
