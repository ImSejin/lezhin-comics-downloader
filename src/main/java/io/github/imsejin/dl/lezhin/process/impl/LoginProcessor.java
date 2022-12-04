package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.attribute.impl.Authentication;
import io.github.imsejin.dl.lezhin.browser.WebBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.exception.LoginFailureException;
import io.github.imsejin.dl.lezhin.http.url.URIs;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import io.github.imsejin.dl.lezhin.process.Processor;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URI;
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
 *
 * @since 3.0.0
 */
@ProcessSpecification(dependsOn = ConfigurationFileProcessor.class)
public class LoginProcessor implements Processor {

    private static final Map<Locale, String> BASE_URL_MAP = Map.ofEntries(
            Map.entry(Locale.KOREA, "https://www.lezhin.com/"),
            Map.entry(Locale.US, "https://www.lezhinus.com/"),
            Map.entry(Locale.JAPAN, "https://www.lezhin.jp/")
    );

    @Override
    public Object process(ProcessContext context) throws LezhinComicsDownloaderException {
        // Resolves an implementation for the locale.
        Locale locale = context.getLanguage().getValue();
        String baseUrl = BASE_URL_MAP.get(locale);

        if (baseUrl == null) {
            throw new IllegalArgumentException("ProcessContext.language.value is not recognized: " + locale);
        }

        // Starts to run web browser.
        WebBrowser.run();

        // Goes to login page.
        gotoLoginPage(baseUrl, locale);

        // Waits for DOM to complete the rendering.
        WebElement loginForm = waitForRenderingLoginPage();

        // Inputs authentication into the element.
        Authentication authentication = context.getAuthentication();
        inputUsername(loginForm, authentication.getUsername());
        inputPassword(loginForm, authentication.getPassword());

        authentication.erasePassword();
        String currentUrl = WebBrowser.getCurrentUrl();

        // Submits login form.
        submit(loginForm);

        validate(currentUrl);

        // Return value will be ignored by ProcessContext.
        return null;
    }

    void gotoLoginPage(String baseUrl, Locale locale) {
        URI loginPageUri = URI.create(baseUrl);
        String path = URIs.LOGIN.get(locale.getLanguage());
        loginPageUri = loginPageUri.resolve(path);

        Loggers.getLogger().info("Request login page: {}", loginPageUri);
        WebBrowser.request(loginPageUri);
    }

    // -------------------------------------------------------------------------------------------------

    private static WebElement waitForRenderingLoginPage() {
        Loggers.getLogger().debug("Wait up to {} sec for login element to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);

        return WebBrowser.waitForVisibilityOfElement(By.xpath(
                "//form[@id='email' and contains(@action, '/login') and @method='post']"));
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
        try {
            // Waits for DOM to complete the rendering.
            Loggers.getLogger().debug("Wait up to {} sec for main page to be rendered", WebBrowser.DEFAULT_TIMEOUT_SECONDS);
            WebBrowser.waitForVisibilityOfElement(By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--home']"));
        } catch (TimeoutException e) {
            // When failed to login because of other problems.
            if (!WebBrowser.getCurrentUrl().equals(currentUrl)) {
                throw e;
            }

            // When failed to login because of invalid account information.
            String errorCode = WebBrowser.evaluate("window.__LZ_ERROR_CODE__", String.class);

            throw new LoginFailureException(errorCode);
        }
    }

}
