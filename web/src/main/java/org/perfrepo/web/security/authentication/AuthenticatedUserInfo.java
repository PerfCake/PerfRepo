package org.perfrepo.web.security.authentication;

import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.search.ReportSearchCriteria;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.service.search.TestSearchCriteria;

/**
 * Used for storing all information about currently logged user.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class AuthenticatedUserInfo {

    private User user;
    private TestSearchCriteria testSearchCriteria;
    private TestExecutionSearchCriteria testExecutionSearchCriteria;
    private ReportSearchCriteria reportSearchCriteria;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TestSearchCriteria getTestSearchCriteria() {
        return testSearchCriteria;
    }

    public void setTestSearchCriteria(TestSearchCriteria testSearchCriteria) {
        this.testSearchCriteria = testSearchCriteria;
    }

    public TestExecutionSearchCriteria getTestExecutionSearchCriteria() {
        return testExecutionSearchCriteria;
    }

    public void setTestExecutionSearchCriteria(TestExecutionSearchCriteria testExecutionSearchCriteria) {
        this.testExecutionSearchCriteria = testExecutionSearchCriteria;
    }

    public ReportSearchCriteria getReportSearchCriteria() {
        return reportSearchCriteria;
    }

    public void setReportSearchCriteria(ReportSearchCriteria reportSearchCriteria) {
        this.reportSearchCriteria = reportSearchCriteria;
    }
}
