package io.github.imsejin.lzcodl.core;

import io.github.imsejin.lzcodl.common.Loggers;
import io.github.imsejin.lzcodl.common.constant.Languages;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Arguments;
import io.github.imsejin.lzcodl.model.Episode;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
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
     * @param args arguments required to download episodes
     * @return webtoon information in the JSON format
     * @see ChromeBrowser
     * @see ChromeDriver#getLocalStorage()
     */
    @Nullable
    public static String getJson(Arguments args) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // 언어/지역 설정 변경 페이지가 노출되면 다운로드할 수 없기에, 미리 API를 호출하여 설정을 변경한다.
        String locale = Languages.from(args.getLanguage()).getLocale();
        String localeUrl = URIs.LOCALE.get(args.getLanguage(), locale);
        Loggers.getLogger().debug("Change locale setting: {}", localeUrl);
        driver.get(localeUrl);

        // 해당 웹툰 페이지로 이동한다.
        String comicUrl = URIs.COMIC.get(args.getLanguage(), args.getComicName());
        Loggers.getLogger().info("Request comic page: {}", comicUrl);
        driver.get(comicUrl);

        // 서비스 종료된 웹툰인지 확인한다.
        if (driver.getCurrentUrl().equals(URIs.EXPIRED.get(args.getLanguage()))) {
            Loggers.getLogger().info("Comic is expired -> try to find it in 'My Library'");
            args.setExpiredComic(true);
            return getJsonInMyLibrary(args);
        }

        // Waits for DOM to complete the rendering.
        final int timeout = 15;
        Loggers.getLogger().debug("Wait up to {} sec for episode list to be rendered", timeout);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main[@id='main' and @class='lzCntnr lzCntnr--episode']")));

        // 웹툰의 정보가 window 객체의 필드로 정의되어 있어, 이를 가져오기 위해 로컬스토리지에 저장한다.
        driver.executeScript("localStorage.setItem('product', JSON.stringify(window.__LZ_PRODUCT__.product));");

        // 웹툰의 정보를 로컬스토리지에서 가져와 JSON 형식의 문자열을 반환한다.
        return driver.getLocalStorage().getItem("product");
    }

    private static String getJsonInMyLibrary(Arguments args) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        String locale = Languages.from(args.getLanguage()).getLocale();
        String libComicUrl = URIs.LIB_COMIC.get(args.getLanguage(), locale, args.getComicName());
        Loggers.getLogger().debug("Request comic page in 'My Library': {}", libComicUrl);
        driver.get(libComicUrl);

        // Waits for DOM to complete the rendering.
        final int timeout = 15;
        Loggers.getLogger().debug("Wait up to {} sec for episode list to be rendered", timeout);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[@id='library-episode-list' and @class='epsList']")));

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
     * @param args    arguments required to find the number of images in episodes
     * @param episode episode
     * @return the number of images
     * @see ChromeBrowser
     * @see ChromeDriver
     * @see WebElement#findElements(By)
     */
    public static int getNumOfImagesInEpisode(Arguments args, Episode episode) {
        ChromeDriver driver = ChromeBrowser.getDriver();

        // 서비스 종료된 웹툰이면 '내 서재'로 접근한다.
        String episodeUrl = args.isExpiredComic()
            ? URIs.LIB_EPISODE.get(args.getLanguage(), Languages.from(args.getLanguage()).getLocale(), args.getComicName(), episode.getName())
            : URIs.EPISODE.get(args.getLanguage(), args.getComicName(), episode.getName());

        Loggers.getLogger().debug("Request episode page: {}", episodeUrl);
        driver.get(episodeUrl);

        WebElement scrollList = driver.findElementById("scroll-list");

        // Waits for DOM to complete the rendering.
        final int timeout = 15;
        Loggers.getLogger().debug("Wait up to {} sec for images to be rendered", timeout);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
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
