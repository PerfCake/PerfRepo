(function() {
    'use strict';

    angular
        .module('org.perfrepo', [
            'patternfly.navigation',
            'ui.router',
            'ngProgress',

            'org.perfrepo.authentication',
            'org.perfrepo.login',
            'org.perfrepo.group',
            'org.perfrepo.common',
            'org.perfrepo.utils',
            'org.perfrepo.dashboard',
            'org.perfrepo.test',
            'org.perfrepo.testExecution',
            'org.perfrepo.report'
        ])
        .constant('API_URL', 'rest/json')
        .constant('API_TEST_URL', 'rest/json/tests')
        .constant('API_METRIC_URL', 'rest/json/metrics')
        .constant('API_ALERT_URL', 'rest/json/alerts');
})();