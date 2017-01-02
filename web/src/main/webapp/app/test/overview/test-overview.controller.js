(function() {

    angular
        .module('org.perfrepo.test.overview')
        .controller('TestOverviewController', TestOverviewController);

    function TestOverviewController(testModalService, $state) {
        var vm = this;
        vm.createTest = createTest;

        function createTest() {
            var modalInstance = testModalService.createTest();

            modalInstance.result.then(function (id) {
                $state.go('app.testDetail', {id: id});
            });
        }
    }
})();