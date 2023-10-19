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

package io.github.imsejin.dl.lezhin.process.impl

import spock.lang.Specification
import spock.lang.Subject

import io.github.imsejin.dl.lezhin.attribute.impl.DirectoryPath
import io.github.imsejin.dl.lezhin.process.ProcessContext
import io.github.imsejin.dl.lezhin.util.PathUtils

@Subject(ChromeInfoResolutionProcessor)
class ChromeInfoResolutionProcessorSpec extends Specification {

    def "Resolves information of chrome products"() {
        given:
        def context = ProcessContext.create(new DirectoryPath(PathUtils.currentPath))

        when:
        def processor = new ChromeInfoResolutionProcessor()
        def chromeInfo = processor.process(context)

        then:
        chromeInfo != null
    }

}
