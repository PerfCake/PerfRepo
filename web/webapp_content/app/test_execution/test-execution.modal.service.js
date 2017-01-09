(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .service('testExecutionModalService', TestExecutionModalService);

    function TestExecutionModalService($modal) {

        return {
            createTestExecution: createTestExecution,
            editTestExecution: editTestExecution,
            removeTestExecution: removeTestExecution
        };

        function createTestExecution(testId) {

        }

        function editTestExecution(id) {

        }

        function removeTestExecution(id) {

        }
    }
})();