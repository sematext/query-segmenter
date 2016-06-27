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

public class AreaTypedSegment extends TypedSegment {

  private static final String DEFAULT_SEPARATOR = ",";

  public static final String TYPE = "area";

  private final double maxlat;
  private final double maxlon;
  private final double minlat;
  private final double minlon;

  public AreaTypedSegment(String csv) {
    this(csv, DEFAULT_SEPARATOR);
  }

  public AreaTypedSegment(String csv, String separator) {
    String[] parts = csv.split(Pattern.quote(separator));
    if (parts.length != 5) {
      throw new IllegalArgumentException("Invalid area csv: " + csv);
    }
    this.label = parts[0];
    this.maxlat = Double.valueOf(parts[1]);
    this.maxlon = Double.valueOf(parts[2]);
    this.minlat = Double.valueOf(parts[3]);
    this.minlon = Double.valueOf(parts[4]);
  }

  public double getMaxlat() {
    return maxlat;
  }

  public double getMaxlon() {
    return maxlon;
  }

  public double getMinlat() {
    return minlat;
  }

  public double getMinlon() {
    return minlon;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Map<String, ?> getMetadata() {
    Map<String, Double> metadata = new HashMap<String, Double>();
    metadata.put("maxlat", maxlat);
    metadata.put("maxlon", maxlon);
    metadata.put("minlat", minlat);
    metadata.put("minlon", minlon);
    return metadata;
  }

}
