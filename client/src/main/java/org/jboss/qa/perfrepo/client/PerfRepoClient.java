/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.bind.JAXB;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.Value;

/**
 * 
 * Performance Repository REST API Client.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 * 
 */
public class PerfRepoClient {

   private static final Logger log = Logger.getLogger(PerfRepoClient.class);

   private static final String CONTENT_TYPE_XML = "text/xml";
   private static final String REST_BASE_URL_TEMPLATE = "http://%s/%s/rest/";
   private String host;
   private String url;
   private String basicAuthHash;

   private HttpClient httpClient;

   /**
    * Create the client.
    * 
    * @param host Host (may contain port in form host:port)
    * @param basicAuthHash Base64 encoded username:password for BASIC Authentication
    */
   public PerfRepoClient(String host, String url, String basicAuthHash) {
      this.host = host;
      this.url = url;
      this.basicAuthHash = basicAuthHash;
      httpClient = new DefaultHttpClient();
   }

   /**
    * Shutdown the underlying HTTP client
    */
   public void shutdown() {
      httpClient.getConnectionManager().shutdown();
   }

   private String restUrl(String urlTemplate, Object... params) {
      return String.format(String.format(REST_BASE_URL_TEMPLATE, host, url) + urlTemplate, params);
   }

   private void logHttpError(String msg, HttpRequestBase req, HttpResponse resp) throws Exception {
      log.error(msg + "\nHTTP Status: " + resp.getStatusLine().getStatusCode() + "\nREST API url: " + req.getURI().toString() + "\nResponse:\n"
            + EntityUtils.toString(resp.getEntity()));
   }

