package org.perfrepo.client;

import org.junit.Test;
import org.perfrepo.client.exception.ClientException;
import org.perfrepo.client.util.TestUtil;

import static org.junit.Assert.assertNotNull;

public class ConnectionTest {

    @Test
    public void testSuccessfulConnection() {
        Client client = new Client(TestUtil.TEST_URL, TestUtil.TEST_USERNAME, TestUtil.TEST_PASSWORD);
        assertNotNull(client.getConnection().getToken());
    }

    @Test(expected = ClientException.class)
    public void testInvalidUrl() {
        new Client("http://non-existing-domain.perfrepo.org", TestUtil.TEST_USERNAME, TestUtil.TEST_PASSWORD);
    }

    @Test(expected = ClientException.class)
    public void testInvalidUsername() {
        new Client(TestUtil.TEST_URL, "non-existent-user", TestUtil.TEST_PASSWORD);
    }

    @Test(expected = ClientException.class)
    public void testInvalidPassword() {
        new Client(TestUtil.TEST_URL, TestUtil.TEST_USERNAME, "not-correct-password");
    }
}
