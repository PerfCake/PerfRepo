(function() {

    angular
        .module('org.perfrepo.test.overview')
        .controller('TestOverviewController', TestOverviewController);

    function TestOverviewController(testService, testModalService, pfViewUtils, $state) {
        var vm = this;
        vm.createTest = createTest;
        vm.editTest = editTest;
        vm.removeTest = removeTest;
        vm.updateSearch = updateSearch;

        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = {
            limit: 5,
            offset: 0,
            orderBy: 'NAME_ASC'
        };

        // ******************* filter config
        vm.filterConfig = {
            fields: [
                {
                    id: 'name',
                    title:  'Name',
                    placeholder: 'Filter by Name...',
                    filterType: 'text'
                },
                {
                    id: 'uid',
                    title:  'Uid',
                    placeholder: 'Filter by Uid...',
                    filterType: 'text'
                },
                {
                    id: 'group',
                    title:  'Group',
                    placeholder: 'Filter by Group...',
                    filterType: 'text'
                }
            ],
            appliedFilters: [],
            onFilterChange: filterChange
        };

        var views = [pfViewUtils.getListView(), pfViewUtils.getCardView()];
        vm.viewsConfig = {
            views: views,
            onViewSelect: viewSelected,
            currentView: views[0].id
        };

        // ******************* sort config
        var sortFields = [
            {
                id: 'name',
                title:  'Name',
                sortType: 'alpha'
            },
            {
                id: 'uid',
                title: 'Uid',
                sortType: 'alpha'
            },
            {
                id: 'group',
                title: 'Group',
                sortType: 'alpha'
            }
        ];

        vm.sortConfig = {
            fields: sortFields,
            onSortChange: sortChange,
            currentField: sortFields[0],
            isAscending: true

        };

        vm.toolbarConfig = {
            viewsConfig: vm.viewsConfig,
            filterConfig: vm.filterConfig,
            sortConfig: vm.sortConfig
        };

        vm.listConfig = {
            selectionMatchProp: 'name',
            checkDisabled: false
        };

        updateSearch();

        function createTest() {
            var modalInstance = testModalService.createTest();

            modalInstance.result.then(function (id) {
                $state.go('app.testDetail', {id: id});
            });
        }

        function editTest(id) {
            var modalInstance = testModalService.editTest(id);

            modalInstance.result.then(function () {
                $state.go('app.testDetail', {id: id});
            });
        }

        function removeTest(id) {
            alert("Not yet implemented");
        }

        function updateSearch() {
            update();
        }

        function update() {
            vm.searchParams.offset = ((vm.currentPage - 1) * vm.searchParams.limit) || 0;

            return testService.search(vm.searchParams).then(function(result){
                vm.items = result.data;
                vm.filterConfig.resultsCount = result.totalCount;
                vm.pagination.pageCount = result.pageCount;
                vm.pagination.currentPage = result.currentPage;
            });
        }

        function applyFilters(filters) {
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
        }

        function filterChange(filters) {
            applyFilters(filters);
            updateSearch();
        }

        function viewSelected(viewId) {
        }

        function sortChange (sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? "_ASC" : "_DESC");
            updateSearch();
        }
    }
})();