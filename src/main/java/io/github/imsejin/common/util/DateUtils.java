package io.github.imsejin.common.util;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.imsejin.common.constants.DateType;

/**
 * 날짜 유틸리티<br>
 * Date utilities
 * 
 * <p>
 * 
 * </p>
 * 
 * @author SEJIN
 */
public final class DateUtils {

    private DateUtils() {}

    /**
     * 윤년인지 확인한다.<br>
     * Checks if it's leap year.
     * 
     * <pre>
     * DateUtils.isLeapYear(2019): false
     * DateUtils.isLeapYear(2020): true
     * </pre>
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ? true : false;
    }

    /**
     * 오늘 날짜를 반환한다.<br>
     * Return today's date.
     * 
     * <pre>
     * DateUtils.today(): "20191231"
     * </pre>
     */
    public static String today() {
        return LocalDate.now().format(ofPattern(DateType.DATE.value()));
    }

    /**
     * 오늘 날짜 중 해당하는 요소를 반환한다.<br>
     * Returns the corresponding element of today's date.
     * 
     * <pre>
     * DateUtils.today(): "20191231"
     * 
     * DateUtils.today(DateType.YEAR): "2019"
     * DateUtils.today(DateType.MONTH): "12"
     * DateUtils.today(DateType.DAY): "31"
     * </pre>
     */
    public static String today(DateType type) {
        return LocalDateTime.now().format(ofPattern(type.value()));
    }

    /**
     * 오늘을 기준으로 어제 날짜(yyyyMMdd)를 반환한다.<br>
     * Returns yesterday's date (yyyyMMdd).
     * 
     * <pre>
     * DateUtils.today(): "20191231"
     * DateUtils.yesterday(): "20191230"
     * </pre>
     */
    public static String yesterday() {
        return LocalDate.now().minusDays(1).format(ofPattern(DateType.DATE.value()));
    }

    /**
     * 오늘을 기준으로 어제 날짜 중 해당하는 요소를 반환한다.<br>
     * Returns the corresponding element of yesterday's date.
     * 
     * <pre>
     * DateUtils.yesterday(): "20191230"
     * 
     * DateUtils.yesterday(DateType.YEAR): "2019"
     * DateUtils.yesterday(DateType.MONTH): "12"
     * DateUtils.yesterday(DateType.DAY): "30"
     * </pre>
     */
    public static String yesterday(DateType type) {
        return LocalDateTime.now().minusDays(1).format(ofPattern(type.value()));
    }

    /**
     * 현재 시간을 반환한다.<br>
     * Returns the current time.
     * 
     * <pre>
     * DateUtils.now(): "20191231175959"
     * </pre>
     */
    public static String now() {
        return LocalDateTime.now().format(ofPattern(DateType.DATE_TIME.value()));
    }

    /**
     * 포맷팅한 현재 시간을 반환한다.<br>
     * Returns the current time formatted.
     * 
     * <pre>
     * DateUtils.formattedNow(): "2019-12-31 17:59:59"
     * </pre>
     */
    public static String formattedNow() {
        return LocalDateTime.now().format(ofPattern(DateType.F_DATE_TIME.value()));
    }

    /**
     * 콘솔 형식의 현재 시간을 반환한다.<br>
     * Returns the current time in console format.
     * 
     * <pre>
     * DateUtils.consoleDateTime(): "2019-12-31 17:59:59.311"
     * </pre>
     */
    public static String consoleDateTime() {
        return LocalDateTime.now().format(ofPattern(DateType.F_ALL.value()));
    }

