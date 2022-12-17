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

package io.github.imsejin.dl.lezhin.util;

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.common.util.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility for name of file.
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/fileio/naming-a-file">
 * Naming Files, Paths, and Namespaces on Windows</a>
 */
public final class FileNameUtils {

    private static final Map<Character, Character> REPLACEMENT_MAP = Map.ofEntries(
            Map.entry('<', '＜'),
            Map.entry('>', '＞'),
            Map.entry(':', '：'),
            Map.entry('"', '＂'),
            Map.entry('/', '／'),
            Map.entry('\\', '＼'),
            Map.entry('|', '｜'),
            Map.entry('?', '？'),
            Map.entry('*', '＊')
    );

    private static final Pattern WINDOWS_RESERVED_BASE_NAME_PATTERN = Pattern.compile(
            "CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9]", Pattern.CASE_INSENSITIVE);

    @ExcludeFromGeneratedJacocoReport
    private FileNameUtils() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    public static String replaceForbiddenCharacters(String fileName) {
        StringBuilder sb = new StringBuilder();
        int length = fileName.length();

        for (int i = 0; i < length; i++) {
            char c = fileName.charAt(i);

            // Printable ASCII characters
            Character replacement = REPLACEMENT_MAP.get(c);
            if (replacement != null) {
                sb.append(replacement);
                continue;
            }

            sb.append(c);
        }

        return sb.toString()
                .replaceAll("\\.{2,}+$", "…")
                .replaceAll("\\.$", "．");
    }

    public static String sanitize(String fileName) {
        StringBuilder baseNameBuilder = new StringBuilder();

        // Sanitizes base name first.
        String baseName = FilenameUtils.getBaseName(fileName).strip();

        for (int i = 0; i < baseName.length(); i++) {
            char c = baseName.charAt(i);

            // Non-printable characters: ASCII control characters (0-31)
            if (c < 32) {
                continue;
            }

            baseNameBuilder.append(c);
        }

        // Reserved base names on Windows
        if (WINDOWS_RESERVED_BASE_NAME_PATTERN.matcher(baseNameBuilder).matches()) {
            // Inserts a prefix into the base name.
            baseNameBuilder.insert(0, '_');
        }

        String extension = FilenameUtils.getExtension(fileName).strip();

        // When file name doesn't have extension.
        if (StringUtils.isNullOrEmpty(extension)) {
            return baseNameBuilder.toString();
        }

        // Sanitizes file extension.
        StringBuilder extensionBuilder = new StringBuilder();

        for (int i = 0; i < extension.length(); i++) {
            char c = extension.charAt(i);

            // Non-printable characters: ASCII control characters (0-31)
            if (c < 32) {
                continue;
            }

            extensionBuilder.append(c);
        }

        return baseNameBuilder.append('.').append(extensionBuilder).toString();
    }

}
