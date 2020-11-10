package io.github.imsejin.lzcodl;

import org.apache.commons.cli.*;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class CommandParserTest {

    @Test
    public void test() throws ParseException {
        // given
        Option lang = Option.builder("l")
                .longOpt("language")
                .desc("the platform of lezhin comics you want to see")
                .valueSeparator()
                .hasArg()
                .build();
        Option name = Option.builder("n")
                .longOpt("name")
                .desc("the webtoon name you want to download")
                .valueSeparator()
                .hasArg()
                .required()
                .build();
        Option range = Option.builder("r")
                .longOpt("range")
                .desc("the range of episodes you want to download")
                .hasArgs()
                .valueSeparator()
                .build();
        Option debug = Option.builder("d")
                .longOpt("debug")
                .desc("debug mode")
                .build();
        Options options = new Options().addOption(lang).addOption(name).addOption(range).addOption(debug);

        // when
        DefaultParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[]{"--language=ko", "-n=snail", "-r=8~", "--debug"});

        // then
        assertThat(cmd)
                .as("#1 CommandLine must have 'language' option")
                .is(new Condition<>(it -> it.hasOption('l'), null))
                .as("#2 CommandLine must have 'name' option")
                .is(new Condition<>(it -> it.hasOption("name"), null))
                .as("#3 CommandLine must have 'range' option")
                .is(new Condition<>(it -> it.hasOption('r'), null))
                .as("#4 CommandLine must have 'debug' option")
                .is(new Condition<>(it -> it.hasOption("debug"), null));
        new HelpFormatter().printHelp(" ", null, options, "", true);
    }

}