    /**
     * 실재하는 날짜인지 확인한다.<br>
     * ("yyyyMMdd", "yyyy-MM-dd"의 날짜 형식을 지원함)<br>
     * Check if the date is actual.<br>
     * (Support date formats for "yyyy-MMdd", "yyyy-MM-dd")
     * 
     * <pre>
     * DateUtils.validate("2019-02-28"): true
     * DateUtils.validate("20190229"): false
     * DateUtils.validate("20200229"): true
     * DateUtils.validate("2020-02-29"): true
     * </pre>
     */
    public static boolean validate(String date) {
        try {
            // 날짜 형식을 통일한다
            date = StringUtils.removeMinusChar(date);

            SimpleDateFormat dateFormat = new SimpleDateFormat(DateType.DATE.value());
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    /**
     * 실재하는 날짜인지 확인한다.<br>
     * ("yyyyMMdd", "yyyy-MM-dd"의 날짜 형식을 지원함)<br>
     * Check if the date and day of the week are actual.<br>
     * (Support date formats for "yyyy-MMdd", "yyyy-MM-dd")
     * 
     * <pre>
     * DateUtils.validate("20190228", DayOfWeek.THURSDAY): true
     * DateUtils.validate("2019-02-28", DayOfWeek.THURSDAY): true
     * DateUtils.validate("20190229", DayOfWeek.FRIDAY): false
     * DateUtils.validate("20200229", DayOfWeek.SATURDAY): true
     * DateUtils.validate("2020-02-29", DayOfWeek.SATURDAY): true
     * </pre>
     */
    public static boolean validate(String date, DayOfWeek dayOfWeek) {
        // 유효한 날짜인지 확인한다
        if (!validate(date)) return false;

        final int year = Integer.parseInt(date.substring(0, 4));
        final int month = Integer.parseInt(date.substring(4, 6));
        final int day = Integer.parseInt(date.substring(6));
        LocalDate ld = LocalDate.of(year, month, day);

        // 유효한 요일인지 확인한다
        return ld.getDayOfWeek().equals(dayOfWeek);
    }

    /**
     * 해당 연월의 말일을 포함한 일자를 반환한다.<br>
     * Returns the date including the last day of the year and month.
     * 
     * <pre>
     * DateUtils.withMonthlyLastDate(2019, 2): "20190228"
     * DateUtils.withMonthlyLastDate(2020, 2): "20200229"
     * </pre>
     */
    public static String withMonthlyLastDate(int year, int month) {
        LocalDate lastDate = YearMonth.of(year, month).atEndOfMonth();
        return lastDate.format(ofPattern(DateType.DATE.value()));
    }

    /**
     * 해당 연월의 말일을 포함한 일자를 반환한다.<br>
     * Returns the date including the last day of the year and month.
     * 
     * <pre>
     * DateUtils.withMonthlyLastDate("2019", "2"): "20190228"
     * DateUtils.withMonthlyLastDate("2020", "2"): "20200229"
     * </pre>
     */
    public static String withMonthlyLastDate(String year, String month) {
        LocalDate lastDate = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month)).atEndOfMonth();
        return lastDate.format(ofPattern(DateType.DATE.value()));
    }

    /**
     * 복합날짜 문자열을 변환한다.<br>
     * Converts compounded date strings.
     * 
     * <pre>
     * String date = "20190606,20190501~20190531,yesterday~today,20190601~today,today,yesterday,20190101,20181201~20190101";
     * 
     * DateUtils.convertCompoundedDate(date):
     * {
     *   "simpleDates": [
     *     "20190606",
     *     "20190611",
     *     "20190610",
     *     "20190101"
     *   ],
     *   "complexDates": [
     *     {
     *       "endDate": "20190531",
     *       "strDate": "20190501"
     *     },
     *     {
     *       "endDate": "20190611",
     *       "strDate": "20190610"
     *     },
     *     {
     *       "endDate": "20190611",
     *       "strDate": "20190601"
     *     },
     *     {
     *       "endDate": "20190101",
     *       "strDate": "20181201"
     *     }
     *   ]
     * }
     *</pre>
     */
    public static Map<String, Object> convertCompoundedDate(String date) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<String> simpleDates = new ArrayList<>();
        List<Map<String, String>> complexDates = new ArrayList<>();

        // today와 yesterday 문자열을 날짜 형태로 변환한다
        date = date.replace("today", today());
        date = date.replace("yesterday", yesterday());

        // ,(comma)를 기준으로 날짜 조건을 분리한다
        List<String> temp1 = Arrays.asList(date.split(","));
        List<String> temp2 = new ArrayList<>();
        for (int i = 0; i < temp1.size(); i++) {
            if (temp1.get(i).contains("~")) {
                // 복합일자를 분리한다
                temp2.add(temp1.get(i));
            } else {
                // 존재하는 날짜인지 확인한다
                if (validate(temp1.get(i)) == false) throw new RuntimeException("Invalid date: " + temp1.get(i));

                // 단일일자를 분리한다
                simpleDates.add(temp1.get(i));
            }
        }

        // 복합일자의 시작일과 종료일을 구분한다
        for (int i = 0; i < temp2.size(); i++) {
            String[] temp3 = temp2.get(i).split("~");

            // 존재하는 날짜인지 확인한다
            if (validate(temp3[0]) == false) throw new RuntimeException("Invalid date: " + temp3[0]);
            if (validate(temp3[1]) == false) throw new RuntimeException("Invalid date: " + temp3[1]);

            // 시작일이 종료일보다 크거나 같은지 확인한다
            if (Integer.parseInt(temp3[0]) >= Integer.parseInt(temp3[1])) throw new RuntimeException(
                    "Start date precedes end date: " + temp3[0] + "~" + temp3[1] + "; You can change it to this: " + temp3[0] + " or " + temp3[1]);

            Map<String, String> map = new HashMap<>();
            map.put("strDate", temp3[0]);
            map.put("endDate", temp3[1]);

            complexDates.add(map);
        }

        result.put("simpleDates", simpleDates);
        result.put("complexDates", complexDates);

        return result;
    }

}