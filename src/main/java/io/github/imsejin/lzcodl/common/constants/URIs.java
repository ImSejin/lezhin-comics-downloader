package io.github.imsejin.lzcodl.common.constants;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum URIs implements KeyValue {

    /**
     * 로그인 URI<br>
     * the URI for login
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/login
     * }</pre>
     */
    LOGIN("https://www.lezhin.com/{language}/login") {
        @Override
        public String get(String... param) {
            return this.value().replaceFirst("\\{language}", param[0]);
        }
    },

    /**
     * <pre>{@code
     *     https://www.lezhin.com/ko/locale/ko-KR?locale=ko-KR
     * }</pre>
     */
    LOCALE("https://www.lezhin.com/{language}/locale/{locale}?locale={locale}") {
        @Override
        public String get(String... param) {
            return this.value()
                    .replaceFirst("\\{language}", param[0])
                    .replaceAll("\\{locale}", param[1]);
        }
    },

    /**
     * 각 회차의 아이디를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI obtain ID for each episode
     *
     * <pre>
     * /comic/
     * </pre>
     */
    COMIC("https://www.lezhin.com/{language}/comic/{comicName}") {
        @Override
        public String get(String... param) {
            return this.value()
                    .replaceFirst("\\{language}", param[0])
                    .replaceFirst("\\{comicName}", param[1]);
        }
    },

    EPISODE("https://www.lezhin.com/{language}/comic/{comicName}/{episodeName}") {
        @Override
        public String get(String... param) {
            return this.value()
                    .replaceFirst("\\{language}", param[0])
                    .replaceFirst("\\{comicName}", param[1])
                    .replaceFirst("\\{episodeName}", param[2]);
        }
    };

    private final String value;

    public static boolean contains(String value) {
        return Arrays.stream(URIs.values())
                .anyMatch(uri -> uri.value.equals(value));
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.value;
    }

    public abstract String get(String... param);

}
