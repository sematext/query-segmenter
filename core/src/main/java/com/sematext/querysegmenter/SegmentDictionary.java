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
 * Manage a dictionary of segments to be identified.
 * 
 * The actual implementation can be based on files or Lucene indexes.
 * 
 * @author sematext, http://www.sematext.com/
 */
public interface SegmentDictionary {

  /**
   * Ask this SegmentDictionary to return a TypedSegment if there is one that matches the segment provided
   * 
   * @param segment .
   * @return TypedSegment
   */
  List<? extends TypedSegment> lookup(String segment);

}
