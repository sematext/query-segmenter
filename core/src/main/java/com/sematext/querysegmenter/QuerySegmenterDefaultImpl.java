/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class QuerySegmenterDefaultImpl implements QuerySegmenter {

  private static final String DEFAULT_SEPARATOR = ",";

  private static final int WINDOW_SIZE = 4;

  private List<AbstractSegmentDictionary> dictionaries = new ArrayList<AbstractSegmentDictionary>();

  @Override
  public void addFileDictionary(String name, String filename,
      Class<? extends FileBasedSegmentDictionary> dictionaryClass) {
    addFileDictionary(name, filename, DEFAULT_SEPARATOR, dictionaryClass);
  }

  @Override
  public void addFileDictionary(String name, String filename, String separator,
      Class<? extends FileBasedSegmentDictionary> dictionaryClass) {

    FileBasedSegmentDictionary dictionary;
    try {
      Constructor<? extends FileBasedSegmentDictionary> ctr = dictionaryClass.getDeclaredConstructor();
      dictionary = ctr.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    dictionary.setName(name);
    dictionary.setSeparator(separator);
    dictionary.load(filename);
    dictionaries.add(dictionary);
  }

  @Override
  public List<TypedSegment> segment(String query) {

    List<TypedSegment> typedSegments = new ArrayList<TypedSegment>();

    String[] segments = query.split("\\s+");

    // Evaluate segments from left to right
    for (int i = 0; i < segments.length; i++) {

      // Always start with the longest string possible
      for (int j = WINDOW_SIZE - 1; j >= 0; j--) {

        int last = i + j;
        if (last >= segments.length) {
          continue;
        }

        StringBuilder sb = new StringBuilder();
        for (int k = i; k <= last; k++) {
          sb.append(segments[k]);
          // Don't add space after last segment
          if (k != last) {
            sb.append(" ");
          }
        }

        for (AbstractSegmentDictionary dictionary : dictionaries) {
          String segment = sb.toString();
          List<? extends TypedSegment> list = dictionary.lookup(segment);
          if (list == null) {
            continue;
          }
          for (TypedSegment typedSegment : list) {
            typedSegment.setSegment(segment);
            typedSegment.setDictionaryName(dictionary.getName());
            typedSegments.add(typedSegment);
          }
        }
      }
    }

    return typedSegments;
  }

}
