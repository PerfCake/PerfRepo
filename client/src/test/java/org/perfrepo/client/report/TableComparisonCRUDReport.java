package org.perfrepo.client.report;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.perfrepo.client.AbstractClientTest;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;

import java.util.Set;

import static org.junit.Assert.assertNull;

public class TableComparisonCRUDReport extends AbstractClientTest {

    private TestDto test;
    private TestExecutionDto testExecution1;
    private TestExecutionDto testExecution2;

    @Before
    public void init() {
        TestDto test = createTest("test1");
        test.setMetrics(createSet(createMetric("metric1")));

        TestDto createdTest = client.test().create(test);
        // we must fetch the test again, because we want to check metrics, which are not loaded by default after creation
        this.test = client.test().get(createdTest.getId());

        TestExecutionDto testExecution = createTestExecution("testExecution1", test);
        Set<ValuesGroupDto> expectedValueGroups = createSet(createValueGroupSingleValue(10, test.getMetrics().stream().findFirst().get()));
        testExecution.setExecutionValuesGroups(expectedValueGroups);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        // we must fetch the test execution again, because we want to check values, which are not loaded by default after creation
        this.testExecution1 = client.testExecution().get(createdTestExecution.getId());

        TestExecutionDto testExecution2 = createTestExecution("testExecution2", test);
        Set<ValuesGroupDto> expectedValueGroups2 = createSet(createValueGroupSingleValue(20, test.getMetrics().stream().findFirst().get()));
        testExecution2.setExecutionValuesGroups(expectedValueGroups2);

        TestExecutionDto createdTestExecution2 = client.testExecution().create(testExecution2);
        // we must fetch the test execution again, because we want to check values, which are not loaded by default after creation
        this.testExecution2 = client.testExecution().get(createdTestExecution2.getId());
    }

    @Test
    @Ignore //TODO: create the tests
    public void testBasic() {
        TestDto test = createTest("test1");

        TestDto createdTest = client.test().create(test);
        assertTest(test, createdTest);

        TestDto retrievedTest = client.test().get(createdTest.getId());
        assertTest(test, retrievedTest);

        client.test().delete(retrievedTest.getId());
        TestDto deletedTest = client.test().get(retrievedTest.getId());
        assertNull(deletedTest);
    }

}
