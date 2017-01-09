(function() {

    angular
        .module('org.perfrepo.testExecution.overview')
        .controller('TestOverviewController', TestExecutionOverviewController);

    function TestExecutionOverviewController(_initialSearchResult, testExecutionModalService, $state) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.createTestExecution = createTestExecution;

        function createTestExecution() {

        }
    }
})();