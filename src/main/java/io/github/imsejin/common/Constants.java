package io.github.imsejin.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    /**
     * 회차별 아이디를 얻을 수 있는 URI의 접두사
     */
    public final String EPISODE_ID_URI_PREFIX = "https://www.lezhin.com/ko/comic/";

    /**
     * 회차별 정보를 얻을 수 있는 URI의 접두사
     */
    public final String EPISODE_INFO_URI_PREFIX = "http://cdn.lezhin.com/episodes/";

    /**
     * 이미지를 불러오는 URI의 접두사 
     */
    public final String IMG_URI_PREFIX = "https://cdn.lezhin.com/v2/comics/";

    /**
     * 이미지를 불러오는 URI의 접미사
     */
    public final String IMG_URI_PARAM = "&purchased=false";

    /**
     * 이미지 포맷 확장자
     */
    public final String IMG_FORMAT_EXTENSION = ".jpg"; // or ".webp"

}
