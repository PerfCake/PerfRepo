
package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.web.adapter.TestExecutionAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.AdapterException;
import org.perfrepo.web.adapter.exceptions.BadRequestException;
import org.perfrepo.web.adapter.exceptions.NotFoundException;
import org.perfrepo.web.adapter.exceptions.ValidationException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Temporary implementation of {@link TestExecutionAdapter} for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionAdapterDummyImpl implements TestExecutionAdapter {

    @Inject
    private Storage storage;

    @Override
    public TestExecutionDto getTestExecution(Long id) {
        TestExecutionDto testExecution = storage.testExecution().getById(id);

        if (testExecution == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        return testExecution;
    }

    @Override
    public TestExecutionDto createTestExecution(TestExecutionDto testExecution) {
        validate(testExecution);

        testExecution.setTest(storage.test().getById(testExecution.getTest().getId()));

        // it is not possible to set it, only in test execution detail
        testExecution.setExecutionParameters(new HashMap<>());
        testExecution.setExecutionValues(new HashSet<>());

        return storage.testExecution().create(testExecution);
    }

    @Override
    public TestExecutionDto updateTestExecution(TestExecutionDto testExecution) {
        if (testExecution == null) {
            throw new BadRequestException("Bad input data.");
        }

        if (testExecution.getId() == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        TestExecutionDto originExecutionTest = storage.testExecution().getById(testExecution.getId());

        if (originExecutionTest == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        validate(testExecution);

        testExecution.setExecutionParameters(originExecutionTest.getExecutionParameters());
        testExecution.setExecutionValues(originExecutionTest.getExecutionValues());

        return storage.testExecution().update(testExecution);
    }

    @Override
    public void removeTestExecution(Long id) {
        TestExecutionDto testExecution = storage.testExecution().getById(id);

        if (testExecution == null) {
            throw new NotFoundException("Test execution does not exist.");
        }

        boolean removed = storage.testExecution().delete(testExecution.getId());

        if (!removed) {
            throw new AdapterException("You can not delete test execution.");
        }
    }

    @Override
    public List<TestExecutionDto> getAllTestExecutions() {
        return storage.testExecution().getAll();
    }

    @Override
    public SearchResult<TestExecutionDto> searchTestExecutions(TestExecutionSearchCriteria searchParams) {
        // delay...
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return storage.testExecution().search(searchParams);
    }

    private void validate(TestExecutionDto testExecution) {
        ValidationErrors validation = new ValidationErrors();

        // test name
        if (testExecution.getName() == null) {
            validation.addFieldError("name", "Test execution name is a required field");
        } else if (testExecution.getName().trim().length() < 3) {
            validation.addFieldError("name", "Test execution name must be at least three characters.");
        }

        if (testExecution.getTags() == null) {
            validation.addFieldError("tags", "Test execution tags is a required field");
        } else if (testExecution.getTags().isEmpty()) {
            validation.addFieldError("tags", "Test execution tags is a required field");
        }

        // test description
        if (testExecution.getComment() != null && testExecution.getComment().length() > 500) {
            validation.addFieldError("comment", "Test execution description must not be more than 500 characters.");
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Test execution contains validation errors, please fix it.", validation);
        }
    }
}
