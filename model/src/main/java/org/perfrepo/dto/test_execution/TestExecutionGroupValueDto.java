package org.perfrepo.dto.test_execution;

import org.perfrepo.dto.metric.MetricDto;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionGroupValueDto {

    private Long metricId;

    private List<TestExecutionValueDto> values;

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public List<TestExecutionValueDto> getValues() {
        return values;
    }

    public void setValues(List<TestExecutionValueDto> values) {
        this.values = values;
    }
}