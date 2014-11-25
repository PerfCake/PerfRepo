package org.jboss.qa.perfrepo.web.controller.reports;

import org.jboss.qa.perfrepo.model.TestExecution;

import java.util.ArrayList;
import java.util.List;

public class ParamUtil {
	public static List<Long> parseExecQuery(String query) {
		try {
			if (query == null) {
				return null;
			}
			String[] idStrings = query.split("\\||-");
			List<Long> r = new ArrayList<Long>(idStrings.length);
			for (String idString : idStrings) {
				r.add(new Long(idString));
			}
			return r.isEmpty() ? null : r;
		} catch (Exception e) {
			return null;
		}
	}

	public static String generateExecQuery(List<TestExecution> execs) {
		if (execs == null || execs.isEmpty()) {
			return null;
		}
		StringBuffer s = new StringBuffer(execs.get(0).getId().toString());
		for (int i = 1; i < execs.size(); i++) {
			s.append("-");
			s.append(execs.get(i).getId());
		}
		return s.toString();
	}
}
