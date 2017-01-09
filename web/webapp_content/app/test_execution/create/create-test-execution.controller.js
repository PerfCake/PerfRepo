(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.create')
        .controller('CreateTestExecutionController', CreateTestExecutionController);

    function CreateTestExecutionController(testExecutionService, validationHelper, $modalInstance) {
        var vm = this;
        vm.testExecution = {};
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