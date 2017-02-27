(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('permissionsTable', {
            bindings: {
                permissions: '<',
                editMode: '<'
            },
            controller: PermissionsTableController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/permissions_table/permissions-table.view.html'
        });

    function PermissionsTableController(wizardModalService) {
        var vm = this;
        vm.addPermission = addPermission;
        vm.editPermission = editPermission;
        vm.removePermission = removePermission;

        function addPermission() {
            var modalInstance =  wizardModalService.createPermission();

            modalInstance.result.then(function (permission) {
               if (vm.permissions == undefined) {
                   vm.permissions = [];
               }
               vm.permissions.push(permission);
            });
        }

        function editPermission(index) {
            var modalInstance = wizardModalService.editPermission(vm.permissions[index]);

            modalInstance.result.then(function (permission) {
                vm.permissions[index] = permission;
            });
        }

        function removePermission(index) {
            var modalInstance =  wizardModalService.removePermission();

            modalInstance.result.then(function () {
                vm.permissions.splice(index, 1);
            });
        }
    }
})();