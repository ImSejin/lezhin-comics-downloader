package io.github.imsejin.model;

import io.github.imsejin.core.ChromeBrowser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Arguments {

    private final String username;
    private final String password;
    private final String language;
    private final String comicName;
    private final String episodeRange;

    private String accessToken;

    @Setter
    private Product product;
    @Setter
    private String comicPathname;

    public void setAccessToken(String accessToken) {
        // 로그인에 실패하면, 프로그램을 종료한다.
        if (accessToken == null) {
            System.err.println("    Failed to login. Check your account information.\n");
            ChromeBrowser.getDriver().quit();
            System.exit(1);
        }

        this.accessToken = accessToken;
    }

}
