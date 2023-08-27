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

package io.github.imsejin.dl.lezhin.process.framework

import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Path

import io.github.imsejin.dl.lezhin.process.impl.AccessTokenProcessor
import io.github.imsejin.dl.lezhin.process.impl.ChromeDriverDownloadProcessor
import io.github.imsejin.dl.lezhin.process.impl.ChromeInfoResolutionProcessor
import io.github.imsejin.dl.lezhin.process.impl.ConfigurationFileProcessor
import io.github.imsejin.dl.lezhin.process.impl.ContentInformationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DirectoryCreationProcessor
import io.github.imsejin.dl.lezhin.process.impl.DownloadProcessor
import io.github.imsejin.dl.lezhin.process.impl.HttpHostsProcessor
import io.github.imsejin.dl.lezhin.process.impl.LocaleSelectionProcessor
import io.github.imsejin.dl.lezhin.process.impl.LoginProcessor
import io.github.imsejin.dl.lezhin.process.impl.PurchasedEpisodesProcessor

@Subject(ProcessorCreator)
class ProcessorCreatorSpec extends Specification {

    def "Returns empty processors"() {
        given:
        def creator = new ProcessorCreator()

        when:
        def processors = creator.create([])

        then:
        processors == []
    }

    def "Creates processors"() {
        given:
        def beans = [Path].collect { Mock(it) }
        def types = [
                ConfigurationFileProcessor,
                ChromeInfoResolutionProcessor,
                ChromeDriverDownloadProcessor,
                LoginProcessor,
                HttpHostsProcessor,
                AccessTokenProcessor,
                LocaleSelectionProcessor,
                ContentInformationProcessor,
                PurchasedEpisodesProcessor,
                DirectoryCreationProcessor,
                DownloadProcessor,
        ]

        when:
        def creator = new ProcessorCreator(beans as Object[])
        def processors = creator.create(types)

        then:
        processors*.class == types
    }

}
