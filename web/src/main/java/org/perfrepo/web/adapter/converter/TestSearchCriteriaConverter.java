package org.perfrepo.web.adapter.converter;

import org.perfrepo.web.model.user.Group;

import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestSearchCriteriaConverter {

    public org.perfrepo.web.service.search.TestSearchCriteria convertFromDtoToEntity(org.perfrepo.dto.test.TestSearchCriteria criteria) {
        org.perfrepo.web.service.search.TestSearchCriteria criteriaEntity = new org.perfrepo.web.service.search.TestSearchCriteria();

        criteriaEntity.setGroups(criteria.getGroupFilters().stream().map(groupString -> { Group group = new Group(); group.setName(groupString); return group; }).collect(Collectors.toSet()));
        //TODO: don't use sets in the DTO search criteria
        criteriaEntity.setUid(criteria.getUidFilters().stream().findFirst().orElse(null));
        criteriaEntity.setName(criteria.getNameFilters().stream().findFirst().orElse(null));
        criteriaEntity.setLimitHowMany(criteria.getLimit());
        criteriaEntity.setLimitFrom(criteria.getOffset());
        criteriaEntity.setOrderBy(criteria.getOrderBy());

        return criteriaEntity;
    }
}
