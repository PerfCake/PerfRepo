(function() {
    'use strict';

    angular
        .module('org.perfrepo.utils')
        .service('validationHelper', ValidationHelper);

    function ValidationHelper() {

        return {
            setFormErrors: setFormErrors
        };

        function setFormErrors(response, form){
            // TODO only 422 status
            angular.forEach(response.data.fieldErrors, function (error) {
                form[error.field].$setValidity('server', false);
                form[error.field].$serverValidationMessage = error.message;
            });
        }


    }
})();