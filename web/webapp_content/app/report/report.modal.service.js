(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportModalService', ReportModalService);

    function ReportModalService($uibModal) {

        return {
            removeReport: removeReport
        };

        function removeReport(report) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/remove/remove-report.view.html',
                controller: 'RemoveReportController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _report: function () {
                        return report;
                    }
                }
            });
        }
    }
})();