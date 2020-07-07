package io.github.imsejin.core;

import io.github.imsejin.common.constants.Languages;
import io.github.imsejin.common.constants.URIs;
import io.github.imsejin.model.Arguments;
import io.github.imsejin.model.Episode;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Crawler {

    private Crawler() {}

    /**
     * 웹툰 정보를 찾아, JSON 형태의 문자열로 반환한다.<br>
     * Finds webtoon information, converts to the JSON format string and return it.<br><br>
     * 
     * 일반적으로 접근했을 때와 크롤러로 접근했을 때의 페이지가 다르다.<br>
     * 필요한 모든 정보가 script 태그 내 JSON 형태로 있어서 이를 이용한다.
     * 
     * <pre>{@code
     * <script>
     * __LZ_MESSAGE__ = {...};
     * __LZ_PRODUCT__ = { productType: 'comic', product: {...}, departure: '', all: {...}, prefree: {...} };
     * __LZ_DATA__ = {...};
     * __LZ_CONTEXT__ = "";
     * __LZ_ERROR_CODE__ = '${error}';
     * __LZ_ERROR_MESSAGE__ = { 'error.COMIC_EPISODE.NOT_FOUND': "찾으시는 에피소드가 없습니다." };
     * </script>
     * }</pre>
     */
    public static String getJson(Arguments arguments) throws InterruptedException {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // 언어/지역 설정 변경 페이지가 노출되면 다운로드할 수 없기에, 미리 API를 호출하여 설정을 변경한다.
        String locale = Languages.from(arguments.getLanguage()).getLocale();
        driver.get(URIs.HOME.value() + arguments.getLanguage() + "/locale/" + locale + "?locale=" + locale);

        // 해당 웹툰 페이지로 이동한다.
        driver.get(URIs.HOME.value() + arguments.getLanguage() + URIs.COMIC.value() + arguments.getComicName());

        // DOM 렌더링을 기다린다
        TimeUnit.SECONDS.sleep(2);

        // 웹툰의 정보가 window 객체의 필드로 정의되어 있어, 이를 가져오기 위해 로컬스토리지에 저장한다.
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");

        // 웹툰의 정보를 로컬스토리지에서 가져와 JSON 형식의 문자열을 반환한다.
        return driver.getLocalStorage().getItem("product");
    }

    @SneakyThrows(InterruptedException.class)
    public static int getNumOfImagesInEpisode(Arguments arguments, Episode episode) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        driver.get(URLFactory.oneEpisodeViewer(arguments.getLanguage(), arguments.getComicName(), episode.getName()).toString());

        // DOM 렌더링을 기다린다
        TimeUnit.SECONDS.sleep(2);

        List<WebElement> images;
        try {
            WebElement scrollList = driver.findElementById("scroll-list");
            images = scrollList.findElements(By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        } catch (NoSuchElementException ex) {
            // Fail
            return 0;
        }

        // Success
        return images.size();
    }

}
