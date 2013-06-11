package org.jboss.qa.perfrepo.client;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXB;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;

/**
 * 
 * Performance Repository REST API Client.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class PerfRepoClient {

   private static final Logger log = Logger.getLogger(PerfRepoClient.class);

   private static final String CONTENT_TYPE_XML = "text/xml";
   private static final String REST_BASE_URL_TEMPLATE = "http://%s/repo/rest/";
   private String host;
   private String basicAuthHash;

   private HttpClient httpClient;

   public PerfRepoClient(String host, String basicAuthHash) {
      this.host = host;
      this.basicAuthHash = basicAuthHash;
      httpClient = new DefaultHttpClient();
   }

   public void shutdown() {
      httpClient.getConnectionManager().shutdown();
   }

   public String restUrl(String urlTemplate, Object... params) {
      return String.format(String.format(REST_BASE_URL_TEMPLATE, host) + urlTemplate, params);
   }

   public Long createTest(Test test) throws Exception {
      String url = restUrl("test/create");
      HttpPost post = new HttpPost(url);
      post.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_XML);
      post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      JAXB.marshal(test, bos);
      post.setEntity(new ByteArrayEntity(bos.toByteArray()));
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         log.error("Error while creating test\nHTTP Status: " + resp.getStatusLine().getStatusCode() + "\nREST API url: " + url + "\nResponse:\n"
               + EntityUtils.toString(resp.getEntity()));
         return null;
      } else {
         Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
         if (locations != null && locations.length > 0) {
            log.debug("Created new test at: " + locations[0].getValue());
         }
      }
      String strId = EntityUtils.toString(resp.getEntity());
      return new Long(strId);
   }

   public Test getTest(Long id) throws Exception {
      String url = restUrl("test/%s", id);
      HttpGet get = new HttpGet(url);
      get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
         return JAXB.unmarshal(resp.getEntity().getContent(), Test.class);
      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
         return null;
      } else {
         log.error("Error while getting test\nHTTP Status: " + resp.getStatusLine().getStatusCode() + "\nREST API url: " + url + "\nResponse:\n"
               + EntityUtils.toString(resp.getEntity()));
         return null;
      }
   }

   public boolean deleteTest(Long id) throws Exception {
      String url = restUrl("test/%s", id);
      HttpDelete get = new HttpDelete(url);
      get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
         return true;
      } else {
         log.error("Error while deleting test\nHTTP Status: " + resp.getStatusLine().getStatusCode() + "\nREST API url: " + url + "\nResponse:\n"
               + EntityUtils.toString(resp.getEntity()));
         return false;
      }
   }

   public static void main(String[] args) throws Exception {
      Test test = new Test();
      test.setName("Test test");
      test.setGroupId("perfrepouser");
      test.setUid("testtestuid");
      test.setDescription("this is a test test");
      test.setTestMetrics(new ArrayList<TestMetric>());
      test.getTestMetrics().add(new TestMetric());
      Metric metric = new Metric();
      test.getTestMetrics().iterator().next().setMetric(metric);
      metric.setName("test metric");
      metric.setDescription("this is a test metric");
      metric.setComparator("0");
      //      JAXB.marshal(test, new File("target/output.xml"));
      //      System.out.println("creating test ...");
      PerfRepoClient client = new PerfRepoClient("localhost:8080", "cDpw");
      client.deleteTest(22l);
//      Long id = client.createTest(test);
//      System.out.println("created test: " + id);

      //      Test testOnServer = client.getTest(new Long(17));
      //      System.out.println("retrieved test: " + testOnServer.getName() + " uid: " + testOnServer.getUid());

   }
}
