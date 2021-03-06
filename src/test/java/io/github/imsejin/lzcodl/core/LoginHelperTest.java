package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.model.Arguments;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginHelperTest {

    @AfterAll
    public static void quitDriver() {
        // ChromeDriver를 닫고 해당 프로세스를 종료한다.
        ChromeBrowser.getDriver().quit();
    }

    @Test
    public void login() {
        // given
        ChromeDriver driver = ChromeBrowser.getDriver();
        Arguments arguments = Arguments.builder()
                .language("ko")
                .comicName("snail")
                .episodeRange("~1")
                .build();

        // then
        String actual = LoginHelper.login(arguments);

        // then
        assertThat(actual).matches("^[\\w-]+$");
    }

}
