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
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import io.github.imsejin.common.constant.OS;

import static java.util.stream.Collectors.*;

/**
 * Platform supported on Chrome product
 *
 * @since 4.0.0
 * @see <a href="https://github.com/openjdk/jdk/blob/9843c97695fab3fec1e319027b14974d0e84bf0a/src/hotspot/os/bsd/os_bsd.cpp#L186-L203">
 *     openjdk/hotspot/os_bsd.cpp</a>
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum Platform {

    /**
     * Linux and 64bit processor.
     */
    LINUX64("linux64"),

    /**
     * MacOS and Apple Silicon processor.
     */
    MAC_ARM64("mac-arm64"),

    /**
     * MacOS and Intel processor.
     */
    MAC_X64("mac-x64"),

    /**
     * Windows and 32bit processor.
     */
    WIN32("win32"),

    /**
     * Windows and 64bit processor.
     */
    WIN64("win64");

    private static final String OS_ARCH = System.getProperty("os.arch");

    private static final String PROCESSOR_IDENTIFIER = System.getenv("PROCESSOR_IDENTIFIER");

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

    public static Optional<Platform> getCurrentPlatform() {
        OS os = OS.getCurrentOS();

        switch (os) {
            case LINUX:
            case AIX:
            case SOLARIS:
                return Optional.of(LINUX64);
            case MAC:
                if (OS_ARCH.contains("aarch") | OS_ARCH.contains("arm")) {
                    return Optional.of(MAC_ARM64);
                } else {
                    return Optional.of(MAC_X64);
                }
            case WINDOWS:
                if (OS_ARCH.contains("64")) {
                    return Optional.of(WIN64);
                } else {
                    return Optional.of(WIN32);
                }
        }

        return Optional.empty();
    }

}
