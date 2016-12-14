(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert', [
            'org.perfrepo.alert.create',
            'org.perfrepo.alert.edit',
            'org.perfrepo.alert.remove'
        ]);
})();