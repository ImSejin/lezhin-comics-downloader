package io.github.imsejin.model;

import io.github.imsejin.common.UsagePrinter;
import io.github.imsejin.common.constants.EpisodeRange;
import io.github.imsejin.common.constants.Languages;
import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.core.ChromeBrowser;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Getter
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

    {
        final File file = new File(PathnameUtils.currentPathname(),"config.ini");

        Map<String, String> section = null;
        try {
            section = IniUtils.readSection(file, "account");
        } catch (IOException ex) {
            // 'config.ini' 파일이 없거나, 'account' 섹션이 없는 경우
            UsagePrinter.printAndQuit("The file 'config.ini' or the section 'account' does not exist.");
        }

        String username = section.get("username");
        String password = section.get("password");

        // 유효하지 않은 계정 정보의 경우
        if (StringUtils.anyBlanks(username, password)) {
            UsagePrinter.printAndQuit("ID or password is not valid.");
        }

        this.username = username;
        this.password = password;
    }

    private Arguments(String language, String comicName, String episodeRange, String accessToken, Product product, String comicPathname) {
        // 유효하지 않은 언어의 경우
        if (!Languages.contains(language)) {
            UsagePrinter.printAndQuit(
                    "- WHAT LANGUAGES DOES THE DOWNLOADER SUPPORT?",
                    "    ko : korean",
                    "    en : english",
                    "    ja : japanese");
        }

        // 유효하지 않은 에피소드 범위의 경우
        final String separator = EpisodeRange.SEPARATOR.value();
        if (episodeRange != null && !episodeRange.contains(separator)) {
            UsagePrinter.printAndQuit(
                    "- HOW TO SETUP EPISODE RANGE?",
                    "    case 1. skipped : all of episodes",
                    String.format("    case 2. 8%s      : from ep.8 to the end of the episode", separator),
                    String.format("    case 3. %s25     : from the beginning of the episode to ep.25", separator),
                    String.format("    case 4. 1%s10    : from ep.1 to ep.10", separator));
        }

        this.language = language;
        this.comicName = comicName;
        this.episodeRange = episodeRange;
        this.accessToken = accessToken;
        this.product = product;
        this.comicPathname = comicPathname;
    }

    public void setAccessToken(String accessToken) {
        // 로그인에 실패하면, 프로그램을 종료한다.
        if (accessToken == null) {
            ChromeBrowser.getDriver().quit();
            UsagePrinter.printAndQuit("Failed to login. Check your account information.");
        }

        this.accessToken = accessToken;
    }

    public static ArgumentsBuilder builder() {
        return new ArgumentsBuilder();
    }

    public static class ArgumentsBuilder {

        private ArgumentsBuilder() {}

        private String _language;
        private String _comicName;
        private String _episodeRange;
        private String _accessToken;
        private Product _product;
        private String _comicPathname;

        public Arguments build() {
            return new Arguments(_language, _comicName, _episodeRange, _accessToken, _product, _comicPathname);
        }

        public ArgumentsBuilder language(String language) {
            this._language = language;
            return this;
        }

        public ArgumentsBuilder comicName(String comicName) {
            this._comicName = comicName;
            return this;
        }

        public ArgumentsBuilder episodeRange(String episodeRange) {
            this._episodeRange = episodeRange;
            return this;
        }

        public ArgumentsBuilder accessToken(String accessToken) {
            this._accessToken = accessToken;
            return this;
        }

        public ArgumentsBuilder product(Product product) {
            this._product = product;
            return this;
        }

        public ArgumentsBuilder comicPathname(String comicPathname) {
            this._comicPathname = comicPathname;
            return this;
        }

    }

}
