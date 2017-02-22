(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportService', ReportService);

    function ReportService($http, $resource, API_REPORT_URL) {
        var reportResource = $resource(API_REPORT_URL + '/:id',
            {
                id: '@id'
            });

        return {
            search: search,
            defaultSearch: defaultSearch,
            getById: getById,
            getDefaultSearchParams: getDefaultSearchParams

        };

        function search(searchParams) {
            return $http.post(API_REPORT_URL + '/search', searchParams).then(function(response) {
                return {
                    data : response.data,
                    totalCount : parseInt(response.headers('X-Pagination-Total-Count')),
                    pageCount : parseInt(response.headers('X-Pagination-Page-Count')),
                    currentPage : parseInt(response.headers('X-Pagination-Current-Page'))
                };
            });
        }

        function defaultSearch() {
            return search(getDefaultSearchParams());
        }

        function getDefaultSearchParams() {
            return {
                limit: 10,
                offset: 0,
                orderBy: 'NAME_ASC'
            };
        }

        function getById(id) {
            return reportResource.get({id: id}).$promise;
        }
    }
})();