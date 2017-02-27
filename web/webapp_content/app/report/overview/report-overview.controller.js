(function() {

    angular
        .module('org.perfrepo.report')
        .controller('ReportOverviewController', ReportOverviewController);

    function ReportOverviewController(_initialSearchResult, reportModalService, $state) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.newReport = newReport;

        function newReport() {
            var modalInstance = reportModalService.newReport();

            modalInstance.result.then(function (id) {
                $state.go('app.reportDetail', {id: id});
            });
        }
    }
})();