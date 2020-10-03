package io.github.imsejin.lzcodl.common.util;

import org.apache.commons.cli.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        Options options = new Options().addOption(lang).addOption(name).addOption(range);

        // when
        DefaultParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[]{"--language=ko", "-n=snail", "-r=8~"});

        // then
        assertTrue(cmd.hasOption('l') == cmd.hasOption("name") == cmd.hasOption('r'));
    }

}
