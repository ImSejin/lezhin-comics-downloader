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

import lombok.experimental.UtilityClass;

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
@UtilityClass
public class DateUtils {

    /** 날짜 유형 */
    public enum DateType {
        YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
    }

    /**
     * 날짜 유형을 문자열로 변환한다.
     */
    private String convertTypeToPattern(DateType type) {
        String pattern;

        if (type == DateType.YEAR) pattern = "yyyy";
        else if (type == DateType.MONTH) pattern = "MM";
        else if (type == DateType.DAY) pattern = "dd";
        else if (type == DateType.HOUR) pattern = "HH";
        else if (type == DateType.MINUTE) pattern = "mm";
        else if (type == DateType.SECOND) pattern = "ss";
        else pattern = "yyyyMMdd";

        return pattern;
    }

    /**
     * 입력받은 연도가 윤년인지 아닌지 검사한다.
     */
    public boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ? true : false;
    }

    /**
     * 오늘 날짜(yyyyMMdd)를 반환한다.
     * 
     * <pre>
     * DateUtils.getToday(): "20191231"
     * </pre>
     */
    public String getToday() {
        return LocalDate.now().format(ofPattern("yyyyMMdd"));
    }

    /**
     * 오늘 날짜 중 해당하는 요소를 반환한다.
     * 
     * <pre>
     * DateUtils.getToday(): "20191231"
     * 
     * DateUtils.getToday(DateType.YEAR): "2019"
     * DateUtils.getToday(DateType.MONTH): "12"
     * DateUtils.getToday(DateType.DAY): "31"
     * </pre>
     */
    public String getToday(DateType type) {
        return LocalDateTime.now().format(ofPattern(convertTypeToPattern(type)));
    }

    /**
     * 오늘을 기준으로 어제 날짜(yyyyMMdd)를 반환한다.
     * 
     * <pre>
     * DateUtils.getToday(): "20191231"
     * DateUtils.getYesterday(): "20191230"
     * </pre>
     */
    public String getYesterday() {
        return LocalDate.now().minusDays(1).format(ofPattern("yyyyMMdd"));
    }

    /**
     * 오늘을 기준으로 어제 날짜 중 해당하는 요소를 반환한다.
     * 
     * <pre>
     * DateUtils.getYesterday(): "20191230"
     * 
     * DateUtils.getYesterday(DateType.YEAR): "2019"
     * DateUtils.getYesterday(DateType.MONTH): "12"
     * DateUtils.getYesterday(DateType.DAY): "30"
     * </pre>
     */
    public String getYesterday(DateType type) {
        return LocalDateTime.now().minusDays(1).format(ofPattern(convertTypeToPattern(type)));
    }

    /**
     * 현재의 연월일시분초(yyyyMMddHHmmss)를 반환한다.
     * 
     * <pre>
     * DateUtils.getCurrentDateTime(): "20191231175959"
     * </pre>
     */
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * 콘솔 형식의 현재 연월일시분초를 반환한다.
     * 
     * <pre>
     * DateUtils.getConsoleTime(): "2019-12-31 17:59:59.311"
     * </pre>
     */
    public String getConsoleTime() {
        return LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /**
     * 달력에 실재하는 날짜인지 확인한다.
     * 날짜 형식은 "yyyyMMdd", "yyyy-MM-dd" 둘 다 지원한다.
     * 
     * <pre>
     * DateUtils.isValidDate("2019-02-28"): true
     * DateUtils.isValidDate("20190229"): false
     * DateUtils.isValidDate("20200229"): true
     * DateUtils.isValidDate("2020-02-29"): true
     * </pre>
     */
    public boolean isValidDate(String date) {
        try {
            // 날짜 형식을 통일한다
            date = StringUtils.removeMinusChar(date);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setLenient(false);
            dateFormat.parse(date);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    /**
     * 달력에 실재하는 날짜와 요일인지 확인한다.
     * 날짜 형식은 "yyyyMMdd", "yyyy-MM-dd" 둘 다 지원한다.
     * 
     * <pre>
     * DateUtils.isValidDate("20190228", DayOfWeek.THURSDAY): true
     * DateUtils.isValidDate("2019-02-28", DayOfWeek.THURSDAY): true
     * DateUtils.isValidDate("20190229", DayOfWeek.FRIDAY): false
     * DateUtils.isValidDate("20200229", DayOfWeek.SATURDAY): true
     * DateUtils.isValidDate("2020-02-29", DayOfWeek.SATURDAY): true
     * </pre>
     */
    public boolean isValidDate(String date, DayOfWeek dayOfWeek) {
        // 유효한 날짜인지 확인한다
        if (!isValidDate(date)) return false;

        final int year = Integer.parseInt(date.substring(0, 4));
        final int month = Integer.parseInt(date.substring(4, 6));
        final int day = Integer.parseInt(date.substring(6));
        LocalDate ld = LocalDate.of(year, month, day);

        // 유효한 요일인지 확인한다
        return ld.getDayOfWeek().equals(dayOfWeek);
    }

    /**
     * 해당 연월의 말일을 포함한 일자를 반환한다.
     * 
     * <pre>
     * DateUtils.getMonthlyLastDate(2019, 2): "20190228"
     * DateUtils.getMonthlyLastDate(2020, 2): "20200229"
     * </pre>
     */
    public String getMonthlyLastDate(int year, int month) {
        LocalDate lastDate = YearMonth.of(year, month).atEndOfMonth();
        return lastDate.format(ofPattern("yyyyMMdd"));
    }

    /**
     * 해당 연월의 말일을 포함한 일자를 반환한다.
     * 
     * <pre>
     * DateUtils.getMonthlyLastDate("2019", "2"): "20190228"
     * DateUtils.getMonthlyLastDate("2020", "2"): "20200229"
     * </pre>
     */
    public String getMonthlyLastDate(String year, String month) {
        LocalDate lastDate = YearMonth.of(Integer.valueOf(year), Integer.valueOf(month)).atEndOfMonth();
        return lastDate.format(ofPattern("yyyyMMdd"));
    }

    /**
     * 복합날짜열을 변환한다.
     * 
     * <p>
     * e.g. "20190606,20190501~20190531,yesterday~today,20190601~today,today,yesterday,20190101,20181201~20190101"
     * </p>
     * 
     * <pre>
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
    public Map<String, Object> convertCompoundedDate(String date) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<String> simpleDates = new ArrayList<>();
        List<Object> complexDates = new ArrayList<>();

        // today와 yesterday 문자열을 날짜 형태로 변환한다
        date = date.replace("today", getToday());
        date = date.replace("yesterday", getYesterday());

        // ,(comma)를 기준으로 날짜 조건을 분리한다
        List<String> temp1 = Arrays.asList(date.split(","));
        List<String> temp2 = new ArrayList<>();
        for (int i = 0; i < temp1.size(); i++) {
            if (temp1.get(i).contains("~")) {
                // 복합일자를 분리한다
                temp2.add(temp1.get(i));
            } else {
                // 존재하는 날짜인지 확인한다
                if (isValidDate(temp1.get(i)) == false) throw new RuntimeException("Invalid date: " + temp1.get(i));

                // 단일일자를 분리한다
                simpleDates.add(temp1.get(i));
            }
        }

        // 복합일자의 시작일과 종료일을 구분한다
        for (int i = 0; i < temp2.size(); i++) {
            String[] temp3 = temp2.get(i).split("~");

            // 존재하는 날짜인지 확인한다
            if (isValidDate(temp3[0]) == false) throw new RuntimeException("Invalid date: " + temp3[0]);
            if (isValidDate(temp3[1]) == false) throw new RuntimeException("Invalid date: " + temp3[1]);

            // 시작일이 종료일보다 크거나 같은지 확인한다
            if (Integer.parseInt(temp3[0]) >= Integer.parseInt(temp3[1]))
                throw new RuntimeException(
                        "Start date precedes end date: " + temp3[0] + "~" + temp3[1] + "; You can change it to this: " + temp3[0] + " or " + temp3[1]);

            Map<String, Object> map = new HashMap<>();
            map.put("strDate", temp3[0]);
            map.put("endDate", temp3[1]);

            complexDates.add(map);
        }

        result.put("simpleDates", simpleDates);
        result.put("complexDates", complexDates);

        return result;
    }

}