(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionListView', {
            bindings: {
                items: '<',
                onUpdateList: '&'
            },
            controller: TestExecutionListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/test_execution_list_view/test-execution-list-view.view.html'
        });

    function TestExecutionListViewController($state,  $templateCache, testExecutionModalService) {
        var vm = this;
        $templateCache.put('test-execution-detail-button-template', '<span class="fa fa-pencil-square-o"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.menuActions = getMenuActions();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                selectionMatchProp: 'id'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Test execution detail',
                    include: 'test-execution-detail-button-template',
                    title: 'Go to test execution detail page',
                    class: 'btn btn-default',
                    actionFn: showTestExecutionDetailAction
                }
            ];
        }

        function getMenuActions() {
            return [
                {
                    name: 'Edit test execution',
                    title: 'Edit test execution',
                    actionFn: editTestExecutionAction
                },
                {
                    isSeparator: true
                },
                {
                    name: 'Delete test execution',
                    title: 'Delete test execution',
                    actionFn: deleteTestExecutionAction
                }
            ];
        }

        function showTestExecutionDetailAction(action, item) {
            $state.go('app.testExecutionDetail', {id: item.id});
        }

        function editTestExecutionAction(action, item) {
            var modalInstance = testExecutionModalService.editTestExecution(item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteTestExecutionAction(action, item) {
            alert('Delete test execution - not yet implemented.');
        }
    }
})();