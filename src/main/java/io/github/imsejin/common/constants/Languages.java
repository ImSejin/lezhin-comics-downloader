package io.github.imsejin.common.constants;

import lombok.Getter;

import java.util.stream.Stream;

public enum Languages implements Dictionary {

    KOREAN("ko", "ko-KR"),
    ENGLISH("en", "en-US"),
    JAPANESE("ja", "ja-JP");

    private final String value;

    @Getter
    private final String locale;

    Languages(String lang, String locale) {
        this.value = lang;
        this.locale = locale;
    }

    @Override
    public String key() {
        return name();
    }

    @Override
    public String value() {
        return this.value;
    }

    public static boolean contains(String value) {
        return Stream.of(values())
                .anyMatch(lang -> lang.value.equals(value));
    }

    public static Languages from(String value) {
        return Stream.of(values())
                .filter(lang -> lang.value.equals(value))
                .findAny()
                .orElse(null);
    }

}
