(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .service('testService', TestService);

    function TestService($http, $resource, API_TEST_URL) {

        var testResource = $resource(API_TEST_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                    isArray: false,
                    url:  API_TEST_URL,
                    params: {}
                },
                'save': {
                    method: 'POST',
                    interceptor: {
                        response: function(response) {
                            return response.headers("Location").split("/").pop();
                        }
                    }
                }
            });

        return {
            search: search,
            getById: getById,
            save: save,
            update: update,
            remove: remove

        };

        function search(searchParams){
            return $http.post(API_TEST_URL + '/search', searchParams).then(function(response) {
                return {
                    data : response.data,
                    totalCount : parseInt(response.headers('X-Pagination-Total-Count')),
                    pageCount : parseInt(response.headers('X-Pagination-Page-Count')),
                    currentPage : parseInt(response.headers('X-Pagination-Current-Page'))
                };
            });
        }

        function getById(id) {
            return testResource.get({id: id}).$promise;
        }

        function save(test) {
            return testResource.save(test).$promise;
        }

        function update(test) {
            return testResource.update(test).$promise;
        }

        function remove(test) {
            return testResource.delete(test).$promise;
        }
    }
})();