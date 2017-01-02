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

    function SearchTestController(testService, testSearchService) {
        var vm = this;

        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = {
            limit: 5,
            offset: 0,
            orderBy: 'NAME_ASC'
        };
        vm.toolbarConfig = testSearchService.getToolbarConfig(filterChanged, sortChanged, viewChanged);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // call initial search
        updateSearch();

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

        function viewChanged() {

        }

        function updateSearch() {
            return testService.search(vm.searchParams).then(function(result){
                vm.items = result.data;
                vm.toolbarConfig.filterConfig.resultsCount = result.totalCount;
                vm.pagination.pageCount = result.pageCount;
                vm.pagination.currentPage = result.currentPage;
            });
        }
    }
})();