package io.github.imsejin.lzcodl.common.constants;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
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

    @Nullable
    public static Languages from(String value) {
        return Arrays.stream(values())
                .filter(lang -> lang.value.equals(value))
                .findAny()
                .orElse(null);
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
