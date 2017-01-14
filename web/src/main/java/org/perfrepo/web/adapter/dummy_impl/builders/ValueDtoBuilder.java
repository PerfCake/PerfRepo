package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.test_execution.ValueDto;

import java.util.HashMap;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueDtoBuilder {

    private ValueDto valueDto;

    public ValueDtoBuilder() {
        valueDto = new ValueDto();
        valueDto.setParameters(new HashMap<>());
    }

    public ValueDtoBuilder value(double value) {
        valueDto.setValue(value);
        return this;
    }

    public ValueDtoBuilder parameter(String name, String value) {
        valueDto.getParameters().put(name, value);
        return this;
    }

    public ValueDto build() {
        return valueDto;
    }
}
