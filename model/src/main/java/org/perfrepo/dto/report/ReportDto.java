package org.perfrepo.dto.report;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@JsonTypeInfo (
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TableComparisonReportDto.class, name = "TABLE_COMPARISON"),
        @JsonSubTypes.Type(value = MetricHistoryReportDto.class, name = "METRIC_HISTORY") })
public abstract class ReportDto {

    private Long id;

    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}