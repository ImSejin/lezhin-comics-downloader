package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.dl.lezhin.argument.impl.ContentName
import io.github.imsejin.dl.lezhin.argument.impl.Language
import io.github.imsejin.dl.lezhin.attribute.impl.Content
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Artist
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Display
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Properties
import io.github.imsejin.dl.lezhin.browser.WebBrowser
import io.github.imsejin.dl.lezhin.http.url.URIs
import io.github.imsejin.dl.lezhin.process.ProcessContext
import org.mockito.MockedStatic
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.StandardCharsets

import static java.util.stream.Collectors.joining
import static org.mockito.Mockito.mockStatic
import static org.mockito.Mockito.when

@Subject(ContentInformationProcessor)
class ContentInformationProcessorSpec extends Specification {

    private ProcessContext context

    private MockedStatic<WebBrowser> webBrowser

    void setup() {
        webBrowser = mockStatic(WebBrowser)
        context = ProcessContext.create()
        context.add(new Language(value: "en"), new ContentName(value: "foo-bar"))
    }

    void cleanup() {
        webBrowser.close()
    }

    // -------------------------------------------------------------------------------------------------

    def "Succeeds"() {
        given:
        with(WebBrowser) {
            def contentPath = URIs.CONTENT.get(context.language.value.language, context.contentName.value);
            when(getCurrentUrl()).then { contentPath }
            when(evaluate("JSON.stringify(window.__LZ_PRODUCT__.product)", String.class)).then {
                def inputStream = Thread.currentThread().contextClassLoader
                        .getResourceAsStream("json/ko-christmas_in_the_elevator.json")
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(joining())
            }
        }

        when:
        def processor = new ContentInformationProcessor()
        def content = processor.process(context)

        then:
        content == new Content(
                id: 5943773066559488,
                alias: "christmas_in_the_elevator",
                display: new Display(title: "엘리베이터에서 하룻밤", synopsis: "계약 만료를 앞둔 계약직 말단 사원 이도는 크리스마스 이브 날 잡힌 회식이 괜시리 쓸쓸하기만 하다. 하지만 크리스마스에 눈이 온다면, 그리고 사랑이 이루어진다면...? 세상에 수많은 if가 있지만 이보다 더 로맨틱할 순 없다. 김지효 작가가 그리는 뜻밖의 크리스마스 러브 스토리 <엘리베이터에서 하룻밤>"),
                properties: new Properties(expired: false, notForSale: false),
                locale: "ko-KR",
                state: "completed",
                artists: [new Artist(id: "kim_ji_hyo", name: "김지효", role: "writer")],
                episodes: [
                        new Episode(
                                id: 5156581780094976,
                                name: "e1",
                                seq: 5,
                                display: new Display(title: "에필로그"),
                                properties: new Properties(expired: false, notForSale: false),
                                coin: 0,
                                updatedAt: 1512042698558,
                                publishedAt: 1514127600000,
                                freedAt: 1514127600000,
                        ),
                        new Episode(
                                id: 5313384744615936,
                                name: "3",
                                seq: 4,
                                display: new Display(title: "메리크리스마스"),
                                properties: new Properties(expired: false, notForSale: false),
                                coin: 3,
                                updatedAt: 1512042687123,
                                publishedAt: 1513522800000,
                        ),
                        new Episode(
                                id: 5176088900796416,
                                name: "2",
                                seq: 3,
                                display: new Display(title: "전야제"),
                                properties: new Properties(expired: false, notForSale: false),
                                coin: 3,
                                updatedAt: 1512042678946,
                                publishedAt: 1512918000000,
                        ),
                        new Episode(
                                id: 5396364351635456,
                                name: "1",
                                seq: 2,
                                display: new Display(title: "회식"),
                                properties: new Properties(expired: false, notForSale: false),
                                coin: 3,
                                updatedAt: 1512285305534,
                                publishedAt: 1512313200000,
                        ),
                        new Episode(
                                id: 5668435598114816,
                                name: "p1",
                                seq: 1,
                                display: new Display(title: "프롤로그"),
                                properties: new Properties(expired: false, notForSale: false),
                                coin: 0,
                                updatedAt: 1512285078479,
                                publishedAt: 1512313200000,
                                freedAt: 1512313200000,
                        ),
                ],
        )
    }

    def "Succeeds with expired content"() {
        given:
        with(WebBrowser) {
            def expirationPath = URIs.EXPIRATION.get(context.language.value.language);
            when(getCurrentUrl()).then { expirationPath }
            when(evaluate("JSON.stringify(window.__LZ_PRODUCT__.product)", String.class)).then {
                """
                {
                  "id": 8235442190235440,
                  "display": {
                    "title": "Foo Bar",
                    "schedule": "완결",
                    "synopsis": "..."
                  },
                  "properties": {
                    "expired": true,
                    "notForSale": false
                  },
                  "artists": [
                    {
                      "id": "anonymous",
                      "name": "익명",
                      "role": "writer",
                      "email": null
                    }
                  ],
                  "alias": "foo_bar",
                  "state": "completed",
                  "locale": "en-US",
                  "episodes": [
                    {
                      "id": 8723552344826203,
                      "name": "p1",
                      "display": {
                        "title": "프롤로그",
                        "type": "p",
                        "displayName": "프롤로그"
                      },
                      "properties": {
                        "expired": true,
                        "notForSale": false
                      },
                      "coin": 3,
                      "freedAt": 1514127600000,
                      "openedAt": 1514127600000,
                      "publishedAt": 1514127600000,
                      "updatedAt": 1512042698558,
                      "seq": 1
                    }
                  ]
                }
                """
            }
        }

        when:
        def processor = new ContentInformationProcessor()
        def content = processor.process(context)

        then:
        content == new Content(
                id: 8235442190235440,
                alias: "foo_bar",
                display: new Display(title: "Foo Bar", synopsis: "..."),
                properties: new Properties(expired: true, notForSale: false),
                locale: "en-US",
                state: "completed",
                artists: [new Artist(id: "anonymous", name: "익명", role: "writer")],
                episodes: [
                        new Episode(
                                id: 8723552344826203,
                                name: "p1",
                                seq: 1,
                                display: new Display(title: "프롤로그"),
                                properties: new Properties(expired: true, notForSale: false),
                                coin: 3,
                                updatedAt: 1512042698558,
                                publishedAt: 1514127600000,
                                freedAt: 1514127600000,
                        ),
                ],
        )
    }

}
