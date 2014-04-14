package org.jboss.qa.perfrepo.reports.testgroup;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.reports.testgroup.TestGroupChartBean.ChartData;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.service.UserService;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.util.ValueComparator;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

@Named
@ViewScoped
public class TestGroupReportController extends ControllerBase {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -482624457203937471L;
	
	@Inject
	private TestService testService;
	
	@Inject
	private UserService userService;	
	
	public static final String REPORT_PREFIX = UserSession.REPORT_KEY_PREFIX;
	
	/**
	 * Test name, tag, value
	 */
	private HashBasedTable<String, ColumnKey, ValueCell> data = HashBasedTable.create();
	
	/**
	 * Report properties
	 */
	private String reportId = null;
	
	private String reportName = null;
	
	//private List<String> baselineTags = Arrays.asList("FSW", "simple-12", "600ER8", "baseline");	
	//format: report_prefix + reportId + "tag.1.alias", report_prefix + reportId + "tag.2.alias"	
	//testGroupReport.FSW600CR2.compare.1.1=600CR1
	//testGroupReport.FSW600CR2.compare.1.2=600CR2
	//testGroupReport.FSW600CR2.compare.1.alias=600CR1 vs. 600CR2
	//testGroupReport.FSW600CR2.tag.1=600CR1 FSW simple-12
	//testGroupReport.FSW600CR2.tag.1.alias=600CR1
	//testGroupReport.FSW600CR2.tag.2=600CR2 FSW simple-12
	//testGroupReport.FSW600CR2.tag.2.alias=600CR2
	//testGroupReport.FSW600CR2.tag.3=2nd.round 600CR2 FSW simple-12
	//testGroupReport.FSW600CR2.tag.3.alias=600CR2
	//testGroupReport.FSW600CR2.threshold=-5.0
	//testGroupReport.FSW600CR2.tests=sy-binding-camel-jms,sy-binding-http-get,sy-binding-http-get-throttling
	
	private List<String> tags = Lists.newArrayList();
	
	private Map<String, String> tagAlias = new HashMap<String, String>(); 
	
	private List<String> tests = Lists.newArrayList();
	
	private List<String> metrics = Lists.newArrayList();

	private List<String> selectedMetrics = Lists.newArrayList();
	
	private Map<String, List<String>> comparison = new HashMap<String, List<String>>();
	
	private Double semiNegativeThreshold = -5.0;
	
	private NumberFormat formatter = new DecimalFormat("#0.00"); 
	
	private List<String> testsCopy;
	
	private String currentTest;
	
	private List<String> tagsCopy;
	
	private Map<String, String> tagAliasCopy;
	
	private Map<String, List<String>> comparisonCopy;
	
	private String currentTag;
	
	private String baseline1 = null;
	
	private String baseline2 = null;
	
	private String newReportId;
	
	private String newReportName;	
	
	public void processTestExecutions() {
		data.clear();
		reloadSessionMessages();
		//get TEs 
		List<TestExecution> testExecutions = testService.getTestExecutions(tags, tests);
		for(TestExecution te : testExecutions) {
			String tagsKey = normalizeTags(te.getTags());
			if (!tags.contains(tagsKey)) {
				tags.add(tagsKey);
			}
			String columnKey1 = tagAlias.get(tagsKey) != null ? tagAlias.get(tagsKey) : tagsKey;
			for (Value value : te.getValues()) {
				String metricName = value.getMetricName();
				if (!metrics.contains(metricName)) {
					metrics.add(metricName);
				}
				if (selectedMetrics.isEmpty() || selectedMetrics.contains(metricName)) {
					ColumnKey columnKeyFull = new ColumnKey(columnKey1, metricName);
					ValueCell valueCell = data.get(te.getTestUid(), columnKeyFull);
					//TODO: filter values according to metric
					if (valueCell == null) {
						data.put(te.getTestUid(), columnKeyFull, new ValueCell(value));
					} else {
						valueCell.addValues(te.getValues());
					}
				}
			}
		}
		if (selectedMetrics.isEmpty()) {
			selectedMetrics.addAll(metrics);
		}
	}
	
