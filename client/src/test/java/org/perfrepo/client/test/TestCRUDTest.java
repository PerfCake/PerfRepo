package org.perfrepo.client.test;

import org.junit.Test;
import org.perfrepo.client.AbstractClientTest;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestCRUDTest extends AbstractClientTest {

    @Test
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

    @Test
    public void testCreateWithMetrics() {
        TestDto test = createTest("test1");

        Set<MetricDto> expectedMetrics = createSet(createMetric("metric1"), createMetric("metric2"), createMetric("metric3"));
        test.setMetrics(expectedMetrics);

        TestDto createdTest = client.test().create(test);
        // we must fetch the test again, because we want to check metrics, which are not loaded by default after creation
        createdTest = client.test().get(createdTest.getId());
        assertTest(test, createdTest);
    }

}
