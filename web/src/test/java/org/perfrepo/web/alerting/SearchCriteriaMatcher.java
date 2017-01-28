package org.perfrepo.web.alerting;

import org.mockito.ArgumentMatcher;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;

import java.util.Calendar;
import java.util.Date;

/**
 * Helper class. When mocking objects with Mockito, Mockito by default uses Object.equals method to distinguish which method to
 * call. There is a problem with Date objects. Even is two Date objects are created with the same Date (like year, month, day,
 * hour and minute), calling equals returns false. This is the reason why this ArgumentMatcher class is used, to do the matching
 * manually.
 */
public class SearchCriteriaMatcher extends ArgumentMatcher<TestExecutionSearchCriteria> {

    private TestExecutionSearchCriteria searchCriteria;

    public SearchCriteriaMatcher(TestExecutionSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof TestExecutionSearchCriteria)) {
            return false;
        }

        TestExecutionSearchCriteria object = (TestExecutionSearchCriteria) o;

        if (!((searchCriteria.getTagsQuery() == null && object.getTagsQuery() == null)
            || (searchCriteria.getTagsQuery() != null && object.getTagsQuery() != null))) {
            return false;
        }

        Date otherDateFrom = object.getStartedAfter();
        Date otherDateTo = object.getStartedBefore();

        if (!((searchCriteria.getStartedAfter() == null && otherDateFrom == null)
            || (searchCriteria.getStartedAfter() != null && otherDateFrom != null))) {
            return false;
        }

        if (!((searchCriteria.getStartedBefore() == null && otherDateTo == null)
            || (searchCriteria.getStartedBefore() != null && otherDateTo != null))) {
            return false;
        }

        Calendar thisCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();

        if (searchCriteria.getStartedAfter() != null) {
            thisCalendar.setTime(searchCriteria.getStartedAfter());
            otherCalendar.setTime(otherDateFrom);

            if ((thisCalendar.get(Calendar.YEAR) != otherCalendar.get(Calendar.YEAR))
                || (thisCalendar.get(Calendar.MONTH) != otherCalendar.get(Calendar.MONTH))
                || (thisCalendar.get(Calendar.DAY_OF_MONTH) != otherCalendar.get(Calendar.DAY_OF_MONTH))
                || (thisCalendar.get(Calendar.HOUR) != otherCalendar.get(Calendar.HOUR))
                || (thisCalendar.get(Calendar.MINUTE) != otherCalendar.get(Calendar.MINUTE))) {
                return false;
            }
        }

        if (searchCriteria.getStartedBefore() != null) {
            thisCalendar.setTime(searchCriteria.getStartedBefore());
            otherCalendar.setTime(otherDateTo);

            if ((thisCalendar.get(Calendar.YEAR) != otherCalendar.get(Calendar.YEAR))
                || (thisCalendar.get(Calendar.MONTH) != otherCalendar.get(Calendar.MONTH))
                || (thisCalendar.get(Calendar.DAY_OF_MONTH) != otherCalendar.get(Calendar.DAY_OF_MONTH))
                || (thisCalendar.get(Calendar.HOUR) != otherCalendar.get(Calendar.HOUR))
                || (thisCalendar.get(Calendar.MINUTE) != otherCalendar.get(Calendar.MINUTE))) {
                return false;
            }
        }

        if (searchCriteria.getTagsQuery() != null && !searchCriteria.getTagsQuery().equals(object.getTagsQuery())) {
            return false;
        }

        return true;
    }
}
