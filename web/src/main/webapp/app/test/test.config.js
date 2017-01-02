(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.testDetail', {
                url: 'test/detail/:id',
                templateUrl: 'app/test/detail/detail-test.view.html',
                controller: 'DetailTestController',
                controllerAs: 'vm',
                resolve: {
                    _test: function(testService, $stateParams) {
                        return testService.getById($stateParams.id)
                    }
                }
            })

            .state('app.testSearch', {
                url: 'test/search',
                templateUrl: 'app/test/search/search-test.view.html',
                controller: 'SearchTestController',
                controllerAs: 'vm'
            });
    }
})();