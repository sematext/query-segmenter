/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.solr;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.params.SpatialParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.impl.PointImpl;

import java.io.IOException;
import java.util.List;

import com.sematext.querysegmenter.QuerySegmenter;
import com.sematext.querysegmenter.QuerySegmenterDefaultImpl;
import com.sematext.querysegmenter.TypedSegment;
import com.sematext.querysegmenter.geolocation.CentroidSegmentDictionaryMemImpl;
import com.sematext.querysegmenter.geolocation.CentroidTypedSegment;

/**
 * This SearchComponent is used to alter the user location if a segment of the query is a centroid. If a match is found,
 * the user location (specified in the pt request param) is changed to the center location of the centroid. Subsequent
 * filters that use this location to filter the result set will then be working with the centroid instead of the
 * original user location. If multiple centroid segments are returned from the user query, the closest centroid to the
 * original user location is used.
 * 
 * For example, if a user searches for “pizza Aaronsburg”, the segment “Aaronsburg” could be returned as a centroid with
 * location 40.9068, -77.4081. This location would be used instead of the original location. This would make the results
 * to be filtered to retain only the ones around the centroid location.
 * 
 * Note that this component only works with the centroid dictionary.
 * 
 * @author sematext, http://www.sematext.com/
 */
public class CentroidComponent extends SearchComponent {

  private static final String FILENAME = "filename";

  private static final String SEPARATOR = "separator";
  
  private SpatialContext ctx;

  private QuerySegmenter segmenter;

  @SuppressWarnings("rawtypes")
  @Override
  public void init(NamedList args) {
    super.init(args);
    segmenter = new QuerySegmenterDefaultImpl();
    String filename = (String) args.get(FILENAME);
    String separator = (String) args.get(SEPARATOR);
    segmenter.addFileDictionary("centroid", filename, separator, CentroidSegmentDictionaryMemImpl.class);
    this.ctx = SpatialContext.GEO;
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

    List<TypedSegment> typedSegments = segmenter.segment(q);
    if (typedSegments.isEmpty()) {
      return;
    }

    // If multiple matches, use the closest from the user
    CentroidTypedSegment centroidSegment = null;
    if (typedSegments.size() > 1) {
      double[] userLatlon = getUserLocation(params);
      centroidSegment = getClosestSegment(typedSegments, userLatlon);
    } else {
      centroidSegment = (CentroidTypedSegment) typedSegments.get(0);
    }

    // Override point to use the value from the matching centroid.
    ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams(params);
    modifiableSolrParams.set(SpatialParams.POINT,
        String.format("%s,%s", centroidSegment.getLatitude(), centroidSegment.getLongitude()));
    
    q = q.replaceAll(centroidSegment.getSegment(), "");
    
    modifiableSolrParams.set(CommonParams.Q, q);
    
    rb.req.setParams(modifiableSolrParams);
  }

  private CentroidTypedSegment getClosestSegment(List<TypedSegment> typedSegments, double[] userLatlon) {
    CentroidTypedSegment closest = null;
    double closestDistance = Double.MAX_VALUE;

    for (TypedSegment typedSegment : typedSegments) {
      CentroidTypedSegment centroidSegment = (CentroidTypedSegment) typedSegment;
      Point p1 = new PointImpl(userLatlon[0], userLatlon[1], ctx);
      Point p2 = new PointImpl(centroidSegment.getLatitude(), centroidSegment.getLongitude(), ctx);
      double distance = ctx.getDistCalc().distance(p1, p2);
      if (distance < closestDistance) {
        closest = centroidSegment;
      }
    }
    return closest;
  }

  private double[] getUserLocation(SolrParams params) {
    String userLocation = params.get(SpatialParams.POINT);
    if (userLocation == null || userLocation.isEmpty()) {
      throw new IllegalArgumentException("pt is missing.");
    }
    String[] latlon = userLocation.split(",");
    if (latlon.length != 2) {
      throw new IllegalArgumentException("pt is invalid (should be [lat,lon]).");
    }
    return new double[] { Double.valueOf(latlon[0]), Double.valueOf(latlon[1]) };
  }

  @Override
  public void process(ResponseBuilder rb) throws IOException {
  }

}
