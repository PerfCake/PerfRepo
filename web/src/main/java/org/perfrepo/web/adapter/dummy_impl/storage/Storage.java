package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.test_execution.ValueGroupDto;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.web.adapter.dummy_impl.builders.*;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;

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

    private final TestToAlertStorage testToAlertStorage;

    private final TestExecutionStorage testExecutionStorage;

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        groupStorage = new GroupStorage();
        userStorage = new UserStorage();
        tokenStorage = new TokenStorage();
        alertStorage = new AlertStorage();
        testToAlertStorage = new TestToAlertStorage();
        testExecutionStorage = new TestExecutionStorage();
        initialize();
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

    private void initialize() {
        // ***** USERS *****
        // user 1
        userStorage.create(new UserDtoBuilder()
                .username("grunwjir")
                .email("grunwjir@gmail.com")
                .firstName("Jiri")
                .lastName("Grunwald")
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

        initializeTests();
        initializeTestExecutions();
    }

    private void initializeTests() {
        // test 1
        testStorage.create(new TestDtoBuilder()
                .name("Echo socket test")
                .uid("echo_socket_test")
                .description("Bla bla")
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
        //TODO
        ValueGroupDto g = new ValueGroupDto();

        g.setMetricId(1L);
        g.setValues(new ArrayList<>());
        g.getValues().add(new ValueDtoBuilder()
                .value(10.0)
                .parameter("time", "1")
                .build());
        g.getValues().add(new ValueDtoBuilder()
                .value(15.0)
                .parameter("time", "2")
                .build());
        g.getValues().add(new ValueDtoBuilder()
                .value(17.0)
                .parameter("time", "3")
                .build());

        ValueGroupDto g2 = new ValueGroupDto();

        g2.setMetricId(1L);
        g2.setValues(new ArrayList<>());
        g2.getValues().add(new ValueDtoBuilder()
                .value(11.0)
                .parameter("time", "1")
                .build());
        g2.getValues().add(new ValueDtoBuilder()
                .value(16.0)
                .parameter("time", "2")
                .build());
        g2.getValues().add(new ValueDtoBuilder()
                .value(23.0)
                .parameter("time", "3")
                .build());
        // test execution 1
        testExecutionStorage.create(new TestExecutionDtoBuilder()
                .name("Execution 1 of Echo socket test")
                .test(testStorage.getById(1L))
                .tag("echo")
                .tag("socket")
                .tag("1.0")
                .started(new Date())
                .executionParameter("environment", "test")
                .executionValue(g)
                .executionValue(g2)
                .comment("Bla bla")
                .build());
        // test execution 2
        testExecutionStorage.create(new TestExecutionDtoBuilder()
                .name("Execution 2 of Echo socket test")
                .test(testStorage.getById(1L))
                .tag("echo")
                .tag("socket")
                .tag("1.1")
                .started(new Date())
                .executionParameter("environment", "test")
                .executionValue(g2)
                .comment("Bla bla")
                .build());
    }
}



