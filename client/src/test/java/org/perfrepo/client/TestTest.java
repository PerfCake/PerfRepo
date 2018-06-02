package org.perfrepo.client;

import org.junit.Before;
import org.junit.Test;
import org.perfrepo.client.util.TestUtil;
import org.perfrepo.dto.test.TestDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestTest {

    private Client client;

    @Before
    public void init() {
        client = new Client(TestUtil.TEST_URL, TestUtil.TEST_USERNAME, TestUtil.TEST_PASSWORD);
    }

    @Test
    public void testGet() {
        TestDto test = client.test().get(1L);
        assertNotNull(test);
        assertEquals("regression_test", test.getUid());
    }

    @Test
    public void testCreate() {
        TestDto test = new TestDto();
        test.setName("New test");
        test.setUid("new_test");

        TestDto createdTest = client.test().create(test);
        assertEquals(test.getName(), createdTest.getName());
        assertEquals(test.getUid(), createdTest.getUid());
    }
}
