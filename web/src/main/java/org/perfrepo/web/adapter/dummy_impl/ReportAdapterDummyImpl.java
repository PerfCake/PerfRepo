package org.perfrepo.web.adapter.dummy_impl;

import com.google.common.collect.Sets;
import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.report.table_comparison.ComparisonItemDto;
import org.perfrepo.dto.report.table_comparison.GroupDto;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;
import org.perfrepo.dto.report.table_comparison.TableDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.enums.AccessLevel;
import org.perfrepo.web.adapter.ReportAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;
import org.perfrepo.web.adapter.exceptions.ValidationException;


import javax.inject.Inject;
import java.util.List;

public class ReportAdapterDummyImpl implements ReportAdapter {

    @Inject
    private Storage storage;

    @Override
    public ReportDto getReport(Long id) {
        return storage.report().getById(id);
    }

    @Override
    public ReportDto createReport(ReportDto report) {
        return storage.report().create(report);
    }

    @Override
    public ReportDto updateReport(ReportDto report) {
        return storage.report().update(report);
    }

    @Override
    public void removeReport(Long id) {
        storage.report().delete(id);
    }

    @Override
    public List<ReportDto> getAllReports() {
        return storage.report().getAll();
    }

    @Override
    public SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams) {
        return storage.report().search(searchParams);
    }

    @Override
    public void validateWizardReportInfoStep(ReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        // test name
        if (report.getName() == null) {
            validation.addFieldError("name", "Report name is a required field.");
        } else if (report.getName().trim().length() < 3) {
            validation.addFieldError("name", "Report name must be at least three characters.");
        }

        // test description
        if (report.getDescription() != null && report.getDescription().length() > 500) {
            validation.addFieldError("description", "Report description must not be more than 500 characters.");
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Report contains validation errors, please fix it.", validation);
        }
    }

    @Override
    public void validateWizardReportConfigurationStep(ReportDto report) {
        if (report instanceof TableComparisonReportDto) {
            validateTableComparisonConfiguration((TableComparisonReportDto) report);
        }

    }

    @Override
    public void validateWizardReportPermissionStep(ReportDto report) {
        ValidationErrors validation = new ValidationErrors();

        if (report.getPermissions() == null || report.getPermissions().size() == 0) {
            // TODO
            return;
        }

        for (int permissionIndex = 0; permissionIndex < report.getPermissions().size(); permissionIndex++) {
            PermissionDto permission = report.getPermissions().get(permissionIndex);
            if (permission.getLevel() == null) {
                validation.addFieldError("permissions[" + permissionIndex + "].level", "Please select value.");
            } else if (permission.getLevel().equals(AccessLevel.GROUP) && permission.getGroupId() == null) {
                validation.addFieldError("permissions[" + permissionIndex + "].groupId", "Please select group.");
            } else if (permission.getLevel().equals(AccessLevel.USER) && permission.getUserId() == null)
                validation.addFieldError("permissions[" + permissionIndex + "].userId", "Please select user.");
            if (permission.getLevel() != null && permission.getType() == null) {
                validation.addFieldError("permissions[" + permissionIndex + "].type", "Please select value.");
            }
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Permissions contain validation errors, please fix it.", validation);
        }
    }

    private void validateTableComparisonConfiguration(TableComparisonReportDto report) {
        // test name
        ValidationErrors validation = new ValidationErrors();

        if(report.getGroups() == null) {
            return;
        }

        for (int groupIndex = 0; groupIndex < report.getGroups().size(); groupIndex++) {
            GroupDto group = report.getGroups().get(groupIndex);
            // group name
            if (group.getName() == null) {
                validation.addFieldError("groups[" + groupIndex + "].name", "Group name is a required field.");
            } else if (group.getName().trim().length() < 3) {
                validation.addFieldError("groups[" + groupIndex + "].name", "Group name must be at least three characters.");
            }
            // group description
            if (group.getDescription() != null && group.getDescription().length() > 500) {
                validation.addFieldError("groups[" + groupIndex + "].description", "Group description must not be more than 500 characters.");
            }
            // group threshold
            if (group.getThreshold() <= 0) {
                validation.addFieldError("groups[" + groupIndex + "].threshold", "Group threshold must be greater than zero.");
            }
            // tables
            if (group.getTables() == null) {
                continue;
            }
            // validate tables
            validateTables(validation, group, groupIndex);
        }

        if (validation.hasFieldErrors()) {
            throw new ValidationException("Report contains validation errors, please fix it.", validation);
        }
    }

    private void validateTables(ValidationErrors validation, GroupDto group, int groupIndex) {
        for (int tableIndex = 0; tableIndex < group.getTables().size(); tableIndex++) {
            TableDto table = group.getTables().get(tableIndex);
            // table name
            if (table.getName() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].name", "Table name is a required field");
            } else if (table.getName().trim().length() < 3) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].name", "Table name must be at least three characters.");
            }
            // table description
            if (table.getDescription() != null && table.getDescription().length() > 500) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].description", "Table description must not be more than 500 characters.");
            }
            // items
            if (table.getItems() == null) {
                continue;
            }
            // validate items
            validateItems(validation, table, groupIndex, tableIndex);
        }
    }

    private void validateItems(ValidationErrors validation, TableDto table, int groupIndex, int tableIndex) {
        for (int itemIndex = 0; itemIndex < table.getItems().size(); itemIndex++) {
            ComparisonItemDto item = table.getItems().get(itemIndex);
            // item alias
            if (item.getAlias() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].alias", "Item alias is a required field.");
            } else if (item.getAlias().trim().length() < 3) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].alias", "Item alias must be at least three characters.");
            }
            // item selector
            if (item.getSelector() == null) {
                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].selector", "Item selector is required.");
            } else {
                switch (item.getSelector()) {
                    case TEST_EXECUTION_ID:
                        if (storage.testExecution().getById(item.getExecutionId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].executionId", "Test execution not found.");
                        }
                        break;
                    case TAG_QUERY:
                        if (storage.test().getById(item.getTestId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].testId", "Test not found.");
                        } else {
                            TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
                            criteria.setTestUIDsFilter(Sets.newHashSet(storage.test().getById(item.getTestId()).getUid()));
                            criteria.setTagQueriesFilter(Sets.newHashSet(item.getTagQuery()));
                            if (storage.testExecution().search(criteria).getTotalCount() == 0) {
                                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].tagQuery", "Test execution not found.");
                            }
                        }
                        break;
                    case PARAMETER_QUERY:
                        if (storage.test().getById(item.getTestId()) == null) {
                            validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].testId", "Test not found.");
                        } else {
                            TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
                            criteria.setTestUIDsFilter(Sets.newHashSet(storage.test().getById(item.getTestId()).getUid()));
                            criteria.setParameterQueriesFilter(Sets.newHashSet(item.getParameterQuery()));
                            if (storage.testExecution().search(criteria).getTotalCount() == 0) {
                                validation.addFieldError("groups[" + groupIndex + "].tables[" + tableIndex + "].items[" + itemIndex + "].parameterQuery", "Test execution not found.");
                            }
                        }
                        break;
                }
            }
        }
    }
}