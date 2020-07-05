package io.github.imsejin.common.constants;

import java.util.stream.Stream;

public enum Languages implements Dictionary {

    KOREAN("ko"),
    ENGLISH("en"),
    JAPANESE("ja");

    private final String value;

    Languages(String lang) {
        this.value = lang;
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
        return Stream.of(Languages.values()).anyMatch(lang -> lang.value.equals(value));
    }

}
