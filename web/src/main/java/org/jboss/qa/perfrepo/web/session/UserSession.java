package org.jboss.qa.perfrepo.web.session;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.model.userproperty.GroupFilter;
import org.jboss.qa.perfrepo.model.userproperty.ReportFilter;
import org.jboss.qa.perfrepo.web.service.UserService;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

/**
 * Holds information about user.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named(value = "userSession")
@SessionScoped
public class UserSession implements Serializable {

	private static final long serialVersionUID = 1487959021438612784L;

	/**
	 * User property name of group filter - used to search tests and test executions
	 */
	private static final String USER_PARAM_GROUP_FILTER = "test.filter.group";

	/**
	 * User property name of report filter used on reports page
	 */
	private static final String USER_PARAM_REPORT_FILTER = "report.filter";

	@Inject
	private UserService userService;

	private User user;

	@PostConstruct
	public void init() {
		refresh();
	}

	public void refresh() {
		user = userService.getFullUser(userService.getLoggedUser().getId());
	}

	/**
	 * Returns user stored in session
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Logout the user
	 * @return
	 */
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "HomeRedirect";
	}

	/**
	 * Returns the user property {@value #USER_PARAM_GROUP_FILTER}
	 * @return
	 */
	public GroupFilter getGroupFilter() {
		UserProperty groupFilter = findUserParameter(USER_PARAM_GROUP_FILTER);
		if (groupFilter == null) {
			return GroupFilter.MY_GROUPS;
		} else {
			return GroupFilter.valueOf(groupFilter.getValue());
		}
	}

	/**
	 * Set the user property {@value #USER_PARAM_GROUP_FILTER}
	 * @param filter
	 * @throws ServiceException
	 */
	public void setGroupFilter(GroupFilter filter) throws ServiceException {
		userService.addUserProperty(USER_PARAM_GROUP_FILTER, filter.name());
		refresh();
	}

	/**
	 * Returns the user property {@value #USER_PARAM_GROUP_FILTER}
	 * @return
	 */
	public ReportFilter getReportFilter() {
		UserProperty reportFilter = findUserParameter(USER_PARAM_REPORT_FILTER);
		if (reportFilter == null) {
			return ReportFilter.TEAM;
		} else {
			return ReportFilter.valueOf(reportFilter.getValue());
		}
	}

	/**
	 * Set the user property {@value #USER_PARAM_GROUP_FILTER}
	 * @param filter
	 * @throws ServiceException
	 */
	public void setReportFilter(ReportFilter filter) throws ServiceException {
		userService.addUserProperty(USER_PARAM_REPORT_FILTER, filter.name());
		refresh();
	}

	/**
	 * Set user property {@value #USER_PARAM_GROUP_FILTER} to {@link GroupFilter.ALL_GROUPS}
	 * @throws ServiceException
	 */
	public void setAllGroupFilter() throws ServiceException {
		setGroupFilter(GroupFilter.ALL_GROUPS);
	}

	/**
	 * Set user property {@value #USER_PARAM_GROUP_FILTER} to {@link GroupFilter.MY_GROUPS}
	 * @throws ServiceException
	 */
	public void setMyGroupFilter() throws ServiceException {
		setGroupFilter(GroupFilter.MY_GROUPS);
	}

	/**
	 * Find user property by name
	 * @param name
	 * @return
	 */
	public UserProperty findUserParameter(String name) {
		for(UserProperty up : user.getProperties()) {
			if (name.equals(up.getName())) {
				return up;
			}
		}
		return null;
	}
}
