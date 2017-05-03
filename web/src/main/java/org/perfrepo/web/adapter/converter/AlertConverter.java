package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.web.model.Alert;
import org.perfrepo.web.model.Tag;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converter for Alert between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class AlertConverter {

    private AlertConverter() { }

    public static AlertDto convertFromEntityToDto(Alert alert) {
        if (alert == null) {
            return null;
        }

        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setName(alert.getName());
        dto.setDescription(alert.getDescription());
        dto.setCondition(alert.getCondition());
        dto.setLinks(Stream.of(alert.getLinks().split(" ")).collect(Collectors.toSet()));
        dto.setTags(alert.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setTestId(alert.getTest() != null ? alert.getTest().getId() : null);

        return dto;
    }

    public static Set<AlertDto> convertFromEntityToDto(Set<Alert> alerts) {
        Set<AlertDto> dtos = new TreeSet<>();
        alerts.stream().forEach(alert -> dtos.add(convertFromEntityToDto(alert)));
        return dtos;
    }
}
