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

package io.github.imsejin.dl.lezhin.common

import spock.lang.Specification
import spock.lang.Subject

@Subject(ApplicationPropertiesCreator)
class ApplicationPropertiesCreatorSpec extends Specification {

    def "Parses application.properties"() {
        given:
        def classLoader = Thread.currentThread().contextClassLoader
        def inputStream = classLoader.getResourceAsStream("application.properties")

        when:
        def creator = new ApplicationPropertiesCreator()
        def properties = creator.parse(inputStream)

        then:
        properties.keySet() == ["version", "chromedriver/url/download-list"].toSet()
        properties.values().every { !it.isBlank() }
    }

    def "Creates application properties"() {
        given:
        def classLoader = Thread.currentThread().contextClassLoader
        def inputStream = classLoader.getResourceAsStream("application.properties")

        when:
        def creator = new ApplicationPropertiesCreator()
        def applicationProperties = creator.create(inputStream)

        then:
        applicationProperties != null
        applicationProperties.version.matches('^\\d+(\\.\\d+)+$')
        applicationProperties.chromedriver.url.downloadList.matches('^https://.+')
    }

}
