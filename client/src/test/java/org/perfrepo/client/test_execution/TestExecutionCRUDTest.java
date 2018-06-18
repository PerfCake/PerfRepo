package org.perfrepo.client.test_execution;

import org.junit.Before;
import org.junit.Test;
import org.perfrepo.client.AbstractClientTest;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestExecutionCRUDTest extends AbstractClientTest {

    private TestDto test;

    @Before
    public void init() {
        test = client.test().create(createTest("test1"));
    }

    @Test
    public void testBasic() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        assertTestExecution(testExecution, createdTestExecution);

        TestExecutionDto retrievedTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertTestExecution(testExecution, retrievedTestExecution);

        client.testExecution().delete(retrievedTestExecution.getId());
        TestExecutionDto deletedTestExecution = client.testExecution().get(retrievedTestExecution.getId());
        assertNull(deletedTestExecution);
    }

    @Test
    public void testCreateWithTags() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        Set<String> expectedTags = createSet("tag1", "tag2", "tag3");
        testExecution.setTags(expectedTags);
        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        assertTestExecution(testExecution, createdTestExecution);
    }

}
