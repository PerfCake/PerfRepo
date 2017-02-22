(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('WizardReportController', WizardReportController);

    function WizardReportController($scope, $uibModalInstance) {
        var vm = this;
        vm.wizardTitle = "New report";
        vm.nextButtonTitle = "Next >";
        vm.backCallback = backCallback;
        vm.nextCallback = nextCallback;
        vm.onFinish = onFinish;
        vm.onCancel = onCancel;
        vm.wizardReady = true;
        vm.wizardDone = false;

        vm.data = {type: 'TABLE_COMPARISON'};

        $scope.$on("wizard:stepChanged", function (e, parameters) {
            if (parameters.step.stepId === 'permissions') {
                vm.nextButtonTitle = "Save";
            } else {
                vm.nextButtonTitle = "Next >";
            }
        });

        function backCallback(step) {
            console.log("back callback");
            console.log(step);
            return true;
        }

        function nextCallback(step) {
            console.log("next callback");
            console.log(step);

            if (step.stepId === 'type') {
                if (vm.data.type == undefined) {
                    return false;
                }
            }

            return true;
        }

        function onFinish() {
            console.log("on finish");
            return true;
        }

        function onCancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();