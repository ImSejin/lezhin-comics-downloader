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

import io.github.imsejin.common.assertion.Asserts;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.api.BaseService;
import io.github.imsejin.dl.lezhin.argument.impl.Language;
import io.github.imsejin.dl.lezhin.attribute.impl.Content;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.http.url.URIs;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.openqa.selenium.By;

import java.util.Locale;

/**
 * Processor for figuring out information of the content
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = LocaleSelectionProcessor.class)
public class ContentInformationProcessor implements Processor {

    @Override
    public Content process(ProcessContext context) throws LezhinComicsDownloaderException {
        Locale locale = context.getLanguage().getValue();

        // Goes to page of the content.
        String contentPath = URIs.CONTENT.get(locale.getLanguage(), context.getContentName().getValue());
        Loggers.getLogger().info("Request comic page: {}", contentPath);
        WebBrowser.request(contentPath);

        String jsonString;

        // Checks expiration of the content.
        String currentUrl = WebBrowser.getCurrentUrl();
        String expirationPath = URIs.EXPIRATION.get(locale.getLanguage());
        boolean expired = currentUrl.endsWith(expirationPath);
        if (expired) {
            Loggers.getLogger().info("Comic is expired -> try to find it in 'My Library'");
            jsonString = getJsonInMyLibrary(context);
        } else {
            // Waits for DOM to complete the rendering.
            Loggers.getLogger().debug("Wait up to {} sec for episode list to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);
            WebBrowser.waitForVisibilityOfElement(By.xpath(
                    "//main[@id='main' and @class='lzCntnr lzCntnr--episode']"));
            jsonString = WebBrowser.evaluate("JSON.stringify(window.__LZ_PRODUCT__.product)", String.class);
        }

        Content content = BaseService.getGson().fromJson(jsonString, Content.class);
        Asserts.that(content)
                .isNotNull()
                .describedAs("Content.properties.expired is expected to be '{0}'", expired)
                .returns(expired, it -> it.getProperties().isExpired());

        return content;
    }

    // -------------------------------------------------------------------------------------------------

    /**
     * @since 2.6.0
     */
    private static String getJsonInMyLibrary(ProcessContext context) {
        Language language = context.getLanguage();

        String libraryContentPath = URIs.LIBRARY_CONTENT.get(
                language.getValue().getLanguage(), language.asLocaleString(), context.getContentName().getValue());
        Loggers.getLogger().debug("Request comic page in 'My Library': {}", libraryContentPath);
        WebBrowser.request(libraryContentPath);

        // Waits for DOM to complete the rendering.
        Loggers.getLogger().debug("Wait up to {} sec for episode list to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);
        WebBrowser.waitForVisibilityOfElement(By.xpath(
                "//ul[@id='library-episode-list' and @class='epsList']"));

        return WebBrowser.evaluate("JSON.stringify(window.__LZ_PRODUCT__.product)", String.class);
    }

}
