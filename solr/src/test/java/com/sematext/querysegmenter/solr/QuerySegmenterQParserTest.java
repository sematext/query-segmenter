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
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

public class QuerySegmenterQParserTest extends AbstractSolrTestCase {

  private static final String DISMAX_QPARSER = "dismax_qparser";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    assertU(adoc("id", "1", "project", "Solr", "type", "wiki", "title", "Solr is great!"));
    assertU(adoc("id", "2", "project", "Tika", "type", "wiki", "title", "Solr can be used with Tika"));
    assertU(adoc("id", "3", "project", "Lucene", "type", "wiki", "title", "Locations are fun!", "location", "45.5,-93.5"));
    assertU(adoc("id", "4", "city", "new york"));

    assertU("commit", commit());
  }

  /**
   * The query "solr" should be rewritten to "project:Solr" thus limiting the results to only document 1.
   */
  @Test
  public void test_only_solr_project_with_request_handler() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.QT, DISMAX_QPARSER);
    params.add("qq", "solr");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='1']");
  }
  
  /**
   * The query "solr" should be rewritten to "project:Solr" thus limiting the results to only document 1.
   */
  @Test
  public void test_only_solr_project_with_qparser_directly() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add("q", "{!segmenter_qparser}solr");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='1']");
  }
  
  /**
   * The query "Buffalo" should be rewritten to "location:[46,-93 TO 45,-94]" thus limiting the 
   * results to only document 3.
   */
  @Test
  public void test_area_location() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.Q, "{!segmenter_qparser}Buffalo");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='3']");
  }

  @Test
  public void test_city_synonym() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.Q, "{!segmenter_qparser}nyc");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='4']");
  }

  private SolrQueryRequest request(ModifiableSolrParams params) {
    SolrCore core = h.getCore();
    SolrRequestHandler handler = core.getRequestHandler(DISMAX_QPARSER);

    SolrQueryResponse rsp = new SolrQueryResponse();
    NamedList<Object> list = new NamedList<Object>();
    list.add("responseHeader", new SimpleOrderedMap<Object>());
    rsp.setAllValues(list);
    SolrQueryRequest req = new LocalSolrQueryRequest(core, params);
    handler.handleRequest(req, rsp);
    return req;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig.xml", "schema.xml", "solr");
    //initCore("./solr/collection1/conf/solrconfig.xml", "./solr/collection1/conf/schema.xml");
  }
}
