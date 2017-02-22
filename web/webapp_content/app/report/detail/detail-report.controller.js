(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .controller('DetailReportController', DetailReportController);

    function DetailReportController(_report, reportService, reportModalService, $state) {
        var vm = this;
        vm.report = _report;
        vm.editReport = editReport;
        vm.removeReport = removeReport;
        vm.updateDetail = updateDetail;

        function editReport() {
            var modalInstance = reportModalService.editReport(vm.report.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeReport() {
            var modalInstance = reportModalService.removeReport(vm.report);

            modalInstance.result.then(function () {
                $state.go('app.reportOverview');
            });
        }

        function updateDetail() {
            reportService.getById(vm.report.id).then(function(response) {
                vm.report = response;
            });
        }
    }
})();