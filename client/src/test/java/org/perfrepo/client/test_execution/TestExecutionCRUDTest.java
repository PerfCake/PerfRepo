package org.perfrepo.client.test_execution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.perfrepo.client.Client;
import org.perfrepo.client.util.TestUtil;
import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestExecutionCRUDTest {

    private Client client;

    private TestDto test;

    @Before
    public void init() {
        client = new Client(TestUtil.TEST_URL, TestUtil.TEST_USERNAME, TestUtil.TEST_PASSWORD);

        test = client.test().create(createTest("test1"));
    }

    @After
    public void teardown() {
        client.testExecution().getAll().stream()
                .forEach(testExecution -> client.testExecution().delete(testExecution.getId()));
        client.test().getAll().stream()
                .forEach(test -> client.test().delete(test.getId()));
    }

    @Test
    public void testCRUD() {
        TestExecutionDto testExecution = createTestExecution("testExecution1");

        TestExecutionDto createdTestExecution = client.testExecution().create(testExecution);
        assertEquals(testExecution.getName(), createdTestExecution.getName());

        TestExecutionDto retrievedTestExecution = client.testExecution().get(createdTestExecution.getId());
        assertEquals(testExecution.getName(), retrievedTestExecution.getName());

        client.testExecution().delete(retrievedTestExecution.getId());
        TestExecutionDto deletedTestExecution = client.testExecution().get(retrievedTestExecution.getId());
        assertNull(deletedTestExecution);
    }

    private TestExecutionDto createTestExecution(String name) {
        TestExecutionDto testExecution = new TestExecutionDto();
        testExecution.setName("name_" + name);
        testExecution.setTest(test);

        return testExecution;
    }

    private TestDto createTest(String name) {
        TestDto test = new TestDto();
        test.setName("name_" + name);
        test.setUid("uid_" + name);

        GroupDto groupDto = new GroupDto();
        groupDto.setName(TestUtil.TEST_GROUP);
        test.setGroup(groupDto);

        return test;
    }
}
