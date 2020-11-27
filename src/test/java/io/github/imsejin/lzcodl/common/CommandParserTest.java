package io.github.imsejin.lzcodl.common;

import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandParserTest {

    @Test
    @SneakyThrows
    public void parse() {
        // given
        String[] arguments = {"--lang=ko", "-n=snail", "-r=8~", "--debug"};

        // when
        CommandLine cmd = CommandParser.parse(arguments);

        // then
        assertThat(cmd)
                .as("#1 CommandLine must have 'lang' option")
                .matches(it -> it.hasOption('l'))
                .as("#2 CommandLine must have 'name' option")
                .matches(it -> it.hasOption("name"))
                .as("#3 CommandLine must have 'range' option")
                .matches(it -> it.hasOption('r'))
                .as("#4 CommandLine must have 'debug' option")
                .matches(it -> it.hasOption("debug"));
    }

    @Test
    @SneakyThrows
    public void parseError() {
        // given
        String[] arguments = {"--language=ko", "-n=snail", "-r=8~", "--debug"};

        // when & then
        assertThatThrownBy(() -> CommandParser.parse(arguments))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Unrecognized option: ");
    }

}
