package io.github.imsejin.lzcodl.common.constant;

import io.github.imsejin.common.constant.interfaces.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public enum URIs implements KeyValue {

    /**
     * Login page.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/login
     * }</pre>
     */
    LOGIN("https://www.lezhin.com/{language}/login"),

    /**
     * Page to choose your locale.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/locale/ko-KR?locale=ko-KR
     * }</pre>
     */
    LOCALE("https://www.lezhin.com/{language}/locale/{locale}?locale={locale}"),

    /**
     * Comic page that shows its episodes.
     *
     * <pre>{@code
     *     https://www.lezhin.com/ko/comic/redhood
     * }</pre>
     */
    COMIC("https://www.lezhin.com/{language}/comic/{comicName}"),

    /**
     * Episode page that shows its cuts(images).
     *
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

    /**
     * Checks if {@link URIs} that has the value exists.
     *
     * @param value {@link #value()}
     * @return {@link URIs}
     */
    public static boolean contains(String value) {
        if (value == null) return false;
        return Arrays.stream(values()).map(uri -> uri.value).anyMatch(value::equals);
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.value;
    }

    /**
     * Returns URI.
     *
     * @param params parameters
     * @return URI string
     */
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
