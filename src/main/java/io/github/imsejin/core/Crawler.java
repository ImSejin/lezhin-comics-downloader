package io.github.imsejin.core;

import io.github.imsejin.common.constants.URIs;
import org.openqa.selenium.chrome.ChromeDriver;

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
    public static String getJson(String username, String password, String comicName) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        driver.get(URIs.COMIC.value() + comicName);

        // 웹툰의 정보가 window 객체의 필드로 정의되어 있어, 이를 가져오기 위해 로컬스토리지에 저장한다.
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");

        // 웹툰의 정보를 로컬스토리지에서 가져와 JSON 형식의 문자열을 반환한다.
        return driver.getLocalStorage().getItem("product");
    }

}
