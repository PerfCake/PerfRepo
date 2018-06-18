package org.perfrepo.client;

import org.junit.Test;
import org.perfrepo.client.exception.ClientException;

import static org.junit.Assert.assertNotNull;

public class ConnectionTest {

    @Test
    public void testSuccessfulConnection() {
        Client client = new Client(AbstractClientTest.TEST_URL, AbstractClientTest.TEST_USERNAME, AbstractClientTest.TEST_PASSWORD);
        assertNotNull(client.getConnection().getToken());
    }

    @Test(expected = ClientException.class)
    public void testInvalidUrl() {
        new Client("http://non-existing-domain.perfrepo.org", AbstractClientTest.TEST_USERNAME, AbstractClientTest.TEST_PASSWORD);
    }

    @Test(expected = ClientException.class)
    public void testInvalidUsername() {
        new Client(AbstractClientTest.TEST_URL, "non-existent-user", AbstractClientTest.TEST_PASSWORD);
    }

    @Test(expected = ClientException.class)
    public void testInvalidPassword() {
        new Client(AbstractClientTest.TEST_URL, AbstractClientTest.TEST_USERNAME, "not-correct-password");
    }
}
