/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A MapSegmentDictionary maintains the dictionary in a Map in memory.
 */
public abstract class MapSegmentDictionary<T extends TypedSegment> extends FileBasedSegmentDictionary {

  private Map<String, List<T>> map = new HashMap<String, List<T>>();

  @Override
  public List<T> lookup(String segment) {
    List<T> list = map.get(segment.toLowerCase());
    if (list == null) {
      list = Collections.emptyList();
    }
    return list;
  }

  protected abstract T buildTypedSegment(String line);

  protected void addLineFromFile(String line) {
    T t = buildTypedSegment(line);
    String key = t.getLabel().toLowerCase();
    List<T> list = map.get(key);
    if (list == null) {
      list = new ArrayList<T>();
      map.put(key, list);
    }
    list.add(t);
  }

}
