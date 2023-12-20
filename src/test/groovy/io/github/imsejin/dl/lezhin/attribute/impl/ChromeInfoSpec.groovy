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

package io.github.imsejin.dl.lezhin.attribute.impl

import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Path

import io.github.imsejin.dl.lezhin.browser.ChromeVersion

@Subject(ChromeInfo)
class ChromeInfoSpec extends Specification {

    def "Instantiates with no driver version"() {
        given:
        def driverPath = Path.of(driverPathString)

        when:
        def chromeInfo = ChromeInfo.builder(driverPath)
                .browserVersion(browserVersion)
                .build()

        then:
        chromeInfo != null
        chromeInfo.browserVersion == browserVersion
        chromeInfo.driverVersion == null
        chromeInfo.driverPath == driverPath
        chromeInfo.status == status

        where:
        browserVersion                      | driverPathString                 || status
        ChromeVersion.from("114.0.5735.90") | "/usr/local/bin/chromedriver"    || ChromeInfo.Status.BROWSER_ONLY
        null                                | "/opt/homebrew/bin/chromedriver" || ChromeInfo.Status.NONE
    }

    def "Instantiates with driver version"() {
        given:
        def driverPath = Path.of("/opt/homebrew/bin/chromedriver")

        when:
        def chromeInfo = ChromeInfo.builder(driverPath)
                .browserVersion(browserVersion)
                .driverVersion(driverVersion)
                .build()

        then:
        chromeInfo != null
        chromeInfo.browserVersion == browserVersion
        chromeInfo.driverVersion == driverVersion
        chromeInfo.driverPath == driverPath
        chromeInfo.status == status

        where:
        browserVersion                      | driverVersion                        || status
        ChromeVersion.from("114.0.5735.90") | ChromeVersion.from("114.0.5790.170") || ChromeInfo.Status.ENTIRE
        null                                | ChromeVersion.from("116.0.5845.97")  || ChromeInfo.Status.DRIVER_ONLY
    }

    def "Failed to instantiate with no driver path"() {
        when:
        ChromeInfo.builder(null)

        then:
        thrown(NullPointerException)
    }

}
