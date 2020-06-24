package io.github.imsejin.common.util;

import static io.github.imsejin.common.util.DateUtils.today;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import io.github.imsejin.common.constants.DateType;

@UtilityClass
public class PathnameUtils {

    private final String WINDOWS_SEPARATOR = "\\\\";

    private final String UNIX_SEPARATOR = "/";
    
    /**
     * 현재 경로명을 반환한다.
     * 
     * <pre>
     * PathnameUtils.currentPathname(): ""
     * </pre>
     */
    @SneakyThrows(IOException.class)
    public String currentPathname() {
        return Paths.get(".").toRealPath().toString();
    }

    public String chromeDriverPathname() {
        final String currentPathname = currentPathname();

        String filename = "chromedriver.exe";                                                 // for Windows
        if (Files.notExists(Paths.get(currentPathname, filename))) filename = "chromedriver"; // for Linux and Mac

        return Paths.get(currentPathname, filename).toString();
    }

    /**
     * 모든 파일 구분자를 제거한다.
     * 
     * <pre>
     * PathnameUtils.removeSeparators("C:\\Program Files\\Java"): "C:Program FilesJava"
     * PathnameUtils.removeSeparators("/users/data/java"): "usersdatajava"
     * </pre>
     */
    public String removeSeparators(String pathname) {
        return pathname.replaceAll(WINDOWS_SEPARATOR, "").replaceAll(UNIX_SEPARATOR, "");
    }

    /**
     * 부적절한 경로명을 올바른 경로명으로 정정한다.<br>
     * OS가 Windows인 경우, 절대경로로 지정해도 앞에 구분자가 들어가지 않는다.
     * 
     * <pre>
     * String pathname1 = "\\/ / C:\\ Program Files / \\/\\ \\ Java\\jdk8 /\\/ \\ ";
     * PathnameUtils.trim(false, pathname1): "C:\\Program Files\\Java\\jdk8"
     * 
     * String pathname2 = "/ / \\ users / data /java/jdk8 / ";
     * PathnameUtils.trim(true, pathname2): "/users/data/java/jdk8"
     * 
     * String pathname3 = "/ / \\ users / data /java/jdk8 / ";
     * PathnameUtils.trim(false, pathname3): "users/data/java/jdk8"
     * </pre>
     */
    public String correct(boolean absolute, String pathname) {
        String trimmed = Stream.of(pathname.split(WINDOWS_SEPARATOR)) // split with Windows separators.
                .map(p -> Stream.of(p.split(UNIX_SEPARATOR)).collect(Collectors.joining())) // split with Unix separators.
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.joining(File.separator));

        return absolute && !OSDetector.isWindows()
                ? UNIX_SEPARATOR + trimmed
                : trimmed;
    }

    /**
     * 경로명을 연결한다.
     * 
     * <pre>
     * PathnameUtils.concat(false, "C:\\", "Program Files", "Java"): "C:\\Program Files\\Java"
     * PathnameUtils.concat(true, "/users/", "/data/", "java"): "/users/data/java"
     * PathnameUtils.concat(false, "/users/", "/data/", "java"): "users/data/java"
     * </pre>
     */
    public String concat(boolean absolute, String... pathnames) {
        return correct(absolute,
                Stream.of(pathnames).collect(Collectors.joining(File.separator)));
    }

    /**
     * 경로 끝에 현재의 연/월(yyyy/MM) 경로를 추가한다.<br>
     * Adds the current year/month (yyyy/MM) pathname to the end of the pathname.
     * 
     * <pre>
     * DateUtils.getToday(): "20191231"
     * 
     * PathnameUtils.appendYearMonth("C:\\Program Files"): "C:\\Program Files\\2019\\12"
     * </pre>
     */
    public String appendYearMonth(String pathname) {
        return concat(false, pathname, today(DateType.YEAR), today(DateType.MONTH));
    }

    /**
     * 경로 끝에 현재의 연/월/일(yyyy/MM/dd) 경로를 추가한다.<br>
     * Adds the current year/month/day (yyyy/MM/dd) pathname to the end of the pathname.
     * 
     * <pre>
     * DateUtils.getToday(): "20191231"
     * 
     * PathnameUtils.appendYearMonthDay("C:\\Program Files"): "C:\\Program Files\\2019\\12\\31"
     * </pre>
     */
    public String appendYearMonthDay(String pathname) {
        return concat(false, pathname, today(DateType.YEAR), today(DateType.MONTH), today(DateType.DAY));
    }

}
