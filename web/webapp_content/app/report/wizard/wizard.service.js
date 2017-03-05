(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .service('wizardService', WizardService);

    function WizardService(API_REPORT_URL, $http) {

        return {
            getPermissionOptions: getPermissionOptions,
            getItemSelectors: getItemSelectors,
            getReportTypes: getReportTypes,
            validateReportInfoStep: validateReportInfoStep,
            validateReportConfigurationStep: validateReportConfigurationStep,
            validateReportPermissionStep: validateReportPermissionStep
        };

        function getPermissionOptions() {
            return [
                {level: 'USER', types: ['READ', 'WRITE']},
                {level: 'GROUP', types: ['READ', 'WRITE']},
                {level: 'PUBLIC', types: ['READ']}
            ];
        }


        function getItemSelectors() {
            return [
                {name: 'TEST_EXECUTION_ID', text: 'Test execution ID'},
                {name: 'TAG_QUERY', text: 'Tag query'},
                {name: 'PARAMETER_QUERY', text: 'Parameter query'}
            ];
        }

        function getReportTypes() {
            return [
                {
                    name: 'Table comparison report',
                    type: 'TABLE_COMPARISON',
                    description: 'Compare multiple sets of test executions against each other, show differences etc.',
                    image: 'table_comparison.png'
                },
                {
                    name: 'Metric history report',
                    type: 'METRIC_HISTORY',
                    description: 'Show results for specific metrics in history',
                    image: 'metric_history.png'
                },
                {
                    name: 'Boxplot report',
                    type: 'BOX_PLOT',
                    description: 'Compute boxplots for test executions and compare them across different test runs',
                    image: 'box_plot.png'
                }
            ];
        }

        function validateReportInfoStep(report) {
            return $http.post(API_REPORT_URL + '/wizard/validate/info-step', report).then(function(response) {
                return response.data;
            });
        }

        function validateReportConfigurationStep(report) {
            return $http.post(API_REPORT_URL + '/wizard/validate/configuration-step', report).then(function(response) {
                return response.data;
            });
        }

        function validateReportPermissionStep(report) {
            return $http.post(API_REPORT_URL + '/wizard/validate/permission-step', report).then(function(response) {
                return response.data;
            });
        }
    }
})();