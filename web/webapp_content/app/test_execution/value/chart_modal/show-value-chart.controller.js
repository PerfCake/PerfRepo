(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('ShowValueChartController', ShowValueChartController);

    function ShowValueChartController(_values, _parameter, _metric, $modalInstance) {
        var vm = this;

        vm.cancel = cancel;

        vm.data = [{
            key: "Cumulative Return",
            values: [
                { x: 10, y: 10},
                { x: 12, y: 16},
                { x: 14, y: 26},
                { x: 20, y: 12}
            ]
        }];

        vm.options = {
            chart: {
                type: 'lineChart',
                height: 450,
                margin : {
                    top: 40,
                    right: 20,
                    bottom: 40,
                    left: 55
                },
                x: function(d){ return d.x; },
                y: function(d){ return d.y; },
                valueFormat: function(d){
                    return d3.format(',.4f')(d);
                },
                useInteractiveGuideline: true,
                xAxis: {
                    axisLabel: 'Time (ms)'
                },
                yAxis: {
                    axisLabel: 'Voltage (v)',
                    tickFormat: function(d){
                        return d3.format('.02f')(d);
                    },
                    axisLabelDistance: -10
                }
            }
        };

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();