(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prTableComparisonReportConfiguration', {
            bindings: {
                configuration: '='
            },
            controller: TableComparisonReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/table_comparison_report/table-comparison-report.view.html'
        });

    function TableComparisonReportConfiguration() {
        var vm = this;

    }
})();