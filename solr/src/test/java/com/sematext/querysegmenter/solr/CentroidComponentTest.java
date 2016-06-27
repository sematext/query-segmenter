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
import org.apache.solr.common.params.SpatialParams;
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

public class CentroidComponentTest extends AbstractSolrTestCase {

  private static final String CENTROID_REQUEST_HANDLER = "centroid";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    assertU(adoc("id", "1", "title", "Near A c e", "location", "30.739,-94.188"));
    assertU(adoc("id", "2", "title", "Near A c k l e y", "location", "42.5548,-93.0497"));
    assertU(adoc("id", "3", "title", "Near A l c o v a 1", "location", "42.4,-107.1"));
    assertU(adoc("id", "4", "title", "Near A l c o v a 2", "location", "42.4,-73.9"));
    assertU(adoc("id", "5", "title", "Near A a r o n s b u r g", "location", "40.9069,-77.4082"));

    assertU("commit", commit());
  }

  /**
   * The CentroidComponent should change the POINT parameter to Ace center (30.539,-94.788) so instead of returning
   * document 2, which is closer to the original user location, it will return document 1 which is closer to the new
   * POINT.
   */
  @Test
  public void test_centroid() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.QT, CENTROID_REQUEST_HANDLER);
    params.add(CommonParams.Q, "near Ace");
    params.add(SpatialParams.POINT, "42.1548,-93.9497");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='1']");

  }

  /**
   * The CentroidComponent should change the POINT parameter to Alcova 1 center (42.4525,-107.1897) because it is
   * closest to the original user location.
   */
  @Test
  public void test_closest_centroid() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.QT, CENTROID_REQUEST_HANDLER);
    params.add(CommonParams.Q, "Alcova");
    // We choose a point that is closer to Alcova 1 than Alcova 2, but far enough of any documents indexed, so that if
    // this user location if used instead of Alcova 1, no document will matched.
    params.add(SpatialParams.POINT, "42.4,-100");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='3']");

  }

  @Test
  public void test_centroid_2() {

    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(CommonParams.QT, CENTROID_REQUEST_HANDLER);
    params.add(CommonParams.Q, "near Aaronsburg");
    params.add(SpatialParams.POINT, "80,-120");

    SolrQueryRequest req = request(params);

    assertQ(req, "//result[@name='response'][@numFound='1']",
        "//result[@name='response']/doc[1]/str[@name='id'][.='5']");

  }

  private SolrQueryRequest request(ModifiableSolrParams params) {
    SolrCore core = h.getCore();
    SolrRequestHandler handler = core.getRequestHandler(CENTROID_REQUEST_HANDLER);

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
