(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('RemovePermissionController', RemovePermissionController);

    function RemovePermissionController($uibModalInstance) {
        var vm = this;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            $uibModalInstance.close();
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();