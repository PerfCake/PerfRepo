package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.web.adapter.ComparisonSessionAdapter;
import org.perfrepo.web.adapter.TestExecutionAdapter;
import org.perfrepo.web.session.ComparisonSession;
import org.perfrepo.web.session.UserSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ComparisonSessionAdapterImpl implements ComparisonSessionAdapter {

    @Inject
    private UserSession userSession;

    @Inject
    private TestExecutionAdapter testExecutionAdapter;

    @Override
    public List<TestExecutionDto> getTestExecutions() {
        ComparisonSession session = userSession.getComparisonSession();

        if (session.getTestExecutionIds().isEmpty()) {
            return new ArrayList<>();
        }

        TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
        searchCriteria.setIdsFilter(session.getTestExecutionIds());

        return testExecutionAdapter.searchTestExecutions(searchCriteria).getData();
    }

    @Override
    public List<TestExecutionDto> addToComparison(Set<Long> testExecutionIds) {
        ComparisonSession session = userSession.getComparisonSession();
        session.getTestExecutionIds().addAll(testExecutionIds);

        return getTestExecutions();
    }

    @Override
    public List<TestExecutionDto> removeFromComparison(Set<Long> testExecutionIds) {
        ComparisonSession session = userSession.getComparisonSession();
        session.getTestExecutionIds().removeAll(testExecutionIds);

        return getTestExecutions();
    }
}
