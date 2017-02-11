(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionParameterListView', {
            bindings: {
                items: '<parameters',
                testExecutionId: '<',
                onUpdateList: '&'
            },
            controller: TestExecutionParameterListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/parameter_list_view/parameter-list-view.view.html'
        });

    function TestExecutionParameterListViewController(testExecutionParameterModalService, $templateCache, $scope) {
        var vm = this;
        $templateCache.put('parameter-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('parameter-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                selectionMatchProp: 'name'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Edit',
                    include: 'parameter-edit-button-template',
                    title: 'Edit parameter',
                    actionFn: editParameterAction
                },
                {
                    name: 'Delete',
                    include: 'parameter-delete-button-template',
                    title: 'Delete parameter',
                    actionFn: deleteParameterAction
                }
            ];
        }

        function editParameterAction(action, item) {
            var modalInstance = testExecutionParameterModalService.editParameter(item, vm.items, vm.testExecutionId);
            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteParameterAction(action, item) {
            var modalInstance = testExecutionParameterModalService.removeParameter(item, vm.items, vm.testExecutionId);
            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }
    }
})();