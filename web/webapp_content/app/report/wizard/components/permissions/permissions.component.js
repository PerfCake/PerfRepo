(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportPermissionsStep', {
            bindings: {
                permissions: '='
            },
            controller: ReportPermissionsWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/permissions/permissions.view.html'
        });

    function ReportPermissionsWizardStep() {
        var vm = this;
    }
})();