	@PostConstruct
	private void readConfiguration() {
		reportId = getRequestParam("reportId");
		if (reportId != null && !"".equals(reportId)) {
			Map<String, String> properties = userService.getUserProperties(REPORT_PREFIX + reportId + ".");
			if (!properties.isEmpty()) {
				//reportname
				reportName = properties.get("name");
				//tests
				String testsProperty = properties.get("tests");
				tests = new ArrayList<String>();
				if (testsProperty != null) {
					tests = Lists.newArrayList(testsProperty.split(", "));
				}
				//tags
				int i = 1;
				tags = new ArrayList<String>();
				tagAlias = new HashMap<String, String>();
				String tag = properties.get("tag." + i);
				while (tag != null) {
					tags.add(tag);
					String ta = properties.get("tag." + i + ".alias");
					if (ta != null) {
						tagAlias.put(tag, ta);
					}
					tag = properties.get("tag." + ++i);
				}
				//comparison
				i = 1;
				comparison = new HashMap<String, List<String>>();
				String compare = properties.get("compare." + i + ".1");
				while (compare != null) {
					String c1 = properties.get("compare." + i + ".1");
					String c2 = properties.get("compare." + i + ".2");
					String alias = properties.get("compare." + i + ".alias");
					comparison.put(alias, Lists.newArrayList(c1, c2));
					compare = properties.get("compare." + ++i + ".1");
				}
				String metricsProperty = properties.get("metrics");
				if (metricsProperty != null) {
					selectedMetrics.addAll(Lists.newArrayList(metricsProperty.split(", ")));
				}
				//TODO: threshold
				processTestExecutions();
			}
		}
	}

	public void saveReport() {
		saveReport(reportId, reportName);
	}
	
