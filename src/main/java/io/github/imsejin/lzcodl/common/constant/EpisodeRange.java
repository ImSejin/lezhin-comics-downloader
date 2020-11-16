package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum EpisodeRange implements KeyValue {

    /**
     * 에피소드 번호의 구분자<br>
     * Separator of episode number
     */
    SEPARATOR("~");

    private final String value;

    public static boolean contains(String value) {
        return Arrays.stream(EpisodeRange.values())
                .anyMatch(range -> range.value.equals(value));
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.value;
    }

}
