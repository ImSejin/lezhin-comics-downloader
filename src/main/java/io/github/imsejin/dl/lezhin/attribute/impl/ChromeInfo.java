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

package io.github.imsejin.dl.lezhin.attribute.impl;

import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import io.github.imsejin.dl.lezhin.attribute.Attribute;
import io.github.imsejin.dl.lezhin.browser.ChromeVersion;
import io.github.imsejin.dl.lezhin.process.impl.ChromeDriverDownloadProcessor;

/**
 * Container for information of resolved chrome
 *
 * @since 4.0.0
 * @see ChromeDriverDownloadProcessor
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChromeInfo implements Attribute {

    /**
     * Chrome browser version.
     * <p>
     * It is not required to download chromedriver, but useful to find the fit version of that.
     */
    @Nullable
    private final ChromeVersion browserVersion;

    /**
     * Chromedriver file path.
     * <p>
     * It is not required to download new chromedriver, but required to resolve the version of that.
     */
    @Nullable
    private final Path driverPath;

    /**
     * Chromedriver version.
     * <p>
     * It is required to download new chromedriver.
     */
    @Nullable
    private final ChromeVersion driverVersion;

    public static ChromeInfo ofBrowser(@Nullable ChromeVersion browserVersion) {
        return new ChromeInfo(browserVersion, null, null);
    }

    public static ChromeInfo ofDriverPath(@Nullable ChromeVersion browserVersion, @NonNull Path driverPath) {
        return new ChromeInfo(browserVersion, driverPath, null);
    }

    public static ChromeInfo ofDriver(
            @Nullable ChromeVersion browserVersion,
            @NonNull Path driverPath,
            @NonNull ChromeVersion driverVersion
    ) {
        return new ChromeInfo(browserVersion, driverPath, driverVersion);
    }

}
