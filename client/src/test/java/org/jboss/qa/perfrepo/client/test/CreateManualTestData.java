package org.jboss.qa.perfrepo.client.test;

import org.jboss.qa.perfrepo.client.PerfRepoClient;
import org.jboss.qa.perfrepo.model.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Manually test {@link PerfRepoClient} with perfrepo running on localhost
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class CreateManualTestData {

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
      test.setName("JDG client/server test");
      test.setGroupId(testUserRole);
      test.setUid("jdg_cs_test");
      test.setDescription("JDG Client/server test");
      test.addMetric("jdg_cs_avg_read_resp_time", "0", "Average Read response time");
      test.addMetric("jdg_cs_avg_write_resp_time", "1", "Average Write response time");
      test.addMetric("jdg_cs_requests_per_sec", "2", "Number of requests per second");
      return test;
   }

   //   private TestExecution createTestExecution(Long testId) {
   //      TestExecution testExecution = new TestExecution();
   //      Test idHolder = new Test();
   //      idHolder.setId(testId);
   //      testExecution.setTest(idHolder);
   //      testExecution.setName("execution1");
   //      testExecution.setStarted(new Date());
   //      testExecution.addParameter("param1", "value1");
   //      testExecution.addParameter("param2", "value2");
   //      testExecution.addTag("tag1");
   //      testExecution.addTag("tag2");
   //      testExecution.addValue("metric1", 12.0d);
   //      Value v2 = testExecution.addValue("metric2", null);
   //      v2.addParameter("10", "20.0");
   //      v2.addParameter("20", "40.0");
   //      v2.addParameter("30", "60.0");
   //      return testExecution;
   //   }

   @org.testng.annotations.Test
   public void testCreate() throws Exception {
      Test test = createTest();
      Long id = client.createTest(test);
      System.out.println("Created test: " + id);
   }

}