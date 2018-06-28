package org.perfrepo.web.service.reports.metrichistory;

import org.perfrepo.dto.report.metric_history.BaselineDto;
import org.perfrepo.dto.report.metric_history.BaselineValueDto;
import org.perfrepo.dto.report.metric_history.ChartDto;
import org.perfrepo.dto.report.metric_history.MetricHistoryReportDto;
import org.perfrepo.dto.report.metric_history.SeriesDto;
import org.perfrepo.dto.report.metric_history.SeriesValueDto;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.enums.report.ComparisonItemSelector;
import org.perfrepo.enums.report.ReportType;
import org.perfrepo.web.adapter.converter.PermissionConverter;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.report.Permission;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.report.ReportProperty;
import org.perfrepo.web.model.to.SingleValueResultWrapper;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.perfrepo.web.service.reports.ReportUtils.createReportProperty;

/**
 * TODO: document this and all methods
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MetricHistoryReportService {

    @Inject
    private MetricDAO metricDAO;

    @Inject
    private TestDAO testDao;

    @Inject
    private TestExecutionDAO testExecutionDAO;

    @Inject
    private ReportService reportService;

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private UserSession userSession;

    @Inject
    private UserService userService;

    /**
     * TODO: document this
     *
     * @param reportToCreate
     * @return
     */
    public MetricHistoryReportDto create(MetricHistoryReportDto reportToCreate) {
        //TODO: handle permissions
        Report report = serialize(reportToCreate);
        Report createdReport = reportService.createReport(report);

        return get(createdReport.getId());
    }

    public MetricHistoryReportDto update(MetricHistoryReportDto reportToUpdate) {
        Report report = serialize(reportToUpdate);
        Report updatedReport = reportService.updateReport(report);

        return get(updatedReport.getId());
    }

    public MetricHistoryReportDto get(Long id) {
        Report report = reportService.getReport(id); //TODO: add non existing check
        MetricHistoryReportDto computedReport = loadReport(report);
        return computedReport;
    }

    private MetricHistoryReportDto loadReport(Report report) {
        MetricHistoryReportDto resultDto = new MetricHistoryReportDto();

        loadBasicInfo(report, resultDto);
        loadPermissions(report, resultDto);
        loadCharts(report, resultDto);

        return resultDto;
    }

    private void loadCharts(Report entity, MetricHistoryReportDto dto) {
        List<ChartDto> chartDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int chartIndex = 0;
        String chartPrefix = "chart" + chartIndex + ".";
        while (properties.containsKey(chartPrefix + "name")) {
            ChartDto chartDto = new ChartDto();
            chartDto.setName(properties.get(chartPrefix + "name").getValue());
            chartDto.setDescription(properties.get(chartPrefix + "description").getValue());

            loadSeries(entity, chartPrefix, chartDto);
            loadBaselines(entity, chartPrefix, chartDto);

            chartDtos.add(chartDto);
            chartIndex++;
            chartPrefix = "chart" + chartIndex + ".";
        }

        dto.setCharts(chartDtos);
    }

    private void loadBaselines(Report entity, String prefix, ChartDto chartDto) {
        List<BaselineDto> baselineDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int baselineIndex = 0;
        String baselinePrefix = prefix + "baseline" + baselineIndex + ".";
        while (properties.containsKey(baselinePrefix + "name")) {
            BaselineDto baselineDto = new BaselineDto();
            baselineDto.setName(properties.get(baselinePrefix + "name").getValue());

            Metric metric = metricDAO.get(Long.valueOf(properties.get(baselinePrefix + "metric").getValue()));
            baselineDto.setMetricName(metric.getName());

            Long executionId = Long.valueOf(properties.get(baselinePrefix + "execId").getValue());
            baselineDto.setExecutionId(executionId);

            BaselineValueDto baselineValueDto = new BaselineValueDto();
            baselineValueDto.setX1(0);
            baselineValueDto.setX2(chartDto.getSeries().stream().mapToInt(series -> series.getValues().size()).max().getAsInt() - 1); // -1 as the first position is 0 and we're counting sizes
            baselineValueDto.setExecutionId(executionId);

            TestExecution testExecution = new TestExecution();
            testExecution.setId(executionId);
            double value = testExecutionService.getValues(metric, testExecution).stream().findFirst().orElse(null).getResultValue();
            baselineValueDto.setY(value);

            baselineDto.setValue(baselineValueDto);

            baselineDtos.add(baselineDto);

            baselineIndex++;
            baselinePrefix = prefix + "baseline" + baselineIndex + ".";
        }

        chartDto.setBaselines(baselineDtos);
    }

    private void loadSeries(Report entity, String prefix, ChartDto dto) {
        List<SeriesDto> seriesDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int seriesIndex = 0;
        String seriesPrefix = prefix + "series" + seriesIndex + ".";
        while (properties.containsKey(seriesPrefix + "name")) {
            SeriesDto seriesDto = new SeriesDto();

            seriesDto.setName(properties.get(seriesPrefix + "name").getValue());

            Metric metric = metricDAO.get(Long.valueOf(properties.get(seriesPrefix + "metric").getValue()));
            seriesDto.setMetricName(metric.getName());

            seriesDto.setParameterQuery(properties.get(seriesPrefix + "paramQuery").getValue());
            seriesDto.setTagQuery(properties.get(seriesPrefix + "tagQuery").getValue());
            seriesDto.setTestId(Long.valueOf(properties.get(seriesPrefix + "test").getValue()));
            seriesDto.setFilter(ComparisonItemSelector.valueOf(properties.get(seriesPrefix + "filter").getValue()));

            List<SeriesValueDto> values = computeSeries(seriesDto);
            seriesDto.setValues(values);

            seriesDtos.add(seriesDto);

            seriesIndex++;
            seriesPrefix = prefix + "series" + seriesIndex + ".";
        }

        dto.setSeries(seriesDtos);
    }

    private List<SeriesValueDto> computeSeries(SeriesDto seriesDto) {
        List<SeriesValueDto> valueDtos = new ArrayList<>();

        TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();

        Test test = testDao.get(seriesDto.getTestId());
        searchCriteria.setTestUIDs(Stream.of(test.getUid()).collect(Collectors.toSet()));
        Metric metric = metricDAO.getByName(seriesDto.getMetricName());

        if (seriesDto.getFilter() == ComparisonItemSelector.PARAMETER_QUERY) {
            searchCriteria.setParametersQuery(seriesDto.getParameterQuery());
        } else {
            searchCriteria.setTagsQuery(seriesDto.getTagQuery());
        }

        searchCriteria.setOrderBy(OrderBy.DATE_DESC);

        List<SingleValueResultWrapper> resultWrappers = testExecutionService.getSingleValues(searchCriteria, metric);

        for (int i = 0; i < resultWrappers.size(); i++) {
            SingleValueResultWrapper resultWrapper = resultWrappers.get(i);

            SeriesValueDto valueDto = new SeriesValueDto();
            valueDto.setX(i);
            valueDto.setY(resultWrapper.getValue());
            valueDto.setExecutionId(resultWrapper.getExecId());
            valueDto.setExecutionName(resultWrapper.getExecName());

            valueDtos.add(valueDto);
        }

        return valueDtos;
    }

    private void loadBasicInfo(Report entity, MetricHistoryReportDto dto) {
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setFavourite(reportService.isReportFavorie(entity));
        dto.setName(entity.getName());
        dto.setTypeName(entity.getType().name());
    }

    private void loadPermissions(Report entity, MetricHistoryReportDto dto) {
        Set<Permission> permissions = reportService.getReportPermissions(entity);
        dto.setPermissions(PermissionConverter.convertFromEntityToDto(permissions));
    }

    private Report serialize(MetricHistoryReportDto dto) {
        Report report = new Report();
        report.setUser(userService.getUser(userSession.getLoggedUser().getId()));

        serializeBasicInfo(dto, report);
        serializeCharts(dto.getCharts(), report);

        return report;
    }

    private void serializeBasicInfo(MetricHistoryReportDto dto, Report entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setType(ReportType.METRIC_HISTORY);
    }

    private void serializeCharts(List<ChartDto> charts, Report entity) {
        for (int i = 0; i < charts.size(); i++) {
            ChartDto chart = charts.get(i);
            String chartPrefix = "chart" + i + ".";
            createReportProperty(chartPrefix + "name", chart.getName(), entity);
            createReportProperty(chartPrefix + "description", chart.getDescription(), entity);

            serializeBaselines(chart.getBaselines(), chartPrefix, entity);
            serializeSeries(chart.getSeries(), chartPrefix, entity);
        }
    }

    private void serializeSeries(List<SeriesDto> seriesList, String prefix, Report entity) {
        for (int i = 0; i < seriesList.size(); i++) {
            SeriesDto series =  seriesList.get(i);
            String seriesPrefix = prefix + "series" + i + ".";
            createReportProperty(seriesPrefix + "name", series.getName(), entity);

            Metric metric = metricDAO.getByName(series.getMetricName());
            createReportProperty(seriesPrefix + "metric", metric.getId(), entity);

            createReportProperty(seriesPrefix + "test", series.getTestId(), entity);
            createReportProperty(seriesPrefix + "filter", series.getFilter(), entity);
            createReportProperty(seriesPrefix + "paramQuery", series.getParameterQuery(), entity);
            createReportProperty(seriesPrefix + "tagQuery", series.getTagQuery(), entity);
        }
    }

    private void serializeBaselines(List<BaselineDto> baselines, String prefix, Report entity) {
        for (int i = 0; i < baselines.size(); i++) {
            BaselineDto baseline = baselines.get(i);
            String baselinePrefix = prefix + "baseline" + i + ".";
            createReportProperty(baselinePrefix + "name", baseline.getName(), entity);

            Metric metric = metricDAO.getByName(baseline.getMetricName());
            createReportProperty(baselinePrefix + "metric", metric.getId(), entity);

            createReportProperty(baselinePrefix + "execId", baseline.getExecutionId(), entity);
        }
    }

}
