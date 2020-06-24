package io.github.imsejin.common.constants;

public enum DateType implements GettableEnum {

    YEAR("yyyy"), MONTH("MM"), DAY("dd"), HOUR("HH"), MINUTE("mm"), SECOND("ss"), MILLISECOND("SSS"),

    // yyyyMM
    YEAR_MONTH(YEAR.value() + MONTH.value()),

    // yyyyMMdd
    DATE(YEAR.value() + MONTH.value() + DAY.value()),

    // HHmmss
    TIME(HOUR.value() + MINUTE.value() + SECOND.value()),

    // HHmmssSSS
    HOUR_2_MILSEC(TIME.value() + MILLISECOND.value()),

    // yyyyMMddHHmmss
    DATE_TIME(DATE.value() + TIME.value()),

    // yyyyMMddHHmmssSSS
    ALL(DATE.value() + HOUR_2_MILSEC.value()),

    // yyyy-MM-dd
    F_DATE(YEAR.value() + "-" + MONTH.value() + "-" + DAY.value()),

    // HH:mm:ss
    F_TIME(HOUR.value() + ":" + MINUTE.value() + ":" + SECOND.value()),

    // HH:mm:ss.SSS
    F_HOUR_2_MILSEC(F_TIME.value() + "." + MILLISECOND.value()),

    // yyyy-MM-dd HH:mm:ss
    F_DATE_TIME(F_DATE.value() + " " + F_TIME.value()),

    // yyyy-MM-dd HH:mm:ss.SSS
    F_ALL(F_DATE.value() + F_HOUR_2_MILSEC.value());

    private final String pattern;

    DateType(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String key() {
        return this.name();
    }

    @Override
    public String value() {
        return this.pattern;
    }

}