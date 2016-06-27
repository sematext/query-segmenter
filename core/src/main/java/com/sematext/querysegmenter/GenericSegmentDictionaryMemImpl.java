/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

/**
 * The name of the class ends by "MemImpl" to indicate that this dictionary is held in memory.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class GenericSegmentDictionaryMemImpl extends MapSegmentDictionary<GenericTypedSegment> {

  @Override
  protected GenericTypedSegment buildTypedSegment(String line) {
    return new GenericTypedSegment(line, separator);
  }
}
