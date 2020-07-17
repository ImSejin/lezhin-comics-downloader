package io.github.imsejin.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Languages implements KeyValue {

    KOREAN("ko", "ko-KR"),
    ENGLISH("en", "en-US"),
    JAPANESE("ja", "ja-JP");

    private final String value;

    @Getter
    private final String locale;

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
