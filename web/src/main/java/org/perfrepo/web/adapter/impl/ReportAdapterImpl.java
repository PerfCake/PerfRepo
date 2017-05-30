package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.report.box_plot.BoxPlotReportDto;
import org.perfrepo.dto.report.metric_history.MetricHistoryReportDto;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.ReportAdapter;
import org.perfrepo.web.adapter.converter.PermissionConverter;
import org.perfrepo.web.adapter.converter.ReportConverter;
import org.perfrepo.web.adapter.converter.ReportSearchCriteriaConverter;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.reports.metrichistory.MetricHistoryReportService;
import org.perfrepo.web.session.UserSession;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportAdapterImpl implements ReportAdapter {

    @Inject
    private ReportService reportService;

    @Inject
    private MetricHistoryReportService metricHistoryReportService;

    @Inject
    private UserSession userSession;

    @Override
    public ReportDto getReport(Long id) {
        return null;
    }

    @Override
    public ReportDto createReport(ReportDto report) {
        ReportDto result = null;
        if (report instanceof MetricHistoryReportDto) {
            result = metricHistoryReportService.create((MetricHistoryReportDto) report);
        } else if (report instanceof TableComparisonReportDto) {
            //TODO: add implementation
            throw new UnsupportedOperationException("Not supported yet.");
        } else if (report instanceof BoxPlotReportDto) {
            //TODO: add implementation
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            throw new IllegalArgumentException("Unsupported report type.");
        }

        return result;
    }

    @Override
    public ReportDto updateReport(ReportDto report) {
        return null;
    }

    @Override
    public void removeReport(Long id) {

    }

    @Override
    public List<ReportDto> getAllReports() {
        return null;
    }

    @Override
    public SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams) {
        org.perfrepo.web.service.search.ReportSearchCriteria criteria = ReportSearchCriteriaConverter.convertFromDtoToEntity(searchParams);
        SearchResultWrapper<Report> resultWrapper = reportService.searchReports(criteria);

        SearchResult<ReportDto> result = new SearchResult<>(ReportConverter.convertFromEntityToDto(resultWrapper.getResult()), resultWrapper.getTotalSearchResultsCount(), searchParams.getLimit(), searchParams.getOffset());

        List<Long> favoriteReportsIds = reportService.getFavoriteReports().stream().map(Report::getId).collect(Collectors.toList());
        if (!favoriteReportsIds.isEmpty()) {
            result.getData().stream()
                    .filter(reportDto -> favoriteReportsIds.contains(reportDto.getId()))
                    .forEach(reportDto -> reportDto.setFavourite(true));
        }

        return result;
    }

    @Override
    public ReportSearchCriteria getSearchCriteria() {
        return ReportSearchCriteriaConverter.convertFromEntityToDto(userSession.getReportSearchCriteria());
    }

    @Override
    public Set<PermissionDto> getDefaultReportPermissions() {
        return PermissionConverter.convertFromEntityToDto(reportService.getDefaultPermissions());
    }

    @Override
    public ReportDto getTableComparisonReportPreview() {
        return null;
    }

    @Override
    public void markReportFavourite(Long reportId, boolean favorite) {
        Report report = new Report();
        report.setId(reportId);
        if (favorite) {
            reportService.markReportAsFavorite(report);
        } else {
            reportService.unmarkReportAsFavorite(report);
        }
    }

    @Override
    public void validateWizardReportInfoStep(ReportDto report) {

    }

    @Override
    public void validateWizardReportConfigurationStep(ReportDto report) {

    }

    @Override
    public void validateWizardReportPermissionStep(ReportDto report) {

    }
}
