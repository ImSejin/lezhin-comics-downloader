package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import io.github.imsejin.dl.lezhin.browser.ChromeBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.exception.LoginFailureException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

/**
 * Processor for login
 *
 * <p> This is the first place to run a chrome driver. First, {@link ChromeDriver} finds
 * the root element that is {@code <form id="email" action="/login" method="post"></form>} and then
 * it finds the three element in the root.
 *
 * <ol>
 *     <li>{@code <input id="login-email">}</li>
 *     <li>{@code <input id="login-password">}</li>
 *     <li>{@code <button type="submit"></button>}</li>
 * </ol>
 *
 * <p> {@link ChromeDriver} inputs username and password to the first and second element.
 * When input tags are filled by username and password, it clicks the third element so that login.
 */
@ProcessSpecification(dependsOn = ConfigurationFileProcessor.class)
public class LoginProcessor implements Processor {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private static final Map<Locale, LoginProcessor> IMPLEMENTATION_MAP = Map.ofEntries(
            Map.entry(Locale.KOREA, new KoreanImpl()),
            Map.entry(Locale.US, new EnglishImpl()),
            Map.entry(Locale.JAPAN, new JapaneseImpl())
    );

    @Override
    public Object process(ProcessContext context) throws LezhinComicsDownloaderException {
        // Resolves an implementation for the locale.
        Locale locale = context.getLanguage().getValue();
        LoginProcessor impl = IMPLEMENTATION_MAP.get(locale);

        if (impl == null) {
            throw new IllegalArgumentException("ProcessContext.language.value is not recognized: " + locale);
        }

        // Goes to login page.
        impl.gotoLoginPage();

        // Waits for DOM to complete the rendering.
        WebElement loginForm = waitForRenderingLoginPage();

        // Inputs authentication into the element.
        Authentication authentication = context.getAuthentication();
        inputUsername(loginForm, authentication.getUsername());
        inputPassword(loginForm, authentication.getPassword());

        authentication.erasePassword();
        String currentUrl = ChromeBrowser.getDriver().getCurrentUrl();

        // Submits login form.
        submit(loginForm);

        validate(currentUrl);

        // Return value will be ignored by ProcessContext.
        return null;
    }

    void gotoLoginPage() {
        throw new UnsupportedOperationException();
    }

    // -------------------------------------------------------------------------------------------------

    private static class KoreanImpl extends LoginProcessor {
        @Override
        void gotoLoginPage() {
            ChromeDriver driver = ChromeBrowser.getDriver();

            String loginPageUrl = "https://www.lezhin.com/ko/login";
            Loggers.getLogger().info("Request login page: {}", loginPageUrl);
            driver.get(loginPageUrl);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class EnglishImpl extends LoginProcessor {
        @Override
        void gotoLoginPage() {
            ChromeDriver driver = ChromeBrowser.getDriver();

            String loginPageUrl = "https://www.lezhinus.com/en/login";
            Loggers.getLogger().info("Request login page: {}", loginPageUrl);
            driver.get(loginPageUrl);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static class JapaneseImpl extends LoginProcessor {
        @Override
        void gotoLoginPage() {
            ChromeDriver driver = ChromeBrowser.getDriver();

            String loginPageUrl = "https://www.lezhin.jp/ja/login";
            Loggers.getLogger().info("Request login page: {}", loginPageUrl);
            driver.get(loginPageUrl);
        }
    }

    // -------------------------------------------------------------------------------------------------

    private static WebElement waitForRenderingLoginPage() {
        ChromeDriver driver = ChromeBrowser.getDriver();

        Loggers.getLogger().debug("Wait up to {} sec for login element to be rendered", TIMEOUT.getSeconds());

        WebDriverWait waiting = new WebDriverWait(driver, TIMEOUT);
        WebElement loginForm = driver.findElement(By
                .xpath("//form[@id='email' and contains(@action, '/login') and @method='post']"));
        waiting.until(ExpectedConditions.visibilityOfAllElements(loginForm));

        return loginForm;
    }

    private static void inputUsername(WebElement loginForm, String username) {
        Loggers.getLogger().debug("Send username: {}", username);

        WebElement usernameInput = loginForm.findElement(By.xpath(".//input[@id='login-email']"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    private static void inputPassword(WebElement loginForm, String password) {
        String maskedPassword = password.charAt(0) + StringUtils.repeat('*', password.length() - 1);
        Loggers.getLogger().debug("Send password: {}", maskedPassword);

        WebElement passwordInput = loginForm.findElement(By.xpath(".//input[@id='login-password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    private static void submit(WebElement loginForm) {
        Loggers.getLogger().debug("Try to login");
        WebElement submitButton = loginForm.findElement(By.xpath(".//button[@type='submit']"));
        submitButton.click();
    }

    private static void validate(String currentUrl) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        try {
            // Waits for DOM to complete the rendering.
            Loggers.getLogger().debug("Wait up to {} sec for main page to be rendered", TIMEOUT.getSeconds());
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--home']")));
        } catch (TimeoutException e) {
            // When failed to login because of other problems.
            if (!currentUrl.equals(driver.getCurrentUrl())) {
                throw e;
            }

            // When failed to login because of invalid account information.
            String errorCode = (String) driver.executeScript("return String(window.__LZ_ERROR_CODE__);");

            throw new LoginFailureException(errorCode);
        }
    }

}
