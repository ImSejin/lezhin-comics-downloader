/*
 * Copyright 2023 Sejin Im
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

package io.github.imsejin.dl.lezhin.api;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;

import io.github.imsejin.dl.lezhin.http.interceptor.FabricatedHeadersInterceptor;

/**
 * @since 3.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseService {

    private static final FabricatedHeadersInterceptor interceptor = new FabricatedHeadersInterceptor();

    @Getter(AccessLevel.PROTECTED)
    private static final Gson gson = new GsonBuilder()
            .disableJdkUnsafe()
            // com.google.gson.stream.MalformedJsonException:
            // Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path $
            .setLenient()
            .create();

    @Getter(AccessLevel.PROTECTED)
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(Duration.ofSeconds(15))
            .writeTimeout(Duration.ofSeconds(15))
            .addInterceptor(interceptor)
            .build();

    protected BaseService(Locale locale, UUID accessToken) {
        interceptor.setLocale(locale);
        interceptor.setAccessToken(accessToken);
    }

}
