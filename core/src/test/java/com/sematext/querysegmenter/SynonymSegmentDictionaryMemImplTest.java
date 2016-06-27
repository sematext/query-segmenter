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

public class SynonymSegmentDictionaryMemImplTest {

  private SynonymSegmentDictionaryMemImpl dict;

  @Before
  public void setup() {
    dict = new SynonymSegmentDictionaryMemImpl();
    String filename = "src/test/resources/synonyms.txt";
    dict.load(filename);
  }

  @Test
  public void test_synonym() {
    assertEquals("New York", dict.lookup("new york").get(0).getLabel());
    assertEquals("New York", dict.lookup("nyc").get(0).getLabel());
    assertEquals("New York", dict.lookup("ny").get(0).getLabel());
    assertEquals("Otis Gospodnetic", dict.lookup("otis gospodnetic").get(0).getLabel());
    assertEquals("Otis Gospodnetic", dict.lookup("otisg").get(0).getLabel());
  }

  @Test
  public void test_single_value() {
    assertEquals("Boston", dict.lookup("boston").get(0).getLabel());
  }

}
