package com.adobe.assignment.http.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.adobe.assignment.http.HttpConstants;

public class DatesUtils {
    static public long parseHttpDate( String dateString ) throws ParseException {
        Locale locale = new Locale.Builder().setLanguage("en").setScript("Latn").build();
        return DateUtils.parseDate(dateString, locale, HttpConstants.DEFAULT_PATTERNS).getTime();
    }

    static public String formatHttpDate( Date date ) {
        Locale locale = new Locale.Builder().setLanguage("en").setScript("Latn").build();
        FastDateFormat dateFormat = FastDateFormat.getInstance(HttpConstants.PATTERN_RFC1123,
                TimeZone.getTimeZone("GMT"), locale);
        return dateFormat.format(date);
    }
}
