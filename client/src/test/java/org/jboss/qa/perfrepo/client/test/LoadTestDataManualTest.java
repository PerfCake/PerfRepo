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
package org.jboss.qa.perfrepo.client.test;

import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.client.PerfRepoClient;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.builder.TestBuilder;
import org.jboss.qa.perfrepo.model.builder.TestExecutionBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Creates test data via {@link PerfRepoClient}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class LoadTestDataManualTest {
   private static final Logger log = Logger.getLogger(LoadTestDataManualTest.class);
   private static PerfRepoClient client;
   private static String testUserRole;

   @BeforeClass
   public static void createClient() {
      String host = System.getProperty("perfrepo.client.host", "localhost:8080");
      String auth = System.getProperty("perfrepo.client.auth", "cDpw");
      testUserRole = System.getProperty("perfrepo.client.role", "perfrepouser");
      client = new PerfRepoClient(host, auth);
   }

   @AfterClass
   public static void destroyClient() {
      client.shutdown();
      client = null;
      testUserRole = null;
   }

   Test createStressTestA() {
      TestBuilder test = Test.builder().name("Stress test A");
      test.groupId(testUserRole).uid("stress_test_a").description("Stress test for testing purposes");
      test.metric("max_throughput", "0", "Max throughput (ops/sec) reached during the stress test");
      test.metric("avg_response_time", "1", "Average response time (milliseconds)");
      return test.build();
   }

   Test createStressTestB() {
      TestBuilder test = Test.builder().name("Stress test B");
      test.groupId(testUserRole).uid("stress_test_b").description("Stress test for testing purposes. This one has more values per execution");
      test.metric("throughput", "0", "Throughput (ops/sec)");
      test.metric("response_time", "1", "Response time (milliseconds)");
      return test.build();
   }

   private TestExecution createStressTestExecA(Long testId, int execNumber, String tag, Double valThroughput, Double valRespTime) {
      TestExecutionBuilder exec = TestExecution.builder().testId(testId).name("Execution " + execNumber).started(new Date());
      exec.parameter("exec.number", "" + execNumber);
      exec.tag("stress");
      exec.tag(tag);
      exec.value("max_throughput", valThroughput, "Client load", "100");
      exec.value("avg_response_time", valRespTime, "Client load", "100");
      return exec.build();
   }

   private TestExecution createStressTestExecB(Long testId, int execNumber, String tag, Random random) {
      TestExecutionBuilder exec = TestExecution.builder().testId(testId).name("Execution " + execNumber).started(new Date());
      exec.parameter("exec.number", "" + execNumber);
      exec.tag("stress");
      exec.tag(tag);
      for (int i = 0; i < 10; i++) {
         String clientLoad = "" + ((i + 1) * 100);
         exec.value("throughput", 1000d + i * 1000d + 1000d * random.nextDouble(), "Client load", clientLoad);
         exec.value("response_time", i * 2d + 2d * random.nextDouble(), "Client load", clientLoad);
      }
      return exec.build();
   }

   @org.junit.Test
   public void testCreateTestData() throws Exception {
      Test test = createStressTestA();
      Long testId = client.createTest(test);
      log.info("Created test: " + testId);
      Random random = new Random();
      // we can have test executions of the same test for different product branches, this will be discriminated by a tag
      for (int i = 0; i < 10; i++) {
         TestExecution exec = createStressTestExecA(testId, i, "branch1x", 10000d + random.nextDouble() * 20000d, 2d + random.nextDouble() * 6d);
         Long execId = client.createTestExecution(exec);
         log.info("Created execution: " + execId);
      }
      for (int i = 0; i < 10; i++) {
         TestExecution exec = createStressTestExecA(testId, i, "branch2x", 5000d + random.nextDouble() * 2000d, 10d + random.nextDouble() * 8d);
         Long execId = client.createTestExecution(exec);
         log.info("Created execution: " + execId);
      }
      test = createStressTestB();
      testId = client.createTest(test);
      log.info("Created test: " + testId);
      for (int i = 0; i < 5; i++) {
         TestExecution exec = createStressTestExecB(testId, i, "branch1x", random);
         Long execId = client.createTestExecution(exec);
         log.info("Created execution: " + execId);
      }
      for (int i = 0; i < 5; i++) {
         TestExecution exec = createStressTestExecB(testId, i, "branch2x", random);
         Long execId = client.createTestExecution(exec);
         log.info("Created execution: " + execId);
      }

   }

}