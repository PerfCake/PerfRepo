package org.perfrepo.web.util;

import org.perfrepo.model.Value;

public class ValueComparator {

	public static int compare(Value value1, Value value2) {
		if (value1.getMetric() != null && value1.getMetric().equals(value2.getMetric())) {
			switch (value1.getMetricComparator()) {
				case HB:
					if (value1.getResultValue() != null && value2.getResultValue() != null) {
						return value1.getResultValue().compareTo(value2.getResultValue());
					} else if (value1.getResultValue() == null) {
						return -1;
					} else {
						return 1;
					}

				case LB:
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
