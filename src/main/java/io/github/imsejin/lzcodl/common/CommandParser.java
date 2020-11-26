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
