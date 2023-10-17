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

package io.github.imsejin.dl.lezhin.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;

import static java.util.stream.Collectors.*;

/**
 * Utility for command to OS.
 */
public final class CommandUtils {

    @ExcludeFromGeneratedJacocoReport
    private CommandUtils() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    public static String runCommand(String... command) throws Exception {
        Process process = new ProcessBuilder()
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .command(command)
                .start();

        process.onExit().get();
        int exitValue = process.exitValue();

        try (InputStream in = exitValue == 0 ? process.getInputStream() : process.getErrorStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(joining("\n")).trim();
        }
    }

}
