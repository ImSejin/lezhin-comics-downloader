package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.TestUtils;
import io.github.imsejin.lzcodl.model.Arguments;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginHelper")
class LoginHelperTest {

    private static ChromeDriver driver;

    @BeforeAll
    static void beforeAll() {
        String path = TestUtils.getDriverPath().getPath();
        System.setProperty("webdriver.chrome.driver", path);

        new File(path).setExecutable(true);
        driver = new ChromeDriver(new ChromeOptions().addArguments(ChromeBrowser.ChromeOption.getArguments()));
    }

    @AfterAll
    static void afterAll() {
        driver.quit();
    }

    @Test
    void login() {
        // given
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
