/*
 * Copyright 2023 Sejin Im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.imsejin.dl.lezhin.api.chromedriver.model

import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.OperatingSystem

@Subject(Platform)
class PlatformSpec extends Specification {

    @Requires({ OperatingSystem.current.isWindows() })
    def "Gets the current platform on windows"() {
        when:
        def platform = Platform.currentPlatform

        then:
        platform.orElseThrow() == Platform.WIN32 || platform.orElseThrow() == Platform.WIN64
    }

    @Requires({ OperatingSystem.current.isLinux() })
    def "Gets the current platform on linux"() {
        when:
        def platform = Platform.currentPlatform

        then:
        platform.orElseThrow() == Platform.LINUX64
    }

    @Requires({ OperatingSystem.current.isMacOs() })
    def "Gets the current platform on macos"() {
        when:
        def platform = Platform.currentPlatform

        then:
        platform.orElseThrow() == Platform.MAC_ARM64 || platform.orElseThrow() == Platform.MAC_X64
    }

}
