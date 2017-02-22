(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportTypeStep', {
            bindings: {
                reportType: '='
            },
            controller: ReportTypeWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/report_type/report-type.view.html'
        });

    function ReportTypeWizardStep() {
        var vm = this;

    }
})();