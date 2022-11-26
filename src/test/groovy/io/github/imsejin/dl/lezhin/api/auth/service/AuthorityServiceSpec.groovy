/*
 * Copyright 2022 Sejin Im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.imsejin.dl.lezhin.api.auth.service

import io.github.imsejin.dl.lezhin.api.auth.model.AuthForViewEpisodeRequest
import spock.lang.Specification

class AuthorityServiceSpec extends Specification {

    def "Gets authority for viewing episode"() {
        given:
        def service = new AuthorityService(Locale.KOREA, new UUID(0, 0))

        when:
        // First episode doesn't need access token in HTTP header.
        // https://www.lezhin.com/ko/comic/bff/p1
        def request = new AuthForViewEpisodeRequest(contentId: 5474379383439360, episodeId: 6310446659534848, firstCheckType: 'P' as char)
        def response = service.getAuthForViewEpisode(request)

        then:
        response != null
        response.policy ==~ /[0-9A-Za-z]+/
        response.signature ==~ /[0-9A-Za-z_~-]+/
        response.keyPairId ==~ /[0-9A-Z]+/
    }

}
