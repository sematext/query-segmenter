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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import com.sematext.querysegmenter.geolocation.AreaSegmentDictionaryMemImpl;
import com.sematext.querysegmenter.geolocation.AreaTypedSegment;

public class QuerySegmenterDefaultImplTest {

  private QuerySegmenterDefaultImpl segmenter;

  @Before
  public void setup() {
    segmenter = new QuerySegmenterDefaultImpl();
    String filename = "src/test/resources/neighborhood.txt";
    segmenter.addFileDictionary("name", filename, AreaSegmentDictionaryMemImpl.class);
  }

  @Test
  public void test_parse() throws Exception {

    String query = "pizza campbell park";
    List<TypedSegment> segments = segmenter.segment(query);

    assertEquals(1, segments.size());
    TypedSegment segment = segments.get(0);
    assertEquals(AreaTypedSegment.TYPE, segment.getType());
    assertEquals("Campbell Park", segment.getLabel());
    assertEquals("campbell park", segment.getSegment());
  }

  @Test
  public void test_parse_after() throws Exception {

    String query = "Eagle River Valley garage";
    List<TypedSegment> segments = segmenter.segment(query);

    assertEquals(1, segments.size());
    TypedSegment segment = segments.get(0);
    assertEquals(AreaTypedSegment.TYPE, segment.getType());
    assertEquals("Eagle River Valley", segment.getLabel());
    assertEquals("Eagle River Valley", segment.getSegment());
  }

}
