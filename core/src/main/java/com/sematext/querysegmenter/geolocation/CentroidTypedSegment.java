/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.geolocation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.sematext.querysegmenter.TypedSegment;

public class CentroidTypedSegment extends TypedSegment {

  private static final String DEFAULT_SEPARATOR = ",";
  public static final String TYPE = "centroid";

  private final double lat;
  private final double lon;

  public CentroidTypedSegment(String csv) {
    this(csv, DEFAULT_SEPARATOR);
  }

  public CentroidTypedSegment(String csv, String separator) {
    String[] parts = csv.split(Pattern.quote(separator));
    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid centroid csv: " + csv);
    }
    this.label = parts[0];
    this.lat = Double.valueOf(parts[1]);
    this.lon = Double.valueOf(parts[2]);
  }

  public double getLatitude() {
    return lat;
  }

  public double getLongitude() {
    return lon;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Map<String, ?> getMetadata() {
    Map<String, Double> metadata = new HashMap<String, Double>();
    metadata.put("latitude", lat);
    metadata.put("longitude", lon);
    return metadata;
  }

}
