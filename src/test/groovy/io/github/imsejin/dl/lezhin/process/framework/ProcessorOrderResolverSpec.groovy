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

import org.reflections.Reflections

import io.github.imsejin.common.util.ClassUtils
import io.github.imsejin.dl.lezhin.Application
import io.github.imsejin.dl.lezhin.process.Processor
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

@Subject(ProcessorOrderResolver)
class ProcessorOrderResolverSpec extends Specification {

    def "Resolves the order of process types"() {
        given:
        def processorTypes = new Reflections(Application.class).getSubTypesOf(Processor)
                .findAll { !ClassUtils.isAbstractClass(it) && it.enclosingClass == null }

        when:
        def orderedTypes = ProcessorOrderResolver.resolve(processorTypes)

        then:
        processorTypes == orderedTypes as Set
        orderedTypes == [
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
    }

}
