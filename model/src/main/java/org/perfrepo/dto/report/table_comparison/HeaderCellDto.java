package org.perfrepo.dto.report.table_comparison;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class HeaderCellDto {

    private String name;

    private Long testExecutionId;

    private boolean baseline;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTestExecutionId() {
        return testExecutionId;
    }

    public void setTestExecutionId(Long testExecutionId) {
        this.testExecutionId = testExecutionId;
    }

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }
}