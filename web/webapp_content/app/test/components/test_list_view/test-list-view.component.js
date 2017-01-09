(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('testListView', {
            bindings: {
                items: '<',
                onUpdateList: '&'
            },
            controller: TestListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/test_list_view/test-list-view.view.html'
        });

    function TestListViewController($state,  $templateCache, testModalService, testExecutionModalService) {
        var vm = this;
        $templateCache.put('test-detail-button-template', '<span class="fa fa-pencil-square-o"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.menuActions = getMenuActions();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                useExpandingRows: true,
                selectionMatchProp: 'id'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Test detail',
                    include: 'test-detail-button-template',
                    title: 'Go to test detail page',
                    class: 'btn btn-default',
                    actionFn: showTestDetailAction
                }
            ];
        }

        function getMenuActions() {
            return [
                {
                    name: 'Edit test',
                    title: 'Edit test',
                    actionFn: editTestAction
                },
                {
                    isSeparator: true
                },
                {
                    name: 'Create test execution',
                    title: 'Create test execution',
                    actionFn: createTestExecutionAction
                },
                {
                    name: 'Show test executions',
                    title: 'Show test executions',
                    actionFn: showTestExecutionsAction
                },
                {
                    isSeparator: true
                },
                {
                    name: 'Delete test',
                    title: 'Delete test',
                    actionFn: deleteTestAction
                }
            ];
        }

        function showTestDetailAction(action, item) {
            $state.go('app.testDetail', {id: item.id});
        }

        function createTestExecutionAction(action, item) {
            var modalInstance = testExecutionModalService.createTestExecution(item.id);

            modalInstance.result.then(function (id) {
                $state.go('app.testExecutionDetail', {id: id});
            });
        }

        function showTestExecutionsAction(action, item) {
            alert('Not yet implemented.');
        }

        function editTestAction(action, item) {
            var modalInstance = testModalService.editTest(item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteTestAction(action, item) {
            alert('Delete test - not yet implemented.');
        }
    }
})();