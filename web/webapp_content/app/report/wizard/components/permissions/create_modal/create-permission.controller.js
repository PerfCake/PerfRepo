(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('CreatePermissionController', CreatePermissionController);

    function CreatePermissionController(_users, _groups, $uibModalInstance) {
        var vm = this;
        vm.users = _users;
        vm.groups = _groups;
        vm.permission = {};
        vm.save = save;
        vm.cancel = cancel;

        function save(permission, form) {
            $uibModalInstance.close(permission);
            // TODO validate
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();