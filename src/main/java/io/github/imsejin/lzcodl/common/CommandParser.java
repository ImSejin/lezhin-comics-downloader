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

import org.apache.commons.cli.*;

import javax.annotation.Nonnull;

public final class CommandParser {

    private static final Options options;

    static {
        // Option: language
        Option lang = Option.builder("l")
                .longOpt("lang")
                .desc("language of lezhin platform you want to see")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // Option: comicName
        Option name = Option.builder("n")
                .longOpt("name")
                .desc("webtoon name you want to download")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        // Option: episodeRange
        Option range = Option.builder("r")
                .longOpt("range")
                .desc("range of episodes you want to download")
                .hasArg()
                .valueSeparator()
                .build();
        // Option: debugging
        Option debug = Option.builder("d")
                .longOpt("debug")
                .desc("debug mode")
                .build();

        options = new Options().addOption(lang).addOption(name).addOption(range).addOption(debug);
    }

    private CommandParser() {
    }

    /**
     * Validates and parses options and arguments.
     *
     * @param arguments arguments
     * @return parsed commands
     */
    public static CommandLine parse(@Nonnull String... arguments) {
        try {
            // Parses options and arguments.
            return new DefaultParser().parse(options, arguments);
        } catch (ParseException e) {
            // Without required options or arguments, the program will exit.
            new HelpFormatter().printHelp(" ", null, options, "", true);
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }

}
