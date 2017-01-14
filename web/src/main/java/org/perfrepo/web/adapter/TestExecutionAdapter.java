package org.perfrepo.web.adapter;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;

import java.util.List;

/**
 * Service adapter for {@link org.perfrepo.dto.test_execution.TestExecutionDto} object.
 * Adapter supports CRUD and another operations over this object.
 * TODO adapter is not complete
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestExecutionAdapter {

    TestExecutionDto getTestExecution(Long id);

    TestExecutionDto createTestExecution(TestExecutionDto testExecution);

    TestExecutionDto updateTestExecution(TestExecutionDto testExecution);

    void removeTestExecution(Long id);

    List<TestExecutionDto> getAllTestExecutions();

    SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams);
}
