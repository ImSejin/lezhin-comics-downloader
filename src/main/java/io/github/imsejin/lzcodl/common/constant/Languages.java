package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Languages implements KeyValue {

    KOREAN("ko", "ko-KR"),
    ENGLISH("en", "en-US"),
    JAPANESE("ja", "ja-JP");

    private final String value;

    @Getter
    private final String locale;

    public static boolean contains(String value) {
        return Arrays.stream(values())
                .anyMatch(lang -> lang.value.equals(value));
    }

    public static Languages from(String value) {
        Languages languages = Arrays.stream(values())
                .filter(lang -> lang.value.equals(value))
                .findAny()
                .orElse(null);

        if (languages == null) throw new IllegalArgumentException("Invalid value for languages: " + value);
        return languages;
    }

    @Override
    public String key() {
        return name();
    }

    @Override
    public String value() {
        return this.value;
    }

}
