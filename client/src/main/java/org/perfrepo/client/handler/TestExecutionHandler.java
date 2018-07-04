package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;

import javax.ws.rs.core.GenericType;
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
        String createdUri = connection.post(path, testExecution);

        return get(popId(createdUri));
    }

    public void delete(Long id) {
        String path = CONTEXT_PATH + id;
        connection.delete(path);
    }

    public List<TestExecutionDto> getAll() {
        String path = CONTEXT_PATH;
        return connection.get(path, new GenericType<List<TestExecutionDto>>() { });
    }

    public List<TestExecutionDto> search(TestExecutionSearchCriteria searchCriteria) {
        String path = CONTEXT_PATH + "search";
        return connection.post(path, searchCriteria, new GenericType<List<TestExecutionDto>>() { });
    }

}
