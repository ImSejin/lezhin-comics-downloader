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

import io.github.imsejin.dl.lezhin.attribute.Attribute;
import org.apache.commons.cli.Option;

/**
 * @since 3.1.0
 */
public class SingleThreading extends BooleanArgument implements Attribute {

    @Override
    protected Option getOption() {
        return Option.builder("s")
                .longOpt("single-threading")
                .optionalArg(true)
                .numberOfArgs(1)
                .valueSeparator()
                .argName("true/false")
                .desc("Download each image in the episode on single-thread")
                .build();
    }

}
