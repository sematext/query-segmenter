/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.solr;

import org.apache.solr.common.util.NamedList;

import java.util.HashMap;
import java.util.Map;

import com.sematext.querysegmenter.FileBasedSegmentDictionary;
import com.sematext.querysegmenter.QuerySegmenterDefaultImpl;

public class QuerySegmenterConfig {

  private static final String INIT_ATTR_SEGMENTS = "segments";
  private static final String INIT_ATTR_DICTIONARY = "dictionary";
  private static final String INIT_ATTR_FILENAME = "filename";
  private static final String INIT_ATTR_FIELD = "field";
  private static final String INIT_ATTR_LATLON = "useLatLon";
  private static final String INIT_ATTR_BQ = "useBoostQuery";
  private static final String INIT_ATTR_TIME = "useTime";

  private final QuerySegmenterDefaultImpl segmenter;

  public class FieldMapping {

    String field;

    boolean useLatLon;

    boolean useBoostQuery;

    boolean useTime;
  }

  private final Map<String, FieldMapping> mappings = new HashMap<String, FieldMapping>();

  public QuerySegmenterDefaultImpl getSegmenter() {
    return segmenter;
  }

  public Map<String, FieldMapping> getMappings() {
    return mappings;
  }

  @SuppressWarnings("rawtypes")
  public QuerySegmenterConfig(NamedList args) {
    segmenter = new QuerySegmenterDefaultImpl();

    NamedList segments = (NamedList) args.get(INIT_ATTR_SEGMENTS);
    for (int i = 0; i < segments.size(); i++) {
      String name = segments.getName(i);
      NamedList values = (NamedList) segments.getVal(i);
      initSegmentType(name, values);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void initSegmentType(String name, NamedList values) {
    String dictionaryClass = (String) values.get(INIT_ATTR_DICTIONARY);
    Class<?> clazz;
    try {
      clazz = Class.forName(dictionaryClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    String filename = (String) values.get(INIT_ATTR_FILENAME);
    segmenter.addFileDictionary(name, filename, (Class<? extends FileBasedSegmentDictionary>) clazz);

    FieldMapping mapping = new FieldMapping();
    mapping.field = (String) values.get(INIT_ATTR_FIELD);
    mapping.useLatLon = values.get(INIT_ATTR_LATLON) == null ? false : (Boolean) values.get(INIT_ATTR_LATLON);
    mapping.useBoostQuery = values.get(INIT_ATTR_BQ) == null ? false : (Boolean) values.get(INIT_ATTR_BQ);
    mapping.useTime = values.get(INIT_ATTR_TIME) == null ? false : (Boolean) values.get(INIT_ATTR_TIME);
    mappings.put(name, mapping);
  }

}
