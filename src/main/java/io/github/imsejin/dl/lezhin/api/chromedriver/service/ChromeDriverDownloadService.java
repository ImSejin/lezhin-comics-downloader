/*
 * Copyright 2022 Sejin Im
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

package io.github.imsejin.dl.lezhin.api.chromedriver.service;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import io.github.imsejin.dl.lezhin.api.BaseService;
import io.github.imsejin.dl.lezhin.api.chromedriver.model.ChromeDriverDownload;
import io.github.imsejin.dl.lezhin.browser.ChromeVersion;
import io.github.imsejin.dl.lezhin.common.Loggers;

/**
 * https://github.com/pf4j/pf4j-shell
 * https://developer.apple.com/forums/thread/652667
 * https://stackoverflow.com/questions/74315665/how-do-i-detect-apple-silicon-m1-vs-intel-in-java
 * https://chromedriver.storage.googleapis.com/index.html
 * https://chromedriver.storage.googleapis.com/index.html?path=114.0.5735.90/
 *
 * @since 4.0.0
 */
public class ChromeDriverDownloadService extends BaseService {

    private final OldVersionServiceInterface oldVersionServiceInterface;

    private final RecentVersionServiceInterface recentChromedriverService;

    public ChromeDriverDownloadService() {
        this.oldVersionServiceInterface = new Retrofit.Builder()
                .baseUrl("https://chromedriver.storage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(BaseService.getGson()))
                .client(BaseService.getHttpClient())
                .build().create(OldVersionServiceInterface.class);

        this.recentChromedriverService = new Retrofit.Builder()
                .baseUrl("https://googlechromelabs.github.io/chrome-for-testing/")
                .addConverterFactory(GsonConverterFactory.create(BaseService.getGson()))
                .client(BaseService.getHttpClient())
                .build().create(RecentVersionServiceInterface.class);
    }

    public ChromeVersion findChromeVersion(int majorVersion) {
        if (majorVersion <= 0) {
            throw new IllegalArgumentException("Invalid majorVersion: " + majorVersion);
        }

        Loggers.getLogger()
                .debug("Request: https://chromedriver.storage.googleapis.com/LATEST_RELEASE_{}", majorVersion);
        Call<String> call = this.oldVersionServiceInterface.getFullVersionByMajorVersion(majorVersion);

        Response<String> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        String chromeVersion = response.body();
        if (chromeVersion == null) {
            throw new IllegalArgumentException("Failed to get version of chrome");
        }

        return new ChromeVersion(chromeVersion);
    }

    public ChromeDriverDownload findChromeDriverDownload() {
        Loggers.getLogger()
                .debug("Request: https://googlechromelabs.github.io/chrome-for-testing/known-good-versions-with-downloads.json");
        Call<ChromeDriverDownload> call = this.recentChromedriverService.getKnownGoodVersionsWithDownloads();

        Response<ChromeDriverDownload> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        ChromeDriverDownload download = response.body();
        if (download == null) {
            throw new IllegalArgumentException("Failed to get list of chromedriver url");
        }

        return download;
    }

    // -------------------------------------------------------------------------------------------------

    private interface OldVersionServiceInterface {
        @GET("LATEST_RELEASE_{major}")
        Call<String> getFullVersionByMajorVersion(@Path("major") int majorVersion);
    }

    private interface RecentVersionServiceInterface {
        @GET("known-good-versions-with-downloads.json")
        Call<ChromeDriverDownload> getKnownGoodVersionsWithDownloads();
    }

}
