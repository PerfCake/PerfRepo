package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.model.MetricComparator;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class MetricDtoBuilder {

    private MetricDto metricDto;

    public MetricDtoBuilder() {
        metricDto = new MetricDto();
    }

    public MetricDtoBuilder name(String name) {
        metricDto.setName(name);
        return this;
    }

    public MetricDtoBuilder description(String description) {
        metricDto.setDescription(description);
        return this;
    }

    public MetricDtoBuilder comparator(MetricComparator comparator) {
        metricDto.setComparator(comparator);
        return this;
    }

    public MetricDto build() {
        return metricDto;
    }
}
