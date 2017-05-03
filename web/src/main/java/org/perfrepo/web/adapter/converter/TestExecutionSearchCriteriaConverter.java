package org.perfrepo.web.adapter.converter;


import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converter for TestExecutionSearchCriteria between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionSearchCriteriaConverter {

    private TestExecutionSearchCriteriaConverter() { }

    public static TestExecutionSearchCriteria convertFromDtoToEntity(org.perfrepo.dto.test_execution.TestExecutionSearchCriteria dto) {
        TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();

        criteria.setGroups(dto.getGroupsFilter().stream().map(groupName -> { Group group = new Group(); group.setName(groupName); return group; }).collect(Collectors.toSet()));
        criteria.setIds(dto.getIdsFilter());
        criteria.setLimitFrom(dto.getOffset());
        criteria.setLimitHowMany(dto.getLimit());
        criteria.setOrderBy(dto.getOrderBy());
        criteria.setParametersQuery(dto.getParameterQueriesFilter() != null && !dto.getParameterQueriesFilter().isEmpty() ? dto.getParameterQueriesFilter().stream().collect(Collectors.joining(" AND ")) : null);
        criteria.setTagsQuery(dto.getTagQueriesFilter() != null && !dto.getTagQueriesFilter().isEmpty() ? dto.getTagQueriesFilter().stream().collect(Collectors.joining(" ")) : null);
        criteria.setStartedAfter(dto.getStartedAfterFilter() != null ? dto.getStartedAfterFilter().stream().findFirst().orElse(null) : null); //TODO: review the set
        criteria.setStartedBefore(dto.getStartedBeforeFilter() != null ? dto.getStartedBeforeFilter().stream().findFirst().orElse(null) : null); //TODO: review the set
        criteria.setTestName(dto.getTestNamesFilter() != null ? dto.getTestNamesFilter().stream().findFirst().orElse(null) : null);
        criteria.setTestUIDs(dto.getTestUniqueIdsFilter() != null ? dto.getTestUniqueIdsFilter() : null);

        return criteria;
    }

    public static org.perfrepo.dto.test_execution.TestExecutionSearchCriteria convertFromEntityToDto(TestExecutionSearchCriteria criteriaEntity) {
        org.perfrepo.dto.test_execution.TestExecutionSearchCriteria dto = new org.perfrepo.dto.test_execution.TestExecutionSearchCriteria();

        dto.setGroupsFilter(criteriaEntity.getGroups().stream().map(Group::getName).collect(Collectors.toSet()));
        dto.setIdsFilter(criteriaEntity.getIds());
        dto.setOffset(criteriaEntity.getLimitFrom());
        dto.setLimit(criteriaEntity.getLimitHowMany());
        dto.setOrderBy(criteriaEntity.getOrderBy());
        dto.setParameterQueriesFilter(criteriaEntity.getParametersQuery() != null ? Stream.of(criteriaEntity.getParametersQuery()).collect(Collectors.toSet()) :  null);
        dto.setTagQueriesFilter(criteriaEntity.getTagsQuery() != null ? Stream.of(criteriaEntity.getTagsQuery()).collect(Collectors.toSet()) : null);
        dto.setStartedAfterFilter(dto.getStartedAfterFilter());
        dto.setStartedBeforeFilter(dto.getStartedBeforeFilter());
        dto.setTestNamesFilter(criteriaEntity.getTestName() != null ? Stream.of(criteriaEntity.getTestName()).collect(Collectors.toSet()) : null);
        dto.setTestUniqueIdsFilter(criteriaEntity.getTestUIDs());

        return dto;
    }
}
