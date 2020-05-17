package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.*;

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

    @SneakyThrows(IOException.class)
    public String getJson(String comicName) {
        // 해당 웹툰의 정보가 담긴 JSON을 찾기 위해 <script/> 태그를 찾는다
        Document doc = Jsoup.connect(EPISODE_ID_URI_PREFIX + comicName).get();
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
