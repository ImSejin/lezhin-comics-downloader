/*
 * Copyright 2021 Sejin Im
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

package io.github.imsejin.lzcodl.core;

import lombok.Cleanup;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReaderTest {

    @Test
    void readPomXML() throws IOException {
        // given
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String filename = "application.properties";

        // when
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(
                classLoader.getResourceAsStream(filename), StandardCharsets.UTF_8));
        Properties properties = reader.lines().filter(it -> !it.startsWith("#"))
                .map(it -> it.split(" ?= ?"))
                .collect(Properties::new, (props, arr) -> props.put(arr[0], arr[1]), (props, arr) -> {
                });

        // then
        assertThat(properties)
                .hasSizeGreaterThan(0)
                .containsKey("version");
        assertThat(properties.getProperty("version"))
                .isNotNull()
                .isNotBlank()
                .matches("[0-9]+\\.[0-9]+\\.[0-9]+");
    }

}
