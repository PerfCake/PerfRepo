(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('searchTest', {
            bindings: {

            },
            controller: SearchTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/search_test/search-test.view.html'
        });

    function SearchTestController() {
        var vm = this;

    }
})();