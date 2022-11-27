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

package io.github.imsejin.dl.lezhin.process

import io.github.imsejin.dl.lezhin.argument.impl.ContentName
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange
import io.github.imsejin.dl.lezhin.argument.impl.Language
import io.github.imsejin.dl.lezhin.argument.impl.SaveAsJpeg
import spock.lang.Specification

class ProcessContextSpec extends Specification {

    def "Creates new context with attributes"() {
        given:
        def attributes = [language, contentName, episodeRange, saveAsJpeg, debugMode]

        when:
        def context = ProcessContext.create(attributes as Object[])

        then:
        context != null
        context.language === language
        context.contentName === contentName
        context.episodeRange === episodeRange
        context.saveAsJpeg === saveAsJpeg
        context.debugMode === debugMode

        where:
        language                  | contentName                       | episodeRange                    | saveAsJpeg                     | debugMode
        new Language(value: "ko") | new ContentName(value: "alpha")   | new EpisodeRange(value: "")     | new SaveAsJpeg(value: "false") | new DebugMode(value: "false")
        new Language(value: "en") | new ContentName(value: "epsilon") | new EpisodeRange(value: "~25")  | new SaveAsJpeg(value: "true")  | new DebugMode(value: "false")
        new Language(value: "ja") | new ContentName(value: "zeta")    | new EpisodeRange(value: "1~10") | new SaveAsJpeg(value: "true")  | new DebugMode(value: "true")
    }

    def "Returns given context with redundant attributes"() {
        given:
        def context = ProcessContext.create()

        when:
        def newContext = ProcessContext.of(context)

        then:
        newContext != null
        newContext === context

        when:
        newContext = ProcessContext.of(context, null, null)

        then:
        newContext != null
        newContext === context
    }

    def "Creates new context with other context"() {
        given:
        def attributes = [language, contentName, episodeRange, saveAsJpeg, debugMode]
        def context = ProcessContext.create(attributes as Object[])

        when:
        def newContext = ProcessContext.of(context)

        then:
        newContext != null
        newContext === context
        newContext.language === language
        newContext.contentName === contentName
        newContext.episodeRange === episodeRange
        newContext.saveAsJpeg === saveAsJpeg
        newContext.debugMode === debugMode

        where:
        language                  | contentName                       | episodeRange                    | saveAsJpeg                     | debugMode
        new Language(value: "ko") | new ContentName(value: "alpha")   | new EpisodeRange(value: "")     | new SaveAsJpeg(value: "false") | new DebugMode(value: "false")
        new Language(value: "en") | new ContentName(value: "epsilon") | new EpisodeRange(value: "~25")  | new SaveAsJpeg(value: "true")  | new DebugMode(value: "false")
        new Language(value: "ja") | new ContentName(value: "zeta")    | new EpisodeRange(value: "1~10") | new SaveAsJpeg(value: "true")  | new DebugMode(value: "true")
    }

}
