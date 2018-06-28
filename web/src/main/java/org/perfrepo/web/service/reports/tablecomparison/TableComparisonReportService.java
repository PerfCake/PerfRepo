package org.perfrepo.web.service.reports.tablecomparison;

import org.perfrepo.dto.report.table_comparison.ComparisonItemDto;
import org.perfrepo.dto.report.table_comparison.GroupDto;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;
import org.perfrepo.dto.report.table_comparison.TableDto;
import org.perfrepo.dto.report.table_comparison.view.ContentCellDto;
import org.perfrepo.dto.report.table_comparison.view.HeaderCellDto;
import org.perfrepo.dto.report.table_comparison.view.MultiContentCellDto;
import org.perfrepo.dto.report.table_comparison.view.MultiContentCellDto.ChartPointDto;
import org.perfrepo.dto.report.table_comparison.view.RowDto;
import org.perfrepo.dto.report.table_comparison.view.SingleContentCellDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValueParameterDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.MetricComparator;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.enums.report.CellStyle;
import org.perfrepo.enums.report.ComparisonItemSelector;
import org.perfrepo.enums.report.ReportType;
import org.perfrepo.web.adapter.TestExecutionAdapter;
import org.perfrepo.web.adapter.converter.PermissionConverter;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.report.Permission;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.report.ReportProperty;
import org.perfrepo.web.service.ReportService;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.perfrepo.web.service.reports.ReportUtils.createReportProperty;

