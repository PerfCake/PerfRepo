package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.dto.test_execution.mass_operation.ParameterMassOperationDto;
import org.perfrepo.dto.test_execution.mass_operation.TagMassOperationDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.TestExecutionAdapter;
import org.perfrepo.web.adapter.converter.AttachmentConverter;
import org.perfrepo.web.adapter.converter.ParameterConverter;
import org.perfrepo.web.adapter.converter.TagConverter;
import org.perfrepo.web.adapter.converter.TestExecutionConverter;
import org.perfrepo.web.adapter.converter.TestExecutionSearchCriteriaConverter;
import org.perfrepo.web.adapter.converter.ValueConverter;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionAttachment;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.TestExecutionService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionAdapterImpl implements TestExecutionAdapter {

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private TestExecutionConverter testExecutionConverter;

    @Inject
    private AttachmentConverter attachmentConverter;

    @Inject
    private ParameterConverter parameterConverter;

    @Inject
    private TagConverter tagConverter;

    @Inject
    private ValueConverter valueConverter;

    @Inject
    private TestExecutionSearchCriteriaConverter searchCriteriaConverter;

    @Override
    public TestExecutionDto getTestExecution(Long id) {
        TestExecution testExecution = testExecutionService.getTestExecution(id);
        TestExecutionDto dto = testExecutionConverter.convertFromEntityToDto(testExecution);

        dto.setExecutionAttachments(attachmentConverter.convertFromEntityToDto(testExecutionService.getAttachments(testExecution)));
        dto.setExecutionParameters(parameterConverter.convertFromEntityToDto(testExecutionService.getParameters(testExecution)));
        dto.setExecutionValuesGroups(valueConverter.convertFromEntityToDto(testExecutionService.getValues(testExecution)));

        return dto;
    }

    @Override
    public TestExecutionDto createTestExecution(TestExecutionDto dto) {
        TestExecution testExecution = testExecutionConverter.convertFromDtoToEntity(dto);
        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);
        return testExecutionConverter.convertFromEntityToDto(createdTestExecution);
    }

    @Override
    public TestExecutionDto updateTestExecution(TestExecutionDto dto) {
        TestExecution testExecution = testExecutionConverter.convertFromDtoToEntity(dto);
        TestExecution updatedTestExecution = testExecutionService.updateTestExecution(testExecution);
        return testExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto setTestExecutionParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Map<String, TestExecutionParameter> parameters = parameterConverter.convertFromDtoToEntity(testExecutionParameters);
        parameters.values().stream().forEach(parameter -> parameter.setTestExecution(testExecution));

        testExecutionService.updateParameters(parameters, testExecution);
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return testExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto addExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroupDto) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Metric metric = new Metric();
        metric.setName(valuesGroupDto.getMetricName());

        List<Value> values = valueConverter.convertFromDtoToEntity(valuesGroupDto.getValues());
        values.stream().forEach(value -> { value.setMetric(metric); value.setTestExecution(testExecution); });

        // TODO this should be done with some replacement, temporal implementation
        values.stream().forEach(value -> testExecutionService.addValue(value));
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return testExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto setExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroupDto) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Metric metric = new Metric();
        metric.setName(valuesGroupDto.getMetricName());

        List<Value> values = valueConverter.convertFromDtoToEntity(valuesGroupDto.getValues());
        values.stream().forEach(value -> { value.setMetric(metric); value.setTestExecution(testExecution); });

        // TODO this should be done with some replacement, temporal implementation
        values.stream().forEach(value -> testExecutionService.addValue(value));
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return testExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public void removeTestExecution(Long id) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(id);
        testExecutionService.removeTestExecution(testExecution);
    }

    @Override
    public List<TestExecutionDto> getAllTestExecutions() {
        List<TestExecution> testExecutions = testExecutionService.getAllTestExecutions();
        return testExecutionConverter.convertFromEntityToDto(testExecutions);
    }

    @Override
    public SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams) {
        org.perfrepo.web.service.search.TestExecutionSearchCriteria criteria = searchCriteriaConverter.convertFromDtoToEntity(searchParams);
        SearchResultWrapper<TestExecution> resultWrapper = testExecutionService.searchTestExecutions(criteria);

        SearchResult<TestExecutionDto> result = new SearchResult<>(testExecutionConverter.convertFromEntityToDto(resultWrapper.getResult()), resultWrapper.getTotalSearchResultsCount(), searchParams.getLimit(), searchParams.getOffset());
        return result;
    }

    @Override
    public AttachmentDto getTestExecutionAttachment(Long attachmentId, String hash) {
        TestExecutionAttachment attachment = testExecutionService.getAttachment(attachmentId);
        return attachmentConverter.convertFromEntityToDto(attachment);
    }

    //TODO: implement the methods below

    @Override
    public TestExecutionSearchCriteria getSearchCriteria() {
        return null;
    }

    @Override
    public void addTags(TagMassOperationDto massOperation) {

    }

    @Override
    public void removeTags(TagMassOperationDto massOperation) {

    }

    @Override
    public void addParameter(ParameterMassOperationDto massOperation) {

    }

    @Override
    public void removeParameter(ParameterMassOperationDto massOperation) {

    }

    @Override
    public void removeTestExecutions(Set<Long> testExecutionIds) {

    }
}
