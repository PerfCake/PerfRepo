package org.perfrepo.web.adapter.dummy_impl.storage;

import org.apache.commons.lang.time.DateUtils;
import org.perfrepo.dto.report.metric_history.*;
import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.table_comparison.*;
import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.MetricComparator;
import org.perfrepo.enums.report.CellStyle;
import org.perfrepo.enums.report.ComparisonItemSelector;
import org.perfrepo.web.adapter.dummy_impl.builders.*;

import javax.inject.Singleton;
import java.util.*;

/**
 * Temporary in-memory storage interface for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Singleton
public class Storage {

    private final TestStorage testStorage;

    private final MetricStorage metricStorage;

    private final GroupStorage groupStorage;

    private final UserStorage userStorage;

    private final TokenStorage tokenStorage;

    private final AlertStorage alertStorage;

    private final ReportStorage reportStorage;

    private final TestToAlertStorage testToAlertStorage;

    private final TestExecutionStorage testExecutionStorage;

    private final AttachmentStorage attachmentStorage;

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        groupStorage = new GroupStorage();
        userStorage = new UserStorage();
        tokenStorage = new TokenStorage();
        alertStorage = new AlertStorage();
        testToAlertStorage = new TestToAlertStorage();
        testExecutionStorage = new TestExecutionStorage();
        attachmentStorage = new AttachmentStorage();
        reportStorage = new ReportStorage();
        initialize();
        initializeTests();
        initializeTestExecutions();
        initializeReports();
    }

    public TestStorage test() {
        return testStorage;
    }

    public MetricStorage metric() {
        return metricStorage;
    }

    public GroupStorage group() {
        return groupStorage;
    }

    public UserStorage user() {
        return userStorage;
    }

    public TokenStorage token() {
        return tokenStorage;
    }

    public AlertStorage alert() {
        return alertStorage;
    }

    public TestToAlertStorage testToAlert() {
        return testToAlertStorage;
    }

    public TestExecutionStorage testExecution() {
        return testExecutionStorage;
    }

    public AttachmentStorage attachment() {
        return attachmentStorage;
    }

    public ReportStorage report() {
        return reportStorage;
    }

    private void initialize() {
        // ***** USERS *****
        // user 1
        userStorage.create(new UserDtoBuilder()
                .username("grunwjir")
                .email("grunwjir@gmail.com")
                .firstName("Jiri")
                .lastName("Grunwald")
                .password("123456").build());
        // user 2
        userStorage.create(new UserDtoBuilder()
                .username("repouser")
                .email("repouser@gmail.com")
                .firstName("Repo")
                .lastName("PerfUser")
                .password("123456").build());

        // ***** GROUPS *****
        // group 1
        groupStorage.create(new GroupDtoBuilder()
                .name("perfrepouser")
                .build());
        // group 2
        groupStorage.create(new GroupDtoBuilder()
                .name("super user group")
                .build());

        // ***** METRICS *****
        // metric 1
        metricStorage.create(new MetricDtoBuilder()
                .comparator(MetricComparator.HIGHER_BETTER)
                .name("Throughput")
                .description("Amount of transactions produced over time during a test.")
                .build());
        // metric 2
        metricStorage.create(new MetricDtoBuilder()
                .comparator(MetricComparator.LOWER_BETTER)
                .name("Response time")
                .build());
    }

    private void initializeTests() {
        // test 1
        testStorage.create(new TestDtoBuilder()
                .name("Echo socket test")
                .uid("echo_socket_test")
                .description("This is echo socket test")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 2
        testStorage.create(new TestDtoBuilder()
                .name("Second sample test")
                .uid("sample_second_test")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 3
        testStorage.create(new TestDtoBuilder()
                .name("Third sample test")
                .uid("sample_third_test")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 4
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 4")
                .uid("sample_test_4")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 5
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 5")
                .uid("sample_test_5")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 6
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 6")
                .uid("sample_test_6")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .build());
        // test 7
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 7")
                .uid("sample_test_7")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 8
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 8")
                .uid("sample_test_8")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 9
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 9")
                .uid("sample_test_9")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .build());
    }

    private void initializeTestExecutions() {
        initializeTestExecutionsMultiValue();
        initializeTestExecutionsSingleValue();
    }

    private void initializeTestExecutionsSingleValue() {
        // test execution 2 - single-value
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(2L).getName())
                    .test(testStorage.getById(2L))
                    .tag("test")
                    .tag("example")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(2L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .comment("Execution comment...")
                    .build());
        }

        // test execution 3 - single-value
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(3L).getName())
                    .test(testStorage.getById(3L))
                    .tag("test")
                    .tag("example")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(1L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(2L))
                            .value(new ValueDtoBuilder()
                                    .value(600.0 - i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .comment("Execution comment...")
                    .build());
        }
    }

    private void initializeTestExecutionsMultiValue() {
        AttachmentDto attachment = new AttachmentDto();
        attachment.setFilename("log.txt");
        attachment.setContent("Hello!".getBytes());
        attachment.setSize(attachment.getContent().length);
        attachment.setMimeType("plain/text");
        attachmentStorage.create(attachment);
        // multi-value test execution for test id 1
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(1L).getName())
                    .test(testStorage.getById(1L))
                    .tag("echo")
                    .tag("socket")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionParameter("server", "technecium")
                    .executionParameter("lib-version", "1.0.3")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(1L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .parameter("time", 10)
                                    .parameter("percent", 30)
                                    .build())
                            .value(new ValueDtoBuilder()
                                    .value(15.0 + i)
                                    .parameter("time", 20)
                                    .parameter("percent", 60)
                                    .build())
                            .value(new ValueDtoBuilder()
                                    .value(17.0 + i)
                                    .parameter("time", 30)
                                    .parameter("percent", 90)
                                    .build())
                            .valueType(MeasuredValueType.MULTI_VALUE)
                            .parameterNames("time", "percent")
                            .build())
                    .comment("Nightly build of Echo socket test, version: " + i + ".0")
                    .executionAttachment(attachment)
                    .build());
        }
    }

    private void initializeReports() {
        MetricHistoryReportDto metricReport1 = new MetricHistoryReportDto();
        metricReport1.setName("Metric history report 1");
        metricReport1.setTypeName("Metric history report");
        metricReport1.setDescription("Description of <strong>metric history</strong> report.");
        reportStorage.create(metricReport1);

        MetricHistoryReportDto metricReport2 = new MetricHistoryReportDto();
        metricReport2.setName("Metric history report 2");
        metricReport2.setTypeName("Metric history report");
        metricReport2.setDescription("Description of <strong>metric history</strong> report.");
        reportStorage.create(metricReport2);

        initializeTableComparisonReport();
        initializeMetricHistoryReport();
    }

    private void initializeMetricHistoryReport() {
        MetricHistoryReportDto metricReport2 = new MetricHistoryReportDto();
        reportStorage.create(metricReport2);
        metricReport2.setName("Metric history report WITH DATA");
        metricReport2.setTypeName("Metric history report");
        metricReport2.setDescription("Description of <strong>metric history</strong> report.");
        metricReport2.setCharts(new ArrayList<>());
        // chart 1
        ChartDto chart1 = new ChartDto();
        metricReport2.getCharts().add(chart1);
        chart1.setName("Response time history of project 1");
        chart1.setDescription("This is first chart of <strong>metric history report.</strong>");
        chart1.setSeries(new ArrayList<>());
        chart1.setBaselines(new ArrayList<>());
        // series 1
        SeriesDto series1 = new SeriesDto();
        series1.setName("Response time A");
        series1.setValues(prepareChartValues1());
        chart1.getSeries().add(series1);
        // series 2
        SeriesDto series2 = new SeriesDto();
        series2.setName("Response time B");
        series2.setValues(prepareChartValues2());
        chart1.getSeries().add(series2);
        // baseline 1
        BaselineDto baseline1 = new BaselineDto();
        baseline1.setName("Base response time");
        baseline1.setValue(prepareBaselineValue1());
        chart1.getBaselines().add(baseline1);
        // chart 2
        ChartDto chart2 = new ChartDto();
        metricReport2.getCharts().add(chart2);
        chart2.setName("Throughput of socket");
        chart2.setDescription("This is second chart.");
        chart2.setSeries(new ArrayList<>());
        chart2.setBaselines(new ArrayList<>());
        // series 1
        SeriesDto series21 = new SeriesDto();
        series21.setName("Throughput");
        series21.setValues(prepareThroughputValues());
        chart2.getSeries().add(series21);
        // baseline 2
        BaselineDto baseline2 = new BaselineDto();
        baseline2.setName("Baseline - average");
        baseline2.setValue(prepareBaselineValue2(series21));
        chart2.getBaselines().add(baseline2);
        // permissions
        List<PermissionDto> permissions = new ArrayList<>();
        permissions.add(preparePermission(AccessLevel.PUBLIC, AccessType.READ, null, null, null, null));
        permissions.add(preparePermission(AccessLevel.GROUP, AccessType.WRITE, 1L, null, "super users", null));
        permissions.add(preparePermission(AccessLevel.USER, AccessType.READ, null, 1L, null, "Jiri Grunwald (grunwjir)"));
        metricReport2.setPermissions(permissions);
    }

    private List<SeriesValueDto> prepareChartValues1() {
        List<SeriesValueDto> values = new ArrayList<>();
        values.add(prepareChartValue(1, 20.23, 2L, "Execution 2 of Echo X socket test"));
        values.add(prepareChartValue(2, 20.53, 3L, "Execution 3 of Echo X socket test"));
        values.add(prepareChartValue(3, 20.83, 4L, "Execution 4 of Echo X socket test"));
        values.add(prepareChartValue(4, 20.33, 5L, "Execution 5 of Echo X socket test"));
        values.add(prepareChartValue(5, 20.21, 6L, "Execution 6 of Echo X socket test"));
        values.add(prepareChartValue(6, 21.03, 7L, "Execution 7 of Echo X socket test"));
        return values;
    }

    private List<SeriesValueDto> prepareChartValues2() {
        List<SeriesValueDto> values = new ArrayList<>();
        values.add(prepareChartValue(1, 21.03, 10L, "Execution 10 of Echo socket test"));
        values.add(prepareChartValue(2, 21.13, 11L, "Execution 11 of Echo socket test"));
        values.add(prepareChartValue(3, 20.53, 12L, "Execution 12 of Echo socket test"));
        values.add(prepareChartValue(4, 19.23, 13L, "Execution 13 of Echo socket test"));
        values.add(prepareChartValue(5, 19.98, 14L, "Execution 14 of Echo socket test"));
        values.add(prepareChartValue(6, 20.43, 15L, "Execution 15 of Echo socket test"));
        return values;
    }

    private BaselineValueDto prepareBaselineValue1() {
        BaselineValueDto value = new BaselineValueDto();
        value.setX1(1);
        value.setX2(6);
        value.setY(20.50);
        value.setExecutionId(1L);
        value.setExecutionName("Execution 1 of Echo socket test");
        return value;
    }

    private BaselineValueDto prepareBaselineValue2(SeriesDto series) {
        BaselineValueDto value = new BaselineValueDto();
        value.setX1(1);
        value.setX2(series.getValues().size());
        value.setY(series.getValues().stream().map(SeriesValueDto::getY).reduce(0.0, Double::sum) / series.getValues().size());
        value.setExecutionId(1L);
        value.setExecutionName("Execution 1 of Echo socket test");
        return value;
    }

    private List<SeriesValueDto> prepareThroughputValues() {
        List<SeriesValueDto> values = new ArrayList<>();
        Random rn = new Random();
        for (int i = 1; i <= 100; i++) {
            values.add(prepareChartValue(i, rn.nextInt(401) + 100, (long) i, "Execution " + i + " of Echo socket test"));
        }
        return values;
    }

    private SeriesValueDto prepareChartValue(int x, double y, Long id, String name) {
        SeriesValueDto value = new SeriesValueDto();
        value.setExecutionId(id);
        value.setExecutionName(name);
        value.setX(x);
        value.setY(y);
        return value;
    }

    private void initializeTableComparisonReport() {
        TableComparisonReportDto tableReport3 = new TableComparisonReportDto();
        reportStorage.create(tableReport3);
        tableReport3.setName("Table comparison report WITH DATA");
        tableReport3.setTypeName("Table comparison report");
        tableReport3.setDescription("Description of <strong>table comparison</strong> report.");
        // group 1
        GroupDto group1 = new GroupDto();
        group1.setName("First group");
        group1.setDescription("This is first group of <strong>table comparison table</strong>");
        group1.setThreshold(5);
        tableReport3.setGroups(new ArrayList<>());
        tableReport3.getGroups().add(group1);
        // table 1
        TableDto table1 = new TableDto();
        group1.setTables(new ArrayList<>());
        // add table to group twice
        group1.getTables().add(table1);
        group1.getTables().add(table1);
        table1.setName("First comparison table");
        table1.setDescription("Description of first table...");
        // table 1 - comparison items
        List<ComparisonItemDto> items1 = new ArrayList<>();
        table1.setItems(items1);
        items1.add(prepareComparisonItem("Test execution 1", 1L, null, false, null, null, ComparisonItemSelector.TEST_EXECUTION_ID));
        items1.add(prepareComparisonItem("Test execution 2", 2L, 1L, true, "echo AND test", null, ComparisonItemSelector.TAG_QUERY));
        items1.add(prepareComparisonItem("Test execution 3", 3L, 1L, false, "socket OR test", "version=1.2", ComparisonItemSelector.PARAMETER_QUERY));
        items1.add(prepareComparisonItem("Test execution 3", 4L, 2L, false, null, "version=1.2", ComparisonItemSelector.TEST_EXECUTION_ID));
        // table 1 - header cells
        List<HeaderCellDto> headerCells1 = new ArrayList<>();
        table1.setTableHeaderCells(headerCells1);
        headerCells1.add(prepareHeaderCell("Test execution 1", false, 1L));
        headerCells1.add(prepareHeaderCell("Test execution 2", true, 2L));
        headerCells1.add(prepareHeaderCell("Test execution 3", false, 3L));
        headerCells1.add(prepareHeaderCell("Test execution 4", false, 4L));
        // table 1 - rows
        List<RowDto> rows = new ArrayList<>();
        table1.setTableRows(rows);
        rows.add(prepareRow("Response time",
                prepareContentCell(false, CellStyle.BAD, 22.0, 10.0),
                prepareContentCell(true, CellStyle.NEUTRAL, 20.0, 0.0),
                prepareContentCell(false, CellStyle.BAD, 26.0, 30.55555),
                prepareContentCell(false, CellStyle.GOOD, 18.0, -10.1)));
        rows.add(prepareRow("Throughput",
                prepareContentCell(false, CellStyle.NEUTRAL, 15.45, 3.0),
                prepareContentCell(true, CellStyle.NEUTRAL, 15.0, 0.0),
                prepareContentCell(false, CellStyle.BAD, 14.25, -5.0),
                prepareContentCell(false, CellStyle.GOOD, 16.5, 10.0)));
        // add group to report twice
        tableReport3.setGroups(new ArrayList<>());
        tableReport3.getGroups().add(group1);
        tableReport3.getGroups().add(group1);
        // permissions
        List<PermissionDto> permissions = new ArrayList<>();
        permissions.add(preparePermission(AccessLevel.PUBLIC, AccessType.READ, null, null, null, null));
        permissions.add(preparePermission(AccessLevel.GROUP, AccessType.WRITE, 1L, null,
                "super users", null));
        permissions.add(preparePermission(AccessLevel.USER, AccessType.READ, null, 1L,
                null, "Jiri Grunwald (grunwjir)"));
        tableReport3.setPermissions(permissions);
    }

    private ComparisonItemDto prepareComparisonItem(String alias, Long executionId, Long testId, boolean baseline,
                                                    String tagQuery, String parameterQuery,
                                                    ComparisonItemSelector selector) {
        ComparisonItemDto item = new ComparisonItemDto();
        item.setAlias(alias);
        item.setExecutionId(executionId);
        item.setTestId(testId);
        item.setBaseline(baseline);
        item.setTagQuery(tagQuery);
        item.setParameterQuery(parameterQuery);
        item.setSelector(selector);
        return item;
    }

    private HeaderCellDto prepareHeaderCell(String name, boolean baseline, Long testExecutionId) {
        HeaderCellDto headerCell = new HeaderCellDto();
        headerCell.setName(name);
        headerCell.setBaseline(baseline);
        headerCell.setTestExecutionId(testExecutionId);
        return  headerCell;
    }

    private ContentCellDto prepareContentCell(boolean baseline, CellStyle style, double value, double valueByBaseline) {
        ContentCellDto contentCell = new ContentCellDto();
        contentCell.setBaseline(baseline);
        contentCell.setStyle(style);
        contentCell.setValue(value);
        contentCell.setValueByBaseline(valueByBaseline);
        return contentCell;
    }

    private RowDto prepareRow(String metricName, ContentCellDto... cells) {
        RowDto row = new RowDto();
        row.setMetricName(metricName);
        row.setCells(Arrays.asList(cells));
        return row;
    }

    private PermissionDto preparePermission(AccessLevel level, AccessType type, Long groupId, Long userId,
                                            String groupName, String userFullName) {
        PermissionDto permission = new PermissionDto();
        permission.setLevel(level);
        permission.setType(type);
        permission.setGroupId(groupId);
        permission.setUserId(userId);
        permission.setGroupName(groupName);
        permission.setUserFullName(userFullName);
        return permission;
    }
}



