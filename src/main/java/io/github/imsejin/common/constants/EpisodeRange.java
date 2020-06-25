package io.github.imsejin.common.constants;

public enum EpisodeRange implements GettableEnum {

    /**
     * 에피소드 번호의 구분자<br>
     * Separator of episode number
     */
    SEPARATOR("~");

    private String value;

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

}
