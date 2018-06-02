package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;
import org.perfrepo.dto.test.TestDto;

public class TestHandler {

    private static final String CONTEXT_PATH = "tests/";

    private Connection connection;

    public TestHandler(Connection connection) {
        this.connection = connection;
    }

    public TestDto get(Long id) {
        String path = CONTEXT_PATH + id;
        return connection.get(path, TestDto.class);
    }

    public TestDto create(TestDto test) {
        String path = CONTEXT_PATH;
        return connection.post(path, test, TestDto.class);
    }


}
