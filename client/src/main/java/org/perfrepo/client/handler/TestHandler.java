package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;
import org.perfrepo.dto.test.TestDto;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class TestHandler extends Handler {

    private static final String CONTEXT_PATH = "tests/";

    public TestHandler(Connection connection) {
        super(connection);
    }

    public TestDto get(Long id) {
        String path = CONTEXT_PATH + id;
        return connection.get(path, TestDto.class);
    }

    public TestDto create(TestDto test) {
        String path = CONTEXT_PATH;
        return connection.post(path, test, TestDto.class);
    }

    public void delete(Long id) {
        String path = CONTEXT_PATH + id;
        connection.delete(path);
    }

    public List<TestDto> getAll() {
        String path = CONTEXT_PATH;
        return connection.get(path, new GenericType<List<TestDto>>() { });
    }


}
