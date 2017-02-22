(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportModalService', ReportModalService);

    function ReportModalService($uibModal) {

        return {
            createReport: createReport
        };

        function createReport() {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                size: 'lg'
            });
        }
    }
})();