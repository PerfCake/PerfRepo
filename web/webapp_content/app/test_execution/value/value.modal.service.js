(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .service('testExecutionValueModalService', TestExecutionValueModalService);

    function TestExecutionValueModalService($modal) {

        return {
            showChart: showChart,
            removeValue: removeValue,
            createValue: createValue,
            editValue: editValue
        };

        function removeValue(executionValuesGroup, testExecutionId, index) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/remove_modal/remove-value.view.html',
                controller: 'RemoveTestExecutionValueController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _executionValuesGroup: function() {
                        return executionValuesGroup;
                    },
                    _metric: function(metricService) {
                        return metricService.getById(executionValuesGroup.metricId);
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    },
                    _index: function() {
                        return index;
                    }
                }
            });
        }

        function createValue(executionValuesGroup, testExecutionId) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/create_modal/create-value.view.html',
                controller: 'CreateTestExecutionValueController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _executionValuesGroup: function() {
                        return executionValuesGroup;
                    },
                    _metric: function(metricService) {
                        return metricService.getById(executionValuesGroup.metricId);
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    }
                }
            });
        }

        function editValue(executionValuesGroup, testExecutionId, index) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/edit_modal/edit-value.view.html',
                controller: 'EditTestExecutionValueController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _executionValuesGroup: function() {
                        return executionValuesGroup;
                    },
                    _metric: function(metricService) {
                        return metricService.getById(executionValuesGroup.metricId);
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    },
                    _index: function() {
                        return index;
                    }
                }
            });
        }

        function showChart(values, parameter, metric) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/chart_modal/show-value-chart.view.html',
                controller: 'ShowValueChartController',
                controllerAs: 'vm',
                size: 'lg',
                resolve : {
                    _values: function() {
                        return values;
                    },
                    _parameter: function () {
                        return parameter;
                    },
                    _metric: function() {
                        return metric;
                    }
                }
            });
        }
    }
})();