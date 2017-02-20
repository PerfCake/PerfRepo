(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .controller('DashboardController', DashboardController);

    function DashboardController(Notifications) {
        var vm = this;
        vm.hello = "dashboard";
        vm.addError = addError;

        function addError() {
            Notifications.error("Error");
        }
    }
})();