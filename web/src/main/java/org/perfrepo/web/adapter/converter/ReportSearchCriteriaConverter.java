package org.perfrepo.web.adapter.converter;

import org.perfrepo.web.service.search.ReportSearchCriteria;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ReportSearchCriteriaConverter {

    private ReportSearchCriteriaConverter() { }

    public static ReportSearchCriteria convertFromDtoToEntity(org.perfrepo.dto.report.ReportSearchCriteria dto) {
        if (dto == null) {
            return null;
        }

        ReportSearchCriteria criteriaEntity = new ReportSearchCriteria();

        //criteriaEntity.setFilter(dto.get);
        criteriaEntity.setLimitFrom(dto.getOffset());
        criteriaEntity.setLimitHowMany(dto.getLimit());
        criteriaEntity.setName(dto.getNamesFilter() != null ? dto.getNamesFilter().stream().findFirst().orElse(null) : null); //review this
        criteriaEntity.setOrderBy(dto.getOrderBy());
        // TODO: search by type

        return criteriaEntity;
    }

    public static org.perfrepo.dto.report.ReportSearchCriteria convertFromEntityToDto(ReportSearchCriteria criteriaEntity) {
        if (criteriaEntity == null) {
            return null;
        }

        org.perfrepo.dto.report.ReportSearchCriteria dto = new org.perfrepo.dto.report.ReportSearchCriteria();

        dto.setLimit(criteriaEntity.getLimitHowMany());
        dto.setOffset(criteriaEntity.getLimitFrom());
        dto.setNamesFilter(criteriaEntity.getName() != null ? Stream.of(criteriaEntity.getName()).collect(Collectors.toSet()) : null);
        dto.setOrderBy(criteriaEntity.getOrderBy());

        return dto;
    }
}
