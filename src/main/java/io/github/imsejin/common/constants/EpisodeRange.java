package io.github.imsejin.common.constants;

import java.util.stream.Stream;

public enum EpisodeRange implements Dictionary {

    /**
     * 에피소드 번호의 구분자<br>
     * Separator of episode number
     */
    SEPARATOR("~");

    private final String value;

    EpisodeRange(String value) {
        this.value = value;
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.value;
    }

    public static boolean contains(String value) {
        return Stream.of(EpisodeRange.values()).anyMatch(range -> range.value.equals(value));
    }

}
