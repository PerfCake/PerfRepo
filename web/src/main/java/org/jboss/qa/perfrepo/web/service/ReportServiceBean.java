package org.jboss.qa.perfrepo.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.auth.AccessLevel;
import org.jboss.qa.perfrepo.model.auth.AccessType;
import org.jboss.qa.perfrepo.model.auth.Permission;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.model.report.ReportProperty;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.model.user.Group;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.web.dao.MetricDAO;
import org.jboss.qa.perfrepo.web.dao.PermissionDAO;
import org.jboss.qa.perfrepo.web.dao.ReportDAO;
import org.jboss.qa.perfrepo.web.dao.TestDAO;
import org.jboss.qa.perfrepo.web.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.web.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.web.security.Secured;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

/**
 * Implements @link{ReportService}.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

	@Inject
	private ReportDAO reportDAO;

	@Inject
	private PermissionDAO permissionDAO;

	@Inject
	private TestDAO testDAO;

	@Inject
	private MetricDAO metricDAO;

	@Inject
	private TestMetricDAO testMetricDAO;

	@Inject
	private TestExecutionDAO testExecutionDAO;

	@Inject
	private UserService userService;

	@Override
	public List<Report> getAllUsersReports() {
		return getAllReports(userService.getLoggedUser().getUsername());
	}

	@Override
	public List<Report> getAllReports() {
		User user = userService.getLoggedUser();
		List<Long> groupIds = new ArrayList<Long>();
		for (Group group : user.getGroups()) {
			groupIds.add(group.getId());
		}
		return reportDAO.getByAnyPermission(user.getId(), groupIds);
	}

	@Override
	public List<Report> getAllGroupReports() {
		User user = userService.getLoggedUser();
		List<Long> groupIds = new ArrayList<Long>();
		for (Group group : user.getGroups()) {
			groupIds.add(group.getId());
		}
		return reportDAO.getByGroupPermission(groupIds);
	}

	@Override
	@Secured
	public void removeReport(Report report) throws ServiceException {
		Report r = reportDAO.get(report.getId());
		permissionDAO.removeReportPermissions(report.getId());
		reportDAO.remove(r);
	}

	@Override
	public Collection<Permission> getReportPermissions(Report report) {
		if (report != null && report.getId() != null) {
			return permissionDAO.getByReport(report.getId());
		} else if (report == null || report.getPermissions() == null || report.getPermissions().size() == 0) {
			return getDefaultPermission();
		} else if (report.getId() == null && report.getPermissions() != null && report.getPermissions().size() != 0) {
			return report.getPermissions();
		}
		return getDefaultPermission();
	}

	@Override
	public Report createReport(Report report) {
		Report r = reportDAO.create(report);
		saveReportPermissions(r, report.getPermissions());
		return r;
	}

	@Override
	@Secured
	public Report updateReport(Report report) {
		//TODO: verify rights
		// somebody is able to read report
		// somebody is able to write report
		// the updater is able to write report
		saveReportPermissions(report, report.getPermissions());
		return reportDAO.update(report);
	}

	@Override
	@Secured(accessType = AccessType.READ)
	public Report getFullReport(Report report) {
		Report freshReport = reportDAO.get(report.getId());
		if (freshReport == null) {
			return null;
		}

		Map<String, ReportProperty> clonedReportProperties = new HashMap<String, ReportProperty>();

		for (String propertyKey : freshReport.getProperties().keySet()) {
			clonedReportProperties.put(propertyKey, freshReport.getProperties().get(propertyKey).clone());
		}
		List<Permission> clonedPermission = new ArrayList<Permission>();
		for (Permission perm : freshReport.getPermissions()) {
			clonedPermission.add(perm.clone());
		}

		Report result = freshReport.clone();
		result.setProperties(clonedReportProperties);
		result.setPermissions(clonedPermission);
		return result;
	}

	@Override
	public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request) {
		MetricReportTO.Response response = new MetricReportTO.Response();
		for (MetricReportTO.ChartRequest chartRequest : request.getCharts()) {
			MetricReportTO.ChartResponse chartResponse = new MetricReportTO.ChartResponse();
			response.addChart(chartResponse);
			if (chartRequest.getTestUid() == null) {
				continue;
			} else {
				Test freshTest = testDAO.findByUid(chartRequest.getTestUid());
				if (freshTest == null) {
					// test uid supplied but doesn't exist - pick another test
					response.setSelectionTests(testDAO.getAll());
					continue;
				} else {
					freshTest = freshTest.clone();
					chartResponse.setSelectedTest(freshTest);
					if (chartRequest.getSeries() == null || chartRequest.getSeries().isEmpty()) {
						continue;
					}
					for (MetricReportTO.SeriesRequest seriesRequest : chartRequest.getSeries()) {
						if (seriesRequest.getName() == null) {
							throw new IllegalArgumentException("series has null name");
						}
						MetricReportTO.SeriesResponse seriesResponse = new MetricReportTO.SeriesResponse(seriesRequest.getName());
						chartResponse.addSeries(seriesResponse);
						if (seriesRequest.getMetricName() == null) {
							continue;
						}
						TestMetric testMetric = testMetricDAO.find(freshTest, seriesRequest.getMetricName());
						if (testMetric == null) {
							chartResponse.setSelectionMetrics(metricDAO.getMetricByTest(freshTest.getId()));
							continue;
						}
						Metric freshMetric = testMetric.getMetric().clone();
						freshMetric.setTestMetrics(null);
						freshMetric.setValues(null);
						seriesResponse.setSelectedMetric(freshMetric);
						List<MetricReportTO.DataPoint> datapoints = testExecutionDAO.searchValues(freshTest.getId(), seriesRequest.getMetricName(),
								seriesRequest.getTags(), request.getLimitSize());
						if (datapoints.isEmpty()) {
							continue;
						}
						Collections.reverse(datapoints);
						seriesResponse.setDatapoints(datapoints);
					}

					for (MetricReportTO.BaselineRequest baselineRequest : chartRequest.getBaselines()) {
						if (baselineRequest.getName() == null) {
							throw new IllegalArgumentException("baseline has null name");
						}
						MetricReportTO.BaselineResponse baselineResponse = new MetricReportTO.BaselineResponse(baselineRequest.getName());
						chartResponse.addBaseline(baselineResponse);
						if (baselineRequest.getMetricName() == null) {
							continue;
						}
						TestMetric testMetric = testMetricDAO.find(freshTest, baselineRequest.getMetricName());
						if (testMetric == null) {
							chartResponse.setSelectionMetrics(metricDAO.getMetricByTest(freshTest.getId()));
							continue;
						}
						Metric freshMetric = testMetric.getMetric().clone();
						freshMetric.setTestMetrics(null);
						freshMetric.setValues(null);
						baselineResponse.setSelectedMetric(freshMetric);
						baselineResponse.setExecId(baselineRequest.getExecId());
						baselineResponse.setValue(testExecutionDAO.getValueForMetric(baselineRequest.getExecId(), baselineRequest.getMetricName()));
					}
				}
			}
		}
		return response;
	}

	/**
	 * Stores permissions to report
	 *
	 * @param report
	 * @param newPermissions
	 */
	private void saveReportPermissions(Report report, Collection<Permission> newPermissions) {
		Report freshReport = reportDAO.get(report.getId());
		List<Permission> oldPermissions = permissionDAO.getByReport(report.getId());
		// if the new permissions not defined, use default permissions
		if (newPermissions == null || newPermissions.isEmpty()) {
			newPermissions = getDefaultPermission();
		}
		for (Permission newPerm : newPermissions) {
			// if new permission has no id and the permission is not contained in the old permissions, store it
			if (newPerm.getId() == null && !isContained(newPerm, oldPermissions)) {
				newPerm.setReport(freshReport);
				permissionDAO.create(newPerm);
			}
		}
		for (Permission oldPerm : oldPermissions) {
			// if the old permission is not contained in the new collection, remove it
			if (!isContained(oldPerm, newPermissions)) {
				permissionDAO.remove(oldPerm);
			}
		}
	}

	/**
	 * Returns true if the permission is contained in the permission collection
	 * The method checks equality of all attributes except id (accessType, level, userId, groupId, reportId).
	 * The main purpose of the method is to avoid situations when the semantically same permission exists in the database and the same is created.
	 *
	 * @param permission
	 * @param permissions
	 * @return
	 */
	private boolean isContained(Permission permission, Collection<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getAccessType().equals(permission.getAccessType()) && p.getLevel().equals(permission.getLevel())) {
				if ((permission.getUserId() != null && permission.getUserId().equals(p.getUserId())) || (permission.getUserId() == null && p.getUserId() == null)) {
					if ((permission.getGroupId() != null && permission.getGroupId().equals(p.getGroupId())) || (permission.getGroupId() == null && p.getGroupId() == null)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Return all users reports
	 *
	 * @param username
	 * @return
	 */
	private List<Report> getAllReports(String username) {
		List<Report> result = new ArrayList<Report>();
		result.addAll(reportDAO.getByUser(username));
		return result;
	}

	/**
	 * Returns the default permissions WRITE group, READ PUBLIC
	 *
	 * @return
	 */
	private Collection<Permission> getDefaultPermission() {
		List<Permission> defaultPermissions = new ArrayList<Permission>();
		Permission read = new Permission();
		read.setAccessType(AccessType.READ);
		read.setLevel(AccessLevel.PUBLIC);
		defaultPermissions.add(read);
		Permission write = new Permission();
		write.setAccessType(AccessType.WRITE);
		write.setLevel(AccessLevel.GROUP);
		User user = userService.getLoggedUser();
		if (user.getGroups() != null && user.getGroups().size() > 0) {
			write.setGroupId(user.getGroups().iterator().next().getId());
		} else {
			throw new IllegalStateException("User is not assigned in any group");
		}
		defaultPermissions.add(write);
		return defaultPermissions;
	}
}
