(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('permissionForm', {
            bindings: {
                groups: '<',
                users: '<',
                permission: '<',
                onSave: '&',
                onCancel: '&'
            },
            controller: PermissionFormController,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/permissions/form/form-permission.view.html'
        });

    function PermissionFormController(wizardService, $filter, $scope) {
        var vm = this;
        vm.save = save;
        vm.options = wizardService.getPermissionOptions();
        vm.permissionTypes = permissionTypes;

        function permissionTypes() {
            var option = $filter('getByProperty')('level', vm.permission.level, vm.options);
            if (option == null) {
                return [];
            } else {
                return option.types;
            }
        }

        function save(form) {
            // set group name
            if (vm.permission.groupId != undefined) {
                var group = $filter('getByProperty')('id', vm.permission.groupId, vm.groups);
                vm.permission.groupName = group.name;
            }

            // set user full name
            if (vm.permission.userId != undefined) {
                var user = $filter('getByProperty')('id', vm.permission.userId, vm.users);
                vm.permission.userFullName = user.firstName + ' ' + user.lastName + ' (' + user.username + ')';
            }

            if (form.$invalid) {
                return;
            }
            this.onSave({permission: vm.permission, form: form});
        }
    }
})();