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

import io.github.imsejin.dl.lezhin.argument.impl.*
import io.github.imsejin.dl.lezhin.attribute.Attribute
import spock.lang.Specification
import spock.lang.Subject

@Subject(ProcessContext)
class ProcessContextSpec extends Specification {

    def "Defines all attributes of context"() {
        given:
        def fields = ProcessContext.FIELDS
        def types = fields*.type

        expect: "All attributes must be unique."
        !types.isEmpty()
        types.grep(Attribute) == types
        types.unique(false) == types
    }

    def "Creates new context with attributes"() {
        given:
        def attributes = [language, contentName, episodeRange, imageFormat, debugMode, singleThreading]

        when:
        def context = ProcessContext.create(attributes as Object[])

        then:
        context != null
        context.language === language
        context.contentName === contentName
        context.episodeRange === episodeRange
        context.imageFormat === imageFormat
        context.debugMode === debugMode
        context.singleThreading === singleThreading

        where:
        language                  | contentName                       | episodeRange                    | imageFormat                     | debugMode                     | singleThreading
        new Language(value: "ko") | new ContentName(value: "alpha")   | new EpisodeRange(value: "")     | new ImageFormat(value: "false") | new DebugMode(value: "false") | new SingleThreading(value: "true")
        new Language(value: "en") | new ContentName(value: "epsilon") | new EpisodeRange(value: "~25")  | new ImageFormat(value: "true")  | new DebugMode(value: "false") | new SingleThreading(value: "true")
        new Language(value: "ja") | new ContentName(value: "zeta")    | new EpisodeRange(value: "1~10") | new ImageFormat(value: "true")  | new DebugMode(value: "true")  | new SingleThreading(value: "false")
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
        def attributes = [language, contentName, episodeRange, imageFormat, debugMode, singleThreading]
        def context = ProcessContext.create(attributes as Object[])

        when:
        def newContext = ProcessContext.of(context)

        then:
        newContext != null
        newContext === context
        newContext.language === language
        newContext.contentName === contentName
        newContext.episodeRange === episodeRange
        newContext.imageFormat === imageFormat
        newContext.debugMode === debugMode
        context.singleThreading === singleThreading

        where:
        language                  | contentName                       | episodeRange                    | imageFormat                     | debugMode                     | singleThreading
        new Language(value: "ko") | new ContentName(value: "alpha")   | new EpisodeRange(value: "")     | new ImageFormat(value: "false") | new DebugMode(value: "false") | new SingleThreading(value: "true")
        new Language(value: "en") | new ContentName(value: "epsilon") | new EpisodeRange(value: "~25")  | new ImageFormat(value: "true")  | new DebugMode(value: "false") | new SingleThreading(value: "true")
        new Language(value: "ja") | new ContentName(value: "zeta")    | new EpisodeRange(value: "1~10") | new ImageFormat(value: "true")  | new DebugMode(value: "true")  | new SingleThreading(value: "false")
    }

    def "Adds attributes"() {
        given:
        def context = ProcessContext.create(originAttributes.values() as Object[])

        when:
        context.add(newAttributes.values() as Object[])

        then:
        newAttributes.keySet().forEach {
            assert context[it] == newAttributes[it]
        }
        (originAttributes.keySet() - newAttributes.keySet()).forEach {
            assert context[it] == originAttributes[it]
        }
        (originAttributes.keySet().intersect(newAttributes.keySet())).forEach {
            assert context[it] == newAttributes[it]
            assert context[it] != originAttributes[it]
        }

        where:
        originAttributes                                                                         || newAttributes
        [language: new Language(value: "ko"), contentName: new ContentName(value: "alpha")]      || [contentName: new ContentName(value: "beta")]
        [episodeRange: new EpisodeRange(value: "")]                                              || [episodeRange: new EpisodeRange(value: "1~10"), imageFormat: new ImageFormat(value: "true")]
        [language: new Language(value: "en"), debugMode: new DebugMode(value: "true")]           || [episodeRange: new EpisodeRange(value: "~8")]
        [contentName: new ContentName(value: "beta"), episodeRange: new EpisodeRange(value: "")] || [imageFormat: new ImageFormat(value: "true"), debugMode: new DebugMode(value: "false")]
    }

}
