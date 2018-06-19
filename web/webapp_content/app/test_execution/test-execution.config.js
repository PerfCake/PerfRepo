(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.testExecutionDetail', {
                url: 'test-execution/detail/:id',
                templateUrl: 'app/test_execution/detail/detail-test-execution.view.html',
                controller: 'DetailTestExecutionController',
                controllerAs: 'vm',
                resolve: {
                    _testExecution: function(testExecutionService, $stateParams) {
                        return testExecutionService.getById($stateParams.id);
                    },
                    _test: function(testService, testExecutionService, $stateParams) {
                        var testExecution = testExecutionService.getById($stateParams.id);
                        return testExecution.then(function(response) {
                            return testService.getById(response.test.id);
                        })
                    }
                }
            })

            .state('app.testExecutionOverview', {
                url: 'test-execution/search',
                templateUrl: 'app/test_execution/overview/test-execution-overview.view.html',
                controller: 'TestExecutionOverviewController',
                controllerAs: 'vm',
                params: {
                    uidFilter: null
                },
                resolve: {
                    _searchCriteria: function(testExecutionService, $stateParams) {
                        if ($stateParams.uidFilter) {
                            return testExecutionService.searchParamsForTestUID($stateParams.uidFilter, 20);
                        } else {
                            return testExecutionService.getSearchCriteria();
                        }
                    },
                    _initialSearchResult: function(testExecutionService, _searchCriteria) {
                        return testExecutionService.search(_searchCriteria);
                    }
                }
            });
    }
})();