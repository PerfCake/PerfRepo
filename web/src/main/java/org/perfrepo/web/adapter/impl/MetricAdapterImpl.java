package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.adapter.MetricAdapter;
import org.perfrepo.web.adapter.converter.MetricConverter;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.service.TestService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class MetricAdapterImpl implements MetricAdapter {

    @Inject
    private TestService testService;

    @Inject
    private MetricConverter metricConverter;

    @Override
    public MetricDto getMetric(Long id) {
        Metric metric = testService.getMetric(id);
        MetricDto dto = metricConverter.convertFromEntityToDto(metric);

        return dto;
    }

    @Override
    public MetricDto updateMetric(MetricDto dto) {
        Metric metric = metricConverter.convertFromDtoToEntity(dto);
        Metric updatedMetric = testService.updateMetric(metric);

        return metricConverter.convertFromEntityToDto(updatedMetric);
    }

    @Override
    public MetricDto addMetric(MetricDto dto, Long testId) {
        Metric metric = metricConverter.convertFromDtoToEntity(dto);
        Test test = new Test();
        test.setId(testId);
        Metric createdMetric = testService.addMetric(metric, test);

        return metricConverter.convertFromEntityToDto(createdMetric);
    }

    @Override
    public void removeMetric(Long metricId, Long testId) {
        Metric metric = new Metric();
        metric.setId(metricId);
        Test test = new Test();
        test.setId(testId);

        testService.removeMetricFromTest(metric, test);
    }

    @Override
    public List<MetricDto> getAllMetrics() {
        return metricConverter.convertFromEntityToDto(testService.getAllMetrics());
    }
}
