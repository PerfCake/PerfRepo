(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .controller('SupportController', SupportController);

    function SupportController($uibModalInstance) {
        var vm = this;
        vm.close = close;

        function close() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();