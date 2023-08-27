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

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 4.0.0
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChromeDriverDownload {

    private String timestamp;
    // private Instant timestamp;

    private List<Version> versions;

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Version {
        @SerializedName("version")
        private String value;

        private String revision;

        private Downloads downloads;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Downloads {
        private List<Program> chrome;
        @Nullable
        private List<Program> chromedriver;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Program {
        private String platform;
        private String url;
    }

}
