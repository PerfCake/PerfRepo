(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.remove')
        .controller('RemoveAlertController', RemoveAlertController);

    function RemoveAlertController(alertService, $modalInstance, alert) {
        var vm = this;
        vm.alert = alert;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            alertService.remove(vm.alert.id).then(function () {
                $modalInstance.close(vm.alert);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();