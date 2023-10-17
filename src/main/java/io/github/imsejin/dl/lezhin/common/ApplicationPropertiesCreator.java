/*
 * Copyright 2023 Sejin Im
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

package io.github.imsejin.dl.lezhin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.VisibleForTesting;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.exception.ApplicationPropertiesCreationException;

/**
 * @since 4.0.0
 */
public class ApplicationPropertiesCreator {

    public ApplicationProperties create(InputStream inputStream) throws ApplicationPropertiesCreationException {
        Map<String, String> props = parse(inputStream);
        return ModelPropertyMapper.INSTANCE.toApplicationProperties(props);
    }

    @VisibleForTesting
    Map<String, String> parse(InputStream inputStream) throws ApplicationPropertiesCreationException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Map<String, String> props = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Ignores comment.
                if (line.startsWith("#")) {
                    continue;
                }

                // Ignores invalid line as a property.
                if (!line.contains("=")) {
                    continue;
                }

                String[] entry = line.split("=", 2);
                String key = entry[0].trim();
                String value = entry.length > 1 ? entry[1].trim() : "";

                // Ignores a property with no key.
                if (StringUtils.isNullOrBlank(key)) {
                    continue;
                }

                // Makes mapstruct generate.
                if (key.contains(".")) {
                    key = key.replace('.', '/');
                }

                props.put(key, value);
            }

            return Collections.unmodifiableMap(props);

        } catch (IOException e) {
            throw new ApplicationPropertiesCreationException(e, "Failed to create application properties");
        }
    }

}
