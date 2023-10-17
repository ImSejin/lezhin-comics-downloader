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

package io.github.imsejin.dl.lezhin.api.chromedriver.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.*;

/**
 * @since 4.0.0
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum Platform {

    LINUX64("linux64"),
    MAC_ARM64("mac-arm64"),
    MAC_X64("mac-x64"),
    WIN32("win32"),
    WIN64("win64");

    private static final Map<String, Platform> cache = Arrays.stream(values())
            .collect(collectingAndThen(toMap(it -> it.value, Function.identity()), Collections::unmodifiableMap));

    private final String value;

    public static Platform from(String value) {
        Platform platform = cache.get(value);

        if (platform == null) {
            throw new IllegalArgumentException("Invalid Platform.value: " + value);
        }

        return platform;
    }

}
