package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.IMG_FORMAT_EXTENSION;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;

import com.google.gson.JsonObject;

import io.github.imsejin.common.util.JsonUtil;
import io.github.imsejin.common.util.StringUtil;
import io.github.imsejin.model.Episode;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

@UtilityClass
public class Downloader {

    private static final byte[] BUFFER = new byte[1024];

    public void download(long comicId, Episode episode, String comicName, String accessToken, File comicDir, int num) {
        // 에피소드의 이미지가 없으면 종료한다
        final int numOfImages = getNumOfImagesInEpisode(comicName, episode.getName(), accessToken);
        if (numOfImages < 1) return;

        // 에피소드 이름으로 디렉터리를 생성한다
        String episodeDirName = StringUtil.lPad(num, 4, '0') + " - " + episode.getDisplay().getTitle();
        File episodeDir = new File(comicDir, episodeDirName);
        if (!episodeDir.exists()) episodeDir.mkdirs();

        try (ProgressBar progressBar = new ProgressBar(comicName + " ep." + num, numOfImages, 500, System.err, ProgressBarStyle.ASCII, "", 1)) {
            // `ProgressBar.step()`이 step 1부터 시작하기 때문에 step 0로 초기화한다
            progressBar.stepTo(0);

            // 마지막 이미지까지 다운로드한다
            for (int i = 1; i <= numOfImages; i++) {
                // 이미지 URI를 생성한다
                URL url = URLBuilder.imageURL(comicId, episode.getId(), i, accessToken);

                // 이미지를 다운로드한다
                File image = new File(episodeDir, StringUtil.lPad(String.valueOf(i), 3, '0') + IMG_FORMAT_EXTENSION);
                int result = createImage(url, image);

                // 다운로드에 실패하면 해당 회차를 건너뛴다
                if (result == 0) break;

                progressBar.stepBy(1);
            }
        }
    }

    /**
     * 이미지 URL로 이미지 파일을 생성한다. 성공하면 1을, 실패하면 0을 반환한다.<br>
     * Creates a image file with the image URL. Returns 1 if success, 0 else.
     */
    private int createImage(URL url, File image) {
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

    @SneakyThrows(IOException.class)
    public String getCurrentPathName() {
        return Paths.get(".").toRealPath().toString();
    }

    private int getNumOfImagesInEpisode(String comicName, String episodeName, String accessToken) {
        URL url = URLBuilder.oneEpisodeURL(comicName, episodeName, accessToken);
        JsonObject json = JsonUtil.readJsonFromUrl(url);

        return json.get("cut").getAsInt();
    }

}
