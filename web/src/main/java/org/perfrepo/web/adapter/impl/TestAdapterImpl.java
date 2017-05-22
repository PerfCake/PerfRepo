package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.TestAdapter;
import org.perfrepo.web.adapter.converter.GroupConverter;
import org.perfrepo.web.adapter.converter.MetricConverter;
import org.perfrepo.web.adapter.converter.TestConverter;
import org.perfrepo.web.adapter.converter.TestSearchCriteriaConverter;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.session.UserSession;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestAdapterImpl implements TestAdapter {

    @Inject
    private UserSession userSession;

    @Inject
    private TestService testService;

    @Override
    public TestDto getTest(Long id) {
        Test test = testService.getTest(id);
        TestDto dto = TestConverter.convertFromEntityToDto(test);
        //dto.setAlerts(testService); TODO: add alerts
        dto.setGroup(GroupConverter.convertFromEntityToDto(test.getGroup()));
        dto.setMetrics(MetricConverter.convertFromEntityToDto(testService.getMetricsForTest(test)));
        return dto;
    }

    @Override
    public TestDto getTest(String uid) {
        Test test = testService.getTest(uid);
        TestDto dto = TestConverter.convertFromEntityToDto(test);
        //dto.setAlerts(testService); TODO: add alerts
        dto.setGroup(GroupConverter.convertFromEntityToDto(test.getGroup()));
        dto.setMetrics(MetricConverter.convertFromEntityToDto(testService.getMetricsForTest(test)));
        return dto;
    }

    @Override
    public TestDto createTest(TestDto testDto) {
        Test testEntity = TestConverter.convertFromDtoToEntity(testDto);
        Test createdTest = testService.createTest(testEntity);

        return TestConverter.convertFromEntityToDto(createdTest);
    }

    @Override
    public TestDto updateTest(TestDto testDto) {
        Test testEntity = TestConverter.convertFromDtoToEntity(testDto);
        Test updatedTest = testService.updateTest(testEntity);

        return TestConverter.convertFromEntityToDto(updatedTest);
    }

    @Override
    public void removeTest(Long id) {
        Test testToRemove = new Test();
        testToRemove.setId(id);
        testService.removeTest(testToRemove);
    }

    @Override
    public List<TestDto> getAllTests() {
        return TestConverter.convertFromEntityToDto(testService.getAllTests());
    }

    @Override
    public SearchResult<TestDto> searchTests(org.perfrepo.dto.test.TestSearchCriteria searchParams) {
        org.perfrepo.web.service.search.TestSearchCriteria criteriaEntity = TestSearchCriteriaConverter.convertFromDtoToEntity(searchParams);
        SearchResultWrapper<Test> resultWrapper = testService.searchTests(criteriaEntity);

        userSession.setTestSearchCriteria(criteriaEntity);
        SearchResult<TestDto> result = new SearchResult<>(TestConverter.convertFromEntityToDto(resultWrapper.getResult()), resultWrapper.getTotalSearchResultsCount(), searchParams.getLimit(), searchParams.getOffset());
        return result;
    }

    @Override
    public TestSearchCriteria getSearchCriteria() {
        return TestSearchCriteriaConverter.convertFromEntityToDto(userSession.getTestSearchCriteria());
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
