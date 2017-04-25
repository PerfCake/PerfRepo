package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValueParameterDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.ValueParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ValueConverter {

    private ValueConverter() { }

    public static ValueDto convertFromEntityToDto(Value value) {
        if (value == null) {
            return null;
        }

        ValueDto dto = new ValueDto();
        dto.setValue(value.getResultValue());
        dto.setParameters(convertFromEntityToDto(value.getParameters().values()));

        return dto;
    }

    public static ValueParameterDto convertFromEntityToDto(ValueParameter valueParameter) {
        if (valueParameter == null) {
            return null;
        }

        ValueParameterDto dto = new ValueParameterDto();
        dto.setName(valueParameter.getName());
        dto.setValue(Double.parseDouble(valueParameter.getParamValue()));

        return dto;
    }

    public static Set<ValueParameterDto> convertFromEntityToDto(Collection<ValueParameter> parameters) {
        return parameters.stream().map(parameter -> convertFromEntityToDto(parameter)).collect(Collectors.toSet());
    }

    public static Set<ValuesGroupDto> convertFromEntityToDto(List<Value> values) {
        Map<Long, ValuesGroupDto> dtos = new HashMap<>();
        for (Value value: values) {
            ValuesGroupDto groupDto = dtos.get(value.getMetric().getId());
            if (groupDto == null) {
                groupDto = new ValuesGroupDto();
                groupDto.setMetricName(value.getMetric().getName());
                dtos.put(value.getMetric().getId(), groupDto);
            }


            groupDto.getValues().add(convertFromEntityToDto(value));

            if (!value.getParameters().isEmpty()) {
                groupDto.getParameterNames().addAll(value.getParameters().keySet());
            }

            if (groupDto.getValues().size() > 1) {
                groupDto.setValueType(MeasuredValueType.MULTI_VALUE);
            } else {
                groupDto.setValueType(MeasuredValueType.SINGLE_VALUE);
            }
        }

        return new TreeSet<>(dtos.values());
    }

    public static List<Value> convertFromValueGroupDtoToEntity(Set<ValuesGroupDto> valuesGroupDtos) {
        List<Value> values = new ArrayList<>();
        for (ValuesGroupDto valuesGroupDto: valuesGroupDtos) {
            List<Value> valuesPart = convertFromDtoToEntity(valuesGroupDto.getValues());
            valuesPart.stream().forEach(value -> { Metric metric = new Metric(); metric.setName(valuesGroupDto.getMetricName()); value.setMetric(metric); });
            values.addAll(valuesPart);
        }

        return values;
    }

    public static Value convertFromDtoToEntity(ValueDto dto) {
        if (dto == null) {
            return null;
        }

        Value value = new Value();
        value.setResultValue(dto.getValue());

        Map<String, ValueParameter> parameters = convertFromDtoToEntity(dto.getParameters());
        parameters.values().stream().forEach(parameter -> parameter.setValue(value));
        value.setParameters(parameters);

        return value;
    }

    public static List<Value> convertFromDtoToEntity(List<ValueDto> dtos) {
        return dtos.stream().map(dto -> convertFromDtoToEntity(dto)).collect(Collectors.toList());
    }

    public static Map<String, ValueParameter> convertFromDtoToEntity(Set<ValueParameterDto> dtos) {
        return dtos.stream().collect(Collectors.toMap(ValueParameterDto::getName, dto -> convertFromDtoToEntity(dto)));
    }

    public static ValueParameter convertFromDtoToEntity(ValueParameterDto dto) {
        if (dto == null) {
            return null;
        }

        ValueParameter parameter = new ValueParameter();
        parameter.setName(dto.getName());
        parameter.setParamValue(String.valueOf(dto.getValue()));

        return parameter;
    }
}
