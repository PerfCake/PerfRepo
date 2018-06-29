package org.perfrepo.client;

import org.perfrepo.client.handler.ReportHandler;
import org.perfrepo.client.handler.TestExecutionHandler;
import org.perfrepo.client.handler.TestHandler;

public class Client {

    private final Connection connection;

    private TestHandler testHandler;
    private TestExecutionHandler testExecutionHandler;
    private ReportHandler reportHandler;

    public Client(String url, String username, String password) {
        connection = new Connection(url, username, password);

        testHandler = new TestHandler(connection);
        testExecutionHandler = new TestExecutionHandler(connection);
        reportHandler = new ReportHandler(connection);
    }

    public Connection getConnection() {
        return connection;
    }

    public TestHandler test() {
        return testHandler;
    }

    public TestExecutionHandler testExecution() {
        return testExecutionHandler;
    }

    public ReportHandler report() {
        return reportHandler;
    }
}
