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

package io.github.imsejin.dl.lezhin.argument.impl

import spock.lang.Specification

import static io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange.RangeType.ALL
import static io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange.RangeType.FROM_BEGINNING
import static io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange.RangeType.ONE
import static io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange.RangeType.SOME
import static io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange.RangeType.TO_END

class EpisodeRangeSpec extends Specification {

    def "Sets the value"() {
        given:
        def episodeRange = new EpisodeRange()

        when:
        episodeRange.value = value

        then:
        episodeRange.startNumber == startNumber
        episodeRange.endNumber == endNumber
        episodeRange.rangeType == rangeType

        where:
        value   || startNumber | endNumber | rangeType
        ""      || null        | null      | ALL
        "12"    || 12          | 12        | ONE
        "2~2"   || 2           | 2         | ONE
        "8~"    || 8           | null      | TO_END
        "109~"  || 109         | null      | TO_END
        "~2"    || null        | 2         | FROM_BEGINNING
        "~25"   || null        | 25        | FROM_BEGINNING
        "1~10"  || 1           | 10        | SOME
        "29~30" || 29          | 30        | SOME
    }

    def "Gets the value"() {
        given:
        def episodeRange = new EpisodeRange()

        when:
        episodeRange.value = value
        def actual = episodeRange.value

        then:
        actual == expected

        where:
        value   | expected
        ""      | "*"
        "12"    | value
        "2~2"   | "2"
        "8~"    | value
        "109~"  | value
        "~2"    | value
        "~25"   | value
        "1~10"  | value
        "29~30" | value
    }

}
