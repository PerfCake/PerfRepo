package org.perfrepo.dto.test_execution;

import java.util.Map;

/**
 * Represents one value of test execution {@link TestExecutionDto}.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ValueDto {

    private double value;

    private Map<String, String> parameters;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}