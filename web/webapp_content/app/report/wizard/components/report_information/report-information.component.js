(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportInformationStep', {
            bindings: {
                data: '='
            },
            controller: ReportInformationWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/report_information/report-information.view.html'
        });

    function ReportInformationWizardStep() {
        var vm = this;
    }
})();