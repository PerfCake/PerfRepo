(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.edit')
        .controller('EditTestExecutionController', EditTestExecutionController);

    function EditTestExecutionController(_testExecution, testExecutionService, validationHelper, $modalInstance) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.save = save;
        vm.cancel = cancel;

        function save(testExecution, form) {
            testExecutionService.update(testExecution)
                .then(function (id) {
                    $modalInstance.close(id);
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();