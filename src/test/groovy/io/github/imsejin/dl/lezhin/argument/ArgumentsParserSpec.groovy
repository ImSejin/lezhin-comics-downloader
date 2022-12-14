/*
 * Copyright 2022 Sejin Im
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

package io.github.imsejin.dl.lezhin.argument

import io.github.imsejin.dl.lezhin.argument.impl.ContentName
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange
import io.github.imsejin.dl.lezhin.argument.impl.ImageFormat
import io.github.imsejin.dl.lezhin.argument.impl.Language
import io.github.imsejin.dl.lezhin.exception.DuplicatedArgumentException
import io.github.imsejin.dl.lezhin.exception.ParsingArgumentException
import org.apache.commons.cli.Option
import spock.lang.Specification

class ArgumentsParserSpec extends Specification {

    def "Failed to create parser due to duplicated argument"() {
        given:
        def arguments = (0..1).collect {
            def argument = Mock(Argument)
            argument.option >> { Option.builder("alpha").build() }
            argument
        }

        when:
        new ArgumentsParser(arguments as Argument[])

        then:
        def e = thrown(DuplicatedArgumentException)
        e.message ==~ /ArgumentsParser received an argument registered already: .+\(option=\[alpha,\w+]\)/
    }

    def "Failed to parse program arguments due to invalid one"() {
        given:
        def argument = Mock(Argument)
        argument.option >> { Option.builder("alpha").build() }

        when:
        def parser = new ArgumentsParser(argument)
        def args = programArgs.split(" ")
        parser.parse(args)

        then:
        def e = thrown(ParsingArgumentException)
        e.message == "Failed to parse argument: $args"

        where:
        programArgs << ["-B", "--alpha --beta=B --gamma=G", "--delta=1001"]
    }

    def "Parses program arguments"() {
        given:
        def argument = Mock(Argument)
        argument.option >> { Option.builder(longName[0]).longOpt(longName).hasArg(value != null).valueSeparator().build() }
        argument.value >> { value }

        when:
        def parser = new ArgumentsParser(argument)
        def arguments = parser.parse(programArgs.split(" "))

        then:
        !arguments.isEmpty()
        argument !== arguments[0]

        where:
        longName | value           | programArgs
        "alpha"  | null            | "--$longName"
        "beta"   | "3.141592"      | "--$longName=$value"
        "gamma"  | null            | "--$longName"
        "delta"  | "lezhin-comics" | "--$longName=$value"
    }

    def "Parses actual program arguments"() {
        given:
        def arguments = [new Language(), new ContentName(), new EpisodeRange(), new ImageFormat(), new DebugMode()]

        when:
        def parser = new ArgumentsParser(arguments as Argument[])
        def actual = parser.parse(programArgs.split(" "))

        then:
        !actual.isEmpty()
        actual != arguments
        actual.size() == arguments.size()
        actual == expected

        where:
        programArgs                   | expected
        "-l=ko -n=alpha"              | [new Language(value: "ko"), new ContentName(value: "alpha"), new EpisodeRange(value: ""), new ImageFormat(value: "false"), new DebugMode(value: "false")]
        "-l=en -n=beta -r=8~"         | [new Language(value: "en"), new ContentName(value: "beta"), new EpisodeRange(value: "8~"), new ImageFormat(value: "false"), new DebugMode(value: "false")]
        "-l=ja -n=gamma -j"           | [new Language(value: "ja"), new ContentName(value: "gamma"), new EpisodeRange(value: ""), new ImageFormat(value: "true"), new DebugMode(value: "false")]
        "-l=ko -n=delta -d"           | [new Language(value: "ko"), new ContentName(value: "delta"), new EpisodeRange(value: ""), new ImageFormat(value: "false"), new DebugMode(value: "true")]
        "-l=en -n=epsilon -r=~25 -j"  | [new Language(value: "en"), new ContentName(value: "epsilon"), new EpisodeRange(value: "~25"), new ImageFormat(value: "true"), new DebugMode(value: "false")]
        "-l=ja -n=zeta -r=1~10 -j -d" | [new Language(value: "ja"), new ContentName(value: "zeta"), new EpisodeRange(value: "1~10"), new ImageFormat(value: "true"), new DebugMode(value: "true")]
    }

}
