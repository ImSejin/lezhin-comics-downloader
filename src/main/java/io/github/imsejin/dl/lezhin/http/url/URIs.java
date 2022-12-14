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

package io.github.imsejin.dl.lezhin.http.url;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.util.ArrayUtils;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 2.5.0
 */
@RequiredArgsConstructor
public enum URIs {

    /**
     * Login page.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/login
     * }</pre>
     */
    LOGIN("/{language}/login"),

    /**
     * Page to choose your locale.
     *
     * <pre>{@code
     *     /ko/locale/ko-KR
     * }</pre>
     */
    LOCALE("/{language}/locale/{locale}"),

    EXPIRATION("/{language}/error/expired"),

    /**
     * Comic page that shows its episodes.
     *
     * <pre>{@code
     *     /ko/comic/redhood
     * }</pre>
     */
    CONTENT("/{language}/comic/{contentName}"),

    /**
     * Episode page that shows its cuts(images).
     *
     * <pre>{@code
     *     /ko/comic/redhood/9
     *     /ko/comic/redhood/e1
     * }</pre>
     */
    EPISODE("/{language}/comic/{comicName}/{episodeName}"),

    LIBRARY_CONTENT("/{language}/library/comic/{locale}/{contentName}"),

    LIBRARY_EPISODE("/{language}/library/comic/{locale}/{contentName}/{episodeName}"),

    EPISODE_IMAGE("/v2/comics/{contentId}/episodes/{episodeId}/contents/scrolls/{num}.{imageFormat}" +
            "?purchased={purchased}&q=30&Policy={policy}&Signature={signature}&Key-Pair-Id={keyPairId}");

    private static final Pattern PATTERN = Pattern.compile("\\{(.+?)}", Pattern.MULTILINE);

    private final String template;

    /**
     * Returns a URI string.
     *
     * @param params parameters
     * @return URI string
     */
    public String get(Object... params) {
        if (ArrayUtils.isNullOrEmpty(params)) {
            return this.template;
        }

        Matcher matcher = PATTERN.matcher(this.template);

        // Converts all variables to parameters.
        String uri = this.template;
        for (int i = 0; i < params.length && matcher.find(); i++) {
            String param = String.valueOf(params[i]);
            uri = uri.replaceAll("\\{" + matcher.group(1) + '}', param);
        }

        // Validates all variables in URI are converted to parameters.
        Asserts.that(PATTERN.matcher(uri).find())
                .describedAs("Template URI has not matched variable(s): '{0}'", uri)
                .isFalse();

        return uri;
    }

}
