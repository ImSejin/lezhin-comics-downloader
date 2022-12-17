package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.dl.lezhin.attribute.impl.AccessToken
import io.github.imsejin.dl.lezhin.browser.WebBrowser
import io.github.imsejin.dl.lezhin.exception.AccessTokenNotFoundException
import io.github.imsejin.dl.lezhin.process.ProcessContext
import org.mockito.MockedStatic
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import spock.lang.Specification
import spock.lang.Subject

import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.when

@Subject(AccessTokenProcessor)
class AccessTokenProcessorSpec extends Specification {

    private ProcessContext context

    private MockedStatic<WebBrowser> webBrowser

    void setup() {
        webBrowser = mockStatic(WebBrowser)
        context = ProcessContext.create()
    }

    void cleanup() {
        webBrowser.close()
    }

    // -------------------------------------------------------------------------------------------------

    def "Succeeds"() {
        given:
        with(WebBrowser) {
            when(waitForPresenceOfElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_ME__')]")))
                    .thenReturn(_ as WebElement)
            when(evaluate("window.__LZ_CONFIG__?.token", String))
                    .thenReturn("ab585aaf-3379-488a-a93e-8658145ff715")
        }

        when:
        def processor = new AccessTokenProcessor()
        def accessToken = processor.process(context)

        then:
        accessToken == new AccessToken("ab585aaf-3379-488a-a93e-8658145ff715")
    }

    def "Fails due to waiting for element"() {
        given:
        with(WebBrowser) {
            when(waitForPresenceOfElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_ME__')]")))
                    .thenThrow(TimeoutException)
        }

        when:
        def processor = new AccessTokenProcessor()
        processor.process(context)

        then:
        def e = thrown(AccessTokenNotFoundException)
        e.message == "There is no access token"
    }

    def "Fails due to invalid token value"() {
        given:
        with(WebBrowser) {
            when(waitForPresenceOfElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_ME__')]")))
                    .thenReturn(_ as WebElement)
            when(evaluate("window.__LZ_CONFIG__?.token", String))
                    .thenReturn("ab585aaf3379488aa93e8658145ff715")
        }

        when:
        def processor = new AccessTokenProcessor()
        processor.process(context)

        then:
        def e = thrown(AccessTokenNotFoundException)
        e.message == "Invalid access token: ab585aaf3379488aa93e8658145ff715"
    }

}
