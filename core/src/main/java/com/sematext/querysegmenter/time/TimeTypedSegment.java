/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.time;

import com.sematext.querysegmenter.TypedSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


public class TimeTypedSegment extends TypedSegment {

    private static final String DEFAULT_SEPARATOR = "|";
    public static final String TYPE = "time";

    private static final long ONE_DAY = 1000L * 60 * 60 * 24;
    private static final long ONE_WEEK = ONE_DAY * 7;
    private static final long FOUR_DAYS = ONE_DAY * 4;
    private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

    private final String time;

    public TimeTypedSegment(String csv) {
        this(csv, DEFAULT_SEPARATOR);
    }

    public TimeTypedSegment(String csv, String separator) {
        String[] parts = csv.split(Pattern.quote(separator));
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time csv: " + csv);
        }
        this.label = parts[0];
        this.time = parts[1];
    }

    private String formatTime(String label, String text) {
        String value = text;
        if ("this week".equalsIgnoreCase(label)) {
            return format(getStartOfThisWeek(), ONE_WEEK);
        } else if ("last week".equalsIgnoreCase(label)) {
            return format(getStartOfThisWeek() - ONE_WEEK, ONE_WEEK);
        } else if ("next week".equalsIgnoreCase(label)) {
            return format(getStartOfThisWeek() + ONE_WEEK, ONE_WEEK);
        } else if ("last monday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(0), ONE_DAY);
        } else if ("last tuesday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(1), ONE_DAY);
        } else if ("last wednesday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(2), ONE_DAY);
        } else if ("last thursday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(3), ONE_DAY);
        } else if ("last friday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(4), ONE_DAY);
        } else if ("last saturday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(5), ONE_DAY);
        } else if ("last sunday".equalsIgnoreCase(label)) {
            return format(getLastNDayOfWeek(6), ONE_DAY);
        }

        return value;
    }

    private String format(long start, long range) {
        long end = start + range - 1;
        return String.format("%s TO %s", FORMATTER.print(start), FORMATTER.print(end));
    }

    private long getLastNDayOfWeek(int nth) {
        long now = System.currentTimeMillis();
        long startWeek = getStartOfThisWeek(now);

        long thisNDay = startWeek + nth*ONE_DAY;
        if (now > thisNDay) {
            return thisNDay;
        } else {
            return thisNDay - ONE_WEEK;
        }
    }

    private long getStartOfThisWeek(long now) {
        long time = now - FOUR_DAYS;
        return time - (time % ONE_WEEK) + FOUR_DAYS;
    }

    private long getStartOfThisWeek() {
        long time = System.currentTimeMillis() - FOUR_DAYS;
        return time - (time % ONE_WEEK) + FOUR_DAYS;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Map<String, ?> getMetadata() {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("time", formatTime(this.label, this.time));
        return metadata;
    }
}
