package io.github.imsejin.dl.lezhin.process.impl;

import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.dl.lezhin.annotation.ProcessSpecification;
import io.github.imsejin.dl.lezhin.browser.ChromeBrowser;
import io.github.imsejin.dl.lezhin.common.Loggers;
import io.github.imsejin.dl.lezhin.exception.LezhinComicsDownloaderException;
import io.github.imsejin.dl.lezhin.process.ProcessContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@ProcessSpecification(dependsOn = ConfigurationFileProcessor.class)
public class LoginProcessor extends AbstractLocaleProcessor {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    @Override
    protected Object processForKorean(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Go to login page.
        visitLoginPage("https://www.lezhin.com/ko/login");
        // Waits for DOM to complete the rendering.
        WebElement loginForm = waitForRenderingLoginPage();
        // Inputs authentication into the element.
        inputUsername(loginForm, context.getAuthentication().getUsername());
        inputPassword(loginForm, context.getAuthentication().getPassword());

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    @Override
    protected Object processForEnglish(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Go to login page.
        visitLoginPage("https://www.lezhinus.com/en/login");
        // Waits for DOM to complete the rendering.
        WebElement loginForm = waitForRenderingLoginPage();
        // Inputs authentication into the element.
        inputUsername(loginForm, context.getAuthentication().getUsername());
        inputPassword(loginForm, context.getAuthentication().getPassword());

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    @Override
    protected Object processForJapanese(ProcessContext context) throws LezhinComicsDownloaderException {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // Go to login page.
        visitLoginPage("https://www.lezhin.jp/ja/login");
        // Waits for DOM to complete the rendering.
        WebElement loginForm = waitForRenderingLoginPage();
        // Inputs authentication into the element.
        inputUsername(loginForm, context.getAuthentication().getUsername());
        inputPassword(loginForm, context.getAuthentication().getPassword());

        return null;
    }

    // -------------------------------------------------------------------------------------------------

    private static void visitLoginPage(String loginPageUrl) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        Loggers.getLogger().info("Request login page: {}", loginPageUrl);
        driver.get(loginPageUrl);
    }

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

}
