(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prMetricHistoryReportConfiguration', {
            bindings: {
                configuration: '='
            },
            controller: MetricHistoryReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/metric_history_report/metric-history-report.view.html'
        });

    function MetricHistoryReportConfiguration() {
        var vm = this;

    }
})();