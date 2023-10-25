package io.github.imsejin.dl.lezhin.process.impl

import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.FileSystem
import java.nio.file.attribute.PosixFileAttributeView

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder

import io.github.imsejin.dl.lezhin.attribute.impl.Content
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Artist
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Display
import io.github.imsejin.dl.lezhin.process.ProcessContext

@Subject(DirectoryCreationProcessor)
class DirectoryCreationProcessorSpec extends Specification {

    private FileSystem fileSystem
    private ProcessContext context

    void setup() {
        fileSystem = MemoryFileSystemBuilder.newEmpty()
                .addFileAttributeView(PosixFileAttributeView)
                .build()
        context = ProcessContext.create()
        context.add(new Content(
                display: new Display(title: "Foo Bar", synopsis: "..."),
                artists: [
                        new Artist(id: "foo", name: "Foo", role: "writer"),
                        new Artist(id: "bar", name: "Bar", role: "illustrator"),
                ],
        ))
    }

    // -------------------------------------------------------------------------------------------------

    def "Succeeds"() {
        given:
        def basePath = fileSystem.getPath("/")

        when:
        def processor = new DirectoryCreationProcessor(basePath)
        def directoryPath = processor.process(context)

        then:
        directoryPath.value == basePath.resolve("L_Foo Bar - Foo, Bar")
    }

}
