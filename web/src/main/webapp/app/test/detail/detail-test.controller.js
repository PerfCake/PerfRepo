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

    function DetailTestController(_test, testService, testModalService, $modal) {
        var vm = this;
        vm.test = _test;
        vm.editTest = editTest;
        vm.createTestExecution = createTestExecution;
        vm.showTestExecutions = showTestExecutions;
        vm.subscribeAlerts = subscribeAlerts;
        vm.addMetric = addMetric;
        vm.editMetric = editMetric;
        vm.removeMetric = removeMetric;
        vm.addAlert = addAlert;
        vm.editAlert = editAlert;
        vm.removeAlert = removeAlert;

        function createTestExecution(testId) {
            alert("Not yet implemented.");
        }

        function showTestExecutions(testId) {
            alert("Not yet implemented.");
        }

        function subscribeAlerts(testId) {
            alert("Not yet implemented.");
        }

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
                    _metrics: function() {
                        return vm.test.metrics;
                    },
                    _testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
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
                    _metrics: function() {
                        return vm.test.metrics;
                    },
                    _alert: function (alertService) {
                        return alertService.getById(alert.id);
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
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
                    _alert: function() {
                        return alert;
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function addMetric() {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/create_modal/create-metric.view.html',
                controller: 'CreateMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    _testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function editMetric(metric) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/edit_modal/edit-metric.view.html',
                controller: 'EditMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    _metric: function (metricService) {
                        return metricService.getById(metric.id);
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function removeMetric(metric) {
            var modalInstance = $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/remove_modal/remove-metric.view.html',
                controller: 'RemoveMetricController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _metric: function() {
                        return metric;
                    },
                    _testId: function () {
                        return vm.test.id;
                    }
                }
            });

            modalInstance.result.then(function () {
                updateDetail();
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function editTest(id) {
            var modalInstance = testModalService.editTest(id);

            modalInstance.result.then(function () {
                updateDetail();
            }, function () {
                console.log('Modal dismissed');
            });
        }

        function updateDetail() {
            testService.getById(vm.test.id).then(function(response) {
                vm.test = response;
            });
        }
    }
})();