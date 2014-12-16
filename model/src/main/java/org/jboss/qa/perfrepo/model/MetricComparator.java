package org.jboss.qa.perfrepo.model;

/**
 * The metric comparator allows to decide which result value is better
 * (higher is better - throughput / lower is better - response time)
 * @author Pavel Drozd
 *
 */
public enum MetricComparator {

	/**
	 * 	Higher Better - The higher value is better, i.e. throughput.
	 */
	HB,

	/**
	 * Lower better - The lower value is better, i.e. response time.
	 */
	LB
}