	public void saveReport(String reportId, String reportName) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("name", reportName);
		properties.put("type", "TestGroupReport");
		properties.put("link", "/repo/reports/testGroupReport/" + reportId);
		//tests
		String testsProperty = "";
		for (String test : tests) {
			testsProperty += test + ", ";
		}
		properties.put("tests", testsProperty.substring(0, testsProperty.length() -2));
		//tags
		int i =1;
		for (String tag : tags) {
			properties.put("tag." + i, tag);
			if (tagAlias.get(tag) != null) {
				properties.put("tag." + i + ".alias", tagAlias.get(tag));
			}
			i++;
		}
		//comparison
		i = 1;
		for (String key : comparison.keySet()) {
			properties.put("compare." + i + ".1", comparison.get(key).get(0));
			properties.put("compare." + i + ".2", comparison.get(key).get(1));
			properties.put("compare." + i + ".alias", key);
			i++;
		}
		String metricsProperty = "";
		for (String metric : selectedMetrics) {
			metricsProperty += metric + ", ";
		}
		properties.put("metrics", metricsProperty.substring(0, metricsProperty.length() -2));
		userService.replacePropertiesWithPrefix(REPORT_PREFIX + reportId + ".", properties);
		addSessionMessage(INFO, "page.reports.testGroup.reportSaved", reportId);
		reloadSessionMessages();
	}
	
	public void cloneReport() {
		if (userService.userPropertiesPrefixExists(REPORT_PREFIX + getNewReportId())) {
			//redirectWithMessage("/reports/testGroupReport/" + getReportId() ,ERROR, "page.reports.testGroup.reportExists", getNewReportId());
			addSessionMessage(ERROR, "page.reports.testGroup.reportExists", getNewReportId());
			reloadSessionMessages();
		} else {
			saveReport(getNewReportId(), getNewReportName());
			redirectWithMessage("/reports/testGroupReport/" + getNewReportId(), INFO, "page.reports.testGroup.reportSaved", getNewReportId());			
		}
	}
	
	public void clearTemporaryProperties() {
		baseline1 = null;
		baseline2 = null;
		newReportId = null;
		newReportName = null;
		currentTag = null;
		currentTest = null;
	}

	public List<String> getTests() {
		List<String> result = new ArrayList<String>(tests);
		Collections.sort(result);
		return  result;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public List<String> getTableTags() {
		List<String> tags = new ArrayList<String>();
		for (ColumnKey key : data.columnKeySet()) {
			if (!tags.contains(key.getTagKey())) {
				tags.add(key.getTagKey());
			}
		}
		return tags;
	}
	
	public ValueCell getValue(String test, String tags, String metric) {
		return data.get(test, new ColumnKey(tags, metric));
	}
	
	public List<String> getComparisonValues() {
		return new ArrayList<String>(comparison.keySet());
	}
	
	private String normalizeTags(List<String> tags) {
		Collections.sort(tags);
		String result = "";
		for (String tag : tags) {
			result += tag + " ";			
		}
		return result.substring(0, result.length() - 1);
	}
	
	public List<String> parseTags(String tags) {
		if (tags != null) {
			return Lists.newArrayList(tags.split(" "));
		}
		return null;
	}	
	
	public float compare(String test, String compareKey, String metric) {
		List<String> compareColumns = comparison.get(compareKey);
		if (compareColumns != null && compareColumns.size() == 2) {
			ColumnKey columnKey1 = new ColumnKey(comparison.get(compareKey).get(0), metric);
			ColumnKey columnKey2 = new ColumnKey(comparison.get(compareKey).get(1), metric);
			Value value1 = data.get(test, columnKey1) != null ? data.get(test, columnKey1).getBestValue() : null;
			Value value2 = data.get(test, columnKey2) != null ? data.get(test, columnKey2).getBestValue() : null;
			if (value1 != null && value2 != null) {
				return compareValues(value1, value2);
			}
			return 0f;
		}
		return 0f;
	}
	
	public String getStyle(float result) {
		if (result > 0) {
			return "green";
		} else if (result < semiNegativeThreshold) {
			return "red";
		} else {
			return "orange";
		}
	}
	
	private float compareValues(Value v1, Value v2) {
		return (float)(((v2.getResultValue() - v1.getResultValue()) * 100f)/v2.getResultValue());
	}
	
	public String format(Object number){
		if (number != null) {
			return formatter.format(number);
		} else {
			return "";
		}
	}
	
	public String getTagAlias(String tag) {
		return tagAlias.get(tag);
	}
	
	public Map<String, String> getTagAlias() {
		return tagAlias;
	}

	public void setTagAlias(Map<String, String> tagAlias) {
		this.tagAlias = tagAlias;
	}

	public List<String> autocompleteTests(String test) {
		List<String> tests = testService.getTestsByPrefix(test);
		java.util.Iterator<String> it = tests.iterator();		
		while(it.hasNext()) {
			String t = it.next();
			if (testsCopy != null && this.testsCopy.contains(t)) {
				it.remove();
			}
		}
		return tests;
	}
	
	public void removeTest(String test) {
	    this.testsCopy.remove(test);
	}
	
	public void addTest() {
		if (currentTest != null) {
			if(testService.getTestByUID(currentTest) != null) {
				this.testsCopy.add(currentTest);
			} else {
				addSessionMessage(ERROR, "page.reports.testGroup.testNotExists", currentTest);
				reloadSessionMessages();
			}
		}
		currentTest = null;
	}
	
	public void storeTests() {
		tests = testsCopy;
		testsCopy = null;
		processTestExecutions();
	}
	
	public List<String> getTestsCopy() {
		if (testsCopy == null) {
			testsCopy = new ArrayList<String>(tests);			
		}
		return testsCopy;
	}
	
	public void clearTestsCopy() {
		testsCopy = null;
	}
	
	public String getCurrentTag() {
		return currentTag;
	}

	public void setCurrentTag(String currentTag) {
		this.currentTag = currentTag;
	}
	
	public List<String> autocompleteTags(String tag) {
		List<String> tests = testService.getTagsByPrefix(tag);
		java.util.Iterator<String> it = tests.iterator();		
		while(it.hasNext()) {
			String t = it.next();
			if (tagsCopy != null && this.tagsCopy.contains(t)) {
				it.remove();
			}
		}
		return tests;
	}
	
	public void removeTag(String tag) {
	    this.tagsCopy.remove(tag);
	}
	
	public void addTag() {
		if (currentTag != null) {
			List<String> tags = parseTags(currentTag);
			Collections.sort(tags);
			String tag = normalizeTags(tags);
			if (!tagsCopy.contains(tag)) {
				this.tagsCopy.add(tag);
			}
		}
		currentTag = null;
	}
	
	public void storeTags() {
		tags = tagsCopy;
		tagsCopy = null;
		// The tag change should be reflected in comparison
		if (comparison != null && !comparison.isEmpty()) {
			MapDifference<String, String> diff = Maps.difference(tagAlias, tagAliasCopy);
			if (!diff.areEqual()) {
				Map<String, MapDifference.ValueDifference<String>> diffs = diff.entriesDiffering();
				for(String key : diffs.keySet()) {
					String newValue = diffs.get(key).rightValue();
					String oldValue = diffs.get(key).leftValue();
					for (String compKey : comparison.keySet()) {
						List<String> comp = comparison.get(compKey);
						if (oldValue.equals(comp.get(0))) {
							comp.set(0, newValue);
						} else if (oldValue.equals(comp.get(1))) {
							comp.set(1, newValue);
						}
					}
				}
			}
		}		
		tagAlias = tagAliasCopy;
		tagAliasCopy = null;
		processTestExecutions();
	}
	
	public Map<String, String> getTagAliasCopy() {
		return tagAliasCopy;
	}

	public void setTagAliasCopy(Map<String, String> tagAliasCopy) {
		this.tagAliasCopy = tagAliasCopy;
	}

	public List<String> getTagsCopy() {
		if (tagsCopy == null) {
			tagsCopy = new ArrayList<String>(tags);
			tagAliasCopy = new HashMap<String, String>(tagAlias);
		}
		return tagsCopy;
	}
	
	public void clearTagsCopy() {
		tagsCopy = null;
		tagAliasCopy = null;
	}
	
	public String getCurrentTest() {
		return currentTest;
	}

	public void setCurrentTest(String currentTest) {
		this.currentTest = currentTest;
	}

	
	public Map<String, List<String>> getComparison() {
		return comparison;
	}

	public void setComparison(Map<String, List<String>> comparison) {
		this.comparison = comparison;
	}
	
	public List<String> getComparisonLabels() {
		if (comparisonCopy == null) {
			comparisonCopy = new HashMap<String, List<String>>(comparison);
		}
		return new ArrayList<String>(comparisonCopy.keySet());
	}
	
	public List<String> getBaselines() {
		List<String> baselines = new ArrayList<String>();
		for (String tag : tags) {
			String alias = tagAlias.get(tag);
			if (alias != null && !baselines.contains(alias)) {
				baselines.add(alias);				
			} else if (alias ==null && !baselines.contains(tag)) {
				baselines.add(tag);
			}
		}
		return baselines;
	}
	
	public List<String> getBaselines2() {
		if (baseline1 != null) {
			List<String> baselines = getBaselines();
			baselines.remove(baseline1);
			return baselines;					
		}
		return new ArrayList<String>();
	}
	
	public void addComparison() {
		if (baseline1 != null && baseline2 != null) {
			List<String> comparison = Lists.newArrayList(baseline1, baseline2);
			if (!comparisonCopy.containsValue(comparison)) {
				comparisonCopy.put(baseline2 + " vs. " + baseline1, comparison);
			}
		}		
	}
	
	public ChartData dataset(String metric) {
		if (comparison != null && !comparison.isEmpty()) {
			String compareKey = (String)comparison.keySet().toArray()[0];
		    List<String> testList = new ArrayList<String>();
		    List<Double> valueList = new ArrayList<Double>();
		    for(String test : data.rowKeySet()) {
			    testList.add(test);
			    valueList.add(Double.valueOf(compare(test, compareKey, metric)));
		    }
		    ChartData dataset = new TestGroupChartBean.ChartData();
		    dataset.setTests(testList.toArray(new String[0]));
		    dataset.setValues(valueList.toArray(new Double[0]));
		    dataset.setTitle(compareKey + " - " + metric);
		    return dataset;
		}
		return null;
    }
	
	public void removeComparison(String label) {
		comparisonCopy.remove(label);
	}
	
	public void storeComparison() {
		comparison = comparisonCopy;
		comparisonCopy = null;
		baseline1 = null; baseline2 = null;
		processTestExecutions();
	}
	
	public void clearComparisonCopy() {
		comparisonCopy = null;
		baseline1 = null; baseline2 = null;
	}
	
	public void storeMetrics() {
		processTestExecutions();
	}

	public String getBaseline1() {
		return baseline1;
	}

	public void setBaseline1(String baseline1) {
		this.baseline1 = baseline1;
	}

	public String getBaseline2() {
		return baseline2;
	}

	public void setBaseline2(String baseline2) {
		this.baseline2 = baseline2;
	}

	public Map<String, List<String>> getComparisonCopy() {
		return comparisonCopy;
	}

	public void setComparisonCopy(Map<String, List<String>> comparisonCopy) {
		this.comparisonCopy = comparisonCopy;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}	

	public String getNewReportId() {
		return newReportId;
	}

	public void setNewReportId(String newReportId) {
		this.newReportId = newReportId;
	}
	
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getNewReportName() {
		return newReportName;
	}

	public void setNewReportName(String newReportName) {
		this.newReportName = newReportName;
	}	
	
	public List<String> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<String> metrics) {
		this.metrics = metrics;
	}

	public List<String> getSelectedMetrics() {
		return selectedMetrics;
	}

	public void setSelectedMetrics(List<String> selectedMetrics) {
		this.selectedMetrics = selectedMetrics;
	}



	public class ValueCell implements Serializable {

		private static final long serialVersionUID = 2771413301301479495L;

		private Collection<Value> values = new ArrayList<Value>();
		
		private Value bestValue;
		
		public ValueCell(Value value) {
			addValues(Arrays.asList(value));
		}

		public ValueCell(Collection<Value> values) {
			addValues(values);
		}
		
		public void addValues(Collection<Value> values) {
			if (values != null) {
				this.values.addAll(values);
				for (Value value: values) {
					if (bestValue == null || ValueComparator.compare(bestValue, value) < 0){
						bestValue = value;
					}
				}
			}
		}

		public Collection<Value> getValues() {
			return values;
		}

		public void setValues(List<Value> values) {
			this.values = values;
		}

		public Value getBestValue() {
			return bestValue;
		}

		public void setBestValue(Value bestValue) {
			this.bestValue = bestValue;
		}	
		
	}

	public class ColumnKey {
		private String metricName;

		private String tagKey;

		public ColumnKey(String tagKey, String metricName) {
			this.tagKey = tagKey;
			this.metricName = metricName;
		}

		public String getMetricName() {
			return metricName;
		}

		public void setMetricName(String metricName) {
			this.metricName = metricName;
		}

		public String getTagKey() {
			return tagKey;
		}

		public void setTagKey(String tagKey) {
			this.tagKey = tagKey;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((metricName == null) ? 0 : metricName.hashCode());
			result = prime * result
					+ ((tagKey == null) ? 0 : tagKey.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ColumnKey other = (ColumnKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (metricName == null) {
				if (other.metricName != null)
					return false;
			} else if (!metricName.equals(other.metricName))
				return false;
			if (tagKey == null) {
				if (other.tagKey != null)
					return false;
			} else if (!tagKey.equals(other.tagKey))
				return false;
			return true;
		}

		private TestGroupReportController getOuterType() {
			return TestGroupReportController.this;
		}
	}
}
