(function() {
    'use strict';

    angular.module('org.perfrepo.report')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.reportDetail', {
                url: 'report/detail/:id',
                templateUrl: 'app/report/detail/detail-report.view.html',
                controller: 'DetailReportController',
                controllerAs: 'vm',
                resolve: {
                    _report: function(reportService, $stateParams) {
                        return reportService.getById($stateParams.id);
                    }
                }
            })

            .state('app.reportOverview', {
                url: 'report/search',
                templateUrl: 'app/report/overview/report-overview.view.html',
                controller: 'ReportOverviewController',
                controllerAs: 'vm',
                resolve: {
                    _initialSearchResult: function(reportService) {
                        return reportService.defaultSearch();
                    }
                }
            });
    }
})();