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
package org.perfrepo.model.util;

import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionParameter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Defines sorting mechanisms for {@link TestExecution}.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public abstract class ExecutionSort implements Comparator<TestExecution>, Serializable {

	public static final ExecutionSort TIME_ASC = new SortByTime(Type.TIME, true);
	public static final ExecutionSort TIME_DESC = new SortByTime(Type.TIME, false);
	public static final ExecutionSort NAME_ASC = new SortByName(Type.NAME, true);
	public static final ExecutionSort NAME_DESC = new SortByName(Type.NAME, false);
	public static final ExecutionSort ID_ASC = new SortByID(Type.ID, true);
	public static final ExecutionSort ID_DESC = new SortByID(Type.ID, false);
	public static final ExecutionSort TEST_NAME_ASC = new SortByTestName(Type.TEST_NAME, true);
	public static final ExecutionSort TEST_NAME_DESC = new SortByTestName(Type.TEST_NAME, false);

	/**
	 * Things you can sort test executions by.
	 */
	public static enum Type {
		TIME(false), PARAM_STRING(true), PARAM_LONG(true), PARAM_DOUBLE(true), NAME(false), TEST_NAME(false), ID(false);

		private Type(boolean parametrized) {
			this.parametrized = parametrized;
		}

		private boolean parametrized;

		public boolean isParametrized() {
			return parametrized;
		}
	}

	private Type type;
	protected boolean asc = false;

	private ExecutionSort(Type type, boolean asc) {
		this.type = type;
		this.asc = asc;
	}

	public Type type() {
		return type;
	}

	public boolean isAscending() {
		return asc;
	}

	private static abstract class SortByAttribute<T extends Comparable<T>> extends ExecutionSort {

		private SortByAttribute(Type type, boolean asc) {
			super(type, asc);
		}

		private int internalCompare(TestExecution o1, TestExecution o2) {
			if (o1 == null || getAttribute(o1) == null) {
				return o2 == null || getAttribute(o2) == null ? 0 : -1;
			} else {
				return o2 == null || getAttribute(o2) == null ? 1 : getAttribute(o1).compareTo(getAttribute(o2));
			}
		}

		@Override
		public int compare(TestExecution o1, TestExecution o2) {
			return asc ? internalCompare(o1, o2) : -internalCompare(o1, o2);
		}

		protected abstract T getAttribute(TestExecution exec);
	}

	private static class SortByTime extends SortByAttribute<Date> {

		private SortByTime(Type type, boolean asc) {
			super(type, asc);
		}

		@Override
		protected Date getAttribute(TestExecution exec) {
			return exec.getStarted();
		}
	}

	private static class SortByName extends SortByAttribute<String> {
		private SortByName(Type type, boolean asc) {
			super(type, asc);
		}

		@Override
		protected String getAttribute(TestExecution exec) {
			return exec.getName();
		}
	}

	private static class SortByID extends SortByAttribute<Long> {
		private SortByID(Type type, boolean asc) {
			super(type, asc);
		}

		@Override
		protected Long getAttribute(TestExecution exec) {
			return exec.getId();
		}
	}

	;

	private static class SortByTestName extends SortByAttribute<String> {
		private SortByTestName(Type type, boolean asc) {
			super(type, asc);
		}

		@Override
		protected String getAttribute(TestExecution exec) {
			return exec.getTest() == null ? null : exec.getTest().getName();
		}
	}

	public static abstract class ParamExecutionSort<T extends Comparable<T>> extends SortByAttribute<T> {

		private String param;

		private ParamExecutionSort(Type type, boolean asc, String param) {
			super(type, asc);
			this.param = param;
		}

		@Override
		protected T getAttribute(TestExecution exec) {
			TestExecutionParameter p = exec.findParameter(param);
			return p == null ? null : (p.getValue() == null ? null : convert(p.getValue()));
		}

		public String getParam() {
			return param;
		}

		protected abstract T convert(String str);
	}

	private static class LongParamExecutionSort extends ParamExecutionSort<Long> {

		private LongParamExecutionSort(String param, boolean asc) {
			super(Type.PARAM_LONG, asc, param);
		}

		@Override
		protected Long convert(String str) {
			try {
				return new Long(str);
			} catch (Exception e) {
				return -1L;
			}
		}
	}

	private static class DoubleParamExecutionSort extends ParamExecutionSort<Double> {

		private DoubleParamExecutionSort(String param, boolean asc) {
			super(Type.PARAM_DOUBLE, asc, param);
		}

		@Override
		protected Double convert(String str) {
			try {
				return new Double(str);
			} catch (Exception e) {
				return Double.NaN;
			}
		}
	}

	private static class StringParamExecutionSort extends ParamExecutionSort<String> {

		private StringParamExecutionSort(String param, boolean asc) {
			super(Type.PARAM_STRING, asc, param);
		}

		@Override
		protected String convert(String str) {
			return str;
		}
	}

	public static ExecutionSort PARAM_STRING(String paramName, boolean asc) {
		return new StringParamExecutionSort(paramName, asc);
	}

	public static ExecutionSort PARAM_LONG(String paramName, boolean asc) {
		return new LongParamExecutionSort(paramName, asc);
	}

	public static ExecutionSort PARAM_DOUBLE(String paramName, boolean asc) {
		return new DoubleParamExecutionSort(paramName, asc);
	}

	/**
	 * Create execution sorter.
	 *
	 * @param type sort type
	 * @param paramName param name, can be null for ID, NAME, TEST_NAME, TIME types
	 * @return execution sorter.
	 */
	public static ExecutionSort create(Type type, String paramName, boolean asc) {
		switch (type) {
			case ID:
				return asc ? ID_ASC : ID_DESC;
			case NAME:
				return asc ? NAME_ASC : NAME_DESC;
			case PARAM_DOUBLE:
				return PARAM_DOUBLE(paramName, asc);
			case PARAM_LONG:
				return PARAM_LONG(paramName, asc);
			case PARAM_STRING:
				return PARAM_STRING(paramName, asc);
			case TEST_NAME:
				return asc ? TEST_NAME_ASC : TEST_NAME_DESC;
			case TIME:
				return asc ? TIME_ASC : TIME_DESC;
			default:
				throw new IllegalArgumentException("unknown sort type");
		}
	}
}
