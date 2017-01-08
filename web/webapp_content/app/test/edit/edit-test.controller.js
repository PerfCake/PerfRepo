(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.edit')
        .controller('EditTestController', EditTestController);

    function EditTestController(_test, _groups, testService, validationHelper, $modalInstance) {
        var vm = this;
        vm.test = _test;
        vm.groups = _groups;
        vm.save = save;
        vm.cancel = cancel;

        function save(test, form) {
            testService.update(test)
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