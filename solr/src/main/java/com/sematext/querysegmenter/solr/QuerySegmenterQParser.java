/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.solr;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import java.util.List;
import java.util.Map;

import com.sematext.querysegmenter.QuerySegmenter;
import com.sematext.querysegmenter.TypedSegment;
import com.sematext.querysegmenter.solr.QuerySegmenterConfig.FieldMapping;

/**
 * This QParser is used to retrieve segments from a user query.
 * 
 * If there is a segment in the user query that matches a dictionary entry, the query is rewritten using either the label
 * or the location of the typed segment. For example, for the query “pizza brooklyn”, if “brooklyn” is a typed segment,
 * the query will be rewritten to:
 * <p>
 *   “pizza neighborhood:brooklyn”
 * </p>
 * or, if useLatLon was set to true:
 * <p>
 *   “pizza location:[minlat,minlon TO maxlat, maxlon]”
 * </p>
 * The field to use and whether we should use the label or the location is configurable.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class QuerySegmenterQParser extends QParser {

  private final QuerySegmenter segmenter;

  private Map<String, FieldMapping> mappings;

  public QuerySegmenterQParser(QuerySegmenterConfig config, String qstr, SolrParams localParams, SolrParams params,
      SolrQueryRequest req) {
    super(qstr, localParams, params, req);
    this.segmenter = config.getSegmenter();
    this.mappings = config.getMappings();
  }

  @Override
  public Query parse() throws SyntaxError {

    String qstr = getString();

    List<TypedSegment> typedSegments = segmenter.segment(qstr);
    for (TypedSegment typedSegment : typedSegments) {
      FieldMapping mapping = mappings.get(typedSegment.getDictionaryName());
      String value = QuerySegmenterComponent.getValue(typedSegment, mapping);
      if (mapping.useBoostQuery) {
        qstr = qstr.replaceFirst(typedSegment.getSegment(), String.format("&bq=%s:%s", mapping.field, value));
      } else {
        qstr = qstr.replaceFirst(typedSegment.getSegment(), String.format("%s:%s", mapping.field, value));
      }
    }

    // Passing null allows to use another qparser defined with defType (like edismax)
    // See SOLR-2972
    QParser parser = subQuery(qstr, null);
    return parser.parse();
  }
}
