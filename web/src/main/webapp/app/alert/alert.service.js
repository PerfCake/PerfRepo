(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert')
        .service('alertService', AlertService);

    function AlertService(API_ALERT_URL, $resource) {
        var AlertResource = $resource(API_ALERT_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                    isArray: false,
                    url: API_ALERT_URL,
                    params: {}
                }
            });

        return {
            getById: getById,
            getAll: getAll,
            create: create,
            remove: remove,
            update: update
        };

        function getById(id) {
            return AlertResource.get({id: id}).$promise;
        }

        function getAll() {
            return AlertResource.query().$promise;
        }

        function create(alert) {
            return AlertResource.save(alert).$promise;
        }

        function remove(id) {
            return AlertResource.remove({id: id}).$promise;
        }

        function update(alert) {
            return AlertResource.update(alert).$promise;
        }
    }
})();