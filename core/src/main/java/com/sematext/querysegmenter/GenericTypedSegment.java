/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A GenericTypedSegment contains a label only and no metadata.
 * 
 * It can be used for simple lists of labels to match. But it can be used with CSV data, but only the first item of each
 * line would be kept.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class GenericTypedSegment extends TypedSegment {

  private static final String DEFAULT_SEPARATOR = ",";

  public static final String TYPE = "generic";

  @Override
  public String getType() {
    return TYPE;
  }

  public GenericTypedSegment(String csv) {
    this(csv, DEFAULT_SEPARATOR);
  }

  public GenericTypedSegment(String csv, String separator) {
    String[] parts = csv.split(Pattern.quote(separator));
    if (parts.length < 1) {
      throw new IllegalArgumentException("Invalid csv: " + csv);
    }
    this.label = parts[0];
    // Other parts are ignored
  }

  @Override
  public Map<String, ?> getMetadata() {
    return Collections.emptyMap();
  }
}
