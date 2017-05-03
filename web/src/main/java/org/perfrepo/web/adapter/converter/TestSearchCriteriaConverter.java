package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test.TestSearchCriteria;
import org.perfrepo.web.model.user.Group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Converter for TestSearchCriteria between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestSearchCriteriaConverter {

    private TestSearchCriteriaConverter() { }

    public org.perfrepo.web.service.search.TestSearchCriteria convertFromDtoToEntity(org.perfrepo.dto.test.TestSearchCriteria criteria) {
        org.perfrepo.web.service.search.TestSearchCriteria criteriaEntity = new org.perfrepo.web.service.search.TestSearchCriteria();

        criteriaEntity.setGroups(criteria.getGroupsFilter() != null ? criteria.getGroupsFilter().stream().map(groupString -> { Group group = new Group(); group.setName(groupString); return group; }).collect(Collectors.toSet()) : null);
        //TODO: review the set
        criteriaEntity.setUid(criteria.getUniqueIdsFilter() != null ? criteria.getUniqueIdsFilter().stream().findFirst().orElse(null) : null);
        criteriaEntity.setName(criteria.getNamesFilter() != null ? criteria.getNamesFilter().stream().findFirst().orElse(null) : null);
        criteriaEntity.setLimitHowMany(criteria.getLimit());
        criteriaEntity.setLimitFrom(criteria.getOffset());
        criteriaEntity.setOrderBy(criteria.getOrderBy());

        return criteriaEntity;
    }

    public TestSearchCriteria convertFromEntityToDto(org.perfrepo.web.service.search.TestSearchCriteria criteria) {
        TestSearchCriteria dto = new TestSearchCriteria();

        dto.setGroupsFilter(criteria.getGroups().stream().map(Group::getName).collect(Collectors.toSet()));
        dto.setUIDsFilterX(criteria.getUid() != null ? new HashSet<>(Arrays.asList(criteria.getUid())) : null);
        dto.setNamesFilter(criteria.getName() != null ? new HashSet<>(Arrays.asList(criteria.getName())) : null);
        dto.setLimit(criteria.getLimitHowMany());
        dto.setOffset(criteria.getLimitFrom());
        dto.setOrderBy(criteria.getOrderBy());

        return dto;
    }
}
