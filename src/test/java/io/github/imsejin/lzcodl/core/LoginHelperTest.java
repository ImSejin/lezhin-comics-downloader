package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.model.Arguments;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginHelper")
class LoginHelperTest {

    @AfterAll
    static void quitDriver() {
        ChromeBrowser.softQuit();
    }

    @Test
    void login() {
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
