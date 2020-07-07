package io.github.imsejin.model;

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
            System.err.println("\n    The file 'config.ini' or the section 'account' does not exist.\n");
            System.exit(1);
        }

        String username = section.get("username");
        String password = section.get("password");

        // 유효하지 않은 계정 정보의 경우
        if (StringUtils.anyBlanks(username, password)) {
            System.err.println("\n    ID or password is not valid.\n");
            System.exit(1);
        }

        this.username = username;
        this.password = password;
    }

    private Arguments(String language, String comicName, String episodeRange, String accessToken, Product product, String comicPathname) {
        // 유효하지 않은 언어의 경우
        if (!Languages.contains(language)) {
            printHelper(false, true, false);
            System.exit(1);
        }

        // 유효하지 않은 에피소드 범위의 경우
        if (episodeRange != null && !episodeRange.contains(EpisodeRange.SEPARATOR.value())) {
            printHelper(false, false, true);
            System.exit(1);
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
            System.err.println("    Failed to login. Check your account information.\n");
            ChromeBrowser.getDriver().quit();
            System.exit(1);
        }

        this.accessToken = accessToken;
    }

    private static void printHelper(boolean usage, boolean lang, boolean range) {
        final String SEPARATOR = EpisodeRange.SEPARATOR.value();

        System.err.println();

        if (usage) {
            System.err.println("    - USAGE: java -jar {JAR filename} {id} {password} {language} {comic name} [{episode range}]");
        }

        if (lang) {
            System.err.println("    - WHAT LANGUAGES DOES THE DOWNLOADER SUPPORT?");
            System.err.println("        ko : korean");
            System.err.println("        en : english");
            System.err.println("        ja : japanese");
        }

        if (range) {
            System.err.println("    - HOW TO SETUP EPISODE RANGE?");
            System.err.println("        case 1. skipped : all of episodes");
            System.err.println("        case 2. 8" + SEPARATOR + "      : from ep.8 to the end of the episode");
            System.err.println("        case 3. " + SEPARATOR + "25     : from the beginning of the episode to ep.25");
            System.err.println("        case 4. 1" + SEPARATOR + "10    : from ep.1 to ep.10");
        }

        System.err.println();
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
