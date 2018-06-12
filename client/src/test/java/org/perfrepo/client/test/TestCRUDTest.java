package org.perfrepo.client.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.perfrepo.client.Client;
import org.perfrepo.client.util.TestUtil;
import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.dto.test.TestDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCRUDTest {

    private Client client;

    @Before
    public void init() {
        client = new Client(TestUtil.TEST_URL, TestUtil.TEST_USERNAME, TestUtil.TEST_PASSWORD);
    }

    @After
    public void teardown() {
        client.test().getAll().stream()
                .forEach(test -> client.test().delete(test.getId()));
    }

    @Test
    public void testCRUD() {
        TestDto test = createTest("test1");

        TestDto createdTest = client.test().create(test);
        assertEquals(test.getName(), createdTest.getName());
        assertEquals(test.getUid(), createdTest.getUid());

        TestDto retrievedTest = client.test().get(createdTest.getId());
        assertEquals(test.getName(), retrievedTest.getName());
        assertEquals(test.getUid(), retrievedTest.getUid());

        client.test().delete(retrievedTest.getId());
        TestDto deletedTest = client.test().get(retrievedTest.getId());
        assertNull(deletedTest);
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
