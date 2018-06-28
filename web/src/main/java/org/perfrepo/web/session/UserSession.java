package org.perfrepo.web.session;

import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.search.ReportSearchCriteria;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.service.search.TestSearchCriteria;

/**
 * Interface remembering currently logged user and his session settings.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface UserSession {

    User getLoggedUser();

    TestSearchCriteria getTestSearchCriteria();

    void setTestSearchCriteria(TestSearchCriteria criteria);

    TestExecutionSearchCriteria getTestExecutionSearchCriteria();

    void setTestExecutionSearchCriteria(TestExecutionSearchCriteria criteria);

    ReportSearchCriteria getReportSearchCriteria();

    void setReportSearchCriteria(ReportSearchCriteria criteria);

    ComparisonSession getComparisonSession();

    void setComparisonSession(ComparisonSession session);

}
