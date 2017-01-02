(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric')
        .service('metricService', MetricService);

    function MetricService(API_METRIC_URL, API_TEST_URL, $http, $resource) {
        var MetricResource = $resource(API_METRIC_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT', isArray: false, url: API_METRIC_URL, params: {}
                }
            });

        return {
            getById: getById,
            getAll: getAll,
            create: create,
            remove: remove,
            update: update,
            getComparators: getComparators
        };

        function getById(id) {
            return MetricResource.get({id: id}).$promise;
        }

        function getAll() {
            return MetricResource.query().$promise;
        }

        function create(metric, testId) {
            return $http.post(API_TEST_URL + '/' + testId + '/metric-addition', metric).then(function(response) {
                return response.data;
            });
        }

        function remove(metricId, testId) {
            return $http.post(API_TEST_URL + '/' + testId + '/metric-removal/' + metricId).then(function(response) {
                return response.data;
            });
        }

        function update(metric) {
            return MetricResource.update(metric).$promise;
        }

        function getComparators() {
            return [{'name':'HIGHER_BETTER', 'text': 'Higher better'}, {'name':'LOWER_BETTER', 'text': 'Lower better'}];
        }
    }
})();