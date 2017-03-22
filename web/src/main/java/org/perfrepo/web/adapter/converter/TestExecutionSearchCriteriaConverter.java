package org.perfrepo.web.adapter.converter;


import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;

import java.util.stream.Collectors;

/**
 * TODO: document this
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
        criteria.setParametersQuery(dto.getParameterQueriesFilter().stream().collect(Collectors.joining(" AND ")));
        criteria.setTagsQuery(dto.getTagQueriesFilter().stream().collect(Collectors.joining(" ")));
        criteria.setStartedAfter(dto.getStartedAfterFilter().stream().findFirst().orElse(null)); //TODO: review the set
        criteria.setStartedBefore(dto.getStartedBeforeFilter().stream().findFirst().orElse(null)); //TODO: review the set
        criteria.setTestName(dto.getTestNamesFilter().stream().findFirst().orElse(null));
        criteria.setTestUIDs(dto.getTestUIDsFilter());
        return criteria;
    }
}
