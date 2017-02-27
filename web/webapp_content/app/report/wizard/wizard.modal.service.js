(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .service('wizardModalService', WizardModalService);

    function WizardModalService($uibModal) {

        return {
            createPermission: createPermission,
            editPermission: editPermission,
            removePermission: removePermission
        };

        function removePermission() {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/components/permissions/remove_modal/remove-permission.view.html',
                controller: 'RemovePermissionController',
                controllerAs: 'vm',
                size: 'sm'
            });
        }

        function createPermission() {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/components/permissions/create_modal/create-permission.view.html',
                controller: 'CreatePermissionController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    }
                }
            });
        }

        function editPermission(permission) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/components/permissions/create_modal/create-permission.view.html',
                controller: 'EditPermissionController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    _permission: function () {
                        return angular.copy(permission);
                    }
                }
            });
        }
    }
})();