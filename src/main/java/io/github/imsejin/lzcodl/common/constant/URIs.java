package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    LOGIN("https://www.lezhin.com/{language}/login"),

    /**
     * <pre>{@code
     *     https://www.lezhin.com/ko/locale/ko-KR?locale=ko-KR
     * }</pre>
     */
    LOCALE("https://www.lezhin.com/{language}/locale/{locale}?locale={locale}"),

    /**
     * 각 회차의 아이디를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI obtain ID for each episode
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/comic/redhood
     * }</pre>
     */
    COMIC("https://www.lezhin.com/{language}/comic/{comicName}"),

    /**
     * <pre>{@code
     *     https://www.lezhin.com/ko/comic/redhood/9
     *     https://www.lezhin.com/ko/comic/redhood/e1
     * }</pre>
     */
    EPISODE("https://www.lezhin.com/{language}/comic/{comicName}/{episodeName}"),

    EXPIRED("https://www.lezhin.com/{language}/error/expired"),

    LIB_COMIC("https://www.lezhin.com/{language}/library/comic/{locale}/{comicName}"),

    LIB_EPISODE("https://www.lezhin.com/{language}/library/comic/{locale}/{comicName}/{episodeName}");

    private static final Pattern pattern = Pattern.compile("\\{(.+?)}", Pattern.MULTILINE);

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

    public String get(String... params) {
        if (params == null || params.length == 0) return this.value;

        Matcher matcher = pattern.matcher(this.value);

        // Converts all variables to parameters.
        String uri = this.value;
        for (int i = 0; i < params.length && matcher.find(); i++) {
            uri = uri.replaceAll("\\{" + matcher.group(1) + '}', params[i]);
        }

        // Validates all variables in URI are converted to parameters.
        if (pattern.matcher(uri).find()) throw new RuntimeException("Template URI has not matched variable(s)");

        return uri;
    }

}
