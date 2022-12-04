package io.github.imsejin.dl.lezhin.process.impl

import io.github.imsejin.common.util.JsonUtils
import io.github.imsejin.dl.lezhin.attribute.impl.Content
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Artist
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Display
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Properties
import spock.lang.Specification

import static java.util.stream.Collectors.joining

class ContentInformationProcessorSpec extends Specification {

    def "test"() {
        given:
        def inputStream = Thread.currentThread().contextClassLoader
                .getResourceAsStream("json/ko-christmas_in_the_elevator.json")

        when:
        def jsonString = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(joining())
        def actual = JsonUtils.toObject(jsonString, Content)

        then:
        def expected = new Content(
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

        actual == expected
    }

}
