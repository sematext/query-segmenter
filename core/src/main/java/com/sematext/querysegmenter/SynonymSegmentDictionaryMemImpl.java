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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Used to list synonyms of a label. If we have this entry in the dictionary:
 * 
 * <pre>
 * New York,nyc
 * </pre>
 * 
 * Then "New York" will be returned when "nyc" is looked up in this dictionary.
 * 
 * The first element of the line is the label that will be returned and all other elements on the same line are
 * synonyms.
 * 
 * If a lookup is done on the first element, that element is returned. For example, using the dictionary
 * described above, a lookup for 'new york' will return "New York".
 * 
 * We can also use plain list of words (without any synonym). In that case, this dictionary will behave exactly like the
 * GenericSegmentDictionaryMemImpl.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class SynonymSegmentDictionaryMemImpl extends FileBasedSegmentDictionary {

  final class SynonymSegment extends TypedSegment {

    @Override
    public String getType() {
      return "synonym";
    }

    @Override
    public Map<String, ?> getMetadata() {
      return Collections.emptyMap();
    }
  }

  private Map<String, String> map = new HashMap<String, String>();

  @Override
  public List<? extends TypedSegment> lookup(String segment) {
    String label = map.get(segment.toLowerCase());
    if (label == null) {
      return Collections.emptyList();
    }

    SynonymSegment synonymSegment = new SynonymSegment();
    synonymSegment.label = label;
    return Collections.singletonList(synonymSegment);
  }

  @Override
  protected void addLineFromFile(String line) {
    String[] parts = line.split(Pattern.quote(separator));
    if (parts.length < 1) {
      throw new IllegalArgumentException("Invalid synonym format: " + line);
    }
    // The first element of the line is the label that will be used as replacement for all other elements (synonyms)
    // of the same line (even the first element itself).
    String label = parts[0];
    for (String part : parts) {
      map.put(part.toLowerCase(), label);
    }
  }
}
