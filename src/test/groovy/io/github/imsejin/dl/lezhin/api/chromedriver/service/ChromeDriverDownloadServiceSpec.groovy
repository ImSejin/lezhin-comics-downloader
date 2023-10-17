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

import java.nio.charset.StandardCharsets
import java.time.Instant

import io.github.imsejin.dl.lezhin.api.BaseService
import io.github.imsejin.dl.lezhin.api.chromedriver.model.ChromeDriverDownload

import static java.util.stream.Collectors.*

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
        def service = Mock(ChromeDriverDownloadService)
        service.findChromeDriverDownload() >> {
            def inputStream = Thread.currentThread().contextClassLoader
                    .getResourceAsStream("json/known-good-versions-with-downloads.json")
            def jsonText = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(joining())
            BaseService.gson.fromJson(jsonText, ChromeDriverDownload)
        }

        when:
        def download = service.findChromeDriverDownload()

        then:
        download != null
        download.timestamp == Instant.parse("2023-10-17T04:08:56.591Z")
        download.versions.size() == 309
        download.versions*.value.every { it != null }
        download.versions*.revision.every { it.matches('^\\d+$') }
        download.versions*.downloads.findAll { it.chromedrivers }.every { it.chromes.size() == it.chromedrivers.size() }
    }

}
