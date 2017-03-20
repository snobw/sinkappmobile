package com.inopek.duvana.sink.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public final class DateUtils {

    private DateUtils() {

    }

    public static Date parseDateFromString(String strDate, String format) {
        if(StringUtils.isEmpty(strDate) || StringUtils.isEmpty(format)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = formatter.parseDateTime(strDate);
        return dateTime.toDate();
    }

    public static String dateToString(DateTime dateTime, String format) {
        if(dateTime == null || StringUtils.isEmpty(format)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return dateTime.toString(formatter);
    }
}
