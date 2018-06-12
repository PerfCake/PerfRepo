package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;
import org.perfrepo.dto.test_execution.TestExecutionDto;

import java.util.ArrayList;
import java.util.List;

public class TestExecutionHandler extends Handler {

    private static final String CONTEXT_PATH = "test-executions/";

    public TestExecutionHandler(Connection connection) {
        super(connection);
    }

    public TestExecutionDto get(Long id) {
        String path = CONTEXT_PATH + id;
        return connection.get(path, TestExecutionDto.class);
    }

    public TestExecutionDto create(TestExecutionDto testExecution) {
        String path = CONTEXT_PATH;
        return connection.post(path, testExecution, TestExecutionDto.class);
    }

    public void delete(Long id) {
        String path = CONTEXT_PATH + id;
        connection.delete(path);
    }

    public List<TestExecutionDto> getAll() {
        String path = CONTEXT_PATH;
        return connection.get(path, ArrayList.class);
    }


}
