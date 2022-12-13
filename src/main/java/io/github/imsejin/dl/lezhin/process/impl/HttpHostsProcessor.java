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

import io.github.imsejin.common.util.CollectionUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.HttpHosts;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.common.PropertyBinder;
import io.github.imsejin.dl.lezhin.exception.URLConfigurationNotFoundException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;

/**
 * Processor for extraction of HTTP URL configuration
 *
 * <p> If you enter any pages of lezhin comics, they provide the configuration of HTTP URL for their APIs
 * into script tag. So {@link ChromeDriver} extracts the token from the script tag of which {@code innerText}.
 *
 * <p> The following code is {@code innerText} in the script tag.
 *
 * <pre>{@code
 *     <script>
 *     __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
 *         apiUrl: 'api.lezhin.com',
 *         cdnUrl: 'https://ccdn.lezhin.com',
 *         contentsCdnUrl: 'https://rcdn.lezhin.com',
 *         recoUrl: 'dondog.lezhin.com',
 *         payUrl: 'https://pay.lezhin.com',
 *         pantherUrl: 'https://panther.lezhin.com',
 *         ...
 *     });
 *     </script>
 * }</pre>
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = LoginProcessor.class)
public class HttpHostsProcessor implements Processor {

    /**
     * Performs a process of HTTP URL configuration.
     *
     * @param context process context
     * @return HTTP URL configuration
     * @throws URLConfigurationNotFoundException if the configuration is not found
     */
    @Override
    public HttpHosts process(ProcessContext context) throws URLConfigurationNotFoundException {
        try {
            // Finds a script tag that has the configuration.
            WebBrowser.waitForPresenceOfElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_CONFIG__')]"));
        } catch (NoSuchElementException | TimeoutException e) {
            throw new URLConfigurationNotFoundException(e, "There is no configuration of URL");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> config = WebBrowser.evaluate("window.__LZ_CONFIG__", Map.class);

        if (CollectionUtils.isNullOrEmpty(config)) {
            throw new URLConfigurationNotFoundException("Invalid configuration of URL: %s", config);
        }

        HttpHosts httpHosts = PropertyBinder.INSTANCE.toHttpHosts(config);
        Loggers.getLogger().debug("Found the configuration of URL: {}", httpHosts);

        return httpHosts;
    }

}
