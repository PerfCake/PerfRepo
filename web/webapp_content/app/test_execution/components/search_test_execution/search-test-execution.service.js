(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .service('testExecutionSearchService', TestExecutionModalService);

    function TestExecutionModalService(pfViewUtils) {

        return {
            getToolbarConfig: getToolbarConfig,
            getSearchOffset: getSearchOffset
        };

        function getToolbarConfig(onFilterChange, onSortChange, onViewSelect) {
            return {
                viewsConfig: prepareViewConfig(onViewSelect),
                filterConfig: prepareFilterConfig(onFilterChange),
                sortConfig: prepareSortConfig(onSortChange)
            };
        }

        function prepareViewConfig(onViewSelect) {
            var views = [pfViewUtils.getListView(), pfViewUtils.getCardView()];

            return {
                views: views,
                onViewSelect: onViewSelect,
                currentView: views[0].id
            };
        }

        function prepareSortConfig(onSortChange) {
            var sortFields = [
                {
                    id: 'name',
                    title:  'Test name',
                    sortType: 'alpha'
                },
                {
                    id: 'uid',
                    title: 'Test uid',
                    sortType: 'alpha'
                },
                {
                    id: 'date',
                    title: 'Started date',
                    sortType: 'numeric'
                }
            ];

            return  {
                fields: sortFields,
                onSortChange: onSortChange,
                currentField: sortFields[0],
                isAscending: true
            };
        }

        function prepareFilterConfig(onFilterChange) {
            var fields = [
                {
                    id: 'name',
                    title:  'Test name',
                    placeholder: 'Filter by Test name...',
                    filterType: 'text'
                },
                {
                    id: 'uid',
                    title:  'Test uid',
                    placeholder: 'Filter by Test uid...',
                    filterType: 'text'
                },
                {
                    id: 'tag',
                    title:  'Tag',
                    placeholder: 'Filter by Tag...',
                    filterType: 'text'
                }
            ];

            return {
                fields: fields,
                appliedFilters: [],
                onFilterChange: onFilterChange
            }
        }

        function getSearchOffset(currentPage, limit) {
            return ((currentPage - 1) * limit) || 0;
        }
    }
})();