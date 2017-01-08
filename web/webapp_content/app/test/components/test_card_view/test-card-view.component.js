(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('testCardView', {
            bindings: {
                items: '<'
            },
            controller: TestCardViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/test_card_view/test-card-view.view.html'
        });

    function TestCardViewController() {
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