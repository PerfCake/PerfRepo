package org.jboss.qa.perfrepo.client.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.qa.perfrepo.client.PerfRepoClient;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.Value;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Manually test {@link PerfRepoClient} with perfrepo running on localhost
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class PerfRepoClientManualTest {

   private static final String ENCODING = "UTF-8";
   private PerfRepoClient client;
   private String testUserRole;

   @BeforeClass
   public void createClient() {
      String host = System.getProperty("perfrepo.client.host", "localhost:8080");
      String auth = System.getProperty("perfrepo.client.auth", "cDpw");
      testUserRole = System.getProperty("perfrepo.client.role", "perfrepouser");
      client = new PerfRepoClient(host, auth);
   }

   @AfterClass
   public void destroyClient() {
      client.shutdown();
      client = null;
      testUserRole = null;
   }

   private Test createTest() {
      Test test = new Test();
      test.setName("test1");
      test.setGroupId(testUserRole);
      test.setUid("testtestuid");
      test.setDescription("this is a test test");
      test.addMetric("metric1", "0", "this is a test metric 1");
      test.addMetric("metric2", "1", "this is a test metric 2");
      return test;
   }

   private TestExecution createTestExecution(Long testId) {
      TestExecution testExecution = new TestExecution();
      Test idHolder = new Test();
      idHolder.setId(testId);
      testExecution.setTest(idHolder);
      testExecution.setName("execution1");
      testExecution.setStarted(new Date());
      testExecution.addParameter("param1", "value1");
      testExecution.addParameter("param2", "value2");
      testExecution.addTag("tag1");
      testExecution.addTag("tag2");
      testExecution.addValue("metric1", 12.0d);
      Value v2 = testExecution.addValue("metric2", null);
      v2.addParameter("10", "20.0");
      v2.addParameter("20", "40.0");
      v2.addParameter("30", "60.0");
      return testExecution;
   }

   private static void assertMetricEquals(Metric actual, Metric expected) {
      assertEquals(actual.getComparator(), expected.getComparator());
      assertEquals(actual.getDescription(), expected.getDescription());
      assertEquals(actual.getName(), expected.getName());
      assertEquals(actual.getTestMetrics(), expected.getTestMetrics());
      assertEquals(actual.getValues(), expected.getValues());
   }

   private static void assertMetricListEquals(List<Metric> actual, List<Metric> expected) {
      assertEquals(actual == null, expected == null);
      if (actual == null)
         return;
      assertEquals(actual.size(), expected.size());
      Iterator<Metric> allActual = actual.iterator();
      Iterator<Metric> allExpected = actual.iterator();
      while (allExpected.hasNext()) {
         assertEquals(allActual.hasNext(), true);
         assertMetricEquals(allActual.next(), allExpected.next());
      }
   }

   @org.testng.annotations.Test
   public void testCreateDeleteTest() throws Exception {
      Test test = createTest();
      Long id = client.createTest(test);
      assertNotNull(id);
      Test test2 = client.getTest(id);

      assertEquals(test2.getName(), test.getName());
      assertEquals(test2.getDescription(), test.getDescription());
      assertEquals(test2.getGroupId(), test.getGroupId());
      assertEquals(test2.getId(), id);
      assertEquals(test2.getUid(), test.getUid());
      assertEquals(test2.getTestExecutions(), null);
      assertMetricListEquals(test2.getSortedMetrics(), test.getSortedMetrics());

      client.deleteTest(id);
   }

   @org.testng.annotations.Test
   public void testCreateDeleteTestExecution() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);
      assertNotNull(testId);
      TestExecution testExecution = createTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);
      assertNotNull(testExecutionId);
      TestExecution testExecution2 = client.getTestExecution(testExecutionId);
      assertNotNull(testExecution2);
      assertEquals(testExecution2.getId(), testExecutionId);
      assertEquals(testExecution2.getName(), testExecution.getName());
      assertEquals(testExecution2.getStarted(), testExecution.getStarted());
      assertEquals(testExecution2.getAttachments().size(), 0);
      assertEquals(testExecution2.getParameters().size(), 2);
      Map<String, String> params = testExecution2.getParametersAsMap();
      assertEquals(params.get("param1"), "value1");
      assertEquals(params.get("param2"), "value2");
      assertEquals(testExecution2.getTestExecutionTags().size(), 2);
      List<String> tags = testExecution2.getSortedTags();
      assertEquals(tags.size(), 2);
      assertEquals(tags.get(0), "tag1");
      assertEquals(tags.get(1), "tag2");
      assertEquals(testExecution2.getValues().size(), 2);
      Map<String, Double> results = testExecution2.getResultValuesAsMap();
      assertEquals(results.size(), 2);
      assertEquals(results.get("metric1"), 12.0d);
      assertEquals(results.get("metric2"), null);
      Value v2 = testExecution2.getValuesAsMap().get("metric2");
      Map<String, String> v2params = v2.getParametersAsMap();
      assertEquals(v2params.size(), 3);
      assertEquals(v2params.get("10"), "20.0");
      assertEquals(v2params.get("20"), "40.0");
      assertEquals(v2params.get("30"), "60.0");

      client.deleteTestExecution(testExecutionId);
      client.deleteTest(testId);
   }

   @org.testng.annotations.Test
   public void testCreateDeleteAttachment() throws Exception {
      Test test = createTest();
      Long testId = client.createTest(test);
      assertNotNull(testId);
      TestExecution testExecution = createTestExecution(testId);
      Long testExecutionId = client.createTestExecution(testExecution);
      assertNotNull(testExecutionId);
      createWithContent("target/testfile.txt", "this is a test file");
      Long attachmentId = client.uploadAttachment(testExecutionId, new File("target/testfile.txt"), "text/plain", "attachment1.txt");
      assertTrue(client.downloadAttachment(attachmentId, new File("target/testfile2.txt")));
      existsWithContent("target/testfile2.txt", "this is a test file");
      client.deleteTestExecution(testExecutionId);
      client.deleteTest(testId);
   }

   private boolean createWithContent(String file, String contentLine) throws IOException {
      File f = new File(file);
      if (f.getParentFile() != null) {
         f.getParentFile().mkdirs();
      }
      PrintWriter p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), ENCODING));
      p.print(contentLine);
      p.close();
      return true;
   }

   private boolean existsWithContent(String file, String contentLine) throws IOException {
      BufferedReader r = null;
      try {

         File f = new File(file);
         if (!f.exists()) {
            return false;
         }
         if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
         }
         r = new BufferedReader(new InputStreamReader(new FileInputStream(f), ENCODING));
         return r.readLine().equals(contentLine);
      } finally {
         if (r != null) {
            r.close();
         }
      }
   }
}