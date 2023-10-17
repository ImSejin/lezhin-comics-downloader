package io.github.imsejin.dl.lezhin.common

import java.time.Instant

import io.github.imsejin.dl.lezhin.api.auth.service.AuthorityService.AuthData
import io.github.imsejin.dl.lezhin.attribute.impl.Content
import io.github.imsejin.dl.lezhin.attribute.impl.Content.Episode
import spock.lang.Specification
import spock.lang.Subject

@Subject(ModelPropertyMapper)
class ModelPropertyMapperSpec extends Specification {

    def "Converts into ServiceRequest"() {
        given:
        def content = new Content(id: 4733152235356160)
        def episode = new Episode(id: 5598233223495680)

        when:
        def serviceRequest = ModelPropertyMapper.INSTANCE.toServiceRequest(content, episode, false)

        then:
        serviceRequest
        serviceRequest.contentId == 4733152235356160
        serviceRequest.episodeId == 5598233223495680
        !serviceRequest.purchased
        serviceRequest.q == 30
        serviceRequest.firstCheckType == 'P'
    }

    def "Converts into Authority"() {
        given:
        def authData = new AuthData(
                policy: "eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vcmNkbi5sZXpoaW4uY29tL3YyLyovMjc3L2VwaXNvZGVzLzYwNjYyMzc4MDk4ODUxODQvY29udGVudHMvKnB1cmNoYXNlZD1mYWxzZSpxPTMwKiIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTY3MDk0NTEwNn0sIklwQWRkcmVzcyI6eyJBV1M6U291cmNlSXAiOiIwLjAuMC4wLzAifX19XX0_",
                signature: "eZ8hK44g2yE9WLmb6trm19rdMl2DJj21~P8OA0DYwrmLa4F-Q8BZia0tUYZTYuwHJInmHOfC42njtXJrN1UBZTEDCJdS0qlsV-7UjJAqH-1Ma8JhVkkK4rcZZPkWsAYBgjEQy-ycOx79qxVeyx7m5oHqEPJnVm9xDh5U7ZvkFFc-WnRarqpSfD8aMhP-p-laIeSzmqmoDP9zAsTVdfxaN0e~x35vPsDSXgPWKr8SJQKaCqNxVswwbt1QNFf2YyRCFnM-vPWhIij86B8xL2vW7sImmmK6F98rZ24BnQVuyTU9C1IHcjlMOczVdQwofuds94sdLcpl9CYRnczZhUClLg__",
                keyPairId: "B192C1A8G7FT0Z",
        )

        when:
        def authority = ModelPropertyMapper.INSTANCE.toAuthority(authData)

        then:
        authority
        authority.policy == authData.policy
        authority.signature == authData.signature
        authority.keyPairId == authData.keyPairId
        authority.contentId == 277
        authority.episodeId == 6066237809885184
        authority.expiredAt == Instant.ofEpochSecond(1670945106)
        authority.expired
    }

    def "Converts into HttpHosts"() {
        given:
        def config = [
                apiUrl        : "api.lezhin.com",
                cdnUrl        : "https://ccdn.lezhin.com",
                contentsCdnUrl: "https://rcdn.lezhin.com",
                recoUrl       : "dondog.lezhin.com",
                payUrl        : "https://pay.lezhin.com",
                pantherUrl    : "https://panther.lezhin.com",
        ]

        when:
        def httpHosts = ModelPropertyMapper.INSTANCE.toHttpHosts(config)

        then:
        httpHosts
        httpHosts.api == "https://api.lezhin.com/"
        httpHosts.cdn == "https://ccdn.lezhin.com/"
        httpHosts.contentsCdn == "https://rcdn.lezhin.com/"
        httpHosts.reco == "https://dondog.lezhin.com/"
        httpHosts.pay == "https://pay.lezhin.com/"
        httpHosts.panther == "https://panther.lezhin.com/"
    }

}
