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

package io.github.imsejin.dl.lezhin.api.purchase.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.imsejin.common.util.StreamUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.api.BaseService;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.common.Loggers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @since 3.0.0
 */
public class PurchasedEpisodeService extends BaseService {

    private final ServiceInterface serviceInterface;

    private final Language language;

    public PurchasedEpisodeService(Language language, UUID accessToken) {
        super(language.getValue(), accessToken);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.lezhin.com/lz-api/v2/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(BaseService.getHttpClient())
                .build();

        this.serviceInterface = retrofit.create(ServiceInterface.class);
        this.language = language;
    }

    public List<Long> getPurchasedEpisodeIdList(String contentAlias) {
        Loggers.getLogger().debug("Request: https://www.lezhin.com/lz-api/v2/contents/{}/users", contentAlias);
        Call<String> call = this.serviceInterface.getPurchasedEpisodes(contentAlias, "comic",
                this.language.getValue().getLanguage(), this.language.asLocaleString());

        Response<String> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        String json = response.body();
        if (StringUtils.isNullOrBlank(json)) {
            throw new IllegalArgumentException("Failed to get list of purchased episode: " + contentAlias);
        }

        JsonElement jsonElement = JsonParser.parseString(json);
        JsonElement data = jsonElement.getAsJsonObject().get("data");
        JsonArray jsonArray = data.getAsJsonObject().get("purchased").getAsJsonArray();

        return StreamUtils.toStream(jsonArray.iterator()).map(JsonElement::getAsLong).collect(toUnmodifiableList());
    }

    // -------------------------------------------------------------------------------------------------

    private interface ServiceInterface {
        @GET("contents/{contentAlias}/users")
        Call<String> getPurchasedEpisodes(
                @Path("contentAlias") String contentAlias,
                @Query("type") String type,
                @Query("country_code") String countryCode,
                @Query("locale") String locale);
    }

}
