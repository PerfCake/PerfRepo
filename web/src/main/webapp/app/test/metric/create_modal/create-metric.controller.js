(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.metric.create')
        .controller('CreateMetricController', CreateMetricController);

    function CreateMetricController(metricService, comparators, $modalInstance, testId) {
        var vm = this;
        vm.comparators = comparators;
        vm.metric = {comparator: vm.comparators[0].name};
        vm.testId = testId;
        vm.save = save;
        vm.cancel = cancel;

        function save(metric) {
            metricService.create(metric, testId).then(function () {
                $modalInstance.close(metric);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();