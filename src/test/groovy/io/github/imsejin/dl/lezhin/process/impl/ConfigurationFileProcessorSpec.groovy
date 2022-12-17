package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.dl.lezhin.exception.ConfigurationFileNotFoundException
import io.github.imsejin.dl.lezhin.exception.InvalidConfigurationFileException
import io.github.imsejin.dl.lezhin.process.ProcessContext
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

@Subject(ConfigurationFileProcessor)
class ConfigurationFileProcessorSpec extends Specification {

    @TempDir
    private Path tempPath

    def "Failed to process due to no file"() {
        given:
        def processor = new ConfigurationFileProcessor(tempPath)

        when:
        processor.process(ProcessContext.create())

        then:
        def e = thrown(ConfigurationFileNotFoundException)
        e.message == "There is no configuration file: ${tempPath.resolve("config.ini")}"
    }

    def "Failed to process due to no section[account]"() {
        given:
        def filePath = tempPath.resolve("config.ini")
        Files.createFile(filePath)

        when:
        def processor = new ConfigurationFileProcessor(tempPath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "Configuration file has no section[account]: $filePath"
    }

    def "Failed to process due to no name[username]"() {
        given:
        def filePath = tempPath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username=
        """)

        when:
        def processor = new ConfigurationFileProcessor(tempPath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "It is invalid value of name[username] in section[account]: "
    }

    def "Failed to process due to no name[password]"() {
        given:
        def filePath = tempPath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username=anonymous
        password=
        """)

        when:
        def processor = new ConfigurationFileProcessor(tempPath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "It is invalid value of name[password] in section[account]: "
    }

    def "Processes configuration file"() {
        given:
        def filePath = tempPath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username = anonymous
        password = encrypted
        """)

        when:
        def processor = new ConfigurationFileProcessor(tempPath)
        def authentication = processor.process(ProcessContext.create())

        then:
        authentication.username == "anonymous"
        authentication.password == "encrypted"
    }

}
