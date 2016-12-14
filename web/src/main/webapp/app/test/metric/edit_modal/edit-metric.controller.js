(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.metric.edit')
        .controller('EditMetricController', EditMetricController);

    function EditMetricController(metricService, comparators, $modalInstance, metric) {
        var vm = this;
        vm.comparators = comparators;
        vm.metric = metric;
        vm.save = save;
        vm.cancel = cancel;

        function save(metric) {
            metricService.update(metric).then(function () {
                $modalInstance.close(metric);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();