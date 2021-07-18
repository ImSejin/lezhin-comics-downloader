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

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.lzcodl.common.exception.InvalidLanguageException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * @since 2.1.2
 */
@Getter
@RequiredArgsConstructor
public enum Languages {

    /**
     * Korean.
     *
     * @see java.util.Locale#KOREA
     */
    KOREAN("ko", "ko-KR"),

    /**
     * English.
     *
     * @see java.util.Locale#US
     */
    ENGLISH("en", "en-US"),

    /**
     * Japanese.
     *
     * @see java.util.Locale#JAPAN
     */
    JAPANESE("ja", "ja-JP");

    private static final Map<String, Languages> $CODE_LOOKUP = EnumSet.allOf(Languages.class).stream()
            .collect(collectingAndThen(toMap(it -> it.value, it -> it), Collections::unmodifiableMap));

    private final String value;

    private final String locale;

    /**
     * Checks if {@link Languages} that has the value exists.
     *
     * @param value {@link #getValue()}
     * @return {@link Languages}
     */
    public static boolean contains(String value) {
        return $CODE_LOOKUP.containsKey(value);
    }

    /**
     * Returns constant of {@link Languages} whose value is equal to the parameter.
     *
     * @param value {@link #getValue()}
     * @return constant of {@link Languages}
     * @throws InvalidLanguageException if {@link Languages} that has the parameter doesn't exist
     */
    public static Languages from(String value) {
        Asserts.that($CODE_LOOKUP)
                .as("Invalid language: '{0}'", value)
                .exception(InvalidLanguageException::new)
                .containsKey(value);

        return $CODE_LOOKUP.get(value);
    }

}
