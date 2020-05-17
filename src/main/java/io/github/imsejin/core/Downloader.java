package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import com.google.gson.JsonObject;

import io.github.imsejin.common.util.JsonUtil;
import io.github.imsejin.common.util.StringUtil;
import io.github.imsejin.model.Episode;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Downloader {

    public void download(long comicId, Episode episode, String comicName, String accessToken, File comicDir, String episodeDirName) {
        final long episodeId = episode.getId();
        final int numOfImages = getNumOfImagesInEpisode(comicName, episode.getName(), accessToken);
        final byte[] buffer = new byte[1024];

        // 에피소드 이름으로 디렉터리를 생성한다
        File episodeDir = new File(comicDir, episodeDirName);
        if (!episodeDir.exists()) episodeDir.mkdirs();

        // 마지막 이미지까지 다운로드한다
        for (int i = 1; i <= numOfImages; i++) {
            // 이미지 URI를 생성한다
            URL url = makeImageUrl(comicId, episodeId, accessToken, i);

            try (InputStream in = url.openStream()) {
                System.out.print("try to download -> ");

                // 이미지를 다운로드한다
                File image = new File(episodeDir, StringUtil.lPad(String.valueOf(i), 3, '0') + IMG_FORMAT_EXTENSION);
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(image))) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    System.out.println(image.getName());
                } catch (IOException ex) {}

            } catch (IOException ex) {
                break;
            }
        }

        System.out.println();

    }

    @SneakyThrows(IOException.class)
    public String getCurrentPathName() {
        return Paths.get(".").toRealPath().toString();
    }

    @SneakyThrows(MalformedURLException.class)
    private URL makeImageUrl(long comicId, long episodeId, String accessToken, int fileName) {
        String uriString = IMG_URI_PREFIX + comicId + "/episodes/" + episodeId + "/contents/scrolls/" + fileName + ".webp?access_token=" + accessToken
                + IMG_URI_SUFFIX;
        return URI.create(uriString).toURL();
    }

    private int getNumOfImagesInEpisode(String comicName, String episodeName, String accessToken) {
        URL url = makeOneEpisodeUrl(comicName, episodeName, accessToken);
        JsonObject json = JsonUtil.readJsonFromUrl(url);

        int numOfImages = json.get("cut").getAsInt();
        System.out.println("found " + numOfImages + (numOfImages > 1 ? " images" : " image"));

        return numOfImages;
    }

    @SneakyThrows(MalformedURLException.class)
    private URL makeOneEpisodeUrl(String comicName, String episodeName, String accessToken) {
        String uriString = EPISODE_INFO_URI_PREFIX + comicName + "/" + episodeName + ".json?access_token=" + accessToken;
        return URI.create(uriString).toURL();
    }

    @SneakyThrows(MalformedURLException.class)
    private URL makeAllEpisodeUrl(String comicName, String accessToken) {
        String uriString = EPISODE_INFO_URI_PREFIX + comicName + "?access_token=" + accessToken;
        return URI.create(uriString).toURL();
    }

}
