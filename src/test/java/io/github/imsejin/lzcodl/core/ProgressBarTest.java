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

package io.github.imsejin.lzcodl.core;

import me.tongfei.progressbar.ConsoleProgressBarConsumer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class ProgressBarTest {

    @Test
    @Disabled
    void testProgressBar() {
        // given
        ProgressBarBuilder builder = new ProgressBarBuilder();
        builder.setTaskName("task-name");
        builder.setInitialMax(20);
        builder.setUpdateIntervalMillis(50);
        builder.setStyle(ProgressBarStyle.COLORFUL_UNICODE_BLOCK);
        builder.setConsumer(new ConsoleProgressBarConsumer(System.out));

        // when
        ProgressBar progressBar = builder.build();
        IntStream.rangeClosed(1, 20).forEach(n -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                progressBar.step(); // == progressBar.stepBy(1);
            } catch (InterruptedException ignored) {
            }
        });
    }

}
