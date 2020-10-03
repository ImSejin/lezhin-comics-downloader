package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.constants.URIs;
import io.github.imsejin.lzcodl.model.Arguments;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.UUID;

/**
 * Helps you login to lezhin comics with selenium.
 *
 * @see ChromeBrowser
 * @see ChromeDriver
 */
public final class LoginHelper {

    private LoginHelper() {
    }

    /**
     * Gets an access token after login.
     *
     * @param arguments arguments required to login
     * @return access token({@link UUID#randomUUID()})
     * (e.g. 5be30a25-a044-410c-88b0-19a1da968a64)
     */
    public static String login(Arguments arguments) {
        String accessToken = getAccessToken(arguments);

        // When the account information is not valid.
        if (StringUtils.isNullOrEmpty(accessToken)) {
            System.err.print("\n    The account does not exist.");
            return null;
        }

        System.out.println("\n    Success to login. -> " + URIs.LOGIN.get(arguments.getLanguage()) + "\n");
        return accessToken;
    }

    /**
     * Finds an access token after login.
     *
     * <p> This is the first place to run a chrome driver.
     * First, {@link ChromeDriver} finds the root element that is
     * {@code <form id="login-form" action="/login" method="post"></form>}.
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
     * @param arguments arguments required to login
     * @return access token
     * (e.g. 5be30a25-a044-410c-88b0-19a1da968a64) ... {@link UUID#randomUUID()}
     * @see ChromeBrowser
     * @see ChromeDriver
     * @see WebElement#getAttribute(String)
     * @see java.util.regex.Matcher#group(int)
     */
    private static String getAccessToken(Arguments arguments) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Requests login page.
        String loginUrl = URIs.LOGIN.get(arguments.getLanguage());
        driver.get(loginUrl);

        // Waits for DOM to complete the rendering.
        WebElement loginForm = driver.findElementByXPath("//form[@id='login-form' and contains(@action, '/login') and @method='post']");
        WebDriverWait waitLogin = new WebDriverWait(driver, 15);
        waitLogin.until(ExpectedConditions.visibilityOfAllElements(loginForm));

        // Inputs account information into the element.
        WebElement usernameInput = loginForm.findElement(By.xpath(".//input[@id='login-email']"));
        usernameInput.clear();
        usernameInput.sendKeys(arguments.getUsername());
        WebElement passwordInput = loginForm.findElement(By.xpath(".//input[@id='login-password']"));
        passwordInput.clear();
        passwordInput.sendKeys(arguments.getPassword());

        // Does login.
        WebElement submitButton = loginForm.findElement(By.xpath(".//button[@type='submit']"));
        submitButton.click();

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--home']")));

        // Finds the script tag that has access token.
        WebElement script;
        try {
            script = driver.findElementByXPath("//script[not(@src) and contains(text(), '__LZ_ME__')]");
        } catch (NoSuchElementException ex) {
            return null;
        }

        return StringUtils.match("token: '([\\w-]+)'", script.getAttribute("innerText"), 1);
    }

}
