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

package io.github.imsejin.dl.lezhin.api.auth.model;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.attribute.Attribute;

/**
 * @since 3.0.0
 */
@Getter
@ToString
@EqualsAndHashCode
public class Authority implements Attribute {

    private static final Pattern RESOURCE_PATTERN = Pattern.compile(
            "^https://\\w+\\.lezhin\\.com/v2/.+/([0-9]+)/episodes/([0-9]+)/contents");

    private final String policy;

    private final String signature;

    private final String keyPairId;

    private final Long contentId;

    private final Long episodeId;

    private final Instant expiredAt;

    public Authority(String policy, String signature, String keyPairId) {
        this.policy = policy;
        this.signature = signature;
        this.keyPairId = keyPairId;

        // URL and Filename safe Base64 (RFC 4648)
        byte[] bytes = policy.replaceAll("[-_]", "").getBytes(StandardCharsets.UTF_8);
        String decoded = new String(Base64.getUrlDecoder().decode(bytes), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(decoded).getAsJsonObject();

        this.contentId = extractContentId(jsonObject);
        this.episodeId = extractEpisodeId(jsonObject);
        this.expiredAt = Instant.ofEpochSecond(extractExpiredAt(jsonObject));
    }

    public boolean isExpired() {
        return this.expiredAt.isBefore(Instant.now());
    }

    // -------------------------------------------------------------------------------------------------

    private static Long extractContentId(JsonObject jsonObject) {
        JsonArray statement = jsonObject.get("Statement").getAsJsonArray();
        JsonObject element = statement.get(0).getAsJsonObject();

        String resourceUrl = element.get("Resource").getAsString();
        String contentId = StringUtils.find(resourceUrl, RESOURCE_PATTERN, 1);

        return Long.parseLong(contentId);
    }

    private static Long extractEpisodeId(JsonObject jsonObject) {
        JsonArray statement = jsonObject.get("Statement").getAsJsonArray();
        JsonObject element = statement.get(0).getAsJsonObject();

        String resourceUrl = element.get("Resource").getAsString();
        String episodeId = StringUtils.find(resourceUrl, RESOURCE_PATTERN, 2);

        return Long.parseLong(episodeId);
    }

    private static Long extractExpiredAt(JsonObject jsonObject) {
        JsonArray statement = jsonObject.get("Statement").getAsJsonArray();
        JsonObject element = statement.get(0).getAsJsonObject();

        JsonObject condition = element.get("Condition").getAsJsonObject();
        JsonObject dateLessThan = condition.get("DateLessThan").getAsJsonObject();

        return dateLessThan.get("AWS:EpochTime").getAsLong();
    }

}
