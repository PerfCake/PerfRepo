(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionCardView', {
            bindings: {
                items: '<'
            },
            controller: TestExecutionCardViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/test_execution_card_view/test-execution-card-view.view.html'
        });

    function TestExecutionCardViewController() {
        var vm = this;
        vm.config = getCardConfig();

        function getCardConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                selectionMatchProp: 'id'
            };
        }
    }
})();