package org.perfrepo.dto.report.metric_history;

import org.perfrepo.enums.report.ComparisonItemSelector;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class BaselineDto {

    private String name;

    private Long metricId;

    private Long executionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }
}
