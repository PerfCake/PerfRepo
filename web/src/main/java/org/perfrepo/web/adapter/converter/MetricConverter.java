package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.web.model.Metric;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Converter for Metric between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class MetricConverter {

    private MetricConverter() { }

    public static MetricDto convertFromEntityToDto(Metric metric) {
        if (metric == null) {
            return null;
        }

        MetricDto dto = new MetricDto();
        dto.setId(metric.getId());
        dto.setName(metric.getName());
        dto.setDescription(metric.getDescription());
        dto.setComparator(metric.getComparator());

        return dto;
    }

    public static Set<MetricDto> convertFromEntityToDto(Set<Metric> metrics) {
        Set<MetricDto> dtos = new TreeSet<>();
        metrics.stream().forEach(metric -> dtos.add(convertFromEntityToDto(metric)));
        return dtos;
    }

    public static List<MetricDto> convertFromEntityToDto(List<Metric> metrics) {
        return metrics.stream().map(test -> convertFromEntityToDto(test)).collect(Collectors.toList());
    }

    public static Metric convertFromDtoToEntity(MetricDto dto) {
        if (dto == null) {
            return null;
        }

        Metric metric = new Metric();
        metric.setId(dto.getId());
        metric.setName(dto.getName());
        metric.setDescription(dto.getDescription());
        metric.setComparator(dto.getComparator());

        return metric;
    }

    public static Set<Metric> convertFromDtoToEntity(Set<MetricDto> dtos) {
        Set<Metric> metrics = new TreeSet<>();
        dtos.stream().forEach(dto -> metrics.add(convertFromDtoToEntity(dto)));
        return metrics;
    }
}
