(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.metric.remove')
        .controller('RemoveMetricController', RemoveMetricController);

    function RemoveMetricController(metricService, $modalInstance, metric, testId) {
        var vm = this;
        vm.metric = metric;
        vm.testId = testId;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            metricService.remove(vm.metric.id, vm.testId).then(function () {
                $modalInstance.close(vm.metric);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();