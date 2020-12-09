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

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Languages implements KeyValue {

    /**
     * Korean.
     */
    KOREAN("ko", "ko-KR"),

    /**
     * English.
     */
    ENGLISH("en", "en-US"),

    /**
     * Japanese.
     */
    JAPANESE("ja", "ja-JP");

    private final String value;

    @Getter
    private final String locale;

    /**
     * Checks if {@link Languages} that has the value exists.
     *
     * @param value {@link #value()}
     * @return {@link Languages}
     */
    public static boolean contains(String value) {
        if (value == null) return false;
        return Arrays.stream(values()).map(lang -> lang.value).anyMatch(value::equals);
    }

    /**
     * Returns constant of {@link Languages} whose value is equal to the parameter.
     *
     * @param value {@link #value()}
     * @return constant of {@link Languages}
     * @throws IllegalArgumentException if {@link Languages} that has the parameter doesn't exist
     */
    public static Languages from(String value) {
        Languages languages = Arrays.stream(values()).filter(lang -> lang.value.equals(value))
                .findAny().orElse(null);

        if (languages == null) throw new IllegalArgumentException("Invalid value for languages: " + value);
        return languages;
    }

    @Override
    public String key() {
        return name();
    }

    @Override
    public String value() {
        return this.value;
    }

}
