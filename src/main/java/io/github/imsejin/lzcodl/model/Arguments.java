/*
 * Copyright 2020 Sejin Im
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

package io.github.imsejin.lzcodl.model;

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.lzcodl.common.constant.EpisodeRange;
import io.github.imsejin.lzcodl.common.constant.Languages;
import io.github.imsejin.lzcodl.common.exception.ConfigParseException;
import io.github.imsejin.lzcodl.common.exception.EpisodeRangeParseException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.Map;

/**
 * @since 2.1.2
 */
@Getter
@Setter
@ToString(of = {"language", "comicName", "episodeRange", "imageFormat", "debugging"})
public class Arguments {

    private final String username;
    private final String password;
    private final Languages language;
    private final String comicName;
    private final String episodeRange;

    /**
     * @since 2.8.0
     */
    private final String imageFormat;

    /**
     * @since 2.6.2
     */
    private final boolean debugging;

    private String accessToken;
    private Product product;

    /**
     * @since 2.6.0
     */
    private boolean expiredComic;

    {
        final File file = new File(PathnameUtils.getCurrentPathname(), "config.ini");

        Map<String, String> section;
        try {
            section = IniUtils.readSection(file, "account");
        } catch (Exception e) {
            // 'config.ini' 파일이 없거나, 'account' 섹션이 없는 경우
            throw new ConfigParseException("File 'config.ini' or section 'account' does not exist.", e);
        }

        String username = section.get("username");
        String password = section.get("password");

        // 유효하지 않은 계정 정보의 경우
        Asserts.that(username)
                .as("ID is not valid.")
                .exception(ConfigParseException::new)
                .isNotNull()
                .hasText();
        Asserts.that(password)
                .as("Password is not valid.")
                .exception(ConfigParseException::new)
                .isNotNull()
                .hasText();

        this.username = username;
        this.password = password;
    }

    @Builder
    private Arguments(String language, String comicName, String episodeRange, boolean jpg, boolean debugging) {
        // 유효하지 않은 에피소드 범위의 경우
        if (EpisodeRange.invalidate(episodeRange)) {
            throw new EpisodeRangeParseException("Invalid episode range: '%s'", episodeRange);
        }

        this.language = Languages.from(language);
        this.comicName = comicName;
        this.episodeRange = episodeRange;
        this.imageFormat = jpg ? "jpg" : "webp";
        this.debugging = debugging;
    }

}
