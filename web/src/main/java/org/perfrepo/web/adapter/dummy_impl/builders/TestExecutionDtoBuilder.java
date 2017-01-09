package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionValueDto;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionDtoBuilder {

    private TestExecutionDto testExecutionDto;

    public TestExecutionDtoBuilder() {
        testExecutionDto = new TestExecutionDto();
        testExecutionDto.setTags(new HashSet<>());
        testExecutionDto.setExecutionParameters(new HashMap<>());
        testExecutionDto.setExecutionValues(new HashMap<>());
    }

    public TestExecutionDtoBuilder name(String name) {
        testExecutionDto.setName(name);
        return this;
    }

    public TestExecutionDtoBuilder test(TestDto test) {
        testExecutionDto.setTest(test);
        return this;
    }

    public TestExecutionDtoBuilder tag(String tag) {
        testExecutionDto.getTags().add(tag);
        return this;
    }

    public TestExecutionDtoBuilder commnet(String comment) {
        testExecutionDto.setComment(comment);
        return this;
    }

    public TestExecutionDtoBuilder started(Date started) {
        testExecutionDto.setStarted(started);
        return this;
    }

    public TestExecutionDtoBuilder executionParameter(String name, String value) {
        testExecutionDto.getExecutionParameters().put(name, value);
        return this;
    }

    public TestExecutionDtoBuilder executionValue(MetricDto metric, TestExecutionValueDto value) {
        testExecutionDto.getExecutionValues().put(metric, value);
        return this;
    }

    public TestExecutionDto build() {
        return testExecutionDto;
    }
}
