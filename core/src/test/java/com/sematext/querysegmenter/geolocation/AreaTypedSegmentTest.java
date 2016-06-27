/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.geolocation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AreaTypedSegmentTest {

  @Test
  public void test_parse() {

    String label = "Northeast";
    String maxlat = "61.235009";
    String maxlon = "-149.703891";
    String minlat = "61.195252";
    String minlon = "-149.778423";

    String csv = String.format("%s,%s,%s,%s,%s", label, maxlat, maxlon, minlat, minlon);

    AreaTypedSegment area = new AreaTypedSegment(csv);
    assertEquals(label, area.getLabel());
    assertEquals(Double.valueOf(maxlat), area.getMaxlat(), 0.0000001);
    assertEquals(Double.valueOf(maxlon), area.getMaxlon(), 0.0000001);
    assertEquals(Double.valueOf(minlat), area.getMinlat(), 0.0000001);
    assertEquals(Double.valueOf(minlon), area.getMinlon(), 0.0000001);

  }

}
