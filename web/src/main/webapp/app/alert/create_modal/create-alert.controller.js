(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.create')
        .controller('CreateAlertController', CreateAlertController);

    function CreateAlertController($scope, alertService, metrics, $modalInstance, testId) {
        var vm = this;
        vm.metrics = metrics;
        vm.alert = {testId: testId};
        vm.save = save;
        vm.cancel = cancel;

        if (vm.metrics != undefined && vm.metrics.length > 0) {
            vm.alert.metric = vm.metrics[0];
        }

        function save(alert) {
            alertService.create(alert).then(function () {
                $modalInstance.close(alert);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();