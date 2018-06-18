package org.perfrepo.dto.test_execution;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one measured value of a test execution {@link TestExecutionDto}.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueDto {

    private double value;

    private Set<ValueParameterDto> parameters = new HashSet<>();

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Set<ValueParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ValueParameterDto> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueDto)) return false;

        ValueDto valueDto = (ValueDto) o;

        if (Double.compare(valueDto.getValue(), getValue()) != 0) return false;
        return getParameters() != null ? getParameters().equals(valueDto.getParameters()) : valueDto.getParameters() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getValue());
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getParameters() != null ? getParameters().hashCode() : 0);
        return result;
    }
}