(function() {

    angular
        .module('org.perfrepo.testExecution.overview')
        .controller('TestExecutionOverviewController', TestExecutionOverviewController);

    function TestExecutionOverviewController(_initialSearchResult) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;


    }
})();
