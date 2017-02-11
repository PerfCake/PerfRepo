(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .component('testExecutionValueForm', {
            bindings: {
                valueObject: '<',
                executionValuesGroup: '<',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormTestExecutionValueController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/value/form/components/form-value.view.html'
        });

    function FormTestExecutionValueController($filter) {
        var vm = this;
        vm.removeParameter = removeParameter;
        vm.addParameter = addParameter;
        vm.save = save;
        init();

        function save(form) {
            if (form.$invalid) {
                return;
            }

            this.onSave({valueObject: vm.valueObject, form: form});
        }

        function removeParameter(index) {
            vm.valueObject.parameters.splice(index, 1);
        }

        function addParameter() {
            vm.valueObject.parameters.push({});
        }

        function init() {
            var parameterNames = vm.executionValuesGroup.parameterNames;
            var missingParameterNames = [];

            if (!vm.valueObject.parameters) {
                vm.valueObject.parameters = [];
            }

            angular.forEach(parameterNames, function(parameterName) {
                var parameterObject = $filter('getByProperty')('name', parameterName, vm.valueObject.parameters);
                if (!parameterObject) {
                    missingParameterNames.push({name: parameterName});
                }
            });

            vm.valueObject.parameters = vm.valueObject.parameters.concat(missingParameterNames);
        }
    }
})();