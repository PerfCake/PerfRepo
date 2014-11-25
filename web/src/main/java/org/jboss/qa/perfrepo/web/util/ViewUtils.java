package org.jboss.qa.perfrepo.web.util;

import org.apache.commons.lang.StringEscapeUtils;

import org.jboss.qa.perfrepo.model.TestExecutionParameter;

/**
 * Various utility methods for view.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @autho Jiri Holusa (jholusa@redhat.com)
 */
public class ViewUtils {

	/**
	 * This method is used on view to create a clickable <a> tag for e.g. a commit etc.
	 * Usage can be found on test execution detail page.
	 *
	 * @param param
	 * @return
	 */
	public static String displayValue(TestExecutionParameter param) {
		if (param == null) {
			return "&nbsp;";
		}
		String value = param.getValue();
		if (value == null) {
			return "&nbsp;";
		}
		if (value.startsWith("http://") || value.startsWith("https://")) {
			if (value.length() > 100) {
				//the magic number 96 is arbitrary, it's used only so the text wouldn't be so long
				return "<a href=\"" + value + "\">" + value.substring(0, 96) + " ...</a>";
			} else {
				return "<a href=\"" + value + "\">" + value + "</a>";
			}
		} else if (value.length() > 100) {
			return "<a href=\"/repo/param/" + param.getId() + "\">" + StringEscapeUtils.escapeHtml(value.substring(0, 96)) + " ...</a>";
		} else {
			return StringEscapeUtils.escapeHtml(value);
		}
	}
}
