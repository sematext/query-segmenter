/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TimeTypedSegment.class})
public class TimeTypedSegmentTest {

  @Test
  public void test_parse() {

    String label = "yesterday";
    String time = "NOW/DAY-1DAY TO NOW/DAY";

    String csv = String.format("%s|%s", label, time);

    TimeTypedSegment segment = new TimeTypedSegment(csv);
    assertEquals(label, segment.getLabel());
    assertEquals(time, segment.getMetadata().get("time"));

  }

  @Test
  public void test_last_week() {
    String label = "last week";
    String time = "ignore";

    String csv = String.format("%s|%s", label, time);

    PowerMockito.mockStatic(System.class);

    // today 2017-05-16
    PowerMockito.when(System.currentTimeMillis()).thenReturn(1494945394482L);
    PowerMockito.when(System.currentTimeMillis()).thenReturn(1494945394482L);
    PowerMockito.when(System.currentTimeMillis()).thenReturn(1494945394482L);

    TimeTypedSegment segment = new TimeTypedSegment(csv);
    assertEquals(label, segment.getLabel());
    assertEquals("2017-05-08T00:00:00Z TO 2017-05-14T23:59:59Z", segment.getMetadata().get("time"));
  }

  @Test
  public void test_last_monday() {
    String label = "last monday";
    String time = "ignore";

    String csv = String.format("%s|%s", label, time);

    PowerMockito.mockStatic(System.class);

    // today 2017-05-16
    PowerMockito.when(System.currentTimeMillis()).thenReturn(1494945394482L);

    TimeTypedSegment segment = new TimeTypedSegment(csv);
    assertEquals(label, segment.getLabel());
    assertEquals("2017-05-15T00:00:00Z TO 2017-05-15T23:59:59Z", segment.getMetadata().get("time"));
  }
}
