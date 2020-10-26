package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.PathnameUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class ChromeBrowser {

    private static final String CHROME_DRIVER_PATHNAME;

    static {
        // Assigns chrome driver pathname.
        final String currentPathname = PathnameUtils.getCurrentPathname();
        String filename = "chromedriver.exe";                                                 // for Microsoft Windows
        if (Files.notExists(Paths.get(currentPathname, filename))) filename = "chromedriver"; // for Linux and macOS
        CHROME_DRIVER_PATHNAME = Paths.get(currentPathname, filename).toString();

        // Sets up pathname of web driver.
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATHNAME);
    }

    private ChromeBrowser() {
    }

    public static ChromeDriver getDriver() {
        return SingletonLazyHolder.DRIVER;
    }

    private static class SingletonLazyHolder {
        private static final ChromeDriver DRIVER = new ChromeDriver(
                new ChromeOptions().addArguments(
                        "--start-maximized",           // 전체화면으로 실행한다.
                        "--disable-popup-blocking",    // 팝업화면을 무시한다.
                        "--disable-default-apps",      // 기본 앱을 사용하지 않는다.
                        "--ignore-certificate-errors", // 인증오류를 무시한다.
                        "--headless",                  // CLI 환경에서도 실행할 수 있게 한다.
                        "--incognito")                 // 프라이빗 모드로 실행한다.
        );
    }

}
