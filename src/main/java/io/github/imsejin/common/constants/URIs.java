package io.github.imsejin.common.constants;

import java.util.stream.Stream;

public enum URIs implements Dictionary {

    /**
     * 레진코믹스의 메인페이지 URI<br>
     * Main page URI of rezhin comics
     *
     * <pre>
     * https://www.lezhin.com/
     * </pre>
     */
    HOME("https://www.lezhin.com/"),

    /**
     * 로그인 URI<br>
     * the URI for login
     *
     * <pre>
     * /login
     * </pre>
     */
    LOGIN("/login"),

    /**
     * 각 회차의 아이디를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI obtain ID for each episode
     *
     * <pre>
     * /comic/
     * </pre>
     */
    COMIC("/comic/"),

    /**
     * <pre>
     * http://cdn.lezhin.com/
     * </pre>
     */
    CDN("http://cdn.lezhin.com/"),

    /**
     * 각 회차의 정보를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI to obtain information for each episode
     *
     * <pre>
     * http://cdn.lezhin.com/episodes/
     * </pre>
     */
    EPISODE_INFO(CDN.value() + "episodes/"),

    /**
     * 이미지 URI의 접두사<br>
     * The prefix of image URI
     *
     * <pre>
     * http://cdn.lezhin.com/v2/comics/
     * </pre>
     */
    IMG(CDN.value() + "v2/comics/");

    private final String value;

    URIs(String uri) {
        this.value = uri;
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
        return Stream.of(URIs.values())
                .anyMatch(uri -> uri.value.equals(value));
    }

}
