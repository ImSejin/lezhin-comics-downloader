package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.dl.lezhin.attribute.impl.Content
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Artist
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Display
import io.github.imsejin.dl.lezhin.process.ProcessContext
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.TempDir

import java.nio.file.Path

@Subject(DirectoryCreationProcessor)
class DirectoryCreationProcessorSpec extends Specification {

    @TempDir
    private Path basePath

    private ProcessContext context

    void setup() {
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
        when:
        def processor = new DirectoryCreationProcessor(basePath)
        def directoryPath = processor.process(context)

        then:
        directoryPath.value == basePath.resolve("L_Foo Bar - Foo, Bar")
    }

}
