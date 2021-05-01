/*
 * Copyright 2020 Sejin Im
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

package io.github.imsejin.lzcodl.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public enum URIs {

    /**
     * Login page.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/login
     * }</pre>
     */
    LOGIN("https://www.lezhin.com/{language}/login"),

    /**
     * Page to choose your locale.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/locale/ko-KR?locale=ko-KR
     * }</pre>
     */
    LOCALE("https://www.lezhin.com/{language}/locale/{locale}?locale={locale}"),

    /**
     * Comic page that shows its episodes.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/comic/redhood
     * }</pre>
     */
    COMIC("https://www.lezhin.com/{language}/comic/{comicName}"),

    /**
     * Episode page that shows its cuts(images).
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/comic/redhood/9
     *     https://www.lezhin.com/ko/comic/redhood/e1
     * }</pre>
     */
    EPISODE("https://www.lezhin.com/{language}/comic/{comicName}/{episodeName}"),

    EXPIRED("https://www.lezhin.com/{language}/error/expired"),

    LIB_COMIC("https://www.lezhin.com/{language}/library/comic/{locale}/{comicName}"),

    LIB_EPISODE("https://www.lezhin.com/{language}/library/comic/{locale}/{comicName}/{episodeName}");

    private static final Pattern pattern = Pattern.compile("\\{(.+?)}", Pattern.MULTILINE);

    private final String value;

    /**
     * Checks if {@link URIs} that has the value exists.
     *
     * @param value {@link #getValue()}
     * @return {@link URIs}
     */
    public static boolean contains(String value) {
        if (value == null) return false;
        return Arrays.stream(values()).map(uri -> uri.value).anyMatch(value::equals);
    }

    /**
     * Returns URI.
     *
     * @param params parameters
     * @return URI string
     */
    public URI get(String... params) {
        if (params == null || params.length == 0) return URI.create(this.value);

        Matcher matcher = pattern.matcher(this.value);

        // Converts all variables to parameters.
        String uri = this.value;
        for (int i = 0; i < params.length && matcher.find(); i++) {
            uri = uri.replaceAll("\\{" + matcher.group(1) + '}', params[i]);
        }

        // Validates all variables in URI are converted to parameters.
        if (pattern.matcher(uri).find()) throw new RuntimeException("Template URI has not matched variable(s)");

        return URI.create(uri);
    }

}
