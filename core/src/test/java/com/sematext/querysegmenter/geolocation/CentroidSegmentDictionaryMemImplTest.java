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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CentroidSegmentDictionaryMemImplTest {

  private CentroidSegmentDictionaryMemImpl dictionary;

  private String filename = "src/test/resources/centroid.csv";

  @Before
  public void setup() {
    dictionary = new CentroidSegmentDictionaryMemImpl();
    dictionary.setSeparator("|");
    dictionary.load(filename);
  }

  @Test
  public void test_lookup() throws Exception {
    String segment = "Aguas Buenas";
    List<CentroidTypedSegment> list = dictionary.lookup(segment);
    assertEquals(1, list.size());
    CentroidTypedSegment centroid = list.get(0);
    // For this test, the segment is the same as the label
    assertEquals(segment, centroid.getLabel());
  }

  @Test
  public void test_lookup_case_insensitive() throws Exception {
    String segment = "Aguas Buenas";
    List<CentroidTypedSegment> list = dictionary.lookup(segment.toLowerCase());
    assertEquals(1, list.size());
    CentroidTypedSegment centroid = list.get(0);
    // For this test, the segment is the same as the label
    assertEquals(segment, centroid.getLabel());
  }

  @Test
  public void test_match_multiple() {
    String segment = "Ainsworth";
    List<CentroidTypedSegment> list = dictionary.lookup(segment);
    assertEquals(2, list.size());

    // For this test, the segment is the same as the label
    assertEquals(segment, list.get(0).getLabel());
    assertEquals(segment, list.get(1).getLabel());
  }

}
