package org.perfrepo.dto.report;

import org.perfrepo.enums.ReportType;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ReportDto {

    private Long id;

    private String name;

    private String description;

    private ReportType type;

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

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }
}