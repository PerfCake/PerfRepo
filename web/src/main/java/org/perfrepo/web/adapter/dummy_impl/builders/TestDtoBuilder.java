package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;

import java.util.HashSet;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestDtoBuilder {

    private TestDto testDto;

    public TestDtoBuilder() {
        testDto = new TestDto();
        testDto.setMetrics(new HashSet<>());
        testDto.setAlerts(new HashSet<>());
    }

    public TestDtoBuilder name(String name) {
        testDto.setName(name);
        return this;
    }

    public TestDtoBuilder uid(String uid) {
        testDto.setUid(uid);
        return this;
    }

    public TestDtoBuilder description(String description) {
        testDto.setDescription(description);
        return this;
    }

    public TestDtoBuilder group(GroupDto group) {
        testDto.setGroup(group);
        return this;
    }

    public TestDtoBuilder metric(MetricDto metric) {
        testDto.getMetrics().add(metric);
        return this;
    }

    public TestDtoBuilder alert(AlertDto alert) {
        testDto.getAlerts().add(alert);
        return this;
    }

    public TestDto build() {
        return testDto;
    }
}
