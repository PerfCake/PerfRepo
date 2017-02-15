(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('EditTestExecutionValueController', EditTestExecutionValueController);

    function EditTestExecutionValueController(_executionValuesGroup, _metric, _testExecutionId, _index,
                                                  testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.executionValuesGroup = _executionValuesGroup;
        vm.parameterNames = _executionValuesGroup.parameterNames;
        vm.index = _index;
        console.log(_executionValuesGroup);
        console.log(_index);
        vm.valueObject = angular.copy(_executionValuesGroup.values[_index]);
        vm.metric = _metric;
        vm.testExecutionId = _testExecutionId;
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, form) {
            var valueObjects =  angular.copy(vm.executionValuesGroup.values);
            valueObjects[vm.index] = valueObject;
            testExecutionService.setExecutionValues(vm.testExecutionId, vm.metric.id, valueObjects).then(function () {
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