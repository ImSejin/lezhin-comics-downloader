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

package io.github.imsejin.dl.lezhin.api.auth.service;

import com.google.gson.annotations.SerializedName;
import io.github.imsejin.dl.lezhin.api.BaseService;
import io.github.imsejin.dl.lezhin.api.auth.model.Authority;
import io.github.imsejin.dl.lezhin.api.auth.model.ServiceRequest;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.common.PropertyBinder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class AuthorityService extends BaseService {

    private final ServiceInterface serviceInterface;

    public AuthorityService(Locale locale, UUID accessToken) {
        super(locale, accessToken);

        OkHttpClient httpClient = BaseService.getHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.lezhin.com/lz-api/v2/cloudfront/signed-url/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        this.serviceInterface = retrofit.create(ServiceInterface.class);
    }

    public Authority getAuthForViewEpisode(ServiceRequest request) {
        Loggers.getLogger().debug("Request: https://www.lezhin.com/lz-api/v2/cloudfront/signed-url/generate" +
                        "?contentId={}&episodeId={}&purchased={}&q={}&firstCheckType={}",
                request.getContentId(),
                request.getEpisodeId(),
                request.isPurchased(),
                request.getQ(),
                request.getFirstCheckType());
        Call<AuthResponse> call = this.serviceInterface.getAuthForViewEpisode(
                request.getContentId(),
                request.getEpisodeId(),
                request.isPurchased(),
                request.getQ(),
                request.getFirstCheckType());

        Response<AuthResponse> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        AuthResponse authResponse = Objects.requireNonNull(response.body());
        return PropertyBinder.INSTANCE.toAuthority(authResponse.getAuthData());
    }

    // -------------------------------------------------------------------------------------------------

    private interface ServiceInterface {
        @GET("generate")
        Call<AuthResponse> getAuthForViewEpisode(
                @Query("contentId") Long contentId,
                @Query("episodeId") Long episodeId,
                @Query("purchased") boolean purchased,
                @Query("q") int q,
                @Query("firstCheckType") Character firstCheckType);
    }

    // -------------------------------------------------------------------------------------------------

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AuthResponse {
        @SerializedName("code")
        private Integer code;

        @SerializedName("description")
        private String description;

        @SerializedName("data")
        private AuthData authData;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AuthData {
        @SerializedName("Policy")
        private String policy;

        @SerializedName("Signature")
        private String signature;

        @SerializedName("Key-Pair-Id")
        private String keyPairId;

        @SerializedName("expiredAt")
        private Long expiredAt;

        @SerializedName("now")
        private Long now;
    }

}