   private HttpPost createBasicPost(String relURL, Object... params) {
      HttpPost post = new HttpPost(restUrl(relURL, params));
      post.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_XML);
      post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      return post;
   }

   private HttpGet createBasicGet(String relURL, Object... params) {
      HttpGet get = new HttpGet(restUrl(relURL, params));
      get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      return get;
   }

   private HttpDelete createBasicDelete(String relURL, Object... params) {
      HttpDelete delete = new HttpDelete(restUrl(relURL, params));
      delete.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      return delete;
   }

   private void setPostEntity(HttpPost req, Object obj) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      JAXB.marshal(obj, bos);
      req.setEntity(new ByteArrayEntity(bos.toByteArray()));
   }

   /**
    * Create a new test with subobjects.
    * 
    * @param test The new test.
    * @return ID of new test.
    * @throws Exception
    */
   public Long createTest(Test test) throws Exception {
      HttpPost post = createBasicPost("test/create");
      setPostEntity(post, test);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         logHttpError("Error while creating test", post, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
      if (locations != null && locations.length > 0) {
         log.debug("Created new test at: " + locations[0].getValue());
      }
      Long id = new Long(EntityUtils.toString(resp.getEntity()));
      EntityUtils.consume(resp.getEntity());
      return id;
   }

   /**
    * Adds value to existing testExecution
    *
    * @param te
    * @return ID of new value.
    * @throws Exception
    */
   public Long addValue(TestExecution te) throws Exception {
      HttpPost post = createBasicPost("testExecution/addValue");
      setPostEntity(post, te);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         logHttpError("Error while creating value", post, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
      if (locations != null && locations.length > 0) {
         log.debug("Added new value at: " + locations[0].getValue());
      }
      Long id = new Long(EntityUtils.toString(resp.getEntity()));
      EntityUtils.consume(resp.getEntity());
      return id;
   }

   /**
    * Get test by id.
    * 
    * @param id
    * @return The test
    * @throws Exception
    */
   public Test getTest(Long id) throws Exception {
      HttpGet get = createBasicGet("test/%s", id);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
         Test obj = JAXB.unmarshal(resp.getEntity().getContent(), Test.class);
         EntityUtils.consume(resp.getEntity());
         return obj;
      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
         EntityUtils.consume(resp.getEntity());
         return null;
      } else {
         logHttpError("Error while getting test", get, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
   }

   /**
    * Delete a test.
    * 
    * @param id
    * @return True on success
    * @throws Exception
    */
   public boolean deleteTest(Long id) throws Exception {
      HttpDelete delete = createBasicDelete("test/%s", id);
      HttpResponse resp = httpClient.execute(delete);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
         EntityUtils.consume(resp.getEntity());
         log.debug("Deleted test: " + id);
         return true;
      } else {
         logHttpError("Error while deleting test", delete, resp);
         EntityUtils.consume(resp.getEntity());
         return false;
      }
   }

   /**
    * Create a new test execution.
    * 
    * @param testExecution
    * @return ID of new execution
    * @throws Exception
    */
   public Long createTestExecution(TestExecution testExecution) throws Exception {
      HttpPost post = createBasicPost("testExecution/create");
      setPostEntity(post, testExecution);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         logHttpError("Error while creating test execution", post, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
      if (locations != null && locations.length > 0) {
         log.debug("Created new test execution at: " + locations[0].getValue());
      }
      Long id = new Long(EntityUtils.toString(resp.getEntity()));
      EntityUtils.consume(resp.getEntity());
      return id;
   }

   /**
    * Get test execution by id.
    * 
    * @param id
    * @return Test execution
    * @throws Exception
    */
   public TestExecution getTestExecution(Long id) throws Exception {
      HttpGet get = createBasicGet("testExecution/%s", id);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
         TestExecution obj = JAXB.unmarshal(resp.getEntity().getContent(), TestExecution.class);
         EntityUtils.consume(resp.getEntity());
         return obj;
      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
         EntityUtils.consume(resp.getEntity());
         return null;
      } else {
         logHttpError("Error while getting test execution", get, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
   }

   /**
    * Delete a test execution.
    * 
    * @param id
    * @return True on success
    * @throws Exception
    */
   public boolean deleteTestExecution(Long id) throws Exception {
      HttpDelete req = createBasicDelete("testExecution/%s", id);
      HttpResponse resp = httpClient.execute(req);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
         EntityUtils.consume(resp.getEntity());
         log.debug("Deleted test execution: " + id);
         return true;
      } else {
         logHttpError("Error while deleting test execution", req, resp);
         EntityUtils.consume(resp.getEntity());
         return false;
      }
   }

   /**
    * Add metric to an existing test.
    * 
    * @param testId Test id
    * @param metric Metric
    * @return Id of the new metric.
    * @throws Exception
    */
   public Long addMetric(Long testId, Metric metric) throws Exception {
      HttpPost post = createBasicPost("test/%s/addMetric", testId);
      setPostEntity(post, metric);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         logHttpError("Error while adding metric", post, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
      if (locations != null && locations.length > 0) {
         log.debug("Created new metric at: " + locations[0].getValue());
      }
      Long id = new Long(EntityUtils.toString(resp.getEntity()));
      EntityUtils.consume(resp.getEntity());
      return id;
   }

   /**
    * Get metric by id.
    * 
    * @param id
    * @return The metric
    * @throws Exception
    */
   public Metric getMetric(Long id) throws Exception {
      HttpGet get = createBasicGet("metric/%s", id);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
         Metric metric = JAXB.unmarshal(resp.getEntity().getContent(), Metric.class);
         EntityUtils.consume(resp.getEntity());
         return metric;
      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
         EntityUtils.consume(resp.getEntity());
         return null;
      } else {
         logHttpError("Error while getting metric", get, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
   }

   /**
    * Add attachment to an existing test execution.
    *
    * @param testExecutionId Test execution id
    * @param file File to upload
    * @param mimeType Mime type
    * @param fileNameInRepo The name the attachment will have in the perf repo.
    * @return Id of the new attachment
    * @throws Exception
    */
   public Long uploadAttachment(Long testExecutionId, File file, String mimeType, String fileNameInRepo) throws Exception {
      InputStreamEntity entity = new InputStreamEntity(new FileInputStream(file), file.length());
      return uploadAttachment(testExecutionId, entity, mimeType, fileNameInRepo);
   }

   /**
    * Add attachment to an existing test execution.
    *
    * @param testExecutionId Test execution id
    * @param content File content to upload
    * @param mimeType Mime type
    * @param fileNameInRepo The name the attachment will have in the perf repo.
    * @return Id of the new attachment
    * @throws Exception
    */
   public Long uploadAttachment(Long testExecutionId, byte[] content, String mimeType, String fileNameInRepo) throws Exception {
      return uploadAttachment(testExecutionId, new ByteArrayEntity(content), mimeType, fileNameInRepo);
   }

   private Long uploadAttachment(Long testExecutionId, AbstractHttpEntity entity, String mimeType, String fileNameInRepo) throws Exception {
      HttpPost post = new HttpPost(restUrl("testExecution/%s/addAttachment", testExecutionId));
      post.setHeader(HttpHeaders.CONTENT_TYPE, mimeType);
      post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
      post.setHeader("filename", fileNameInRepo);
      post.setEntity(entity);
      HttpResponse resp = httpClient.execute(post);
      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
         logHttpError("Error while uploading attachment", post, resp);
         EntityUtils.consume(resp.getEntity());
         return null;
      }
      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
      if (locations != null && locations.length > 0) {
         log.debug("Uploaded attachment: " + locations[0].getValue());
      }
      Long id = new Long(EntityUtils.toString(resp.getEntity()));
      EntityUtils.consume(resp.getEntity());
      return id;
   }

   /**
    * Downloads attachment to a local file.
    * 
    * @param attachmentId Attachment id
    * @param file File to save the attachment to
    * @return True on success
    * @throws Exception
    */
   public boolean downloadAttachment(Long attachmentId, File file) throws Exception {
      HttpGet get = createBasicGet("testExecution/attachment/%s", attachmentId);
      HttpResponse resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
         resp.getEntity().writeTo(new FileOutputStream(file));
         EntityUtils.consume(resp.getEntity());
         return true;
      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
         EntityUtils.consume(resp.getEntity());
         return false;
      } else {
         logHttpError("Error while downloading attachment", get, resp);
         EntityUtils.consume(resp.getEntity());
         return false;
      }
   }
}
