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

package io.github.imsejin.lzcodl;

import io.github.imsejin.common.constant.OS;

import java.net.URL;

public final class TestUtils {

    public static URL getDriverPath() {
        OS os = OS.getCurrentOS();

        switch (os) {
            case LINUX:
                return Thread.currentThread().getContextClassLoader()
                        .getResource("chrome-driver/92.0.4515.107/linux/chromedriver");
            case MAC:
                return Thread.currentThread().getContextClassLoader()
                        .getResource("chrome-driver/92.0.4515.107/mac/chromedriver");
            case WINDOWS:
                return Thread.currentThread().getContextClassLoader()
                        .getResource("chrome-driver/92.0.4515.107/windows/chromedriver.exe");
            default:
                throw new UnsupportedOperationException("Unsupported operating system: " + os.name().toLowerCase());
        }
    }

}
