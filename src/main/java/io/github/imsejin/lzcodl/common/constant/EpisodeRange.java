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

package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum EpisodeRange implements KeyValue {

    /**
     * 에피소드 번호의 구분자<br>
     * Separator of episode number
     */
    SEPARATOR("~");

    private final String value;

    /**
     * Checks if {@link EpisodeRange} that has the value exists.
     *
     * @param value {@link #value()}
     * @return {@link EpisodeRange}
     */
    public static boolean contains(String value) {
        if (value == null) return false;
        return Arrays.stream(values()).map(range -> range.value).anyMatch(value::equals);
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.value;
    }

}
