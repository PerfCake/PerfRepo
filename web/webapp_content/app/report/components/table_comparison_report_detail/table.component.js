(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('tableComparisonReportTable', {
            bindings: {
                table: '<'
            },
            controller: TableComparisonReportTableController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/table_comparison_report_detail/table.view.html'
        });

    function TableComparisonReportTableController() {
        var vm = this;
    }
})();