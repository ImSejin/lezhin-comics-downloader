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

package io.github.imsejin.lzcodl.common;

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.lzcodl.common.constant.Languages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.imsejin.lzcodl.common.constant.EpisodeRange.SEPARATOR;
import static java.util.stream.Collectors.toList;

/**
 * @since 2.2.0
 */
public final class UsagePrinter {

    @ExcludeFromGeneratedJacocoReport
    private UsagePrinter() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    public static void printAndQuit(String... messages) {
        print(messages);
        System.exit(1);
    }

    public static void print(String... messages) {
        System.out.println();
        for (String message : messages) {
            Loggers.getLogger().warn(message);
        }
    }

    /**
     * @since 2.7.1
     */
    public static void printLanguageAndQuit() {
        printLanguageAndQuit(null);
    }

    /**
     * @since 2.7.1
     */
    public static void printLanguageAndQuit(@Nullable String format, Object... args) {
        List<String> languageExamples = Arrays.stream(Languages.values())
                .map(it -> String.format("    %s : %s", it.getValue(), it.name().toLowerCase()))
                .collect(toList());

        List<String> messages = new ArrayList<>();
        if (format != null) messages.add(String.format(format + "%n", args));
        messages.add("- WHAT LANGUAGES DOES THE DOWNLOADER SUPPORT?");
        messages.addAll(languageExamples);

        printAndQuit(messages.toArray(new String[0]));
    }

    /**
     * @since 2.7.1
     */
    public static void printEpisodeRangeAndQuit() {
        printEpisodeRangeAndQuit(null);
    }

    /**
     * @since 2.7.1
     */
    public static void printEpisodeRangeAndQuit(@Nullable String format, Object... args) {
        List<String> messages = new ArrayList<>();
        if (format != null) messages.add(String.format(format + "%n", args));
        messages.add("- HOW TO SETUP EPISODE RANGE?");
        messages.add("    case 1. skipped : all episodes");
        messages.add(String.format("    case 2. 8%s   : from ep.8 to the last", SEPARATOR));
        messages.add(String.format("    case 3. %s25  : from the first to ep.25", SEPARATOR));
        messages.add(String.format("    case 4. 1%s10 : from ep.1 to ep.10", SEPARATOR));

        printAndQuit(messages.toArray(new String[0]));
    }

}
