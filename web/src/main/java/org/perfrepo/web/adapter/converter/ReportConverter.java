package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.web.model.report.Report;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportConverter {

    private ReportConverter() { }

    public static ReportDto convertFromEntityToDto(Report entity) {
        if (entity == null) {
            return null;
        }

        ReportDto dto = new ReportDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setFavourite(false); //TODO: implement this
        dto.setName(entity.getName());
        dto.setTypeName(entity.getType().name());
        //TODO: this method is used only for search, hence we don't convert permissions

        return dto;
    }

    public static List<ReportDto> convertFromEntityToDto(List<Report> reports) {
        return reports.stream().map(entity -> convertFromEntityToDto(entity)).collect(Collectors.toList());
    }
}
