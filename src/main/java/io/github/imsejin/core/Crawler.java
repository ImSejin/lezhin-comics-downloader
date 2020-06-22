package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.ADULT_EPISODE_ID_URI_PARAM;
import static io.github.imsejin.common.Constants.LOGIN_URI;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Crawler {

    /**
     * 웹툰 정보를 찾아, JSON 형태의 문자열로 반환한다.<br>
     * Finds webtoon information, converts to the JSON format string and return it.<br><br>
     * 
     * 일반적으로 접근했을 때와 크롤러로 접근했을 때의 페이지가 다르다.<br>
     * 다행히 필요한 모든 정보가 script 태그 내 JSON 형태로 있어서 이를 이용한다.
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
    @SneakyThrows(IOException.class)
    public String getJson(String username, String password, String comicName) {
        // 해당 웹툰의 정보가 담긴 JSON을 찾기 위해 <script/> 태그를 찾는다
//        Document doc = Jsoup.connect(EPISODE_ID_URI_PREFIX + comicName).get();
        Document doc = Jsoup.connect(LOGIN_URI + ADULT_EPISODE_ID_URI_PARAM + comicName)
                .data("username", username)
                .data("password", password)
                .post();
        Elements elements = doc.select("body > script");

        // `__LZ_PRODUCT__` 객체가 있는 script 태그의 내용을 가져온다
        String script = StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements.iterator(), Spliterator.ORDERED), false)
                .map(element -> element.childNode(0).toString())
                .filter(node -> node.contains("__LZ_PRODUCT__"))
                .findFirst()
                .get();

        // `__LZ_PRODUCT__.product` 부분을 파싱한다
        String productJson = Stream.of(script.split("\n"))
                .filter(line -> line.contains("product: {"))
                .map(String::trim)
                .findAny()
                .get();

        // 앞 부분 "product: " 제거
        productJson = productJson.substring("product: ".length());

        // 마지막 "," 제거
        productJson = productJson.substring(0, productJson.length() - 1);

        return productJson;
    }

}
