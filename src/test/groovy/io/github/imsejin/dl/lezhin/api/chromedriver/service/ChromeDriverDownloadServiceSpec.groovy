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

package io.github.imsejin.dl.lezhin.api.chromedriver.service

import spock.lang.Specification
import spock.lang.Subject

@Subject(ChromeDriverDownloadService)
class ChromeDriverDownloadServiceSpec extends Specification {

    def "Finds full chrome version by major version"() {
        given:
        def service = new ChromeDriverDownloadService()

        when:
        def chromeVersion = service.findChromeVersion(114)

        then:
        chromeVersion != null
        chromeVersion.majorVersion == 114
    }

    def "Finds chromedriver downloads"() {
        given:
        def service = new ChromeDriverDownloadService()

        when:
        def download = service.findChromeDriverDownload()

        then:
        download != null
        download.timestamp != null
        download.versions.size() > 0
        download.versions*.value.every { it.matches('^\\d+(\\.\\d+){3}$') }
        download.versions*.revision.every { it.matches('^\\d+$') }
        download.versions*.downloads.findAll { it.chromedriver }.every { it.chrome.size() == it.chromedriver.size() }
    }

}
