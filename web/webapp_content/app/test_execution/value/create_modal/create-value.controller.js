(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('CreateTestExecutionValueController', CreateTestExecutionValueController);

    function CreateTestExecutionValueController(_executionValuesGroup, _metric, _testExecutionId, testExecutionService,
                                                    validationHelper, $uibModalInstance) {
        var vm = this;
        vm.valueObject = {};
        vm.executionValuesGroup = _executionValuesGroup;
        vm.parameterNames = _executionValuesGroup.parameterNames;
        vm.metric = _metric;
        vm.testExecutionId = _testExecutionId;
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, form) {
            testExecutionService.addExecutionValues(vm.testExecutionId, vm.metric.id, [valueObject]).then(function () {
                $uibModalInstance.close(valueObject);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();