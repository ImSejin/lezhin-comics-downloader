package io.github.imsejin.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.imsejin.common.constants.URIs;
import io.github.imsejin.common.util.StringUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginHelper {

    /**
     * 로그인하여 액세스 토큰을 얻는다.<br>
     * Logins and gets an access token.
     */
    public String login(String username, String password) {
        // 유효하지 않은 계정 정보의 경우
        if (StringUtils.anyBlanks(username, password)) {
            System.err.println("\n    ID or password is not valid.");
            return null;
        }

        String accessToken = getAccessToken(username, password);

        // 존재하지 않는 계정의 경우
        if (StringUtils.isBlank(accessToken)) {
            System.err.println("\n    The account does not exists.");
            return null;
        }

        return accessToken;
    }

    /**
     * 로그인하여 액세스 토큰을 찾아, 반환한다.<br>
     * Finds access token after login, returns it.
     * 
     * <pre>{@code
     * <script>
     * __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
     *     apiUrl: 'api.lezhin.com',
     *     cdnUrl: 'https://cdn.lezhin.com',
     *     recoUrl: 'dondog.lezhin.com',
     *     payUrl: 'https://pay.lezhin.com',
     *     pantherUrl: 'https://panther.lezhin.com',
     *     locale: 'ko-KR',
     *     country: 'kr',
     *     language: 'ko',
     *     adultKind: 'kid',
     *     allowAdult: true,
     *     isEmbedded: false,
     *     now: (new Date('2020-06-22T09:27:54+09:00')).getTime(),
     *     authAdult: 'true',
     *     rid: '58Hk',
     *     token: '5be30a25-a044-410c-88b0-19a1da968a64',
     *     genres: {"romance":"로맨스","fantasy":"판타지","horror":"호러","lightnovel":"라이트노벨","sports":"스포츠","gl":"백합","historical":"시대극","bl":"BL","gore":"스릴러","girl":"소녀만화","gag":"개그","food":"음식","otona":"오토나","drama":"드라마","mystery":"미스터리","sf":"SF","martial":"무협","school":"학원","mature_female":"레이디스코믹","tl":"TL","action":"액션","adult":"성인","day":"일상","gallery":"갤러리"}
     * });
     * 
     * __LZ_ME__ = _.merge(window.__LZ_ME__, {
     *     userId: '5412133348822268',
     *     adult: true,
     *     email: '',
     *     paidTime: 0,
     *     paidCount: 0,
     *     coin: { android: 0, ios: 0, web: 0 },
     *     point:{ android: 0, ios: 0, web: 0 }
     * });
     * ...
     * </script>
     * }</pre>
     */
    private String getAccessToken(String username, String password) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // 로그인 페이지를 요청한다.
        driver.get(URIs.LOGIN.value());

        // 계정정보를 작성한다.
        WebElement loginForm = driver.findElementByXPath("//form[@id='login-form' and contains(@action, '/ko/login') and @method='post']");
        WebElement usernameInput = loginForm.findElement(By.xpath(".//input[@id='login-email']"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        WebElement passwordInput = loginForm.findElement(By.xpath(".//input[@id='login-password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        // 로그인한다.
        WebElement submitButton = loginForm.findElement(By.xpath(".//button[@type='submit']"));
        submitButton.click();

        // 로그인 딜레이를 대기하기 위해 메인페이지로 이동한다.
        driver.get(URIs.HOME.value());

        // 액세스 토큰 정보가 있는 script 태그를 찾는다.
        WebElement script;
        try {
            script = driver.findElementByXPath("//script[not(@src) and contains(text(), '__LZ_ME__')]");
        } catch (NoSuchElementException ex) {
            System.err.println("The account does not exists.");
            return null;
        }

        return StringUtils.match("token: '([\\w-]+)'", script.getAttribute("innerText"), 1);
    }

}
