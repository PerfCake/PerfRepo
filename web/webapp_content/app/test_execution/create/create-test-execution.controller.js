(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.create')
        .controller('CreateTestExecutionController', CreateTestExecutionController);

    function CreateTestExecutionController(_testId, testExecutionService, validationHelper, $modalInstance) {
        var vm = this;
        vm.testExecution = {
            test: {
                id: _testId
            }
        };
        vm.save = save;
        vm.cancel = cancel;

        function save(testExecution, form) {
            testExecutionService.save(testExecution)
                .then(function (id) {
                    $modalInstance.close(id);
                }, function(errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();