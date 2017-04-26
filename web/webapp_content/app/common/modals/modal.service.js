(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .service('modalService', ModalService);

    function ModalService($uibModal) {

        return {
            getSupport: getSupport
        };

        function getSupport() {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/common/modals/support_modal/support.view.html',
                controller: 'SupportController',
                controllerAs: 'vm',
                size: 'md'
            });
        }
    }
})();