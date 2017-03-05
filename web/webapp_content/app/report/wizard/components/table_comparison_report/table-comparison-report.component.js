(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prTableComparisonReportConfiguration', {
            bindings: {
                data: '='
            },
            controller: TableComparisonReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/table_comparison_report/table-comparison-report.view.html'
        });

    function TableComparisonReportConfiguration(wizardService, testService, validationHelper) {
        var vm = this;
        vm.addGroup = addGroup;
        vm.removeGroup = removeGroup;
        vm.addComparison = addComparison;
        vm.removeComparison = removeComparison;
        vm.addItem = addItem;
        vm.removeItem = removeItem;
        vm.onBaselineChecked = onBaselineChecked;
        vm.refreshTestsList = refreshTestsList;
        vm.itemSelectors = wizardService.getItemSelectors();
        vm.validate = validate;

        function addGroup() {
            if (vm.data.groups == undefined) {
                vm.data.groups = [];
            }
            vm.data.groups.push({name: 'New group', tables:[]}); // add new pane
        }

        function addComparison(groupIndex) {
            if (vm.data.groups[groupIndex].tables == undefined) {
                vm.data.groups[groupIndex].tables = [];
            }
            vm.data.groups[groupIndex].tables.push({name: 'New comparison'}); // add new pane
        }

        function addItem(groupIndex, comparisonIndex) {
            if (vm.data.groups[groupIndex].tables[comparisonIndex].items == undefined) {
                vm.data.groups[groupIndex].tables[comparisonIndex].items = [];
            }
            vm.data.groups[groupIndex].tables[comparisonIndex].items.push({selector: 'TEST_EXECUTION_ID'});
        }

        function removeGroup(groupIndex) {
            vm.data.groups.splice(groupIndex, 1);
        }

        function removeComparison(groupIndex, comparisonIndex) {
            vm.data.groups[groupIndex].tables.splice(comparisonIndex, 1);
        }

        function removeItem(groupIndex, comparisonIndex, itemIndex) {
            if (vm.data.groups[groupIndex].tables[comparisonIndex].items == undefined) {
                vm.data.groups[groupIndex].tables[comparisonIndex].items = [];
            }
            vm.data.groups[groupIndex].tables[comparisonIndex].items.splice(itemIndex, 1);
        }

        function onBaselineChecked(groupIndex, comparisonIndex, itemIndex) {
            angular.forEach(vm.data.groups[groupIndex].tables[comparisonIndex].items, function(item, index) {
                if (index != itemIndex) {
                    item.baseline = false;
                }
            });
        }

        function refreshTestsList(search) {
            testService.asyncSelectSearch(search).then(function(result) {
                vm.testsList = result.data;
            });
        }

        function validate() {
            wizardService.validateReportConfigurationStep(vm.data).then(function() {
                // ok
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, vm.wizardTableComparisonStep);
                return false;
            });
        }
    }
})();