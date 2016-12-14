(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.metric')
        .service('metricService', MetricService);

    function MetricService(API_URL, $http, $resource) {
        var Metric = $resource(
            API_URL + '/metrics/:id',
            {id: '@id'});

        return {
            getById: getById,
            getAll: getAll,
            create: create,
            remove: remove,
            update: update,
            getComparators: getComparators
        };

        function getById(id) {
            return Metric.get({id: id}).$promise;
        }

        function getAll() {
            return Metric.query().$promise;
        }

        function create(metric, testId) {
            return $http.post(API_URL + '/tests/' + testId + '/metric-addition', metric);
        }

        function remove(metricId, testId) {
            return $http.post(API_URL + '/tests/' + testId + '/metric-removal/' + metricId);
        }

        function update(metric) {
            return $http.put(API_URL + '/metrics/', metric);
        }

        function getComparators() {
            return [{'name':'HB', 'text': 'Higher better'}, {'name':'LB', 'text': 'Lower better'}];
        }
    }
})();