package org.perfrepo.client;

import org.perfrepo.client.handler.TestHandler;

public class Client {

    private final Connection connection;

    private TestHandler testHandler;

    public Client(String url, String username, String password) {
        connection = new Connection(url, username, password);

        testHandler = new TestHandler(connection);
    }

    public Connection getConnection() {
        return connection;
    }

    public TestHandler test() {
        return testHandler;
    }

}
