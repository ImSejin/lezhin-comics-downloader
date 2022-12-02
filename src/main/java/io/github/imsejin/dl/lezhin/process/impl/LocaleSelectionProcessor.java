/*
 * Copyright 2022 Sejin Im
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

package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.AccessTokenNotFoundException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

/**
 * Processor for selecting locale
 *
 * <p> When you see the content of lezhin platform not as usual, they show you a page of locale selection.
 * It is a obstacle to progress a process, so this processor requests that in advance.
 */
@ProcessSpecification(dependsOn = AccessTokenProcessor.class)
public class LocaleSelectionProcessor implements Processor {

    @Override
    public Void process(ProcessContext context) throws AccessTokenNotFoundException {
        String localePath = String.format("/%s/locale/%s",
                context.getLanguage().getValue().getLanguage(), context.getLanguage().asLocaleString());

        Loggers.getLogger().debug("Change locale setting: {}", localePath);
        WebBrowser.request(localePath);

        return null;
    }

}
