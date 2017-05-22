package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.ReportAdapter;
import org.perfrepo.web.adapter.converter.ReportConverter;
import org.perfrepo.web.adapter.converter.ReportSearchCriteriaConverter;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.session.UserSession;

import javax.inject.Inject;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportAdapterImpl implements ReportAdapter {

    @Inject
    private ReportService reportService;

    @Inject
    private UserSession userSession;

    @Override
    public ReportDto getReport(Long id) {
        return null;
    }

    @Override
    public ReportDto createReport(ReportDto report) {
        return null;
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
        return result;
    }

    @Override
    public ReportSearchCriteria getSearchCriteria() {
        return ReportSearchCriteriaConverter.convertFromEntityToDto(userSession.getReportSearchCriteria());
    }

    @Override
    public List<PermissionDto> getDefaultReportPermissions() {
        return null;
    }

    @Override
    public ReportDto getTableComparisonReportPreview() {
        return null;
    }

    @Override
    public void markReportFavourite(Long reportId, boolean favourite) {

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
