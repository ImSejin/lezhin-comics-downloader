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

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.dl.lezhin.argument.Argument;
import io.github.imsejin.dl.lezhin.attribute.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.cli.Option;

/**
 * Webtoon name
 *
 * @since 3.0.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ContentName extends Argument implements Attribute {

    private String value;

    @Override
    protected Option getOption() {
        return Option.builder("n")
                .longOpt("name")
                .desc("webtoon name you want to download")
                .required()
                .hasArg()
                .valueSeparator()
                .argName("webtoon_name")
                .build();
    }

    @Override
    protected void validate(String value) {
        Asserts.that(value)
                .describedAs("Invalid ContentName.value: {0}", value)
                .isNotNull()
                .hasText();
    }

}
