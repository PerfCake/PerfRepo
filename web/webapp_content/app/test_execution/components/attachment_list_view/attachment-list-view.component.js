(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionAttachmentListView', {
            bindings: {
                attachments: '<',
                onUpdateList: '&'
            },
            controller: TestExecutionAttachmentsListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/attachment_list_view/attachment-list-view.view.html'
        });

    function TestExecutionAttachmentsListViewController(testExecutionAttachmentModalService, $templateCache,
                                                        testExecutionService) {
        var vm = this;

        vm.items = [
            {
                id: 1,
                filename: 'log',
                media: 'text',
                link: downloadAttachmentLink(1)
            },
            {
                id: 2,
                filename: 'results',
                media: 'text',
                link: downloadAttachmentLink(2)
            }
        ];


        $templateCache.put('attachment-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
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
                    name: 'Delete',
                    include: 'attachment-delete-button-template',
                    title: 'Delete attachment',
                    actionFn: deleteAttachmentAction
                }
            ];
        }

        function deleteAttachmentAction(action, item) {
            var modalInstance = testExecutionAttachmentModalService.removeAttachment(item.filename, item.id);
            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function downloadAttachmentLink(id) {
            return testExecutionService.downloadAttachmentLink(id);
        }
    }
})();