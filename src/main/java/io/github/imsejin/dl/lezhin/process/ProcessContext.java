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
import io.github.imsejin.dl.lezhin.attribute.Attribute;
import io.github.imsejin.dl.lezhin.attribute.impl.AccessToken;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@ToString
@ThreadSafe
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessContext {

    private static final List<Field> FIELDS = Arrays.stream(ProcessContext.class.getDeclaredFields())
            .filter(it -> Attribute.class.isAssignableFrom(it.getType()))
            .filter(it -> !Modifier.isStatic(it.getModifiers()))
            .collect(toUnmodifiableList());

    // From command line -------------------------------------------------------------------------------

    private Language language;

    private ContentName contentName;

    private EpisodeRange episodeRange;

    private SaveAsJpeg saveAsJpeg;

    private DebugMode debugMode;

    // From processors ---------------------------------------------------------------------------------

    private Authentication authentication;

    private AccessToken accessToken;

    private Authority authority;

    /**
     * Creates new instance.
     *
     * <p> If the attributes is null or empty, returns an empty instance.
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

        field_scope:
        for (Field field : FIELDS) {
            for (Object attribute : attributes) {
                if (attribute == null) {
                    continue;
                }

                if (field.getType().isAssignableFrom(attribute.getClass())) {
                    ReflectionUtils.setFieldValue(context, field, attribute);

                    // Discards other attributes whose type is the same as the matched attribute.
                    continue field_scope;
                }
            }
        }

        return context;
    }

    /**
     * Returns an instance.
     *
     * <p> If the attributes are null or empty or have non-null one, returns new context.
     * Otherwise returns the given context as it is.
     *
     * <p> A new context is merged with given context and attributes. if they have the same type of attribute,
     * always overwrites one of the context with one of the attributes.
     *
     * @param context    process context
     * @param attributes attributes
     * @return new context if given attributes have a valid item
     */
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
