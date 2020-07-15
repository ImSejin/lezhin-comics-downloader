package io.github.imsejin.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.imsejin.common.constants.Languages;
import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.common.util.JsonUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.model.Arguments;
import io.github.imsejin.model.Episode;
import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

public final class Downloader {

    private Downloader() {}

    private static final String IMG_FORMAT_EXTENSION = ".webp"; // or ".jpg"

    private static final byte[] BUFFER = new byte[1024];

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
        long comicId = arguments.getProduct().getId();
        String comicName = arguments.getProduct().getAlias();

        List<Episode> episodes = arguments.getProduct().getEpisodes();

        // 에피소드 번호를 인덱스에 맞게 변경한다.
        from = from <= 0 ? 0 : from - 1;

        // 해당 웹툰의 마지막 에피소드 번호를 초과하는 에피소드 번호를 지정하면, 마지막 에피소드까지 다운로드하는 것으로 변경한다.
        to = Math.min(to, episodes.size());

        /*
        // Single-threaded way
        for (int i = from; i < to; i++) {
            Episode episode = episodes.get(i);
            downloadOne(arguments, episode, i + 1);
        }
        */

        // Multi-threaded way
        List<Episode> slicedEpisodes = episodes.subList(from, to);
        Map<Integer, Episode> map = CollectionUtils.toMap(slicedEpisodes);

        map.entrySet().parallelStream().forEach(entry -> {
            int i = entry.getKey();
            Episode episode = entry.getValue();
            downloadOne(arguments, episode, i + 1);
        });
    }

    public static void downloadOne(Arguments arguments, Episode episode, int num) {
        // 미리보기할 수 있는 유료회차의 경우, 다운로드할 수 없다.
        long now = System.currentTimeMillis();
        if (episode.getFreedAt() > now) return;

        // 한국이 아닌 다른 국가의 플랫폼은 에피소드 API를 찾을 수 없어, 직접 크롤링한다.
        final int numOfImages = arguments.getLanguage().equals(Languages.KOREAN.value())
                ? getNumOfImagesInEpisode(arguments, episode)
                : Crawler.getNumOfImagesInEpisode(arguments, episode);

        // 에피소드의 이미지가 없으면 종료한다.
        if (numOfImages < 1) return;

        // 에피소드 이름으로 디렉터리를 생성한다.
        String episodeDirName = StringUtils.lPad(num, 4, '0') + " - " + episode.getDisplay().getTitle();
        File episodeDir = new File(arguments.getComicPathname(), episodeDirName);
        if (!episodeDir.exists()) episodeDir.mkdirs();

        try (ProgressBar progressBar = new ProgressBar(
                arguments.getProduct().getAlias() + " ep." + num, numOfImages,
                500, System.err, ProgressBarStyle.ASCII, "", 1)) {
            // `ProgressBar.step()`이 step 1부터 시작하기 때문에 step 0로 초기화한다.
            progressBar.stepTo(0);

            // 마지막 이미지까지 다운로드한다.
            for (int i = 1; i <= numOfImages; i++) {
                // 이미지 URI를 생성한다
                URL url = URLFactory.image(arguments.getProduct().getId(), episode.getId(), i, arguments.getAccessToken());

                // 이미지를 다운로드한다.
                File image = new File(episodeDir, StringUtils.lPad(String.valueOf(i), 3, '0') + IMG_FORMAT_EXTENSION);
                int result = createImage(url, image);

                // 다운로드에 실패하면 해당 회차를 건너뛴다.
                if (result == 0) break;

                progressBar.stepBy(1);
            }
        }
    }

    /**
     * 이미지 URL로 이미지 파일을 생성한다. 성공하면 1을, 실패하면 0을 반환한다.<br>
     * Creates a image file with the image URL. Returns 1 if success, 0 else.
     */
    private static int createImage(URL url, File image) {
        try (InputStream in = url.openStream(); OutputStream out = new BufferedOutputStream(new FileOutputStream(image))) {
            // 이미지 파일을 만든다.
            // Creates a image file.
            int len;
            while ((len = in.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
            }

            // Success
            return 1;
        } catch (IOException ex) {
            // Fail
            return 0;
        }
    }

    @SneakyThrows({ IOException.class, JsonSyntaxException.class })
    private static int getNumOfImagesInEpisode(Arguments arguments, Episode episode) {
        URL url = URLFactory.oneEpisodeAPI(arguments.getProduct().getAlias(), episode.getName(), arguments.getAccessToken());
        JsonObject json = JsonUtils.readJsonFromUrl(url);

        return json.get("cut").getAsInt();
    }

}
