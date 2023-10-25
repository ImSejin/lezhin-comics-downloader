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

package io.github.imsejin.dl.lezhin.browser

import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder

@Subject(WebBrowser)
class WebBrowserSpec extends Specification {

    def "Turns on debug mode"() {
        given:
        def arguments = ChromeOption.arguments

        expect:
        WebBrowser.@arguments == arguments

        when:
        WebBrowser.debugging()

        then:
        with(ChromeOption) {
            def debugArgs = [HEADLESS, NO_SANDBOX, DISABLE_GPU].collect { it.argument }
            arguments.removeAll(debugArgs)
        }
        WebBrowser.@arguments == arguments
    }

    def "Runs"() {
        given:
        def fileSystem = MemoryFileSystemBuilder.newEmpty()
                .addFileAttributeView(PosixFileAttributeView)
                .build()
        def chromeDriverPath = Files.createFile(fileSystem.getPath("/", "chromedriver"))

        when:
        WebBrowser.run(chromeDriverPath)

        then: "Failed to instantiate WebBrowser.SingletonLazyHolder"
        thrown(ExceptionInInitializerError)
    }

    def "Checks if web browser is running"() {
        expect:
        !WebBrowser.running
    }

}
