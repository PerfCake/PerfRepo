(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .service('wizardService', WizardService);

    function WizardService() {

        return {
            getPermissionOptions: getPermissionOptions
        };

        function getPermissionOptions() {
            return [
                {level: 'USER', types: ['READ', 'WRITE']},
                {level: 'GROUP', types: ['READ', 'WRITE']},
                {level: 'PUBLIC', types: ['READ']}
            ];
        }
    }
})();