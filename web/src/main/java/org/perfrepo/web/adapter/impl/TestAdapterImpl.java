package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.TestAdapter;
import org.perfrepo.web.adapter.converter.GroupConverter;
import org.perfrepo.web.adapter.converter.MetricConverter;
import org.perfrepo.web.adapter.converter.TestConverter;
import org.perfrepo.web.adapter.converter.TestSearchCriteriaConverter;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.TestService;

import javax.inject.Inject;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestAdapterImpl implements TestAdapter {

    @Inject
    private TestService testService;

    @Inject
    private TestConverter testConverter;

    @Inject
    private GroupConverter groupConverter;

    @Inject
    private MetricConverter metricConverter;

    @Inject
    private TestSearchCriteriaConverter testSearchCriteriaConverter;

    @Override
    public TestDto getTest(Long id) {
        Test test = testService.getTest(id);
        TestDto dto = testConverter.convertFromEntityToDto(test);
        //dto.setAlerts(testService); TODO: add alerts
        dto.setGroup(groupConverter.convertFromEntityToDto(test.getGroup()));
        dto.setMetrics(metricConverter.convertFromEntityToDto(testService.getMetricsForTest(test)));
        return dto;
    }

    @Override
    public TestDto getTest(String uid) {
        Test test = testService.getTest(uid);
        TestDto dto = testConverter.convertFromEntityToDto(test);
        //dto.setAlerts(testService); TODO: add alerts
        dto.setGroup(groupConverter.convertFromEntityToDto(test.getGroup()));
        dto.setMetrics(metricConverter.convertFromEntityToDto(testService.getMetricsForTest(test)));
        return dto;
    }

    @Override
    public TestDto createTest(TestDto testDto) {
        Test testEntity = testConverter.convertFromDtoToEntity(testDto);
        Test createdTest = testService.createTest(testEntity);

        return testConverter.convertFromEntityToDto(createdTest);
    }

    @Override
    public TestDto updateTest(TestDto testDto) {
        Test testEntity = testConverter.convertFromDtoToEntity(testDto);
        Test updatedTest = testService.updateTest(testEntity);

        return testConverter.convertFromEntityToDto(updatedTest);
    }

    @Override
    public void removeTest(Long id) {
        Test testToRemove = new Test();
        testToRemove.setId(id);
        testService.removeTest(testToRemove);
    }

    @Override
    public List<TestDto> getAllTests() {
        return testConverter.convertFromEntityToDto(testService.getAllTests());
    }

    @Override
    public SearchResult<TestDto> searchTests(org.perfrepo.dto.test.TestSearchCriteria searchParams) {
        org.perfrepo.web.service.search.TestSearchCriteria criteriaEntity = testSearchCriteriaConverter.convertFromDtoToEntity(searchParams);
        SearchResultWrapper<Test> resultWrapper = testService.searchTests(criteriaEntity);

        SearchResult<TestDto> result = new SearchResult<>(testConverter.convertFromEntityToDto(resultWrapper.getResult()), resultWrapper.getTotalSearchResultsCount(), searchParams.getLimit(), searchParams.getOffset());
        return result;
    }

    @Override
    public boolean isSubscriber(Long testId) {
        Test test = new Test();
        test.setId(testId);

//        return testService.isUserSubscribed(test); TODO: solve this
        return false;
    }

    @Override
    public void addSubscriber(Long testId) {
        Test test = new Test();
        test.setId(testId);
        testService.addSubscriber(test);
    }

    @Override
    public void removeSubscriber(Long testId) {
        Test test = new Test();
        test.setId(testId);
        testService.removeSubscriber(test);
    }
}
