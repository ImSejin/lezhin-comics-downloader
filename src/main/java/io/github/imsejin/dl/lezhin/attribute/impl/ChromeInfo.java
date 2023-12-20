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
public final class ChromeInfo implements Attribute {

    /**
     * Chrome browser version.
     * <p>
     * It is not required to download chromedriver, but useful to find the fit version of that.
     */
    @Nullable
    private final ChromeVersion browserVersion;

    /**
     * Chromedriver version.
     * <p>
     * It is required to download new chromedriver.
     */
    @Nullable
    private final ChromeVersion driverVersion;

    /**
     * Chromedriver file path.
     * <p>
     * It is not required to download new chromedriver, but required to resolve the version of that.
     */
    @NonNull
    private final Path driverPath;

    private final Status status;

    private ChromeInfo(
            @Nullable ChromeVersion browserVersion,
            @Nullable ChromeVersion driverVersion,
            @NonNull Path driverPath
    ) {
        this.browserVersion = browserVersion;
        this.driverVersion = driverVersion;
        this.driverPath = driverPath;

        int bits = 0;

        if (browserVersion != null) {
            bits |= Status.BROWSER_ONLY.getBits();
        }

        if (driverVersion != null) {
            bits |= Status.DRIVER_ONLY.getBits();
        }

        this.status = Status.from(bits);
    }

    public static Builder builder(@NonNull Path driverPath) {
        return new Builder(driverPath);
    }

    // -------------------------------------------------------------------------------------------------

    @RequiredArgsConstructor
    public static class Builder {
        @Nullable
        private ChromeVersion browserVersion;
        @Nullable
        private ChromeVersion driverVersion;
        @NonNull
        private final Path driverPath;

        public Builder browserVersion(@Nullable ChromeVersion browserVersion) {
            this.browserVersion = browserVersion;
            return this;
        }

        public Builder driverVersion(@Nullable ChromeVersion driverVersion) {
            this.driverVersion = driverVersion;
            return this;
        }

        public ChromeInfo build() {
            return new ChromeInfo(this.browserVersion, this.driverVersion, this.driverPath);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        NONE(0),
        DRIVER_ONLY(1),
        BROWSER_ONLY(2),
        ENTIRE(DRIVER_ONLY.bits | BROWSER_ONLY.bits),
        ;

        private final int bits;

        public static Status from(int bits) {
            for (Status status : values()) {
                if (status.bits == bits) {
                    return status;
                }
            }

            throw new IllegalArgumentException("Invalid ChromeInfo.Statue.bits: " + bits);
        }
    }

}
