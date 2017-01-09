package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.test_execution.TestExecutionDto;

import java.util.*;

/**
 * Temporary in-memory test execution storage for development purpose.
 * TODO
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionStorage {

    private Long key = 1L;
    private List<TestExecutionDto> data = new ArrayList<>();

    public TestExecutionDto getById(Long id) {
        Optional<TestExecutionDto> testExecution = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return testExecution.isPresent() ? testExecution.get() : null;
    }

    public TestExecutionDto create(TestExecutionDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public List<TestExecutionDto> getAll() {
        return data;
    }

    private Long getNextId() {
        return key++;
    }
}

