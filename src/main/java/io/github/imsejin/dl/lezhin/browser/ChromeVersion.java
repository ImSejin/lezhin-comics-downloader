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

package io.github.imsejin.dl.lezhin.browser;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import io.github.imsejin.common.util.StringUtils;

/**
 * @since 4.0.0
 */
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class ChromeVersion {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+\\.0(\\.\\d+){2}");

    @EqualsAndHashCode.Include
    private final String value;

    private ChromeVersion(String value) {
        if (StringUtils.isNullOrBlank(value) || !VERSION_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid ChromeVersion.value: " + value);
        }

        this.value = value;
    }

    public static ChromeVersion from(String versionString) {
        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        if (matcher.find()) {
            return new ChromeVersion(matcher.group());
        }

        throw new IllegalArgumentException("Invalid ChromeVersion.value: " + versionString);
    }

    public boolean isCompatibleWith(@Nullable ChromeVersion other) {
        if (other == null) {
            return false;
        }

        String thisMajorVersion = new StringTokenizer(this.value, ".").nextToken();
        String otherMajorVersion = new StringTokenizer(other.value, ".").nextToken();

        return Objects.equals(thisMajorVersion, otherMajorVersion);
    }

}
