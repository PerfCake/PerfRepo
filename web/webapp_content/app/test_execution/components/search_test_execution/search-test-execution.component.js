(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('searchTestExecution', {
            bindings: {
                initialSearchResult: '='
            },
            controller: SearchTestExecutionController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/search_test_execution/search-test-execution.view.html'
        });

    function SearchTestExecutionController(testExecutionService, testExecutionSearchService, $rootScope) {
        var vm = this;
        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = testExecutionService.getDefaultSearchParams();

        vm.toolbarConfig = testExecutionSearchService.getToolbarConfig(filterChanged, sortChanged, viewChanged);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = testExecutionSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            vm.searchParams.testNameFilters = [];
            vm.searchParams.testUidFilters = [];
            vm.searchParams.tagFilters = [];

            filters.forEach(function(filter) {
                if(filter.id == 'name') {
                    vm.searchParams.testNameFilters.push(filter.value);
                } else if(filter.id == 'uid') {
                    vm.searchParams.testUidFilters.push(filter.value);
                } else if(filter.id == 'tag') {
                    vm.searchParams.tagFilters.push(filter.value);
                }
            });
            updateSearch();
        }

        function sortChanged(sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? "_ASC" : "_DESC");
            updateSearch();
        }

        function viewChanged() {

        }

        function updateSearch() {
            $rootScope.ngProgress.start();

            testExecutionService.search(vm.searchParams).then(function(searchResult) {
                applySearchResult(searchResult);
                $rootScope.ngProgress.complete();
            });
        }

        function applySearchResult(searchResult) {
            vm.items = searchResult.data;
            vm.toolbarConfig.filterConfig.resultsCount = searchResult.totalCount;
            vm.pagination.pageCount = searchResult.pageCount;
            vm.pagination.currentPage = searchResult.currentPage;
        }
    }
})();