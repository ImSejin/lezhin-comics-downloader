package io.github.imsejin.core;

import static io.github.imsejin.common.Constants.*;

import java.io.IOException;
import java.util.Scanner;
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
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginHelper {

    private final Scanner scanner = new Scanner(System.in);

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private String accessToken;

    private final int LOGIN_LIMIT = 3;
    private int loginCount = 0;

    public void login() {
        receive();

        setAccessToken();

        // 로그인에 실패하면, 재시도 여부를 묻는다.
        boolean valid = validate();
        if (!valid) System.exit(1);

        // Crawler에서 Scanner를 다시 사용해야 하기에 닫지 않는다.
        // 로그인하여 세션을 유지하는 법을 알아내면 닫자.
//        if (scanner != null) scanner.close();
    }

    /**
     * 계정 정보를 입력받는다.<br>
     * Receives the account information.
     */
    private void receive() {
        System.out.print("ID: ");
        username = scanner.nextLine();
        System.out.print("PW: ");
        password = scanner.nextLine();
    }

    private boolean validate() {
        loginCount++;

        if (accessToken != null) {
            System.out.println("\nSuccess to login.\n");
            return true;
        }

        System.out.println("\nFailed to login. Check your account information.");
        if (loginCount > LOGIN_LIMIT) {
            System.out.println("Application exited.");
            return false;
        }

        while (true) {
            System.out.print("Do you want to try again? (true/y/yes): ");
            Boolean retry = StringUtil.string2boolean(scanner.nextLine());
            System.out.println();

            if (retry != null && retry) {
                login();
                return accessToken != null;
            } else {
                return false;
            }
        }
    }

    /**
     * 로그인하여 액세스 토큰을 찾아, 할당한다.<br>
     * Finds access token after login, assigns it.
     */
    @SneakyThrows(IOException.class)
    private void setAccessToken() {
        // 유효하지 않은 계정 정보의 경우
        if (StringUtil.areAnyBlanks(username, password)) {
            accessToken = null;
            return;
        }

        Document doc = Jsoup.connect(LOGIN_URI_PREFIX)
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
        if (script == null) {
            accessToken = null;
            return;
        }

        Matcher matcher = Pattern.compile("token: '([\\w\\--z]*)'", Pattern.MULTILINE).matcher(script);

        // "token: '5be30a25-a044-410c-88b0-19a1da968a64'"
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }

        accessToken = sb.toString().replaceAll("'", "").replace("token: ", "");
    }

}
