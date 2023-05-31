package io.github.imsejin.dl.lezhin.browser

import io.github.imsejin.dl.lezhin.exception.ChromeDriverNotFoundException
import io.github.imsejin.dl.lezhin.util.PathUtils
import org.mockito.MockedStatic
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.TempDir

import java.nio.file.Path

import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.when

@Subject(WebBrowser)
class WebBrowserSpec extends Specification {

    @TempDir
    private Path basePath

    private MockedStatic<PathUtils> pathUtils

    void setup() {
        pathUtils = mockStatic(PathUtils)
    }

    void cleanup() {
        pathUtils.close()
    }

    // -------------------------------------------------------------------------------------------------

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
        with(PathUtils) {
            when(getCurrentPath()).thenReturn(basePath)
        }

        when:
        WebBrowser.run()

        then:
        def e = thrown(ChromeDriverNotFoundException)
        e.message.startsWith("There is no chromedriver: ")
    }

    def "Checks if web browser is running"() {
        expect:
        !WebBrowser.running
    }

}
