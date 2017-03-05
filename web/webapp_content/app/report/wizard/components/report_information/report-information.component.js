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

    function ReportInformationWizardStep(wizardService, validationHelper) {
        var vm = this;
        vm.validate = validate;

        function validate() {
            wizardService.validateReportInfoStep(vm.data).then(function() {
                // ok
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, vm.wizardInfoStep);
                return false;
            });
        }
    }
})();