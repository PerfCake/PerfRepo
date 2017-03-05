(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportPermissionsStep', {
            bindings: {
                data: '=',
                users: '<',
                groups: '<',
                validate: '='
            },
            controller: ReportPermissionsWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/permissions/permissions.view.html'
        });

    function ReportPermissionsWizardStep(wizardService, validationHelper, $filter) {
        var vm = this;
        vm.options = wizardService.getPermissionOptions();
        vm.permissionTypes = permissionTypes;
        vm.addPermission = addPermission;
        vm.removePermission = removePermission;
        vm.validate = validate;

        function permissionTypes(permissionLevel) {
            var option = $filter('getByProperty')('level', permissionLevel, vm.options);
            if (option == null) {
                return [];
            } else {
                return option.types;
            }
        }

        function addPermission() {
            if (vm.data.permissions == undefined) {
                vm.data.permissions = [];
            }
            vm.data.permissions.push({});
        }

        function removePermission(index) {
            vm.data.permissions.splice(index, 1);
        }

        function validate() {
            wizardService.validateReportPermissionStep(vm.data).then(function() {
                return true;
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, vm.wizardPermissionStep);
                return false;
            });
        }
    }
})();