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
import lombok.ToString;
import org.apache.commons.cli.Option;

/**
 * @since 3.0.0
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ImageFormat extends Argument implements Attribute {

    private static final String DEFAULT_VALUE = "webp";

    private String value = DEFAULT_VALUE;

    @Override
    protected Option getOption() {
        return Option.builder("j")
                .longOpt("jpg")
                .desc("Save images as JPEG format (default: WEBP format)")
                .build();
    }

    @Override
    protected void validate(String value) {
        Asserts.that(value)
                .describedAs("Invalid ImageFormat.value: {0}", value)
                .isNotNull()
                .isNotEmpty()
                .matches("true|false");
    }

    @Override
    protected void setValue(String value) {
        switch (value) {
            case "true":
                this.value = "jpg";
                break;
            case "false":
                this.value = DEFAULT_VALUE;
                break;
        }
    }

}
