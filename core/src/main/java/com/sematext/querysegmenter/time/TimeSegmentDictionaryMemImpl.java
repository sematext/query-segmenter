/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.time;

import com.sematext.querysegmenter.MapSegmentDictionary;

public class TimeSegmentDictionaryMemImpl extends MapSegmentDictionary<TimeTypedSegment>{
    @Override
    protected TimeTypedSegment buildTypedSegment(String line) {
        return new TimeTypedSegment(line);
    }
}
