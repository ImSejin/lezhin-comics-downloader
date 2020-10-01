package io.github.imsejin.core;

import io.github.imsejin.common.constants.Languages;
import io.github.imsejin.common.constants.URIs;
import io.github.imsejin.model.Arguments;
import io.github.imsejin.model.Episode;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * DOM Crawler
 */
public final class Crawler {

    private Crawler() {
    }

    /**
     * Gets webtoon information as JSON string.
     *
     * <p> {@link Crawler} finds webtoon information,
     * converts it to the JSON format string and return it.
     *
     * <p> The page is different when generally approached and approached by the crawler.
     * All necessary information to download episodes is in JSON format at the script tag.
     *
     * <p> The following code is {@code innerText} in the script tag.
     *
     * <pre>{@code
     *     <script>
     *     __LZ_MESSAGE__ = {...};
     *     __LZ_PRODUCT__ = { productType: 'comic', product: {...}, departure: '', all: {...}, prefree: {...} };
     *     __LZ_DATA__ = {...};
     *     __LZ_CONTEXT__ = "";
     *     __LZ_ERROR_CODE__ = '${error}';
     *     __LZ_ERROR_MESSAGE__ = { 'error.COMIC_EPISODE.NOT_FOUND': "찾으시는 에피소드가 없습니다." };
     *     </script>
     * }</pre>
     *
     * @param arguments arguments required to download episodes
     * @return webtoon information in the JSON format
     * @see ChromeBrowser
     * @see ChromeDriver#getLocalStorage()
     */
    public static String getJson(Arguments arguments) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // 언어/지역 설정 변경 페이지가 노출되면 다운로드할 수 없기에, 미리 API를 호출하여 설정을 변경한다.
        String locale = Languages.from(arguments.getLanguage()).getLocale();
        String localeUrl = URIs.LOCALE.get(arguments.getLanguage(), locale);
        driver.get(localeUrl);

        // 해당 웹툰 페이지로 이동한다.
        String comicUrl = URIs.COMIC.get(arguments.getLanguage(), arguments.getComicName());
        driver.get(comicUrl);

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--episode']")));

        // 웹툰의 정보가 window 객체의 필드로 정의되어 있어, 이를 가져오기 위해 로컬스토리지에 저장한다.
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");

        // 웹툰의 정보를 로컬스토리지에서 가져와 JSON 형식의 문자열을 반환한다.
        return driver.getLocalStorage().getItem("product");
    }

    /**
     * Gets the number of images in the episode.
     *
     * <p> {@link ChromeDriver} finds the root element that is
     * <pre>{@code
     *     <div id="scroll-list" class="viewer-list scroll-control"></div>
     * }</pre>
     * The root element contains several cut-elements that has cut image.
     * You might think that only the numbers needs to be counted, but it's not.
     *
     * <p> The front and back of the images are cover or warning image.
     * There is the way to distinguish it from others.
     * <pre>{@code
     *     <div class="cut cutLicense" data-cut-index="1" data-cut-type="top"></div>
     *     <div class="cut cutLicense" data-cut-index="14" data-cut-type="bottom"></div>
     * }</pre>
     * The points are {@code class} and {@code data-cut-type}.
     *
     * <p> Let's see the other tags you need to download images at.
     * <pre>{@code
     *     <div class="cut" data-cut-index="2" data-cut-type="cut"></div>
     *     <div class="cut" data-cut-index="3" data-cut-type="cut"></div>
     * }</pre>
     * The tag must have the class 'cut' but not 'cutLicense'
     * and the data-cut-type is 'cut' but not 'top' or 'bottom'.
     *
     * @param arguments arguments required to find the number of images in episodes
     * @param episode   episode
     * @return the number of images
     * @see ChromeBrowser
     * @see ChromeDriver
     * @see WebElement#findElements(By)
     */
    public static int getNumOfImagesInEpisode(Arguments arguments, Episode episode) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        String episodeUrl = URIs.EPISODE.get(
                arguments.getLanguage(), arguments.getComicName(), episode.getName());
        driver.get(episodeUrl);

        WebElement scrollList = driver.findElementById("scroll-list");

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfAllElements(scrollList));

        try {
            List<WebElement> images = scrollList.findElements(
                    By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));

            // Success
            return images.size();
        } catch (NoSuchElementException ex) {
            // Fail
            return 0;
        }
    }

}
