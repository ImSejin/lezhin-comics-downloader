package io.github.imsejin.core;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.imsejin.common.util.StringUtil;

public class LoginHelperTest {

    @Test
    public void testLogin() {
        // given
        ChromeDriver driver = ChromeBrowser.getDriver();
        String username = "이메일";
        String password = "비밀번호";

        // then
        driver.executeScript("window.open('about:blank', '_blank');");

        // 첫 번째 탭으로 전환한다.
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0));

        driver.get("https://www.lezhin.com/ko/login");

        WebElement loginForm = driver.findElementByXPath("//form[@id='login-form' and contains(@action, '/ko/login') and @method='post']");
        WebElement usernameInput = loginForm.findElement(By.xpath(".//input[@id='login-email']"));
        usernameInput.clear();
        usernameInput.sendKeys(username);

        WebElement passwordInput = loginForm.findElement(By.xpath(".//input[@id='login-password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement submitButton = loginForm.findElement(By.xpath(".//button[@type='submit']"));
        submitButton.click();

        driver.get("https://www.lezhin.com/ko");

        WebElement script;
        try {
            script = driver.findElementByXPath("//script[not(@src) and contains(text(), '__LZ_ME__')]");
        } catch (NoSuchElementException ex) {
            System.err.println("The account does not exists.");
            return;
        }

        String accessToken = StringUtil.match("token: '([\\w-]+)'", script.getAttribute("innerText"), 1);
        System.out.println("accessToken: " + accessToken);

        // then
        assertTrue(Pattern.matches("^[\\w-]+$", accessToken));
    }

    @AfterClass
    public static void quitDriver() {
        // ChromeDriver를 닫고 해당 프로세스를 종료한다.
        ChromeBrowser.getDriver().quit();
    }

}
