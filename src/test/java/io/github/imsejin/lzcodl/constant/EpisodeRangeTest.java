/*
 * Copyright 2020 Sejin Im
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

package io.github.imsejin.lzcodl.constant;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.constant.EpisodeRange;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class EpisodeRangeTest {

    @ParameterizedTest
    @CsvSource({
            "1,1024", "5,27", "13,13", "200,3502",
    })
    void SOME(String start, String end) {
        // given
        String range = start + EpisodeRange.SEPARATOR + end;

        // when
        Pattern regex = Pattern.compile("([0-9]*)~([0-9]*)", Pattern.MULTILINE);
        Matcher matcher = regex.matcher(range);
        matcher.find();
        System.out.printf("group(): '%s'\n", matcher.group());
        System.out.printf("group(1): '%s'\n", matcher.group(1));
        System.out.printf("group(2): '%s'\n", matcher.group(2));
        Map<Integer, String> group = StringUtils.find(range, "^([0-9]*)~([0-9]*)$", Pattern.MULTILINE, 1, 2);

        // then
        assertThat(group).isNotNull().hasSize(2)
                .containsEntry(1, start)
                .containsEntry(2, end);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "5", "13", "200"})
    void START_POINT(String start) {
        // given
        String range = start + EpisodeRange.SEPARATOR;

        // when
        Map<Integer, String> group = StringUtils.find(range, "^([0-9]*)~([0-9]*)$", Pattern.MULTILINE, 1, 2);

        // then
        assertThat(group).isNotNull().hasSize(2)
                .containsEntry(1, start)
                .containsEntry(2, "");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1024", "27", "13", "3502"})
    void END_POINT(String end) {
        // given
        String range = EpisodeRange.SEPARATOR + end;

        // when
        Map<Integer, String> group = StringUtils.find(range, "^([0-9]*)~([0-9]*)$", Pattern.MULTILINE, 1, 2);

        // then
        assertThat(group).isNotNull().hasSize(2)
                .containsEntry(1, "")
                .containsEntry(2, end);
    }

}
