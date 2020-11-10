package io.github.imsejin.lzcodl.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.constants.Languages;
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
import java.util.List;
import java.util.stream.IntStream;

public final class Downloader {

    private static final String IMG_FORMAT_EXTENSION = ".webp"; // or ".jpg"

    private Downloader() {
    }

    public static void downloadAll(Arguments arguments) {
        int to = arguments.getProduct().getEpisodes().size();
        downloadSome(arguments, 1, to);
    }

    public static void downloadFrom(Arguments arguments, int from) {
        int to = arguments.getProduct().getEpisodes().size();
        downloadSome(arguments, from, to);
    }

    public static void downloadTo(Arguments arguments, int to) {
        downloadSome(arguments, 1, to);
    }

    public static void downloadSome(Arguments arguments, int from, int to) {
        List<Episode> episodes = arguments.getProduct().getEpisodes();

        // 에피소드 번호를 인덱스에 맞게 변경한다.
        from = from <= 0 ? 0 : from - 1;

        // 해당 웹툰의 마지막 에피소드 번호를 초과하는 에피소드 번호를 지정하면, 마지막 에피소드까지 다운로드하는 것으로 변경한다.
        to = Math.min(to, episodes.size());

        for (int i = from; i < to; i++) {
            Episode episode = episodes.get(i);
            downloadOne(arguments, episode, i + 1);
        }
    }

    @SneakyThrows
    public static void downloadOne(Arguments arguments, Episode episode, int num) {
        // 미리보기할 수 있는 유료회차의 경우, 다운로드할 수 없다.
        if (!episode.didTurnFree()) return;

        // 한국이 아닌 다른 국가의 플랫폼은 에피소드 API를 찾을 수 없어, 직접 크롤링한다.
        final int numOfImages = arguments.getLanguage().equals(Languages.KOREAN.value())
                ? getNumOfImagesInEpisode(arguments, episode)
                : Crawler.getNumOfImagesInEpisode(arguments, episode);

        // 에피소드의 이미지가 없으면 종료한다.
        if (numOfImages < 1) return;

        // 에피소드 이름으로 디렉터리를 생성한다.
        String episodeDirName = StringUtils.padStart(4, String.valueOf(num), "0")
                + " - "
                + episode.getDisplay().getTitle();
        Path episodeDir = Paths.get(arguments.getComicPath().toString(), episodeDirName);
        Files.createDirectories(episodeDir);

        try (ProgressBar progressBar = new ProgressBar(
                arguments.getProduct().getAlias() + " ep." + num, numOfImages,
                250, System.err, ProgressBarStyle.ASCII, "", 1)) {
            // `ProgressBar.step()`이 step 1부터 시작하기 때문에 step 0로 초기화한다.
            progressBar.stepTo(0);

            // 에피소드의 모든 이미지를 다운로드한다.
            IntStream.rangeClosed(1, numOfImages).parallel().forEach(i -> {
                // 이미지 URI를 생성한다.
                URL url = URLFactory.image(arguments, episode, i);

                // 이미지를 다운로드한다.
                File file = new File(episodeDir.toFile(),
                        StringUtils.padStart(3, String.valueOf(i), "0")
                                + IMG_FORMAT_EXTENSION);
                int result = createImage(url, file);

                // 다운로드에 실패하면 해당 회차를 건너뛴다.
                if (result == 0) return;

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

    @SneakyThrows(JsonSyntaxException.class)
    private static int getNumOfImagesInEpisode(Arguments arguments, Episode episode) {
        URL url = URLFactory.oneEpisodeAPI(arguments, episode);
        JsonObject json = JsonUtils.readJsonFromUrl(url);

        return json.get("cut").getAsInt();
    }

}
