package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.LOGIN_URI;

import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.github.imsejin.common.util.StringUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginHelper {

    public String login(String username, String password) {
        // 유효하지 않은 계정 정보의 경우
        if (StringUtil.areAnyBlanks(username, password)) {
            System.err.println("\n    ID or password is not valid.");
            return null;
        }

        String accessToken = getAccessToken(username, password);

        // 존재하지 않는 계정의 경우
        if (StringUtil.isBlank(accessToken)) {
            System.err.println("\n    The account does not exists.");
            return null;
        }

        return accessToken;
    }

    /**
     * 로그인하여 액세스 토큰을 찾아, 할당한다.<br>
     * Finds access token after login, assigns it.
     * 
     * <pre>{@code
     * <script>
     * __LZ_CONFIG__ = _.merge(window.__LZ_CONFIG__, {
     *     apiUrl: 'api.lezhin.com',
     *     cdnUrl: 'https://cdn.lezhin.com',
     *     recoUrl: 'dondog.lezhin.com',
     *     payUrl: 'https://pay.lezhin.com',
     *     pantherUrl: 'https://panther.lezhin.com',
     *     locale: 'ko-KR',
     *     country: 'kr',
     *     language: 'ko',
     *     adultKind: 'kid',
     *     allowAdult: true,
     *     isEmbedded: false,
     *     now: (new Date('2020-06-22T09:27:54+09:00')).getTime(),
     *     authAdult: 'true',
     *     rid: '58Hk',
     *     token: '5be30a25-a044-410c-88b0-19a1da968a64',
     *     genres: {"romance":"로맨스","fantasy":"판타지","horror":"호러","lightnovel":"라이트노벨","sports":"스포츠","gl":"백합","historical":"시대극","bl":"BL","gore":"스릴러","girl":"소녀만화","gag":"개그","food":"음식","otona":"오토나","drama":"드라마","mystery":"미스터리","sf":"SF","martial":"무협","school":"학원","mature_female":"레이디스코믹","tl":"TL","action":"액션","adult":"성인","day":"일상","gallery":"갤러리"}
     * });
     * 
     * __LZ_ME__ = _.merge(window.__LZ_ME__, {
     *     userId: '5412133348822268',
     *     adult: true,
     *     email: '',
     *     paidTime: 0,
     *     paidCount: 0,
     *     coin: { android: 0, ios: 0, web: 0 },
     *     point:{ android: 0, ios: 0, web: 0 }
     * });
     * ...
     * </script>
     * }</pre>
     */
    @SneakyThrows(IOException.class)
    private String getAccessToken(String username, String password) {
        Document doc = Jsoup.connect(LOGIN_URI)
                .data("username", username)
                .data("password", password)
                .post();
        Elements elements = doc.select("body > script");

        String script = StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements.iterator(), Spliterator.ORDERED), false)
                .map(Element::toString)
                .filter(node -> node.contains("token: "))
                .findFirst()
                .orElse(null);

        // 존재하지 않는 계정의 경우
        if (script == null) return null;

        // "token: '5be30a25-a044-410c-88b0-19a1da968a64'"
        String accessToken = StringUtil.match("token: '([\\w-]*)'", script, 1);
        System.out.println(accessToken);
        
        return accessToken;
//        Matcher matcher = Pattern.compile("token: '([\\w-]*)'", Pattern.MULTILINE).matcher(script);
//
//        StringBuilder sb = new StringBuilder();
//        while (matcher.find()) {
//            sb.append(matcher.group(1));
//        }
//
//        return sb.toString();
//        return sb.toString().replaceAll("'", "").replace("token: ", "");
    }

}
