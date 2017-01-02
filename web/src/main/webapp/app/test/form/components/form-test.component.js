(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.form')
        .component('testForm', {
            bindings: {
                groups: '=',
                test: '=',
                onSave: '&'
            },
            controller: FormTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/form/components/form-test.view.html'
        });

    function FormTestController($scope) {
        var vm = this;
        vm.save = save;
        vm.submitButtonText = vm.test.id != undefined ? 'Update' : 'Create';

        $scope.$watch('vm.test.group', function() {

        });

        function save(form) {
            if (form.$invalid) {
                return;
            }
            this.onSave({test: vm.test, form:form});
        }

    }
})();