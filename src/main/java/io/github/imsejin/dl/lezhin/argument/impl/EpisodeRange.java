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

package io.github.imsejin.dl.lezhin.argument.impl;

import io.github.imsejin.dl.lezhin.argument.Argument;
import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.cli.Option;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class EpisodeRange extends Argument implements Attribute {

    private static final Pattern PATTERN = Pattern.compile("\\d+|\\d+~(\\d+)?|(\\d+)?~\\d+");

    private Integer startNumber;

    private Integer endNumber;

    private RangeType rangeType;

    @Override
    public String getValue() {
        if (this.startNumber == null && this.endNumber == null) {
            return "*";
        }

        if (Objects.equals(this.startNumber, this.endNumber)) {
            return String.valueOf(this.startNumber);
        }

        Object startNumber = Objects.requireNonNullElse(this.startNumber, "");
        Object endNumber = Objects.requireNonNullElse(this.endNumber, "");

        return startNumber + "~" + endNumber;
    }

    // -------------------------------------------------------------------------------------------------

    @Override
    protected Option getOption() {
        return Option.builder("r")
                .longOpt("range")
                .desc("Range of episodes you want to download")
                .hasArg()
                .valueSeparator()
                .argName("episode_range")
                .build();
    }

    @Override
    protected void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid EpisodeRange.value: null");
        }

        if (value.isEmpty()) {
            return;
        }

        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid EpisodeRange.value: " + value);
        }
    }

    @Override
    protected void setValue(String value) {
        for (RangeType rangeType : RangeType.values()) {
            if (rangeType.supports(value)) {
                rangeType.parse(value).accept(this);
                return;
            }
        }
    }

    // -------------------------------------------------------------------------------------------------

    public enum RangeType {
        ALL {
            @Override
            boolean supports(String value) {
                return value.isEmpty();
            }

            @Override
            Consumer<EpisodeRange> parse(String value) {
                return it -> it.rangeType = this;
            }
        },

        ONE {
            @Override
            boolean supports(String value) {
                return !value.contains("~");
            }

            @Override
            Consumer<EpisodeRange> parse(String value) {
                return it -> {
                    int number = Integer.parseInt(value);
                    it.startNumber = number;
                    it.endNumber = number;
                    it.rangeType = this;
                };
            }
        },

        TO_END {
            @Override
            boolean supports(String value) {
                return value.endsWith("~");
            }

            @Override
            Consumer<EpisodeRange> parse(String value) {
                return it -> {
                    String[] numbers = value.split("~");
                    it.startNumber = Integer.parseInt(numbers[0]);
                    it.rangeType = this;
                };
            }
        },

        FROM_BEGINNING {
            @Override
            boolean supports(String value) {
                return value.startsWith("~");
            }

            @Override
            Consumer<EpisodeRange> parse(String value) {
                return it -> {
                    String[] numbers = value.split("~");
                    it.endNumber = Integer.parseInt(numbers[1]);
                    it.rangeType = this;
                };
            }
        },

        SOME {
            @Override
            boolean supports(String value) {
                return value.matches("\\d+~\\d+");
            }

            @Override
            Consumer<EpisodeRange> parse(String value) {
                return it -> {
                    String[] numbers = value.split("~");
                    int startNumber = Integer.parseInt(numbers[0]);
                    int endNumber = Integer.parseInt(numbers[1]);

                    if (startNumber == endNumber) {
                        it.startNumber = startNumber;
                        it.endNumber = startNumber;
                        it.rangeType = ONE;
                        return;
                    }

                    it.startNumber = startNumber;
                    it.endNumber = endNumber;
                    it.rangeType = this;
                };
            }
        };

        abstract boolean supports(String value);

        abstract Consumer<EpisodeRange> parse(String value);
    }

}
