package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.model.MetricComparator;
import org.perfrepo.web.adapter.dummy_impl.builders.GroupDtoBuilder;
import org.perfrepo.web.adapter.dummy_impl.builders.MetricDtoBuilder;
import org.perfrepo.web.adapter.dummy_impl.builders.TestDtoBuilder;
import org.perfrepo.web.adapter.dummy_impl.builders.UserDtoBuilder;

import javax.inject.Singleton;

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

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        groupStorage = new GroupStorage();
        userStorage = new UserStorage();
        tokenStorage = new TokenStorage();
        alertStorage = new AlertStorage();
        testToAlertStorage = new TestToAlertStorage();
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

        // ***** TESTS *****
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
}



