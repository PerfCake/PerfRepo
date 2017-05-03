package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.web.model.TestExecutionParameter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Converter for Parameter between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ParameterConverter {

    private ParameterConverter() { }

    public static ParameterDto convertFromEntityToDto(TestExecutionParameter parameter) {
        if (parameter == null) {
            return null;
        }

        ParameterDto dto = new ParameterDto();
        dto.setName(parameter.getName());
        dto.setValue(parameter.getValue());

        return dto;
    }

    public static Set<ParameterDto> convertFromEntityToDto(List<TestExecutionParameter> parameters) {
        Set<ParameterDto> dtos = new TreeSet<>();
        parameters.stream().forEach(parameter -> dtos.add(convertFromEntityToDto(parameter)));
        return dtos;
    }

    public static TestExecutionParameter convertFromDtoToEntity(ParameterDto dto) {
        if (dto == null) {
            return null;
        }

        TestExecutionParameter parameter = new TestExecutionParameter();
        parameter.setName(dto.getName());
        parameter.setValue(dto.getValue());

        return parameter;
    }

    public static Map<String, TestExecutionParameter> convertFromDtoToEntity(Set<ParameterDto> dtos) {
        return dtos.stream().collect(Collectors.toMap(ParameterDto::getName, dto -> convertFromDtoToEntity(dto)));
    }

}
