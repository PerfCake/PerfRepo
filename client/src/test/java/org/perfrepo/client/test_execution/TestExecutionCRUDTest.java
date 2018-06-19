package org.perfrepo.client.test_execution;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.perfrepo.client.AbstractClientTest;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestExecutionCRUDTest extends AbstractClientTest {

    private TestDto test;

    @Before
    public void init() {
        TestDto test = createTest("test1");
        test.setMetrics(createSet(createMetric("metric1")));

        TestDto createdTest = client.test().create(test);
        // we must fetch the test again, because we want to check metrics, which are not loaded by default after creation
        this.test = client.test().get(createdTest.getId());
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

    @Test
    public void testCreateWithParameters() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        Set<ParameterDto> expectedParameters = createSet(createParameter("param1"), createParameter("param2"), createParameter("param3"));
        testExecution.setExecutionParameters(expectedParameters);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        // we must fetch the test execution again, because we want to check parameters, which are not loaded by default after creation
        createdTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertTestExecution(testExecution, createdTestExecution);
    }

    @Test
    public void testCreateWithSingleValues() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        Set<ValuesGroupDto> expectedValueGroups = createSet(createValueGroupSingleValue(10, test.getMetrics().stream().findFirst().get()));
        testExecution.setExecutionValuesGroups(expectedValueGroups);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        // we must fetch the test execution again, because we want to check values, which are not loaded by default after creation
        createdTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertTestExecution(testExecution, createdTestExecution);
    }

    @Test
    public void testCreateWithMultiValues() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        Set<ValuesGroupDto> expectedValueGroups = createSet(createValueGroupMultiValue(10, test.getMetrics().stream().findFirst().get()));
        testExecution.setExecutionValuesGroups(expectedValueGroups);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        // we must fetch the test execution again, because we want to check values, which are not loaded by default after creation
        createdTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertTestExecution(testExecution, createdTestExecution);
    }

    @Test
    public void testCreateWithAttachments() {
        TestExecutionDto testExecution = createTestExecution("testExecution1", test);

        List<AttachmentDto> expectedAttachments = Arrays.asList(createAttachment("attachment1"), createAttachment("attachment2"), createAttachment("attachment3"));
        testExecution.setExecutionAttachments(expectedAttachments);

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        // we must fetch the test execution again, because we want to check attachments, which are not loaded by default after creation
        createdTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertTestExecution(testExecution, createdTestExecution);
    }

}
