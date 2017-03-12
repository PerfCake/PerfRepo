package org.perfrepo.dto.report.table_comparison.view;

import org.perfrepo.enums.report.CellStyle;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class SingleContentCellDto extends ContentCellDto{

    private double value;

    private double valueDifferenceFromBaseline;

    private CellStyle style;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValueDifferenceFromBaseline() {
        return valueDifferenceFromBaseline;
    }

    public void setValueDifferenceFromBaseline(double valueDifferenceFromBaseline) {
        this.valueDifferenceFromBaseline = valueDifferenceFromBaseline;
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }
}