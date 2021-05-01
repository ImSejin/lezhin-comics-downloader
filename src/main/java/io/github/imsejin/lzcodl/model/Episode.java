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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class Episode {

    /**
     * Episode name in URI.
     */
    private String name;

    private Display display;
    private Properties properties;
    private int coin;
    private int point;

    /**
     * When episode was uploaded to the Lezhin Comics server.
     */
    private long updatedAt;

    /**
     * When to change to a free episode.
     *
     * <p> If this is {@code 0}, it means the episode doesn't turn free even if you wait.
     */
    private long freedAt;

    private int seq;

    /**
     * When episode actually appears on a web page to users.
     */
    private long publishedAt;
    private long id;

    /**
     * @since 2.6.0
     */
    public boolean isFree() {
        return this.freedAt <= System.currentTimeMillis();
    }

}
