package io.github.imsejin.core;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.imsejin.common.util.PathnameUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChromeBrowser {

    private final String CHROME_DRIVER_PATHNAME = PathnameUtil.chromeDriverPathname();

    static {
        // WebDriver의 경로를 설정한다.
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATHNAME);
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
                        "--incognito",                 // 프라이빗 모드로 실행한다.
                        "--headless")                  // CLI 환경에서도 실행할 수 있게 한다.
        );
    }

}
