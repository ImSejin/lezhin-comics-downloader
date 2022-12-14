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

package io.github.imsejin.dl.lezhin.api.image;

import com.google.gson.annotations.SerializedName;
import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.dl.lezhin.api.BaseService;
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode;
import io.github.imsejin.dl.lezhin.common.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

/**
 * @since 3.0.0
 */
public class EpisodeImageCountService extends BaseService {

    private final ServiceInterface serviceInterface;

    public EpisodeImageCountService(UUID accessToken) {
        // Supported on only korea platform.
        super(Locale.KOREA, accessToken);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lezhin.com/")
                .addConverterFactory(GsonConverterFactory.create(BaseService.getGson()))
                .client(BaseService.getHttpClient())
                .build();

        this.serviceInterface = retrofit.create(ServiceInterface.class);
    }

    public Map<String, Integer> getImageCountMap(String contentAlias) {
        Loggers.getLogger().debug("Request: https://api.lezhin.com/episodes/{}", contentAlias);
        Call<List<EpisodeModel>> call = this.serviceInterface.getEpisodes(contentAlias);

        Response<List<EpisodeModel>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        List<EpisodeModel> models = response.body();
        if (CollectionUtils.isNullOrEmpty(models)) {
            throw new IllegalArgumentException("Failed to get number of episode images: " + contentAlias);
        }

        Map<String, Integer> imageCountMap = models.stream()
                .collect(toMap(EpisodeModel::getName, EpisodeModel::getImageCount));
        return Collections.unmodifiableMap(imageCountMap);
    }

    public int getImageCount(String contentAlias, Episode episode) {
        Loggers.getLogger().debug("Request: https://api.lezhin.com/episodes/{}/{}", contentAlias, episode.getName());
        Call<EpisodeModel> call = this.serviceInterface.getEpisode(contentAlias, episode.getName());

        Response<EpisodeModel> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        EpisodeModel model = response.body();
        if (model == null) {
            throw new IllegalArgumentException("Failed to get number of episode images: "
                    + contentAlias + '/' + episode.getName());
        }

        return model.getImageCount();
    }

    // -------------------------------------------------------------------------------------------------

    private interface ServiceInterface {
        @GET("episodes/{contentAlias}")
        Call<List<EpisodeModel>> getEpisodes(@Path("contentAlias") String contentAlias);

        @GET("episodes/{contentAlias}/{name}")
        Call<EpisodeModel> getEpisode(
                @Path("contentAlias") String contentAlias,
                @Path("name") String name);
    }

    // -------------------------------------------------------------------------------------------------

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EpisodeModel {
        @SerializedName("episodeId")
        String id;

        @SerializedName("seq")
        Integer seq;

        @SerializedName("name")
        String name;

        @SerializedName("comicId")
        String contentAlias;

        @SerializedName("cut")
        Integer imageCount;
    }

}
