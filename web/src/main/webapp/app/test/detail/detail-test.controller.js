/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail', [

        ])
        .controller('DetailTestController', DetailTestController);

    function DetailTestController(test, $modal, testService, $scope) {
        var vm = this;
        vm.test = test;
        vm.addMetric = addMetric;
        vm.editMetric = editMetric;
        vm.removeMetric = removeMetric;
        vm.addAlert = addAlert;
        vm.editAlert = editAlert;
        vm.removeAlert = removeAlert;

        function addAlert() {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/create_modal/create-alert.view.html',
                controller: 'CreateAlertController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    metrics: function() {
                        return vm.test.metrics;
                    },
                    testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function editAlert(alert) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/edit_modal/edit-alert.view.html',
                controller: 'EditAlertController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    metrics: function() {
                        return vm.test.metrics;
                    },
                    alert: function (alertService) {
                        return alertService.getById(alert.id);
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function removeAlert(alert) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/remove_modal/remove-alert.view.html',
                controller: 'RemoveAlertController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    alert: function() {
                        return alert;
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function addMetric() {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/metric/create_modal/create-metric.view.html',
                controller: 'CreateMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function editMetric(metric) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/metric/edit_modal/edit-metric.view.html',
                controller: 'EditMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    metric: function (metricService) {
                        return metricService.getById(metric.id);
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function removeMetric(metric) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/metric/remove_modal/remove-metric.view.html',
                controller: 'RemoveMetricController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    metric: function() {
                        return metric;
                    },
                    testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                // update test detail
                testService.getById(vm.test.id).then(function(response) {
                    vm.test = response;
                });
            }, function () {
                console.log('Modal dismissed');
            });
        }
    }
})();