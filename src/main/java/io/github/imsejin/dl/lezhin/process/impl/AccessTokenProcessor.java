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
import io.github.imsejin.dl.lezhin.attribute.impl.AccessToken;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.AccessTokenNotFoundException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.regex.Pattern;

/**
 * Processor for extraction of access token
 *
 * <p> If you success to login, lezhin generates user's configuration that has an access token
 * into script tag. So {@link ChromeDriver} extracts the token from the script tag of which {@code innerText}.
 *
 * <p> The following code is {@code innerText} in the script tag.
 *
 * <pre>{@code
 *     <script>
 *     __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
 *         ...,
 *         locale: 'ko-KR',
 *         country: 'kr',
 *         language: 'ko',
 *         adultKind: 'kid',
 *         allowAdult: true,
 *         isEmbedded: false,
 *         now: (new Date('2020-06-22T09:27:54+09:00')).getTime(),
 *         authAdult: 'true',
 *         rid: '58Hk',
 *         token: '5be30a25-a044-410c-88b0-19a1da968a64',
 *         ...,
 *     });
 *
 *     __LZ_ME__ = _.merge(window.__LZ_ME__, {
 *         userId: '5412133348822268',
 *         adult: true,
 *         email: '',
 *         paidTime: 0,
 *         paidCount: 0,
 *         coin: { android: 0, ios: 0, web: 0 },
 *         point:{ android: 0, ios: 0, web: 0 }
 *     });
 *     </script>
 * }</pre>
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = HttpHostsProcessor.class)
public class AccessTokenProcessor implements Processor {

    /**
     * Performs a process of access token.
     *
     * @param context process context
     * @return access token
     * @throws AccessTokenNotFoundException if access token is not found
     */
    @Override
    public AccessToken process(ProcessContext context) throws AccessTokenNotFoundException {
        try {
            // Finds a script tag that has access token.
            WebBrowser.waitForPresenceOfElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_ME__')]"));
        } catch (NoSuchElementException | TimeoutException e) {
            throw new AccessTokenNotFoundException(e, "There is no access token");
        }

        String token = WebBrowser.evaluate("window.__LZ_CONFIG__?.token", String.class);

        Pattern pattern = Pattern.compile("[a-z0-9]{8}-([a-z0-9]{4}-){3}[a-z0-9]{12}");
        if (token == null || !pattern.matcher(token).matches()) {
            throw new AccessTokenNotFoundException("Invalid access token: %s", token);
        }

        AccessToken accessToken = new AccessToken(token);
        Loggers.getLogger().info("Successfully logged in: access token({})", accessToken.getValue());

        return accessToken;
    }

}
