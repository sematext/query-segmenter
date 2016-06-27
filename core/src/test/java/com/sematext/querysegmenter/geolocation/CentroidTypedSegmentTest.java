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

public class CentroidTypedSegmentTest {

  @Test
  public void test_parse() {

    String label = "Menlo Park";
    String lat = "61.235009";
    String lon = "-149.703891";

    String csv = String.format("%s,%s,%s", label, lat, lon);

    CentroidTypedSegment centroid = new CentroidTypedSegment(csv);
    assertEquals(label, centroid.getLabel());
    assertEquals(Double.valueOf(lat), centroid.getLatitude(), 0.0000001);
    assertEquals(Double.valueOf(lon), centroid.getLongitude(), 0.0000001);

  }

}
