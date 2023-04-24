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
import io.github.imsejin.dl.lezhin.argument.impl.ContentName;
import io.github.imsejin.dl.lezhin.argument.impl.DebugMode;
import io.github.imsejin.dl.lezhin.argument.impl.EpisodeRange;
import io.github.imsejin.dl.lezhin.argument.impl.ImageFormat;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.attribute.Attribute;
import io.github.imsejin.dl.lezhin.attribute.impl.AccessToken;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import io.github.imsejin.dl.lezhin.attribute.impl.Content;
import io.github.imsejin.dl.lezhin.attribute.impl.DirectoryPath;
import io.github.imsejin.dl.lezhin.attribute.impl.HttpHosts;
import io.github.imsejin.dl.lezhin.attribute.impl.PurchasedEpisodes;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * @since 3.0.0
 */
@Getter
@ToString
@ThreadSafe
@EqualsAndHashCode
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessContext {

    @VisibleForTesting
    static final List<Field> FIELDS = Arrays.stream(ProcessContext.class.getDeclaredFields())
            .filter(it -> Attribute.class.isAssignableFrom(it.getType()))
            .filter(it -> !Modifier.isStatic(it.getModifiers()))
            .collect(toUnmodifiableList());

    // From command line -------------------------------------------------------------------------------

    private Language language;

    private ContentName contentName;

    private EpisodeRange episodeRange;

    private ImageFormat imageFormat;

    private DebugMode debugMode;

    // From processors ---------------------------------------------------------------------------------

    private Authentication authentication;

    private HttpHosts httpHosts;

    private AccessToken accessToken;

    private Content content;

    private DirectoryPath directoryPath;

    private PurchasedEpisodes purchasedEpisodes;

    // -------------------------------------------------------------------------------------------------

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
    public static ProcessContext create(@Nullable Object... attributes) {
        ProcessContext context = new ProcessContext();

        if (hasNoAttribute(attributes)) {
            return context;
        }

        field_scope:
        for (Field field : FIELDS) {
            for (Object attribute : attributes) {
                if (attribute == null) {
                    continue;
                }

                if (field.getType().isInstance(attribute)) {
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
     * <p> New context is merged with given context and attributes. if they have the same type of attribute,
     * always overwrites one of the context with one of the attributes.
     *
     * @param context    process context
     * @param attributes attributes to overwrite the ones of the context
     * @return new context if given attributes have a non-null item
     */
    public static ProcessContext of(ProcessContext context, @Nullable Object... attributes) {
        if (hasNoAttribute(attributes)) {
            return context;
        }

        Object[] originAttributes = FIELDS.stream().map(it -> ReflectionUtils.getFieldValue(context, it)).toArray();

        // New attributes take precedence over the ones of context.
        Object[] prepended = ArrayUtils.prepend(originAttributes, attributes);
        return create(prepended);
    }

    /**
     * Adds the attributes.
     *
     * @param attributes attributes to overwrite the ones of the context
     */
    public void add(@Nullable Object... attributes) {
        if (hasNoAttribute(attributes)) {
            return;
        }

        Map<Field, Object> originAttributeMap = new HashMap<>();
        for (Field field : FIELDS) {
            Object originAttribute = ReflectionUtils.getFieldValue(this, field);
            originAttributeMap.put(field, originAttribute);
        }

        field_scope:
        for (Entry<Field, Object> entry : originAttributeMap.entrySet()) {
            for (Object attribute : attributes) {
                if (attribute == null) {
                    continue;
                }

                Field field = entry.getKey();
                if (!field.getType().isInstance(attribute)) {
                    continue;
                }

                // If a matched attribute is found and it is equal to the original attribute,
                // it is unnecessary to assign new attribute.
                Object originAttribute = entry.getValue();
                if (attribute.equals(originAttribute)) {
                    continue field_scope;
                }

                // New attributes take precedence over the ones of context.
                ReflectionUtils.setFieldValue(this, field, attribute);

                // Discards other attributes whose type is the same as the matched attribute.
                continue field_scope;
            }
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static boolean hasNoAttribute(@Nullable Object[] attributes) {
        if (ArrayUtils.isNullOrEmpty(attributes)) {
            return true;
        }

        if (Arrays.stream(attributes).allMatch(Objects::isNull)) {
            return true;
        }

        return false;
    }

}
