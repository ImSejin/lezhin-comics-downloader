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
import io.github.imsejin.dl.lezhin.exception.DirectoryCreationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility for path
 */
public final class PathUtils {

    @ExcludeFromGeneratedJacocoReport
    private PathUtils() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    public static Path getCurrentPath() {
        try {
            return Path.of(".").toRealPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean createDirectoryIfNotExists(Path dir) throws DirectoryCreationException {
        if (Files.isDirectory(dir)) {
            return false;
        }

        try {
            Files.createDirectory(dir);
            return true;
        } catch (IOException e) {
            throw new DirectoryCreationException(e, "Failed to create directory: %s", dir);
        }
    }

}
