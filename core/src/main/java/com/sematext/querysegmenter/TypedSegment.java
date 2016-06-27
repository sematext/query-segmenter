/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.util.Map;

public abstract class TypedSegment {

  protected String label;
  protected String segment;
  protected String dictionaryName;

  /**
   * @return the type of this segment
   */
  abstract public String getType();

  /**
   * @return the metadata defined for this segment
   */
  abstract public Map<String, ?> getMetadata();

  /**
   * @return the label defined in the dictionary
   */
  public String getLabel() {
    return label;
  }

  protected void setLabel(String label) {
    this.label = label;
  }

  /**
   * Returns the segment of the original query it is associated to.
   * 
   * Note that this is not necessarily the same as the label. For example, the label defined in the dictionary might be
   * "New York", but we could match it to the segment "new york".
   * 
   * @return segment
   */
  public String getSegment() {
    return segment;
  }

  protected void setSegment(String segment) {
    this.segment = segment;
  }

  /**
   * @return the name of the dictionary this segment is from
   */
  public String getDictionaryName() {
    return dictionaryName;
  }

  protected void setDictionaryName(String dictionaryName) {
    this.dictionaryName = dictionaryName;
  }
}
