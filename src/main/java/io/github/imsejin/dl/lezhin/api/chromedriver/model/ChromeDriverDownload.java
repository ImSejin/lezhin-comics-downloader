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

package io.github.imsejin.dl.lezhin.api.chromedriver.model;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.dl.lezhin.browser.ChromeVersion;

import static java.util.Comparator.*;

/**
 * @since 4.0.0
 */
@Getter
@ToString
public class ChromeDriverDownload {

    private Instant timestamp;

    private List<Version> versions;

    public Optional<Version> findLatestVersion() {
        if (CollectionUtils.isNullOrEmpty(this.versions)) {
            return Optional.empty();
        }

        return this.versions.stream()
                .max(comparing(Version::getValue))
                .filter(it -> it.getDownloads() != null)
                .filter(it -> CollectionUtils.exists(it.getDownloads().getChromedrivers()));
    }

    // -------------------------------------------------------------------------------------------------

    @Getter
    @ToString
    public static final class Version {
        @SerializedName("version")
        private ChromeVersion value;

        private String revision;

        private Downloads downloads;
    }

    @Getter
    @ToString
    public static final class Downloads {
        @SerializedName("chrome")
        private List<Program> chromes;

        @Nullable
        @SerializedName("chromedriver")
        private List<Program> chromedrivers;
    }

    @Getter
    @ToString
    public static final class Program {
        private Platform platform;

        private URL url;
    }

}
