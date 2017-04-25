package org.perfrepo.dto.test_execution;

import org.perfrepo.enums.OrderBy;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Data transfer object that contains filter parameters for {@link TestExecutionDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionSearchCriteria {

    private Set<Long> idsFilter = new HashSet<>();

    private Set<String> namesFilter = new HashSet<>();

    private Set<String> testNamesFilter = new HashSet<>();

    private Set<String> testUniqueIdsFilter = new HashSet<>();

    private Set<String> groupsFilter = new HashSet<>();

    private Set<String> tagQueriesFilter = new HashSet<>();

    private Set<String> parameterQueriesFilter = new HashSet<>();

    private Set<Date> startedAfterFilter = new HashSet<>();

    private Set<Date> startedBeforeFilter = new HashSet<>();

    private int limit = 10;

    private int offset = 0;

    private OrderBy orderBy = OrderBy.NAME_ASC;

    public Set<Long> getIdsFilter() {
        return idsFilter;
    }

    public void setIdsFilter(Set<Long> idsFilter) {
        this.idsFilter = idsFilter;
    }

    public Set<String> getNamesFilter() {
        return namesFilter;
    }

    public void setNamesFilter(Set<String> namesFilter) {
        this.namesFilter = namesFilter;
    }

    public Set<String> getTestNamesFilter() {
        return testNamesFilter;
    }

    public void setTestNamesFilter(Set<String> testNamesFilter) {
        this.testNamesFilter = testNamesFilter;
    }

    public Set<String> getTestUniqueIdsFilter() {
        return testUniqueIdsFilter;
    }

    public void setTestUniqueIdsFilter(Set<String> testUniqueIdsFilter) {
        this.testUniqueIdsFilter = testUniqueIdsFilter;
    }

    public Set<String> getGroupsFilter() {
        return groupsFilter;
    }

    public void setGroupsFilter(Set<String> groupsFilter) {
        this.groupsFilter = groupsFilter;
    }

    public Set<String> getTagQueriesFilter() {
        return tagQueriesFilter;
    }

    public void setTagQueriesFilter(Set<String> tagQueriesFilter) {
        this.tagQueriesFilter = tagQueriesFilter;
    }

    public Set<String> getParameterQueriesFilter() {
        return parameterQueriesFilter;
    }

    public void setParameterQueriesFilter(Set<String> parameterQueriesFilter) {
        this.parameterQueriesFilter = parameterQueriesFilter;
    }

    public Set<Date> getStartedAfterFilter() {
        return startedAfterFilter;
    }

    public void setStartedAfterFilter(Set<Date> startedAfterFilter) {
        this.startedAfterFilter = startedAfterFilter;
    }

    public Set<Date> getStartedBeforeFilter() {
        return startedBeforeFilter;
    }

    public void setStartedBeforeFilter(Set<Date> startedBeforeFilter) {
        this.startedBeforeFilter = startedBeforeFilter;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }
}
