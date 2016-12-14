(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert')
        .service('alertService', AlertService);

    function AlertService(API_URL, $http, $resource) {
        var Alert = $resource(
            API_URL + '/alerts/:id',
            {id: '@id'},
            {
                'update': {method: 'PUT', isArray: false, url:  API_URL + '/alerts', params: {}}
            });

        return {
            getById: getById,
            getAll: getAll,
            create: create,
            remove: remove,
            update: update
        };

        function getById(id) {
            return Alert.get({id: id}).$promise;
        }

        function getAll() {
            return Alert.query().$promise;
        }

        function create(alert) {
            return Alert.save(alert).$promise;
        }

        function remove(id) {
            return Alert.remove({id: id}).$promise;
        }

        function update(alert) {
            return Alert.update(alert).$promise;
        }
    }
})();