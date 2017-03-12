package org.perfrepo.dto.report.table_comparison.view;

/**
 * Data transfer object for the table comparison report. Represents one value cell in the comparison table.
 * This object is used for view of the report.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public abstract class ContentCellDto {

    private boolean baseline;

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }
}