/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail')
        .controller('DetailTestExecutionController', DetailTestExecutionController);

    function DetailTestExecutionController(_testExecution, testExecutionService, testExecutionModalService,
                                           testExecutionParameterModalService, testExecutionAttachmentModalService,
                                           testExecutionValueModalService) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.editTestExecution = editTestExecution;
        vm.addParameter = addParameter;
        vm.updateDetail = updateDetail;
        vm.addAttachment = addAttachment;
        vm.addValue = addValue;
        vm.getMetricName = getMetricName;
        setExecutionValuesGroups();

        function editTestExecution() {
            var modalInstance = testExecutionModalService.editTestExecution(vm.testExecution.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function addValue(executionValuesGroup) {
            var modalInstance = testExecutionValueModalService.createValue(executionValuesGroup, vm.testExecution.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function addParameter() {
            var modalInstance = testExecutionParameterModalService.createParameter(vm.testExecution.executionParameters,
                vm.testExecution.id);
            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function addAttachment() {
            var modalInstance = testExecutionAttachmentModalService.createAttachment(vm.testExecution.id);
            modalInstance.result.then(function () {
                updateDetail();
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