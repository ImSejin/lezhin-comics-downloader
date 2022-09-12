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

package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.exception.EpisodeRangeParseException;
import io.github.imsejin.lzcodl.model.Arguments;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @since 2.7.1
 */
@ToString
@RequiredArgsConstructor
public enum EpisodeRange {

    ALL() {
        @Override
        public int[] getArray(Arguments args) {
            int end = args.getProduct().getEpisodes().size();
            return IntStream.range(0, end).toArray(); // [0 ~ last]
        }
    },

    SOME() {
        @Override
        public int[] getArray(Arguments args) {
            String[] parsed = parse(args.getEpisodeRange());
            int start = Integer.parseInt(parsed[0]) - 1; // For index.

            // 해당 웹툰의 마지막 에피소드 번호를 초과하는 에피소드 번호를 지정하면,
            // 마지막 에피소드까지 다운로드하는 것으로 변경한다.
            int numOfEpisodes = args.getProduct().getEpisodes().size();
            int end = Math.min(Integer.parseInt(parsed[1]), numOfEpisodes);

            return IntStream.range(start, end).toArray(); // [m ~ n]
        }
    },

    START_POINT() {
        @Override
        public int[] getArray(Arguments args) {
            String[] parsed = parse(args.getEpisodeRange());
            int start = Integer.parseInt(parsed[0]) - 1; // For index.
            int end = args.getProduct().getEpisodes().size();

            return IntStream.range(start, end).toArray(); // [n ~ last]
        }
    },

    END_POINT() {
        @Override
        public int[] getArray(Arguments args) {
            String[] parsed = parse(args.getEpisodeRange());

            // 해당 웹툰의 마지막 에피소드 번호를 초과하는 에피소드 번호를 지정하면,
            // 마지막 에피소드까지 다운로드하는 것으로 변경한다.
            int numOfEpisodes = args.getProduct().getEpisodes().size();
            int end = Math.min(Integer.parseInt(parsed[1]), numOfEpisodes);

            return IntStream.range(0, end).toArray(); // [0 ~ n]
        }
    };

    /**
     * Delimiter of episode number.
     */
    public static final String DELIMITER = "~";

    private static final Pattern RANGE_REGEX = Pattern.compile("^([0-9]*)" + DELIMITER + "([0-9]*)$");

    public static boolean invalidate(String range) {
        return !StringUtils.isNullOrBlank(range) && !RANGE_REGEX.matcher(range).find();
    }

    private static String[] parse(String range) {
        Matcher matcher = RANGE_REGEX.matcher(range);

        Asserts.that(range)
                .describedAs("Invalid episode range: '{0}'", range)
                .thrownBy(EpisodeRangeParseException::new)
                .isNotNull()
                .hasText()
                .returns(matcher.find(), it -> RANGE_REGEX.matcher(it).matches());

        String start = matcher.group(1);
        String end = matcher.group(2);

        return new String[]{start, end};
    }

    /**
     * @param range stringified episode range
     * @return episode range
     * @throws EpisodeRangeParseException if range is invalid
     */
    public static EpisodeRange from(String range) {
        if (StringUtils.isNullOrBlank(range)) return ALL;

        String[] parsed = parse(range);
        String start = parsed[0];
        String end = parsed[1];

        if (!StringUtils.isNullOrEmpty(start) && !StringUtils.isNullOrEmpty(end)) {
            return SOME;
        } else if (!StringUtils.isNullOrEmpty(start) && StringUtils.isNullOrEmpty(end)) {
            return START_POINT;
        } else if (StringUtils.isNullOrEmpty(start) && !StringUtils.isNullOrEmpty(end)) {
            return END_POINT;
        } else {
            return ALL;
        }
    }

    /**
     * @param args arguments
     * @return range array
     * @throws EpisodeRangeParseException if range is invalid
     */
    public abstract int[] getArray(Arguments args);

}
