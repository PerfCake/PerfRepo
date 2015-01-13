package org.perfrepo.web.alerting;

/**
 * API for checking of condition for alerting.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface ConditionChecker {

	/**
	 * Checks the condition for alerting
	 *
	 * @param condition specification of condition in String in our custom DSL that should hold
	 * @param currentResult result of the test execution that is currently being processed, e.g. against which the
	 * condition should hold
	 * @return true if condition still hold | false if condition is broken
	 */
	public boolean checkCondition(String condition, double currentResult);
}
