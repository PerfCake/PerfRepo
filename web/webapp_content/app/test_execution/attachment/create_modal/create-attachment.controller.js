(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.attachment')
        .controller('CreateTestExecutionAttachmentController', CreateTestExecutionAttachmentController);

    function CreateTestExecutionAttachmentController(_testExecutionId, testExecutionService,
                                                    validationHelper, $modalInstance) {
        var vm = this;
        vm.attachment = {};
        vm.testExecutionId = _testExecutionId;
        vm.save = save;
        vm.cancel = cancel;

        function save(form) {
            console.log(form);
            testExecutionService.uploadAttachment(vm.formData).then(function () {
                $modalInstance.close(parameter);
            }, function(errorResponse) {
                //validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();