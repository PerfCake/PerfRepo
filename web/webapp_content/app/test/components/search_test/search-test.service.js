(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .service('testSearchService', TestModalService);

    function TestModalService() {

        return {
            getToolbarConfig: getToolbarConfig,
            getSearchOffset: getSearchOffset
        };

        function getToolbarConfig(onFilterChange, onSortChange) {
            return {
                filterConfig: prepareFilterConfig(onFilterChange),
                sortConfig: prepareSortConfig(onSortChange)
            };
        }

        function prepareSortConfig(onSortChange) {
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