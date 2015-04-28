/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.perfrepo.model.TestExecutionParameter;

import javax.faces.context.FacesContext;

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
			return "<a href=\"" +  FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/param/" + param.getId() + "\">" + StringEscapeUtils.escapeHtml(value.substring(0, 96)) + " ...</a>";
		} else {
			return StringEscapeUtils.escapeHtml(value);
		}
	}
}
