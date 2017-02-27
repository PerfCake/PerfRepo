package org.perfrepo.dto.report.table_comparison;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TableDto {

    private String name;

    private String description;

    private List<ComparisonItemDto> items;

    private List<HeaderCellDto> tableHeaderCells;

    private List<RowDto> tableRows;

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

    public List<ComparisonItemDto> getItems() {
        return items;
    }

    public void setItems(List<ComparisonItemDto> items) {
        this.items = items;
    }

    public List<HeaderCellDto> getTableHeaderCells() {
        return tableHeaderCells;
    }

    public void setTableHeaderCells(List<HeaderCellDto> tableHeaderCells) {
        this.tableHeaderCells = tableHeaderCells;
    }

    public List<RowDto> getTableRows() {
        return tableRows;
    }

    public void setTableRows(List<RowDto> tableRows) {
        this.tableRows = tableRows;
    }
}