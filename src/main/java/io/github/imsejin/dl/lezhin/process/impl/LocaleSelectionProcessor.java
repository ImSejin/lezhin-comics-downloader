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
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.http.url.URIs;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;

/**
 * Processor for selecting locale
 *
 * <p> When you see the content of lezhin platform not as usual, they show you a page of locale selection.
 * It is an obstacle to progress a process, so this processor requests that in advance.
 *
 * <pre>{@code
 * $("#locale-form").on("submit", function (t) {
 *   t.preventDefault();
 *   var n = $(this).find("[name=locale]:checked").val();
 *   location.href = "/".concat(e.a.get("language"), "/locale/")
 *       .concat(n, "?redirect=").concat(encodeURIComponent(location.href))
 * })
 * }</pre>
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = AccessTokenProcessor.class)
public class LocaleSelectionProcessor implements Processor {

    @Override
    public Void process(ProcessContext context) throws LezhinComicsDownloaderException {
        Language language = context.getLanguage();
        String localePath = URIs.LOCALE.get(language.getValue().getLanguage(), language.asLocaleString());

        Loggers.getLogger().debug("Change locale setting: {}", localePath);
        WebBrowser.request(localePath);

        // Return value will be ignored by ProcessContext.
        return null;
    }

}
