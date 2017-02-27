(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('WizardReportController', WizardReportController);

    function WizardReportController(_data, _state, reportService, $scope, $state) {
        var vm = this;
        vm.newReport = (_data.id == undefined);
        vm.backCallback = backCallback;
        vm.nextCallback = nextCallback;
        vm.onCancel = onCancel;
        vm.wizardReady = true;
        vm.wizardDone = false;

        vm.data = _data;
        vm.state = _state;

        initialize();

        function initialize() {
            if (vm.newReport) {
                vm.pageTitle = 'New report wizard';
                vm.data = {
                    type: 'TABLE_COMPARISON',
                    groups: [{
                        name: 'New group'
                    }]
                };
            } else {
                vm.pageTitle = 'Edit report wizard';
            }

            $scope.$on("wizard:stepChanged", function (e, parameters) {
                console.log("changed");
                if (parameters.step.stepId === 'permissions') {
                    vm.nextButtonTitle = "Save";
                } else {
                    vm.nextButtonTitle = "Next >";
                }
            });
        }

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

            if (step.stepId === 'permissions') {
                save();
                return false
            }

            return true;
        }

        function onCancel() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            if (vm.data.id == undefined) {
                createReport();
            } else {
                updateReport();
            }
        }

        function createReport() {
            reportService.create(vm.data).then(function (id) {
                $state.go('app.reportDetail', {id: id});
            }, function(errorResponse) {
                //validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function updateReport() {
            reportService.update(vm.data).then(function (report) {
                $state.go('app.reportDetail', {id: report.id});
            }, function(errorResponse) {
                //validationHelper.setFormErrors(errorResponse, form);
            });
        }
    }
})();