package io.github.imsejin.dl.lezhin.process.impl

import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder

import io.github.imsejin.dl.lezhin.exception.ConfigurationFileNotFoundException
import io.github.imsejin.dl.lezhin.exception.InvalidConfigurationFileException
import io.github.imsejin.dl.lezhin.process.ProcessContext

@Subject(ConfigurationFileProcessor)
class ConfigurationFileProcessorSpec extends Specification {

    private FileSystem fileSystem

    void setup() {
        fileSystem = MemoryFileSystemBuilder.newEmpty()
                .addFileAttributeView(PosixFileAttributeView)
                .build()
    }

    def "Fails due to no file"() {
        given:
        def basePath = fileSystem.getPath("/")
        def filePath = basePath.resolve("config.ini")
        def processor = new ConfigurationFileProcessor(basePath)

        when:
        processor.process(ProcessContext.create())

        then:
        def e = thrown(ConfigurationFileNotFoundException)

        e.message == "There is no configuration file: $filePath"
    }

    def "Fails due to no section[account]"() {
        given:
        def basePath = fileSystem.getPath("/")
        def filePath = basePath.resolve("config.ini")
        Files.createFile(filePath)

        when:
        def processor = new ConfigurationFileProcessor(basePath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "Configuration file has no section[account]: $filePath"
    }

    def "Fails due to no name[username]"() {
        given:
        def basePath = fileSystem.getPath("/")
        def filePath = basePath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username=
        """)

        when:
        def processor = new ConfigurationFileProcessor(basePath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "It is invalid value of name[username] in section[account]: "
    }

    def "Fails due to no name[password]"() {
        given:
        def basePath = fileSystem.getPath("/")
        def filePath = basePath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username=anonymous
        password=
        """)

        when:
        def processor = new ConfigurationFileProcessor(basePath)
        processor.process(ProcessContext.create())

        then:
        def e = thrown(InvalidConfigurationFileException)
        e.message == "It is invalid value of name[password] in section[account]: "
    }

    def "Processes configuration file"() {
        given:
        def basePath = fileSystem.getPath("/")
        def filePath = basePath.resolve("config.ini")
        Files.writeString(filePath, """
        [account]
        username = anonymous
        password = encrypted
        """)

        when:
        def processor = new ConfigurationFileProcessor(basePath)
        def authentication = processor.process(ProcessContext.create())

        then:
        authentication.username == "anonymous"
        authentication.password == "encrypted"
    }

}
