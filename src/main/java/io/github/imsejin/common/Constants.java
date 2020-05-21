package io.github.imsejin.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    /**
     * 각 회차의 아이디를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI obtain ID for each episode
     */
    public final String EPISODE_ID_URI_PREFIX = "https://www.lezhin.com/ko/comic/";

    /**
     * 각 회차의 정보를 얻을 수 있는 URI의 접두사<br>
     * The prefix of URI to obtain information for each episode
     */
    public final String EPISODE_INFO_URI_PREFIX = "http://cdn.lezhin.com/episodes/";

    /**
     * 이미지 URI의 접두사<br>
     * The prefix of image URI
     */
    public final String IMG_URI_PREFIX = "https://cdn.lezhin.com/v2/comics/";

    /**
     * 이미지 URI의 매개변수; 해당 에피소드의 구매 여부<br>
     * The parameter of image URI; this episode is purchased or not
     */
    public final String IMG_URI_PARAM = "&purchased=false";

    /**
     * 이미지 포맷 확장자<br>
     * File extension matching image format
     */
    public final String IMG_FORMAT_EXTENSION = ".jpg"; // or ".webp"

}
