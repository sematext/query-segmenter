/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.time;

import com.sematext.querysegmenter.geolocation.CentroidSegmentDictionaryMemImpl;
import com.sematext.querysegmenter.geolocation.CentroidTypedSegment;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeSegmentDictionaryMemImplTest {

  private TimeSegmentDictionaryMemImpl dictionary;

  private String filename = "src/test/resources/time.txt";

  @Before
  public void setup() {
    dictionary = new TimeSegmentDictionaryMemImpl();
    dictionary.setSeparator("|");
    dictionary.load(filename);
  }

  @Test
  public void test_lookup() throws Exception {
    String segment = "yesterday";
    List<TimeTypedSegment> list = dictionary.lookup(segment);
    assertEquals(1, list.size());
    TimeTypedSegment time = list.get(0);
    // For this test, the segment is the same as the label
    assertEquals(segment, time.getLabel());
  }

  @Test
  public void test_lookup_case_insensitive() throws Exception {
    String segment = "Yesterday";
    List<TimeTypedSegment> list = dictionary.lookup(segment.toLowerCase());
    assertEquals(1, list.size());
    TimeTypedSegment time = list.get(0);
    // For this test, the segment is the same as the label
    assertEquals(segment.toLowerCase(), time.getLabel());
  }

}
