package org.perfrepo.client;

import org.junit.After;
import org.junit.Before;
import org.perfrepo.dto.alert.AlertDto;
import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.ValueDto;
import org.perfrepo.dto.test_execution.ValueParameterDto;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.MetricComparator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractClientTest {

    public static final String TEST_USERNAME = "perfrepouser";
    public static final String TEST_PASSWORD = "perfrepouser1.";
    public static final String TEST_URL = "http://localhost:8080";

    public static final String TEST_GROUP = "perfrepouser";

    protected Client client;

    @Before
    public void basicSetup() {
        client = new Client(TEST_URL, TEST_USERNAME, TEST_PASSWORD);
    }

    @After
    public void cleanup() {
        client.testExecution().getAll().stream()
                .forEach(testExecution -> client.testExecution().delete(testExecution.getId()));
        client.test().getAll().stream()
                .forEach(test -> client.test().delete(test.getId()));
    }

    /** ---------- Helper methods for entity creation ------------ **/

    public static TestExecutionDto createTestExecution(String name, TestDto test) {
        TestExecutionDto testExecution = new TestExecutionDto();
        testExecution.setName("name_" + name);
        testExecution.setTest(test);

        return testExecution;
    }

    public static TestDto createTest(String name) {
        TestDto test = new TestDto();
        test.setName("name_" + name);
        test.setUid("uid_" + name);

        GroupDto groupDto = new GroupDto();
        groupDto.setName(TEST_GROUP);
        test.setGroup(groupDto);

        return test;
    }

    public static MetricDto createMetric(String name) {
        MetricDto metric = new MetricDto();
        metric.setName("name_" + name);
        metric.setComparator(MetricComparator.HIGHER_BETTER);

        return metric;
    }

    public static ParameterDto createParameter(String name) {
        ParameterDto parameter = new ParameterDto();
        parameter.setName("name_" + name);
        parameter.setValue("value_" + name);

        return parameter;
    }

    public static ValuesGroupDto createValueGroupSingleValue(double initialValue, MetricDto metric) {
        ValuesGroupDto valueGroup = new ValuesGroupDto();
        valueGroup.setMetricName(metric.getName());
        valueGroup.setValueType(MeasuredValueType.SINGLE_VALUE);
        valueGroup.setValues(Arrays.asList(createSingleValue(initialValue)));

        return valueGroup;
    }

    public static ValuesGroupDto createValueGroupMultiValue(double initialValue, MetricDto metric) {
        ValuesGroupDto valueGroup = new ValuesGroupDto();
        valueGroup.setMetricName(metric.getName());
        valueGroup.setValueType(MeasuredValueType.MULTI_VALUE);

        String parameterName = "param1_name";
        valueGroup.setParameterNames(createSet(parameterName));
        valueGroup.setValues(Arrays.asList(createMultiValue(initialValue, parameterName), createMultiValue(initialValue + 10, parameterName), createMultiValue(initialValue + 20, parameterName)));

        return valueGroup;
    }

    public static ValueDto createSingleValue(double result) {
        ValueDto value = new ValueDto();
        value.setValue(result);

        return value;
    }

    public static ValueDto createMultiValue(double result, String parameterName) {
        ValueDto value = new ValueDto();
        value.setValue(result);

        ValueParameterDto valueParameter = new ValueParameterDto();
        valueParameter.setName(parameterName);
        valueParameter.setValue(result);
        value.setParameters(createSet(valueParameter));

        return value;
    }

    public static AttachmentDto createAttachment(String name) {
        AttachmentDto attachment = new AttachmentDto();

        byte[] content = new byte[10];
        Random random = new Random();
        random.nextBytes(content);
        attachment.setContent(content);

        attachment.setFilename("filename_" + name);
        attachment.setMimeType("text/plain");

        return attachment;
    }

    /** ---------- Helper assertions ---------- **/

    public static void assertTestExecution(TestExecutionDto expected, TestExecutionDto actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getComment(), actual.getComment());
        assertEquals(expected.getStarted(), actual.getStarted());
        assertEquals(expected.getTags(), actual.getTags());

        assertAttachments(expected.getExecutionAttachments(), actual.getExecutionAttachments());
        assertParameters(expected.getExecutionParameters(), actual.getExecutionParameters());
        assertValueGroups(expected.getExecutionValuesGroups(), actual.getExecutionValuesGroups());
    }

    public static void assertTest(TestDto expected, TestDto actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.getUid(), actual.getUid());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());

        assertGroup(expected.getGroup(), actual.getGroup());
        assertAlerts(expected.getAlerts(), actual.getAlerts());
        assertMetrics(expected.getMetrics(), actual.getMetrics());
    }

    public static void assertGroup(GroupDto expected, GroupDto actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.getName(), actual.getName());
    }

    public static void assertAlerts(Collection<AlertDto> expected, Collection<AlertDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> assertAlert(expectedItem, actualItem))));
    }

    public static boolean assertAlert(AlertDto expected, AlertDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCondition(), actual.getCondition());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getTestId(), actual.getTestId());

        assertEquals(expected.getLinks(), actual.getLinks());
        assertEquals(expected.getTags(), actual.getTags());
        assertMetric(expected.getMetric(), actual.getMetric());

        return true;
    }

    public static void assertMetrics(Collection<MetricDto> expected, Collection<MetricDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> expectedItem.equals(actualItem))));
    }

    public static boolean assertMetric(MetricDto expected, MetricDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getComparator(), actual.getComparator());
        assertEquals(expected.getDescription(), actual.getDescription());

        return true;
    }

    public static void assertAttachments(Collection<AttachmentDto> expected, Collection<AttachmentDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> expectedItem.equals(actualItem))));
    }

    public static boolean assertAttachment(AttachmentDto expected, AttachmentDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getFilename(), actual.getFilename());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getHash(), actual.getHash());
        assertEquals(expected.getMimeType(), actual.getMimeType());
        assertEquals(expected.getSize(), actual.getSize());

        return true;
    }

    public static void assertParameters(Collection<ParameterDto> expected, Collection<ParameterDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> expectedItem.equals(actualItem))));
    }

    public static void assertValueGroups(Collection<ValuesGroupDto> expected, Collection<ValuesGroupDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> expectedItem.equals(actualItem))));
    }

    public static boolean assertValueGroup(ValuesGroupDto expected, ValuesGroupDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getValueType(), actual.getValueType());
        assertEquals(expected.getMetricName(), actual.getMetricName());
        assertEquals(expected.getParameterNames(), actual.getParameterNames());

        assertValues(expected.getValues(), actual.getValues());

        return true;
    }

    public static void assertValues(Collection<ValueDto> expected, Collection<ValueDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> assertValue(expectedItem, actualItem))));
    }

    public static boolean assertValue(ValueDto expected, ValueDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getValue(), actual.getValue(), 0.001);
        assertValueParameters(expected.getParameters(), actual.getParameters());

        return true;
    }

    public static void assertValueParameters(Collection<ValueParameterDto> expected, Collection<ValueParameterDto> actual) {
        if (areBothNull(expected, actual)) return;

        assertEquals(expected.size(), actual.size());
        assertTrue("Expected: " + expected + "; Actual: " + actual, expected.stream().allMatch(expectedItem -> actual.stream()
                .anyMatch(actualItem -> assertValueParameter(expectedItem, actualItem))));
    }

    public static boolean assertValueParameter(ValueParameterDto expected, ValueParameterDto actual) {
        if (areBothNull(expected, actual)) return true;

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue(), 0.001);

        return true;
    }

    private static boolean areBothNull(Object expected, Object actual) {
        return expected == null && actual == null;
    }

    /** ---------- Helper methods ---------- **/

    public static <T> Set<T> createSet(T... objects) {
        return new HashSet<T>(Arrays.asList(objects));
    }
}
