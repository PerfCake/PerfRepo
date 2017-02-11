(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionValuesGroupTableView', {
            bindings: {
                executionValuesGroup: '<',
                testExecutionId: '<',
                onUpdateTable: '&'
            },
            controller: TestExecutionValuesGroupTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/values_group_table_view/values-group-table-view.view.html'
        });

    function TestExecutionValuesGroupTableViewController(testExecutionValueModalService, $templateCache, $scope, $filter) {
        var vm = this;


        vm.showChart = showChart;
        vm.editValueObject = editValueObject;
        vm.deleteValueObject = deleteValueObject;
        vm.getValueObjectParameter = getValueObjectParameter;

        function showChart(parameterName) {
            testExecutionValueModalService.showChart();
        }

        function editValueObject(index) {
            var modalInstance = testExecutionValueModalService.editValue(vm.executionValuesGroup, vm.testExecutionId, index);

            modalInstance.result.then(function () {
                vm.onUpdateTable();
            });
        }

        function deleteValueObject(index) {
            var modalInstance =  testExecutionValueModalService.removeValue(vm.executionValuesGroup, vm.testExecutionId, index);

            modalInstance.result.then(function () {
                vm.onUpdateTable();
            });
        }

        function getValueObjectParameter(parameterName, parameters) {
            return $filter('getByProperty')('name', parameterName, parameters);
        }
    }
})();