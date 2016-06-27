/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GenericTypedSegmentTest {

  @Test
  public void test_only_label() {

    String line = "new york";
    GenericTypedSegment segment = new GenericTypedSegment(line);
    assertEquals("new york", segment.getLabel());
  }
  
  @Test
  public void test_csv() {

    String line = "new york, 1, 2";
    GenericTypedSegment segment = new GenericTypedSegment(line);
    assertEquals("new york", segment.getLabel());
  }

}
