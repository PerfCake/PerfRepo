package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.web.model.TestExecution;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionConverter {

    private TestExecutionConverter() { }

    public static TestExecutionDto convertFromEntityToDto(TestExecution testExecution) {
        if (testExecution == null) {
            return null;
        }

        TestExecutionDto dto = new TestExecutionDto();
        dto.setId(testExecution.getId());
        dto.setName(testExecution.getName());
        dto.setComment(testExecution.getComment());
        dto.setStarted(testExecution.getStarted());
        dto.setTest(TestConverter.convertFromEntityToDto(testExecution.getTest()));

        return dto;
    }

    public static List<TestExecutionDto> convertFromEntityToDto(List<TestExecution> testExecutions) {
        return testExecutions.stream().map(TestExecutionConverter::convertFromEntityToDto).collect(Collectors.toList());
    }

    public static TestExecution convertFromDtoToEntity(TestExecutionDto dto) {
        if (dto == null) {
            return null;
        }

        TestExecution testExecution = new TestExecution();
        testExecution.setId(dto.getId());
        testExecution.setName(dto.getName());
        testExecution.setComment(dto.getComment());
        testExecution.setStarted(dto.getStarted());
        testExecution.setTest(TestConverter.convertFromDtoToEntity(dto.getTest()));
        testExecution.setAttachments(AttachmentConverter.convertFromDtoToEntity(dto.getExecutionAttachments()));
        testExecution.setParameters(ParameterConverter.convertFromDtoToEntity(dto.getExecutionParameters()));
        testExecution.setTags(TagConverter.convertFromDtoToEntity(dto.getTags()));
        testExecution.setValues(ValueConverter.convertFromValueGroupDtoToEntity(dto.getExecutionValuesGroups()));

        return testExecution;
    }
}
