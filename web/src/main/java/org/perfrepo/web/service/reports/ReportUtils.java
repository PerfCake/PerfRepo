package org.perfrepo.web.service.reports;

import org.perfrepo.enums.report.ComparisonItemSelector;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.report.ReportProperty;

public class ReportUtils {

    private ReportUtils() {

    }

    public static void createReportProperty(String name, String value, Report report) {
        //TODO: this method doesn't deal with updating existing properties at all, probably copy the implementation from boxplot service
        ReportProperty property = new ReportProperty();
        property.setName(name);
        property.setValue(value);
        property.setReport(report);

        report.getProperties().put(name, property);
    }

    public static void createReportProperty(String name, Number value, Report report) {
        createReportProperty(name, value != null ? String.valueOf(value) : null, report);
    }

    public static void createReportProperty(String name, ComparisonItemSelector selector, Report report) {
        createReportProperty(name, selector.toString(), report);
    }

    public static void createReportProperty(String name, boolean value, Report report) {
        createReportProperty(name, String.valueOf(value), report);
    }

}
