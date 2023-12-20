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
import spock.util.environment.OperatingSystem

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView
import java.time.Instant

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder

import io.github.imsejin.dl.lezhin.api.chromedriver.model.ChromeDriverDownload
import io.github.imsejin.dl.lezhin.api.chromedriver.model.Platform
import io.github.imsejin.dl.lezhin.api.chromedriver.service.ChromeDriverDownloadService
import io.github.imsejin.dl.lezhin.attribute.impl.ChromeInfo
import io.github.imsejin.dl.lezhin.attribute.impl.DirectoryPath
import io.github.imsejin.dl.lezhin.browser.ChromeVersion
import io.github.imsejin.dl.lezhin.process.ProcessContext

/**
 * <pre>
 * io.github.imsejin.dl.lezhin.process.impl
 * ChromeDriverDownloadProcessorSpec
 * </pre>
 *
 * @author : imsejin
 * @date : 2023-10-20
 */
class ChromeDriverDownloadProcessorSpec extends Specification {

    private FileSystem fileSystem
    private ProcessContext context

    void setup() {
        fileSystem = MemoryFileSystemBuilder.newEmpty()
                .addFileAttributeView(PosixFileAttributeView)
                .build()
        context = ProcessContext.create()
        context.add(new DirectoryPath(fileSystem.getPath("/")))
    }

    def "Downloads a chromedriver"() {
        given:
        def driverFileName = OperatingSystem.current.windows ? "chromedriver.exe" : "chromedriver"
        context.add(
                ChromeInfo.builder(fileSystem.getPath("/", driverFileName))
                        .browserVersion(ChromeVersion.from("114.0.5735.90"))
                        .build()
        )

        and:
        def service = Mock(ChromeDriverDownloadService)
        service.findChromeDriverDownload() >> {
            def program = new ChromeDriverDownload.Program()
            program.@platform = Platform.currentPlatform.orElseThrow()
            program.@url = Thread.currentThread().contextClassLoader.getResource("archive/chromedriver-test.zip")

            def downloads = new ChromeDriverDownload.Downloads()
            downloads.@chromedrivers = [program]

            def version = new ChromeDriverDownload.Version()
            version.@value = ChromeVersion.from("114.0.5735.90")
            version.@downloads = downloads

            def download = new ChromeDriverDownload()
            download.@timestamp = Instant.now()
            download.@versions = [version]

            download
        }

        when:
        def processor = new ChromeDriverDownloadProcessor(service)
        processor.process(context)

        then:
        context.chromeInfo.driverPath
        Files.isRegularFile(context.chromeInfo.driverPath)
        Files.isExecutable(context.chromeInfo.driverPath)

        cleanup:
        fileSystem.close()
    }

}
