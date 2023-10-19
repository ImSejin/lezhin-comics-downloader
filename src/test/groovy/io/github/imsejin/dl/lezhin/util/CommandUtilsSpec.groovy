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

package io.github.imsejin.dl.lezhin.util

import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.OperatingSystem

@Subject(CommandUtils)
class CommandUtilsSpec extends Specification {

    @Requires({ OperatingSystem.current.isWindows() })
    def "Runs command on windows"() {
        given:
        def expected = "foobar"

        when:
        def result = CommandUtils.runCommand("cmd", "/c", "echo $expected")

        then:
        result == expected
    }

    @Requires({ OperatingSystem.current.isLinux() })
    def "Runs command on linux"() {
        given:
        def expected = "foobar"

        when:
        def result = CommandUtils.runCommand("bash", "-c", "echo '$expected'")

        then:
        result == expected
    }

    @Requires({ OperatingSystem.current.isMacOs() })
    def "Runs command on macos"() {
        given:
        def expected = "foobar"

        when:
        def result = CommandUtils.runCommand("bash", "-c", "echo '$expected'")

        then:
        result == expected
    }

}
