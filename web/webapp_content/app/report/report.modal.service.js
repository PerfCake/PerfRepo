(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportModalService', ReportModalService);

    function ReportModalService($uibModal) {

        return {
            newReport: newReport,
            editReport: editReport,
            newPreparedReport: newPreparedReport
        };

        function newReport() {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                size: 'lg',
                resolve: {
                    _data: function() {
                        return {};
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: true
                        }
                    }
                }
            });
        }

        function editReport(id) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                size: 'lg',
                resolve: {
                    _data: function(reportService) {
                        return reportService.getById(id);
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: false
                        }
                    }
                }
            });
        }
        
        function newPreparedReport(data) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                size: 'lg',
                resolve: {
                    _data: function() {
                        return data;
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: false
                        }
                    }
                }
            });
        }
    }
})();