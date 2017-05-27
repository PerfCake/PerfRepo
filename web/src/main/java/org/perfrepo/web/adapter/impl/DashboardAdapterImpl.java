package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.dashboard.DashboardContent;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.adapter.DashboardAdapter;
import org.perfrepo.web.adapter.converter.ReportConverter;
import org.perfrepo.web.adapter.converter.TestExecutionConverter;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;

import javax.inject.Inject;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class DashboardAdapterImpl implements DashboardAdapter {

    @Inject
    private ReportService reportService;

    @Inject
    private TestExecutionService testExecutionService;

    private DashboardContent content;

    @Override
    public DashboardContent getDashboardContent() {
        content = new DashboardContent();

        setFavoriteReports();
        setLastTestExecutions();

        return content;
    }

    /**
     * TODO: document this
     *
     */
    private void setFavoriteReports() {
        content.setFavouriteReports(ReportConverter.convertFromEntityToDto(reportService.getFavoriteReports()));
    }

    /**
     * TODO: document this
     *
     */
    private void setLastTestExecutions() {
        TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
        criteria.setLimitHowMany(6);
        criteria.setOrderBy(OrderBy.DATE_DESC);

        content.setLatestTestExecutions(TestExecutionConverter.convertFromEntityToDto(testExecutionService.searchTestExecutions(criteria).getResult()));
    }
}
