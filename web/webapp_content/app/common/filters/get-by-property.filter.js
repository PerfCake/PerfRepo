(function() {
    'use strict';

    angular
        .module('org.perfrepo.common')
        .filter('getByProperty', GetByProperty);

    function GetByProperty() {
        return function(propertyName, propertyValue, array) {
            var i = 0, len = array.length;
            for (; i < len; i++) {
                if (array[i][propertyName] == propertyValue) {
                    return array[i];
                }
            }
            return null;
        }
    }
})();
