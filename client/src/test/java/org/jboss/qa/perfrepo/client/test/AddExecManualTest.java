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

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.client.PerfRepoClient;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.builder.TestExecutionBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Creates test data via {@link PerfRepoClient}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class AddExecManualTest {
   private static final Logger log = Logger.getLogger(AddExecManualTest.class);
   private static PerfRepoClient client;

   @BeforeClass
   public static void createClient() {
      String host = System.getProperty("perfrepo.client.host", "localhost:8080");
      String auth = System.getProperty("perfrepo.client.auth", "cDpw");
      client = new PerfRepoClient(host, auth);
   }

   @AfterClass
   public static void destroyClient() {
      client.shutdown();
      client = null;
   }

   private TestExecution createStressTestExecA(Long testId, int execNumber, String tag, Double valThroughput, Double valRespTime) {
      TestExecutionBuilder exec = TestExecution.builder().testId(testId).name("Execution " + execNumber).started(new Date());
      exec.parameter("exec.number", "" + execNumber);
      exec.tag("stress");
      exec.tag(tag);
      exec.value("max_throughput", valThroughput);
      exec.value("avg_response_time", valRespTime);
      exec.locked();
      return exec.build();
   }

   @org.junit.Test
   public void testCreateTestData() throws Exception {
      TestExecution exec = createStressTestExecA(18l, 10, "branch1x", 20000d, 5d);
      Long execId = client.createTestExecution(exec);
      log.info("Created execution: " + execId);
   }

}