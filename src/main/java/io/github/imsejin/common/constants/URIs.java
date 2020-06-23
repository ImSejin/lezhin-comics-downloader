package io.github.imsejin.common.constants;

public enum URIs implements GettableEnum {

    /**
     * 레진코믹스의 메인페이지 URI<br>
     * Main page URI of rezhin comics
     */
    HOME("https://www.lezhin.com/ko"),

    /**
     * 로그인 URI<br>
     * the URI for login
     */
    LOGIN(HOME.value() + "/login"),

    /**
     * 각 회차의 아이디를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI obtain ID for each episode
     */
    COMIC(HOME.value() + "/comic/"),

    CDN("http://cdn.lezhin.com/"),

    /**
     * 각 회차의 정보를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI to obtain information for each episode
     */
    EPISODE_INFO(CDN.value() + "episodes/"),

    /**
     * 이미지 URI의 접두사<br>
     * The prefix of image URI
     */
    IMG(CDN.value() + "v2/comics/");

    private String value;

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

}
