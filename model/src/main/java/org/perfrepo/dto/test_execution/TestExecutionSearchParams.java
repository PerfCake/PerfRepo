package org.perfrepo.dto.test_execution;

import org.perfrepo.model.to.OrderBy;

import java.util.Set;

/**
 * Data transfer object that contains filter parameters for {@link TestExecutionDto} searching query.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionSearchParams {

    private Set<String> testNameFilters;

    private Set<String> testUidFilters;

    private Set<String> tagFilters;

    private int limit = 5;

    private int offset = 0;

    private OrderBy orderBy;

    public Set<String> getTestNameFilters() {
        return testNameFilters;
    }

    public void setTestNameFilters(Set<String> testNameFilters) {
        this.testNameFilters = testNameFilters;
    }

    public Set<String> getTestUidFilters() {
        return testUidFilters;
    }

    public void setTestUidFilters(Set<String> testUidFilters) {
        this.testUidFilters = testUidFilters;
    }

    public Set<String> getTagFilters() {
        return tagFilters;
    }

    public void setTagFilters(Set<String> tagFilters) {
        this.tagFilters = tagFilters;
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
