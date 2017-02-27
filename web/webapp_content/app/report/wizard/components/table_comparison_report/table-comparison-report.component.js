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

    function TableComparisonReportConfiguration() {
        var vm = this;
        vm.activeGroups = [];
        vm.addGroup = addGroup;
        vm.clickGroupAccordion = clickGroupAccordion;

        function addGroup() {
            vm.data.groups.push({name: 'New group'});
            for (var i = 0; i <  vm.activeGroups.length; i++) {
                vm.activeGroups[i] = false;
            }
            vm.activeGroups[vm.data.groups.length - 1] = true;
        }

        function clickGroupAccordion(index) {
            vm.activeGroups[index] = true;
        }
    }
})();