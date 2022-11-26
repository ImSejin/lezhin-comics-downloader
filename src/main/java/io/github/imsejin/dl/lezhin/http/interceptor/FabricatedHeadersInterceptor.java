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

package io.github.imsejin.dl.lezhin.http.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class FabricatedHeadersInterceptor implements Interceptor {

    private final AtomicReference<Locale> locale = new AtomicReference<>(Locale.ROOT);

    private final AtomicReference<UUID> accessToken = new AtomicReference<>(new UUID(0, 0));

    @Override
    public Response intercept(Chain chain) throws IOException {
        Locale locale = this.locale.get();

        Request.Builder requestBuilder = chain.request()
                .newBuilder()
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-encoding", "gzip, deflate, br")
                .addHeader("cache-control", "no-cache")
                .addHeader("pragma", "no-cache")
                // Fabricated user agent.
                .addHeader("user-agent", randomizeUserAgent())
                // User-defined header used on lezhin only.
                .addHeader("x-lz-adult", "0")
                .addHeader("x-lz-allowadult", "true")
                .addHeader("x-lz-country", locale.getCountry().toLowerCase(locale))
                .addHeader("x-lz-locale", locale.getLanguage() + '-' + locale.getCountry());

        UUID accessToken = this.accessToken.get();
        if (!accessToken.equals(new UUID(0, 0))) {
            requestBuilder.addHeader("authorization", "Bearer " + accessToken);
        }

        Request request = requestBuilder.build();

        return chain.proceed(request);
    }

    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public void setAccessToken(UUID accessToken) {
        this.accessToken.set(accessToken);
    }

    // -------------------------------------------------------------------------------------------------

    private static String randomizeUserAgent() {
        Random random = ThreadLocalRandom.current();
        Period period = Period.between(LocalDate.of(2005, 1, 1), LocalDate.now().withDayOfMonth(1));

        // 2005-01-01: 1, 2022-11-01: 107
        int majorVersion = (int) period.toTotalMonths() / 2;
        // between 2000 and 5499
        int minorVersion = random.nextInt(3500) + 2000;
        // between 0 and 159
        int bugfixVersion = random.nextInt(160);

        String chromeVersion = String.format("%d.0.%d.%d", majorVersion, minorVersion, bugfixVersion);

        String os;
        switch (random.nextInt(5)) {
            case 0:
                os = "Windows NT 10.0; Win64; x64";
                break;
            case 1:
                os = "Macintosh; Intel Mac OS X 10_15_7";
                break;
            case 2:
                os = "X11; Ubuntu; Linux x86_64";
                break;
            case 3:
                os = "Macintosh; Intel Mac OS X 10_14_6";
                break;
            case 4:
                os = "X11; Linux x86_64";
                break;
            default:
                throw new AssertionError("Never thrown");
        }

        return String.format("Mozilla/5.0 (%s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Safari/537.36", os, chromeVersion);
    }

}
