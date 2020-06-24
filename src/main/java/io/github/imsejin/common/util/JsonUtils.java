package io.github.imsejin.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * JSON 유틸리티<br>
 * JSON utilities
 * 
 * <p>
 * JSON을 처리하는 유틸리티.<br>
 * Utilities that process the JSON.
 * </p>
 * 
 * @author SEJIN
 */
public final class JsonUtils {

    private JsonUtils() {}

    /**
     * URL에서 반환하는 JSON 형식의 문자열을 읽어 JsonObject로 변환한다.<br>
     * Reads the JSON format string returned by the URL and converts it to JsonObject.
     * 
     * <pre>
     * String uriText = "http://cdn.lezhin.com/episodes/snail/1.json?access_token=5be30a25-a044-410c-88b0-19a1da968a64";
     * URL url = URI.create(uriText).toURL();
     * 
     * JsonObject json = JsonUtils.readJsonFromUrl(url);
     * </pre>
     */
    public static JsonObject readJsonFromUrl(URL url) throws IOException, JsonSyntaxException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String jsonText = readAllJson(reader);
            JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();

            return json;
        }
    }

    /**
     * Reader가 읽은 모든 문자들을 문자열로 변환한다.<br>
     * Converts all characters read by Reader to string.
     */
    private static String readAllJson(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * JSON 형식의 문자열을 지정한 오브젝트로 변환한다.<br>
     * Converts the JSON format string to the specified object.
     * 
     * <pre>
     * String jsonText = "{\"id\":1011,\"list\":[{\"id\":10,\"name\":\"foo\"},{\"id\":11,\"name\":\"bar\"}]}";
     * 
     * T t = JsonUtils.toObject(jsonText, T.class);
     * </pre>
     */
    public static <T> T toObject(String jsonText, Class<T> clazz) throws JsonSyntaxException {
        return new Gson().fromJson(jsonText, clazz);
    }

    /**
     * JsonArray를 지정한 오브젝트의 리스트로 변환한다.<br>
     * Converts JsonArray to a list of the specified objects.
     * 
     * <pre>
     * String jsonText = "{\"id\":1011,\"list\":[{\"id\":10,\"name\":\"foo\"},{\"id\":11,\"name\":\"bar\"}]}";
     * JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
     * JsonArray jsonArray = jsonObject.get("list").getAsJsonArray();
     * 
     * List<T> list = JsonUtils.toList(jsonArray, T.class);
     * </pre>
     */
    public static <T> List<T> toList(JsonArray jsonArray, Class<T> clazz) throws JsonSyntaxException {
        Gson gson = new Gson();

        List<T> list = StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonArray.iterator(), Spliterator.ORDERED), false)
                .map(jsonElement -> gson.fromJson(jsonElement, clazz))
                .collect(Collectors.toList());

        return list;
    }

}
