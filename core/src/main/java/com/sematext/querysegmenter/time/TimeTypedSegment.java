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

public class TimeTypedSegment extends TypedSegment {

    private static final String DEFAULT_SEPARATOR = "|";
    public static final String TYPE = "time";

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

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Map<String, ?> getMetadata() {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("time", time);
        return metadata;
    }

    public String getTime() {
        return time;
    }
}
