package org.perfrepo.dto.report.table_comparison;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class RowDto {

    private String metricName;

    private List<ContentCellDto> cells;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public List<ContentCellDto> getCells() {
        return cells;
    }

    public void setCells(List<ContentCellDto> cells) {
        this.cells = cells;
    }
}