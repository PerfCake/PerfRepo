package org.jboss.qa.perfrepo.model.userproperty;

/**
 * User property enumeration ReportFilter is used to filter reports. 
 * @author Pavel Drozd
 *
 */
public enum ReportFilter {

	/**
	 * Filter the reports, which are created by the logged user. 
	 */
	MY,

	/**
	 * Filter the team reports.
	 */
	TEAM,

	/**
	 * Show all reports, which are accessible by the logged user.
	 */
	ALL

}
