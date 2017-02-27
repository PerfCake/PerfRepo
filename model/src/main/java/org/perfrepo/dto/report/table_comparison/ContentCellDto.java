package org.perfrepo.dto.report.table_comparison;

import org.perfrepo.enums.report.CellStyle;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ContentCellDto {

    private double value;

    private double valueByBaseline;

    private boolean baseline;

    private CellStyle style;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValueByBaseline() {
        return valueByBaseline;
    }

    public void setValueByBaseline(double valueByBaseline) {
        this.valueByBaseline = valueByBaseline;
    }

    public boolean isBaseline() {
        return baseline;
    }

    public void setBaseline(boolean baseline) {
        this.baseline = baseline;
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }
}