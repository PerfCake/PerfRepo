package org.perfrepo.dto.test_execution;

import org.perfrepo.enums.MeasuredValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents group of test execution values. The value group is for each test metric.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValuesGroupDto implements Comparable<ValuesGroupDto> {

    private String metricName;

    private Set<String> parameterNames = new TreeSet<>();

    private List<ValueDto> values = new ArrayList<>();

    private MeasuredValueType valueType;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Set<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(Set<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public List<ValueDto> getValues() {
        return values;
    }

    public void setValues(List<ValueDto> values) {
        this.values = values;
    }

    public MeasuredValueType getValueType() {
        return valueType;
    }

    public void setValueType(MeasuredValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public int compareTo(ValuesGroupDto o) {
        return metricName.compareTo(o.getMetricName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValuesGroupDto)) return false;

        ValuesGroupDto that = (ValuesGroupDto) o;

        if (getMetricName() != null ? !getMetricName().equals(that.getMetricName()) : that.getMetricName() != null)
            return false;
        if (getParameterNames() != null ? !getParameterNames().equals(that.getParameterNames()) : that.getParameterNames() != null)
            return false;
        if (getValues() != null ? !getValues().equals(that.getValues()) : that.getValues() != null) return false;
        return getValueType() == that.getValueType();
    }

    @Override
    public int hashCode() {
        int result = getMetricName() != null ? getMetricName().hashCode() : 0;
        result = 31 * result + (getParameterNames() != null ? getParameterNames().hashCode() : 0);
        result = 31 * result + (getValues() != null ? getValues().hashCode() : 0);
        result = 31 * result + (getValueType() != null ? getValueType().hashCode() : 0);
        return result;
    }
}