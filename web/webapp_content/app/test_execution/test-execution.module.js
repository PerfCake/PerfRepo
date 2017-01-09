(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution', [
            'ngResource',
            'ui.router',

            'org.perfrepo.testExecution.overview',
            'org.perfrepo.testExecution.detail',
            'org.perfrepo.testExecution.create',
            'org.perfrepo.testExecution.edit'
        ]);
})();