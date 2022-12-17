package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.dl.lezhin.argument.impl.Language
import io.github.imsejin.dl.lezhin.browser.WebBrowser
import io.github.imsejin.dl.lezhin.process.ProcessContext
import org.mockito.MockedStatic
import spock.lang.Specification
import spock.lang.Subject

import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.when

@Subject(LocaleSelectionProcessor)
class LocaleSelectionProcessorSpec extends Specification {

    private ProcessContext context

    private MockedStatic<WebBrowser> webBrowser

    void setup() {
        webBrowser = mockStatic(WebBrowser)
        context = ProcessContext.create()
        context.add(new Language(value: "en"))
    }

    void cleanup() {
        webBrowser.close()
    }

    // -------------------------------------------------------------------------------------------------

    def "Succeeds"() {
        given:
        with(WebBrowser) {
            when(request("/en/locale/en-US")).then({})
        }

        when:
        def processor = new LocaleSelectionProcessor()
        processor.process(context)

        then:
        noExceptionThrown()
    }

}
