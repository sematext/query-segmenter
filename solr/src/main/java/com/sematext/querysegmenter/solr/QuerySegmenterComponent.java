/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.solr;

import com.sematext.querysegmenter.TypedSegment;
import com.sematext.querysegmenter.geolocation.AreaTypedSegment;
import com.sematext.querysegmenter.solr.QuerySegmenterConfig.FieldMapping;
import com.sematext.querysegmenter.time.TimeTypedSegment;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This SearchComponent is used to retrieve segments from a user query. It must be set before the "query"
 * SearchComponent.
 * 
 * If there is a segment in the user query that matches in a dictionary, the query is rewritten using either the label
 * or the location of the typed segment. For example, for the query “pizza brooklyn”, if “brooklyn” is a typed segment,
 * the query will be rewritten to “pizza neighborhood:brooklyn” or “pizza location:[minlat,minlon TO maxlat, maxlon]”.
 * The field to use and whether we should use the label or the location is configurable.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class QuerySegmenterComponent extends SearchComponent {

  private QuerySegmenterConfig config;

  private static final long ONE_WEEK = 1000L * 60 * 60 * 24 * 7;
  private static final long FOUR_DAYS = 1000L * 60 * 60 * 24 * 4;
  private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

  @SuppressWarnings("rawtypes")
  @Override
  public void init(NamedList args) {
    super.init(args);
    config = new QuerySegmenterConfig(args);
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public String getSource() {
    return null;
  }

  @Override
  public String getVersion() {
    return null;
  }

  @Override
  public void prepare(ResponseBuilder rb) throws IOException {

    SolrParams params = rb.req.getParams();
    String q = params.get(CommonParams.Q);
    String bq = null;

    if (q == null || q.isEmpty()) {
      return;
    }

    // Segment query and add prefix if a typed segments are found
    List<TypedSegment> typedSegments = config.getSegmenter().segment(q);
    for (TypedSegment typedSegment : typedSegments) {
      FieldMapping mapping = config.getMappings().get(typedSegment.getDictionaryName());
      String value = getValue(typedSegment, mapping);
      if (mapping.useBoostQuery) {
        q = q.replaceAll(typedSegment.getSegment(), "");
        bq = String.format("%s:%s", mapping.field, value);
      } else {
        q = q.replaceAll(typedSegment.getSegment(), String.format("%s:%s", mapping.field, value));
      }
    }

    if (typedSegments.isEmpty()) {
      return;
    }

    // Override q for the "query" component.
    ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams(params);
    modifiableSolrParams.set(CommonParams.Q, q);
    if (bq != null) {
      modifiableSolrParams.set(DisMaxParams.BQ, bq);
    }
    rb.req.setParams(modifiableSolrParams);
  }

  @Override
  public void process(ResponseBuilder rb) throws IOException {
  }

  static String getValue(TypedSegment typedSegment, FieldMapping mapping) {
    String value;
    if (mapping.useLatLon && typedSegment.getClass() == AreaTypedSegment.class) {
      Map<String, ?> metadata = typedSegment.getMetadata();
      double minlat = (Double) metadata.get("minlat");
      double minlon = (Double) metadata.get("minlon");
      double maxlat = (Double) metadata.get("maxlat");
      double maxlon = (Double) metadata.get("maxlon");
      value = String.format("[%s,%s TO %s,%s]", minlat, minlon, maxlat, maxlon);
    } else if (mapping.useTime && typedSegment instanceof TimeTypedSegment) {
      Map<String, ?> metadata = typedSegment.getMetadata();
      String time = (String) metadata.get("time");
      value = String.format("[%s]", time);
    } else {
      value = String.format("\"%s\"", typedSegment.getLabel());
    }
    return value;
  }

}
