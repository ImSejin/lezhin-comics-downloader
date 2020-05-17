package io.github.imsejin.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    @SneakyThrows(IOException.class)
    public String readAllJson(Reader reader) {
        StringBuilder sb = new StringBuilder();

        int cp;
        while ((cp = reader.read()) > 0) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    @SneakyThrows(IOException.class)
    public JsonObject readJsonFromUrl(URL url) {
        try (InputStream in = url.openStream()) {
            Reader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String jsonText = readAllJson(reader);
            JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();

            return json;
        }
    }

    public <T> T toObject(String jsonText, Class<T> clazz) {
        return new Gson().fromJson(jsonText, clazz);
    }

    /**
     * <pre>
     * String jsonText = "{\"id\":1011,\"list\":[{\"id\":10,\"name\":\"foo\"},{\"id\":11,\"name\":\"bar\"}]}";
     * JsonObject jsonObject = JsonParser.parseString(jsonText).getAsJsonObject();
     * JsonArray jsonArray = jsonObject.get("list").getAsJsonArray();
     * 
     * List<T> list = JsonUtil.toList(jsonArray, T.class);
     * </pre>
     */
    public <T> List<T> toList(JsonArray jsonArray, Class<T> clazz) {
        Gson gson = new Gson();

        List<T> list = StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonArray.iterator(), Spliterator.ORDERED), false)
                .map(jsonElement -> gson.fromJson(jsonElement, clazz))
                .collect(Collectors.toList());

        return list;
    }

}
