/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.time;

import com.sematext.querysegmenter.geolocation.CentroidTypedSegment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeTypedSegmentTest {

  @Test
  public void test_parse() {

    String label = "yesterday";
    String time = "NOW/DAY-1DAY TO NOW/DAY";

    String csv = String.format("%s|%s", label, time);

    TimeTypedSegment segment = new TimeTypedSegment(csv);
    assertEquals(label, segment.getLabel());
    assertEquals(time, segment.getTime());

  }

}