/**
 * Service bean for table comparison report. Contains all necessary methods to be able to work with table comparison reports.
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TableComparisonReportService {

    @Inject
    private TestDAO testDAO;

    @Inject
    private TestExecutionDAO testExecutionDAO;

    @Inject
    private UserService userService;

    @Inject
    private UserSession userSession;

    @Inject
    private ReportService reportService;

    @Inject
    private TestService testService;

    @Inject
    private PermissionConverter permissionConverter;

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private TestExecutionAdapter testExecutionAdapter;

    @Inject
    private MetricDAO metricDAO;

    /**
     * Creates new table comparison report
     *
     * @return ID of newly created report
     */
    public TableComparisonReportDto create(TableComparisonReportDto reportToCreate) {
        //TODO: handle permissions
        Report report = serialize(reportToCreate);
        Report createdReport = reportService.createReport(report);

        return get(createdReport.getId());
    }

    /**
     * Updates existing table comparison report.
     *
     * @param reportToUpdate
     * @return ID of the updated report
     */
    public TableComparisonReportDto update(TableComparisonReportDto reportToUpdate) {
        Report report = serialize(reportToUpdate);
        Report updatedReport = reportService.updateReport(report);

        return get(updatedReport.getId());
    }

    /**
     * Loads all Group transfer objects of specified report from database and report properties.
     *
     * @param id
     * @return pair of report name and list of groups
     */
    public TableComparisonReportDto get(Long id) {
        Report report = reportService.getReport(id); //TODO: add non existing check
        TableComparisonReportDto computedReport = loadReport(report);
        loadPermissions(report, computedReport);
        return computedReport;
    }

    /**
     * TODO: document this
     *
     * @param dto
     * @return
     */
    public TableComparisonReportDto preview(TableComparisonReportDto dto) {
        Report report = serialize(dto);
        return loadReport(report);
    }

    private TableComparisonReportDto loadReport(Report report) {
        TableComparisonReportDto resultDto = new TableComparisonReportDto();

        loadBasicInfo(report, resultDto);
        loadGroups(report, resultDto);

        return resultDto;
    }

    private void loadGroups(Report entity, TableComparisonReportDto dto) {
        List<GroupDto> groupDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int groupIndex = 0;
        String groupPrefix = "group" + groupIndex + ".";
        while (properties.containsKey(groupPrefix + "name")) {
            GroupDto groupDto = new GroupDto();
            groupDto.setName(properties.get(groupPrefix + "name").getValue());
            groupDto.setDescription(properties.get(groupPrefix + "description").getValue());
            groupDto.setThreshold(Integer.parseInt(properties.get(groupPrefix + "threshold").getValue()));

            loadTables(entity, groupPrefix, groupDto);

            groupDtos.add(groupDto);
            groupIndex++;
            groupPrefix = "group" + groupIndex + ".";
        }

        dto.setGroups(groupDtos);
    }

    private void loadTables(Report entity, String prefix, GroupDto groupDto) {
        List<TableDto> tableDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int tableIndex = 0;
        String tablePrefix = prefix + "table" + tableIndex + ".";
        while (properties.containsKey(tablePrefix + "name")) {
            TableDto tableDto = new TableDto();
            tableDto.setName(properties.get(tablePrefix + "name").getValue());
            tableDto.setDescription(properties.get(tablePrefix + "description").getValue());

            loadItems(entity, tablePrefix, tableDto);
            computeTable(tableDto, groupDto.getThreshold());

            tableDtos.add(tableDto);

            tableIndex++;
            tablePrefix = prefix + "table" + tableIndex + ".";
        }

        groupDto.setTables(tableDtos);
    }

    private void loadItems(Report entity, String prefix, TableDto tableDto) {
        List<ComparisonItemDto> itemDtos = new ArrayList<>();
        Map<String, ReportProperty> properties = entity.getProperties();

        int itemIndex = 0;
        String itemPrefix = prefix + "item" + itemIndex + ".";
        while (properties.containsKey(itemPrefix + "alias")) {
            ComparisonItemDto itemDto = new ComparisonItemDto();

            itemDto.setAlias(properties.get(itemPrefix + "alias").getValue());
            itemDto.setBaseline(Boolean.parseBoolean(properties.get(itemPrefix + "isBaseline").getValue()));
            itemDto.setExecutionId(properties.get(itemPrefix + "execId").getValue() != null ? Long.parseLong(properties.get(itemPrefix + "execId").getValue()) : null);
            itemDto.setParameterQuery(properties.get(itemPrefix + "paramQuery").getValue());
            itemDto.setTagQuery(properties.get(itemPrefix + "tagQuery").getValue());
            itemDto.setSelector(ComparisonItemSelector.valueOf(properties.get(itemPrefix + "selector").getValue()));
            itemDto.setTestId(properties.get(itemPrefix + "testId").getValue() != null ? Long.parseLong(properties.get(itemPrefix + "testId").getValue()) : null);

            itemDtos.add(itemDto);

            itemIndex++;
            itemPrefix = prefix + "item" + itemIndex + ".";
        }

        tableDto.setItems(itemDtos);
    }

    private void loadBasicInfo(Report entity, TableComparisonReportDto dto) {
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setFavourite(reportService.isReportFavorie(entity));
        dto.setName(entity.getName());
        dto.setTypeName(entity.getType().name());
    }

    private void loadPermissions(Report entity, TableComparisonReportDto dto) {
        Set<Permission> permissions = reportService.getReportPermissions(entity);
        dto.setPermissions(PermissionConverter.convertFromEntityToDto(permissions));
    }

    private void computeTable(TableDto tableDto, int threshold) {
        // we retrieve test executions in the same order as the items
        List<TestExecutionDto> selectedTestExecutions = selectTestExecutions(tableDto.getItems());

        List<HeaderCellDto> headers = new ArrayList<>();
        for (int i = 0; i < selectedTestExecutions.size(); i++) {
            ComparisonItemDto item = tableDto.getItems().get(i);
            TestExecutionDto testExecution = selectedTestExecutions.get(i);

            HeaderCellDto header = new HeaderCellDto();
            header.setName(item.getAlias());
            header.setTestExecutionId(testExecution.getId());
            header.setBaseline(item.isBaseline());

            headers.add(header);
        }

        tableDto.setTableHeaderCells(headers);

        fillTable(tableDto.getItems(), selectedTestExecutions, tableDto, threshold);
    }

    private void fillTable(List<ComparisonItemDto> items, List<TestExecutionDto> testExecutions, TableDto tableDto, int threshold) {
        Set<String> metrics = findCommonMetrics(testExecutions);

        List<RowDto> rows = new ArrayList<>();
        TestExecutionDto baseline = findBaseline(items, testExecutions);
        for (String metricName: metrics) {
            RowDto row = new RowDto();
            row.setMetricName(metricName);
            Metric metric = metricDAO.getByName(metricName);

            List<ContentCellDto> cells = new ArrayList<>();
            for (int i = 0; i < testExecutions.size(); i++) {
                TestExecutionDto testExecution = testExecutions.get(i);

                ValuesGroupDto value = getValueByMetric(testExecution, metricName);
                if (value.getValueType() == MeasuredValueType.SINGLE_VALUE) {
                    SingleContentCellDto cell = new SingleContentCellDto();
                    cell.setBaseline(testExecution.equals(baseline));

                    double resultValue = value.getValues().get(0).getValue();
                    double baselineValue = getValueByMetric(baseline, metricName).getValues().get(0).getValue();
                    cell.setValue(resultValue);

                    double differenceFromBaseline = calculateDifferenceFromBaseline(resultValue, baselineValue, metric.getComparator());
                    cell.setValueDifferenceFromBaseline(differenceFromBaseline);
                    cell.setStyle(getCellStyle(differenceFromBaseline, threshold));

                    row.setValueType(MeasuredValueType.SINGLE_VALUE);
                    cells.add(cell);
                } else if (value.getValueType() == MeasuredValueType.MULTI_VALUE) {
                    MultiContentCellDto cell = new MultiContentCellDto();
                    cell.setBaseline(testExecution.equals(baseline));

                    List<ValueDto> baselineValues = getValueByMetric(testExecution, metricName).getValues();

                    List<ChartPointDto> points = new ArrayList<>();
                    Set<String> parametersNames = new HashSet<>();
                    for (int j = 0; j < value.getValues().size(); j++) {
                        ValueDto point = value.getValues().get(j);
                        for (ValueParameterDto parameter: point.getParameters()) {
                            parametersNames.add(parameter.getName());

                            ChartPointDto pointDto = new ChartPointDto();
                            pointDto.setX(parameter.getValue());
                            pointDto.setValue(point.getValue());

                            double baselineValue = baselineValues.get(j).getValue();
                            pointDto.setValueDifferenceFromBaseline(calculateDifferenceFromBaseline(point.getValue(), baselineValue, metric.getComparator()));
                            points.add(pointDto);
                        }
                    }

                    Map<String, List<ChartPointDto>> cellValues = new HashMap<>();
                    for (String parameterName: parametersNames) {
                        cellValues.put(parameterName, points);
                    }

                    row.setValueType(MeasuredValueType.MULTI_VALUE);
                    cell.setValues(cellValues);
                    cells.add(cell);
                } else {
                    throw new IllegalStateException("Invalid value type: " + value.getValueType());
                }
            }
            row.setCells(cells);
            rows.add(row);
        }

        tableDto.setTableRows(rows);
    }

    private double calculateDifferenceFromBaseline(double result, double baseline, MetricComparator comparator) {
        double difference;
        switch (comparator) {
            case HIGHER_BETTER:
                difference = (result / baseline) * 100d - 100;
                break;
            case LOWER_BETTER:
                difference = ((result / baseline) * 100d - 100) * -1;
                break;
            default:
                throw new IllegalStateException("Impossible to get here.");
        }
        return difference;
    }

    private TestExecutionDto findBaseline(List<ComparisonItemDto> items, List<TestExecutionDto> testExecutions) {
        for (int i = 0; i < testExecutions.size(); i++) {
            if (items.get(i).isBaseline()) {
                return testExecutions.get(i);
            }
        }

        return testExecutions.get(0);
    }

    private Set<String> findCommonMetrics(List<TestExecutionDto> testExecutions) {
        Set<String> metrics = new TreeSet<>();
        for (ValuesGroupDto commonMetricCandidate: testExecutions.get(0).getExecutionValuesGroups()) {
            if (testExecutions.stream().allMatch(testExecution ->
                testExecution.getExecutionValuesGroups().stream()
                        .anyMatch(valueGroup -> valueGroup.getMetricName().equals(commonMetricCandidate.getMetricName()))
            )) {
                metrics.add(commonMetricCandidate.getMetricName());
            }
        }

        return metrics;
    }

    private ValuesGroupDto getValueByMetric(TestExecutionDto testExecution, String metric) {
        for (ValuesGroupDto value : testExecution.getExecutionValuesGroups()) {
            if (value.getMetricName().equals(metric)) {
                return value;
            }
        }

        return null;
    }

    private CellStyle getCellStyle(double difference, int threshold) {
        if (difference > threshold) {
            return CellStyle.GOOD;
        }
        if (difference < threshold) {
            return CellStyle.BAD;
        }
        return CellStyle.NEUTRAL;
    }

    private List<TestExecutionDto> selectTestExecutions(List<ComparisonItemDto> items) {
        List<TestExecutionDto> testExecutions = new ArrayList<>();
        for (ComparisonItemDto item: items) {
            TestExecutionDto selectedTestExecution;
            if (item.getSelector() == ComparisonItemSelector.TEST_EXECUTION_ID) {
                selectedTestExecution = testExecutionAdapter.getTestExecution(item.getExecutionId());
            } else if (item.getSelector() == ComparisonItemSelector.TAG_QUERY) {
                TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();

                Test test = testService.getTest(item.getTestId());
                Set<String> tests = new HashSet<>();
                tests.add(test.getUid());
                searchCriteria.setTestUniqueIdsFilter(tests);

                Set<String> tags = new HashSet<>();
                tags.add(item.getTagQuery());
                searchCriteria.setTagQueriesFilter(tags);

                searchCriteria.setLimit(1);
                searchCriteria.setOrderBy(OrderBy.DATE_DESC);

                SearchResult<TestExecutionDto> result = testExecutionAdapter.searchTestExecutions(searchCriteria);
                selectedTestExecution = !result.getData().isEmpty() ?  testExecutionAdapter.getTestExecution(result.getData().get(0).getId()) : null;
            } else if (item.getSelector() == ComparisonItemSelector.PARAMETER_QUERY) {
                TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();

                Test test = testService.getTest(item.getTestId());
                Set<String> tests = new HashSet<>();
                tests.add(test.getUid());
                searchCriteria.setTestUniqueIdsFilter(tests);

                Set<String> parameters = new HashSet<>();
                parameters.add(item.getParameterQuery());
                searchCriteria.setParameterQueriesFilter(parameters);

                searchCriteria.setLimit(1);
                searchCriteria.setOrderBy(OrderBy.DATE_DESC);

                SearchResult<TestExecutionDto> result = testExecutionAdapter.searchTestExecutions(searchCriteria);
                selectedTestExecution = !result.getData().isEmpty() ?  testExecutionAdapter.getTestExecution(result.getData().get(0).getId()) : null;
            } else {
                throw new IllegalStateException("Unsupported item selector: " + item.getSelector());
            }

            testExecutions.add(selectedTestExecution);
        }

        return testExecutions;
    }

    private Report serialize(TableComparisonReportDto dto) {
        Report report = new Report();
        report.setUser(userService.getUser(userSession.getLoggedUser().getId()));

        serializeBasicInfo(dto, report);
        serializeGroups(dto.getGroups(), report);

        return report;
    }

    private void serializeBasicInfo(TableComparisonReportDto dto, Report entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setType(ReportType.TABLE_COMPARISON);
    }

    private void serializeGroups(List<GroupDto> groups, Report entity) {
        for (int i = 0; i < groups.size(); i++) {
            GroupDto groupDto = groups.get(i);
            String groupPrefix = "group" + i + ".";
            createReportProperty(groupPrefix + "name", groupDto.getName(), entity);
            createReportProperty(groupPrefix + "description", groupDto.getDescription(), entity);
            createReportProperty(groupPrefix + "threshold", groupDto.getThreshold(), entity);

            serializeTables(groupDto.getTables(), groupPrefix, entity);
        }
    }

    private void serializeTables(List<TableDto> tables, String prefix, Report entity) {
        for (int i = 0; i < tables.size(); i++) {
            TableDto tableDto = tables.get(i);
            String tablePrefix = prefix + "table" + i + ".";
            createReportProperty(tablePrefix + "name", tableDto.getName(), entity);
            createReportProperty(tablePrefix + "description", tableDto.getDescription(), entity);

            serializeItems(tableDto.getItems(), tablePrefix, entity);
        }
    }

    private void serializeItems(List<ComparisonItemDto> items, String prefix, Report entity) {
        for (int i = 0; i < items.size(); i++) {
            ComparisonItemDto itemDto = items.get(i);
            String itemPrefix = prefix + "item" + i + ".";
            createReportProperty(itemPrefix + "alias", itemDto.getAlias(), entity);
            createReportProperty(itemPrefix + "selector", itemDto.getSelector(), entity);

            createReportProperty(itemPrefix + "execId", itemDto.getExecutionId(), entity);
            createReportProperty(itemPrefix + "testId", itemDto.getTestId(), entity);
            createReportProperty(itemPrefix + "paramQuery", itemDto.getParameterQuery(), entity);
            createReportProperty(itemPrefix + "tagQuery", itemDto.getTagQuery(), entity);
            createReportProperty(itemPrefix + "isBaseline", itemDto.isBaseline(), entity);
        }
    }

}
