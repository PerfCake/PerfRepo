/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail')
        .controller('DetailTestExecutionController', DetailTestExecutionController);

    function DetailTestExecutionController(_testExecution, testExecutionService, testExecutionModalService, $state) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.editTestExecution = editTestExecution;
        vm.updateDetail = updateDetail;
        vm.removeTestExecution = removeTestExecution;
        vm.getMetricName = getMetricName;
        setExecutionValuesGroups();

        function editTestExecution() {
            var modalInstance = testExecutionModalService.editTestExecution(vm.testExecution.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeTestExecution() {
            var modalInstance = testExecutionModalService.removeTestExecution(vm.testExecution);

            modalInstance.result.then(function () {
                $state.go('app.testExecutionOverview');
            });
        }

        function updateDetail() {
            testExecutionService.getById(vm.testExecution.id).then(function(response) {
                vm.testExecution = response;
                setExecutionValuesGroups();
            });
        }

        function getMetricName(metricId) {
            return vm.testExecution.test.metrics.filter(function(m) {
                return m.id == metricId;
            })[0].name;
        }

        function setExecutionValuesGroups() {
            vm.executionValuesGroups =  vm.testExecution.executionValuesGroups;

            var metricIdsWithValues = [];
            angular.forEach(vm.testExecution.executionValuesGroups, function(valuesGroup) {
                metricIdsWithValues.push(valuesGroup.metricId);
            });
            // add missing
            angular.forEach(vm.testExecution.test.metrics, function(metric) {
                if (metricIdsWithValues.indexOf(metric.id) == -1) {
                    vm.executionValuesGroups.push({
                        metricId: metric.id
                    });
                }
            });
        }
    }
})();