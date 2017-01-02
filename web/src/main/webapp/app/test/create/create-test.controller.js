(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.create')
        .controller('CreateTestController', CreateTestController);

    function CreateTestController(_groups, testService, validationHelper, $modalInstance) {
        var vm = this;
        vm.test = {};
        vm.groups = _groups;
        vm.save = save;
        vm.cancel = cancel;

        function save(test, form) {
            testService.save(test)
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