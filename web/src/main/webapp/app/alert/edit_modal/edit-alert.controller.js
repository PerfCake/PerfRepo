(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.edit')
        .controller('EditAlertController', EditAlertController);

    function EditAlertController(alertService, metrics, $modalInstance, alert) {
        var vm = this;
        vm.metrics = metrics;
        vm.alert = alert;
        vm.save = save;
        vm.cancel = cancel;

        function save(alert) {
            alertService.update(alert).then(function () {
                $modalInstance.close(alert);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();