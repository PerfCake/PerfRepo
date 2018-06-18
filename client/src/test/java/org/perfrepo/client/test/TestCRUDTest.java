package org.perfrepo.client.test;

import org.junit.Test;
import org.perfrepo.client.AbstractClientTest;
import org.perfrepo.dto.test.TestDto;

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

}
