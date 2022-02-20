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

import io.github.imsejin.common.annotation.ExcludeFromGeneratedJacocoReport;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.Loggers;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.common.exception.LoginFailureException;
import io.github.imsejin.lzcodl.model.Arguments;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

/**
 * Helps you login to lezhin comics with selenium.
 *
 * @see ChromeBrowser
 * @see ChromeDriver
 * @since 2.0.0
 */
public final class LoginHelper {

    @ExcludeFromGeneratedJacocoReport
    private LoginHelper() {
        throw new UnsupportedOperationException(getClass().getName() + " is not allowed to instantiate");
    }

    /**
     * Gets an access token after login.
     *
     * @param args arguments required to login
     * @return access token({@link UUID#randomUUID()})
     * (e.g. 5be30a25-a044-410c-88b0-19a1da968a64)
     */
    public static String login(Arguments args) {
        // Does login.
        tryLogin(args);

        // Validates account information.
        validate(args);

        // Gets a token to access.
        return getAccessToken();
    }

    /**
     * Tries to login.
     *
     * <p> This is the first place to run a chrome driver.
     * First, {@link ChromeDriver} finds the root element that is
     * {@code <form id="email" action="/login" method="post"></form>}.
     * And then it finds the three element in the root.
     *     <ol>
     *         <li>{@code <input id="login-email">}</li>
     *         <li>{@code <input id="login-password">}</li>
     *         <li>{@code <button type="submit"></button>}</li>
     *     </ol>
     *
     * <p> {@link ChromeDriver} inputs username to the first element
     * and does password to the second element.
     * When input tags are filled by username and password,
     * it clicks the third element so that login.
     *
     * @param args arguments required to login
     * @since 2.7.0
     */
    private static void tryLogin(Arguments args) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Requests login page.
        URI loginUrl = URIs.LOGIN.get(args.getLanguage().getValue());
        Loggers.getLogger().info("Request login page: {}", loginUrl);
        driver.get(loginUrl.toString());

        // Waits for DOM to complete the rendering.
        final int timeout = 15;
        Loggers.getLogger().debug("Wait up to {} sec for login element to be rendered", timeout);
        WebDriverWait waitLogin = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        WebElement loginForm = driver.findElement(By
                .xpath("//form[@id='email' and contains(@action, '/login') and @method='post']"));
        waitLogin.until(ExpectedConditions.visibilityOfAllElements(loginForm));

        // Inputs username into the element.
        Loggers.getLogger().debug("Send username: {}", args.getUsername());
        WebElement usernameInput = loginForm.findElement(By.xpath(".//input[@id='login-email']"));
        usernameInput.clear();
        usernameInput.sendKeys(args.getUsername());

        // Inputs password into the element.
        String maskedPassword = args.getPassword().charAt(0) + StringUtils.repeat('*', args.getPassword().length() - 1);
        Loggers.getLogger().debug("Send password: {}", maskedPassword);
        WebElement passwordInput = loginForm.findElement(By.xpath(".//input[@id='login-password']"));
        passwordInput.clear();
        passwordInput.sendKeys(args.getPassword());

        // Does login.
        Loggers.getLogger().debug("Try to login");
        WebElement submitButton = loginForm.findElement(By.xpath(".//button[@type='submit']"));
        submitButton.click();
    }

    /**
     * Validates that login is successfully done.
     *
     * @param args arguments required to login
     * @throws LoginFailureException if failed to login because of invalid account info
     * @throws TimeoutException      if failed to login because of the other
     *                               or to move to main page
     * @since 2.7.0
     */
    private static void validate(Arguments args) {
        ChromeDriver driver = ChromeBrowser.getDriver();
        final int timeout = 10;

        try {
            // Waits for DOM to complete the rendering.
            Loggers.getLogger().debug("Wait up to {} sec for main page to be rendered", timeout);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--home']")));
        } catch (TimeoutException e) {
            // When failed to login because of other problems.
            URI currentUri = URI.create(driver.getCurrentUrl());
            URI loginUri = URIs.LOGIN.get(args.getLanguage().getValue());
            if (!currentUri.getPath().equals(loginUri.getPath())) throw e;

            // When failed to login because of invalid account information.
            driver.executeScript("localStorage.setItem('errorCode', window.__LZ_ERROR_CODE__);");
            String errorCode = driver.getLocalStorage().getItem("errorCode");

            throw new LoginFailureException(errorCode);
        }
    }

    /**
     * Finds an access token after login.
     *
     * <p> If {@link LoginHelper} successes to login,
     * lezhin generates user's configurations that has an access token into script tag.
     * So {@link ChromeDriver} extracts a token from the script tag of which {@code innerText}.
     *
     * <p> The following code is {@code innerText} in the script tag.
     *
     * <pre>{@code
     *     <script>
     *     __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
     *         apiUrl: 'api.lezhin.com',
     *         cdnUrl: 'https://cdn.lezhin.com',
     *         recoUrl: 'dondog.lezhin.com',
     *         payUrl: 'https://pay.lezhin.com',
     *         pantherUrl: 'https://panther.lezhin.com',
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
     *         genres: {"romance":"로맨스","fantasy":"판타지","horror":"호러","lightnovel":"라이트노벨","sports":"스포츠","gl":"백합","historical":"시대극","bl":"BL","gore":"스릴러","girl":"소녀만화","gag":"개그","food":"음식","otona":"오토나","drama":"드라마","mystery":"미스터리","sf":"SF","martial":"무협","school":"학원","mature_female":"레이디스코믹","tl":"TL","action":"액션","adult":"성인","day":"일상","gallery":"갤러리"}
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
     *     ...
     *     </script>
     * }</pre>
     *
     * @return access token
     * (e.g. 5be30a25-a044-410c-88b0-19a1da968a64) ... {@link UUID#randomUUID()}
     * @see ChromeBrowser
     * @see ChromeDriver
     * @see WebElement#getAttribute(String)
     * @see java.util.regex.Matcher#group(int)
     */
    private static String getAccessToken() {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Finds the script tag that has access token.
        WebElement script;
        try {
            script = driver.findElement(By.xpath("//script[not(@src) and contains(text(), '__LZ_ME__')]"));
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Cannot find access token", e);
        }

        String accessToken = StringUtils.find(script.getAttribute("innerText"), "token: '([\\w-]+)'", 1);
        Loggers.getLogger().info("Successfully logged in: access token({})", accessToken);
        return accessToken;
    }

}
