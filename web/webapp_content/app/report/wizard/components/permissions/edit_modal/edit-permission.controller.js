(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('EditPermissionController', EditPermissionController);

    function EditPermissionController(_users, _groups, _permission, $uibModalInstance) {
        var vm = this;
        vm.users = _users;
        vm.groups = _groups;
        vm.permission = _permission;
        vm.save = save;
        vm.cancel = cancel;

        function save(permission, form) {
            $uibModalInstance.close(permission);
            //TODO validation
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();