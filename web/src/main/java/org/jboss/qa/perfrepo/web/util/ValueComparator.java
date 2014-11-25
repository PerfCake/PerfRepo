package org.jboss.qa.perfrepo.web.util;

import org.jboss.qa.perfrepo.model.Value;

public class ValueComparator {

	public static enum MetricComparator {BIGGER_BETTER, LESS_BETTER}

	;

	public static int compare(Value value1, Value value2) {
		if (value1.getMetric() != null && value1.getMetric().equals(value2.getMetric())) {
			switch (value1.getMetricComparator()) {
				case "BIGGER_BETTER":
					if (value1.getResultValue() != null && value2.getResultValue() != null) {
						return value1.getResultValue().compareTo(value2.getResultValue());
					} else if (value1.getResultValue() == null) {
						return -1;
					} else {
						return 1;
					}

				case "LESS_BETTER":
					if (value1.getResultValue() != null && value2.getResultValue() != null) {
						return -value1.getResultValue().compareTo(value2.getResultValue());
					} else if (value1.getResultValue() == null) {
						return -1;
					} else {
						return 1;
					}
				default:
					if (value1.getResultValue() != null && value2.getResultValue() != null) {
						return value1.getResultValue().compareTo(value2.getResultValue());
					} else if (value1.getResultValue() == null) {
						return -1;
					} else {
						return 1;
					}
			}
		}
		return 0;
	}
}
