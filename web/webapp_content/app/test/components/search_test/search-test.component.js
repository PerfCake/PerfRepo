(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('searchTest', {
            bindings: {
                initialSearchResult: '='
            },
            controller: SearchTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/search_test/search-test.view.html'
        });

    function SearchTestController(testService, testSearchService, $rootScope) {
        var vm = this;

        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = testService.getDefaultSearchParams();

        vm.toolbarConfig = testSearchService.getToolbarConfig(filterChanged, sortChanged);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = testSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            vm.searchParams.nameFilters = [];
            vm.searchParams.uidFilters = [];
            vm.searchParams.groupFilters = [];

            filters.forEach(function(filter) {
                if(filter.id == 'name') {
                    vm.searchParams.nameFilters.push(filter.value);
                } else if(filter.id == 'uid') {
                    vm.searchParams.uidFilters.push(filter.value);
                } else if(filter.id == 'group') {
                    vm.searchParams.groupFilters.push(filter.value);
                }
            });
            updateSearch();
        }

        function sortChanged(sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? "_ASC" : "_DESC");
            updateSearch();
        }

        function updateSearch() {
            $rootScope.ngProgress.start();

            testService.search(vm.searchParams).then(function(searchResult) {
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