/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.querysegmenter.solr;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class QuerySegmenterQParserPlugin extends QParserPlugin {

  private QuerySegmenterConfig config;

  @SuppressWarnings("rawtypes")
  @Override
  public void init(NamedList args) {
    config = new QuerySegmenterConfig(args);
  }

  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new QuerySegmenterQParser(config, qstr, localParams, params, req);
  }

}
