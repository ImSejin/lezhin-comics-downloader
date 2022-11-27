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

package io.github.imsejin.dl.lezhin.process;

import io.github.imsejin.common.util.ArrayUtils;
import io.github.imsejin.common.util.ReflectionUtils;
import io.github.imsejin.dl.lezhin.api.auth.model.Authority;
import io.github.imsejin.dl.lezhin.argument.impl.ContentName;
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode;
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.argument.impl.SaveAsJpeg;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessContext {

    private static final List<Field> FIELDS = Arrays.stream(ProcessContext.class.getDeclaredFields())
            .filter(it -> !Modifier.isStatic(it.getModifiers()))
            .collect(toUnmodifiableList());

    // From command line ---------------------------------------------------------------------

    private Language language;

    private ContentName contentName;

    private EpisodeRange episodeRange;

    private SaveAsJpeg saveAsJpeg;

    private DebugMode debugMode;

    // Arguments from command line ---------------------------------------------------------------------

    private UUID accessToken;

    private Authority authority;

    /**
     * Creates new instance.
     *
     * <p> There are some careful points. First, if the attributes have {@code null} element, it is ignored.
     * Second, an attribute that context doesn't know is discarded — in other words, the attribute which
     * doesn't match one of the fields in {@link ProcessContext} doesn't be assigned. Third, when an attribute
     * matches one of the fields, the attribute is assigned to new instance but other attributes whose type is
     * the same as that attribute is discarded.
     *
     * @param attributes attributes
     * @return new context
     */
    public static ProcessContext create(Object... attributes) {
        ProcessContext context = new ProcessContext();

        if (ArrayUtils.isNullOrEmpty(attributes)) {
            return context;
        }

        outer:
        for (Field field : FIELDS) {
            for (Object attribute : attributes) {
                if (attribute == null) {
                    continue;
                }

                if (field.getType().isAssignableFrom(attribute.getClass())) {
                    ReflectionUtils.setFieldValue(context, field, attribute);

                    // Discards other attributes whose type is the same as the matched attribute.
                    continue outer;
                }
            }
        }

        return context;
    }

    public static ProcessContext of(ProcessContext context, Object... attributes) {
        if (ArrayUtils.isNullOrEmpty(attributes)) {
            return context;
        }

        if (Arrays.stream(attributes).allMatch(Objects::isNull)) {
            return context;
        }

        Object[] originAttributes = FIELDS.stream().map(it -> ReflectionUtils.getFieldValue(context, it)).toArray();

        // New attributes take precedence over the attributes of context.
        Object[] prepended = ArrayUtils.prepend(originAttributes, attributes);
        return create(prepended);
    }

}
