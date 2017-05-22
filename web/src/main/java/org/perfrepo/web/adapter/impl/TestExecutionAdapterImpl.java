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
import org.perfrepo.web.adapter.converter.TestExecutionConverter;
import org.perfrepo.web.adapter.converter.TestExecutionSearchCriteriaConverter;
import org.perfrepo.web.adapter.converter.ValueConverter;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionAttachment;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.session.UserSession;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionAdapterImpl implements TestExecutionAdapter {

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private UserSession userSession;

    @Override
    public TestExecutionDto getTestExecution(Long id) {
        TestExecution testExecution = testExecutionService.getTestExecution(id);
        TestExecutionDto dto = TestExecutionConverter.convertFromEntityToDto(testExecution);

        dto.setExecutionAttachments(AttachmentConverter.convertFromEntityToDto(testExecutionService.getAttachments(testExecution)));
        dto.setExecutionParameters(ParameterConverter.convertFromEntityToDto(testExecutionService.getParameters(testExecution)));
        dto.setExecutionValuesGroups(ValueConverter.convertFromEntityToDto(testExecutionService.getValues(testExecution)));

        return dto;
    }

    @Override
    public TestExecutionDto createTestExecution(TestExecutionDto dto) {
        TestExecution testExecution = TestExecutionConverter.convertFromDtoToEntity(dto);
        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);
        return TestExecutionConverter.convertFromEntityToDto(createdTestExecution);
    }

    @Override
    public TestExecutionDto updateTestExecution(TestExecutionDto dto) {
        TestExecution testExecution = TestExecutionConverter.convertFromDtoToEntity(dto);
        TestExecution updatedTestExecution = testExecutionService.updateTestExecution(testExecution);
        return TestExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto setTestExecutionParameters(Long testExecutionId, Set<ParameterDto> testExecutionParameters) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Map<String, TestExecutionParameter> parameters = ParameterConverter.convertFromDtoToEntity(testExecutionParameters);
        parameters.values().stream().forEach(parameter -> parameter.setTestExecution(testExecution));

        testExecutionService.updateParameters(parameters, testExecution);
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return TestExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto addExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroupDto) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Metric metric = new Metric();
        metric.setName(valuesGroupDto.getMetricName());

        List<Value> values = ValueConverter.convertFromDtoToEntity(valuesGroupDto.getValues());
        values.stream().forEach(value -> { value.setMetric(metric); value.setTestExecution(testExecution); });

        // TODO this should be done with some replacement, temporal implementation
        values.stream().forEach(value -> testExecutionService.addValue(value));
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return TestExecutionConverter.convertFromEntityToDto(updatedTestExecution);
    }

    @Override
    public TestExecutionDto setExecutionValues(Long testExecutionId, ValuesGroupDto valuesGroupDto) {
        TestExecution testExecution = new TestExecution();
        testExecution.setId(testExecutionId);

        Metric metric = new Metric();
        metric.setName(valuesGroupDto.getMetricName());

        List<Value> values = ValueConverter.convertFromDtoToEntity(valuesGroupDto.getValues());
        values.stream().forEach(value -> { value.setMetric(metric); value.setTestExecution(testExecution); });

        // TODO this should be done with some replacement, temporal implementation
        values.stream().forEach(value -> testExecutionService.addValue(value));
        TestExecution updatedTestExecution = testExecutionService.getTestExecution(testExecutionId);
        return TestExecutionConverter.convertFromEntityToDto(updatedTestExecution);
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
        return TestExecutionConverter.convertFromEntityToDto(testExecutions);
    }

    @Override
    public SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams) {
        org.perfrepo.web.service.search.TestExecutionSearchCriteria criteria = TestExecutionSearchCriteriaConverter.convertFromDtoToEntity(searchParams);
        SearchResultWrapper<TestExecution> resultWrapper = testExecutionService.searchTestExecutions(criteria);

        userSession.setTestExecutionSearchCriteria(criteria); //TODO: revisit this, when searching for last test executions, it messes up the "normal" search
        SearchResult<TestExecutionDto> result = new SearchResult<>(TestExecutionConverter.convertFromEntityToDto(resultWrapper.getResult()), resultWrapper.getTotalSearchResultsCount(), searchParams.getLimit(), searchParams.getOffset());
        return result;
    }

    @Override
    public AttachmentDto getTestExecutionAttachment(Long attachmentId, String hash) {
        TestExecutionAttachment attachment = testExecutionService.getAttachment(attachmentId);
        return AttachmentConverter.convertFromEntityToDto(attachment);
    }

    @Override
    public TestExecutionSearchCriteria getSearchCriteria() {
        return TestExecutionSearchCriteriaConverter.convertFromEntityToDto(userSession.getTestExecutionSearchCriteria());
    }

    @Override
    public void addTags(TagMassOperationDto massOperation) {
        Set<Tag> tags = massOperation.getTags().stream().map(tagString -> { Tag tag = new Tag(); tag.setName(tagString); return tag; }).collect(Collectors.toSet());
        Set<TestExecution> testExecutions = massOperation.getTestExecutionIds().stream().map(id -> { TestExecution testExecution = new TestExecution(); testExecution.setId(id); return testExecution; }).collect(Collectors.toSet());
        testExecutionService.addTagsToTestExecutions(tags, testExecutions);
    }

    @Override
    public void removeTags(TagMassOperationDto massOperation) {
        Set<Tag> tags = massOperation.getTags().stream().map(tagString -> { Tag tag = new Tag(); tag.setName(tagString); return tag; }).collect(Collectors.toSet());
        Set<TestExecution> testExecutions = massOperation.getTestExecutionIds().stream().map(id -> { TestExecution testExecution = new TestExecution(); testExecution.setId(id); return testExecution; }).collect(Collectors.toSet());
        testExecutionService.removeTagsFromTestExecutions(tags, testExecutions);
    }

    @Override
    public void addParameter(ParameterMassOperationDto massOperation) {
        TestExecutionParameter parameter = ParameterConverter.convertFromDtoToEntity(massOperation.getParameter());
        Set<TestExecution> testExecutions = massOperation.getTestExecutionIds().stream().map(id -> { TestExecution testExecution = new TestExecution(); testExecution.setId(id); return testExecution; }).collect(Collectors.toSet());
        testExecutionService.addParametersToTestExecutions(Stream.of(parameter).collect(Collectors.toSet()), testExecutions);
    }

    @Override
    public void removeParameter(ParameterMassOperationDto massOperation) {
        TestExecutionParameter parameter = ParameterConverter.convertFromDtoToEntity(massOperation.getParameter());
        Set<TestExecution> testExecutions = massOperation.getTestExecutionIds().stream().map(id -> { TestExecution testExecution = new TestExecution(); testExecution.setId(id); return testExecution; }).collect(Collectors.toSet());
        testExecutionService.removeParametersFromTestExecutions(Stream.of(parameter).collect(Collectors.toSet()), testExecutions);
    }

    @Override
    public void removeTestExecutions(Set<Long> testExecutionIds) {
        Set<TestExecution> testExecutions = testExecutionIds.stream().map(id -> { TestExecution testExecution = new TestExecution(); testExecution.setId(id); return testExecution; }).collect(Collectors.toSet());
        testExecutionService.removeTestExecutions(testExecutions);
    }
}
