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

import io.github.imsejin.lzcodl.common.constant.Languages;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.github.imsejin.lzcodl.common.constant.EpisodeRange.SEPARATOR;

/**
 * @since 2.2.0
 */
public final class UsagePrinter {

    private UsagePrinter() {
    }

    public static void printAndQuit(String... messages) {
        System.out.println();

        print(messages);

        System.out.println();
        System.exit(1);
    }

    public static void print(String... messages) {
        for (String message : messages) {
            System.err.println("    " + message);
        }
    }

    /**
     * @since 2.7.1
     */
    public static void printLanguageAndQuit() {
        String[] messages = Stream.concat(Stream.of("- WHAT LANGUAGES DOES THE DOWNLOADER SUPPORT?"),
                Arrays.stream(Languages.values())
                        .map(it -> String.format("    %s : %s", it.getValue(), it.name().toLowerCase())))
                .toArray(String[]::new);
        printAndQuit(messages);
    }

    /**
     * @since 2.7.1
     */
    public static void printEpisodeRangeAndQuit() {
        printAndQuit(
                "- HOW TO SETUP EPISODE RANGE?",
                "    case 1. skipped : all episodes",
                String.format("    case 2. 8%s   : from ep.8 to the last", SEPARATOR),
                String.format("    case 3. %s25  : from the first to ep.25", SEPARATOR),
                String.format("    case 4. 1%s10 : from ep.1 to ep.10", SEPARATOR));
    }

}
