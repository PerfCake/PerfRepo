package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.test_execution.TestExecutionValueDto;

import java.util.HashMap;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionValueDtoBuilder {

    private TestExecutionValueDto testExecutionValueDto;

    public TestExecutionValueDtoBuilder() {
        testExecutionValueDto = new TestExecutionValueDto();
        testExecutionValueDto.setParameters(new HashMap<>());
    }

    public TestExecutionValueDtoBuilder value(double value) {
        testExecutionValueDto.setValue(value);
        return this;
    }

    public TestExecutionValueDtoBuilder parameter(String name, String value) {
        testExecutionValueDto.getParameters().put(name, value);
        return this;
    }

    public TestExecutionValueDto build() {
        return testExecutionValueDto;
    }
}
