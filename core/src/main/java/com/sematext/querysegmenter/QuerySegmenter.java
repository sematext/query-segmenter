/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.util.List;

/**
 * Used to segment a query into segments identified by types.
 * 
 * @author sematext, http://www.sematext.com/
 */
public interface QuerySegmenter {

  /**
   * Add a file dictionary that will be used to identify segments within queries.
   * 
   * @param name
   *          name of the dictionary
   * @param filename
   *          filename of the dictionary
   * @param dictionaryClass
   *          dictionary to use
   */
  void addFileDictionary(String name, String filename, Class<? extends FileBasedSegmentDictionary> dictionaryClass);

  /**
   * Add a file dictionary that will be used to identify segments within queries.
   * 
   * @param name
   *          name of the dictionary
   * @param filename
   *          filename of the dictionary
   * @param separator
   *          separator for the records of each line
   * @param dictionaryClass
   *          dictionary to use
   */
  void addFileDictionary(String name, String filename, String separator,
      Class<? extends FileBasedSegmentDictionary> dictionaryClass);

  /**
   * Segment a query into a list of typed segments.
   * 
   * @param query
   * @return typed segments
   */
  List<TypedSegment> segment(String query);

}
