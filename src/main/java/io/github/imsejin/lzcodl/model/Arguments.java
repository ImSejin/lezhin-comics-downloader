package io.github.imsejin.lzcodl.model;

import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.common.util.StringUtils;
import io.github.imsejin.lzcodl.common.UsagePrinter;
import io.github.imsejin.lzcodl.common.constant.EpisodeRange;
import io.github.imsejin.lzcodl.common.constant.Languages;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

@Getter
@Setter
@ToString
public class Arguments {

    private final String username;
    private final String password;
    private final String language;
    private final String comicName;
    private final String episodeRange;
    private final boolean debugging;

    private String accessToken;
    private Product product;
    private Path comicPath;
    private boolean expiredComic;

    {
        final File file = new File(PathnameUtils.getCurrentPathname(), "config.ini");

        Map<String, String> section = null;
        try {
            section = IniUtils.readSection(file, "account");
        } catch (Exception ex) {
            // 'config.ini' 파일이 없거나, 'account' 섹션이 없는 경우
            UsagePrinter.printAndQuit("The file 'config.ini' or the section 'account' does not exist.");
        }

        String username = section.get("username");
        String password = section.get("password");

        // 유효하지 않은 계정 정보의 경우
        if (StringUtils.isNullOrBlank(username) || StringUtils.isNullOrBlank(password)) {
            UsagePrinter.printAndQuit("ID or password is not valid.");
        }

        this.username = username;
        this.password = password;
    }

    @Builder
    private Arguments(String language, String comicName, String episodeRange, boolean debugging) {
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
                    "    case 1. skipped : all episodes",
                    String.format("    case 2. 8%s      : from ep.8 to the last", separator),
                    String.format("    case 3. %s25     : from the first to ep.25", separator),
                    String.format("    case 4. 1%s10    : from ep.1 to ep.10", separator));
        }

        this.language = language;
        this.comicName = comicName;
        this.episodeRange = episodeRange;
        this.debugging = debugging;
    }

}
