(function() {
    'use strict';

    angular
        .module('org.perfrepo.common')
        .directive('prKebabDropdown', function(){
          return {
              transclude: true,
              controller: KebabDropdownController,
              controllerAs: 'vm',
              templateUrl: 'app/common/components/kebab-dropdown/kebab-dropdown.view.html'
          }
        });

        /*
        .component('prKebabItem', {
            bindings: {
                clickFn: '&'
            },
            transclude: true,
            // replace is not supported for components, and for directives it is deprecated
            replace: true,
            controllerAs: 'vm',
            template: '<li class="l"><a ng-click="clickFn()" ng-transclude></a></li>'
        });
        */

    function KebabDropdownController() {
        var vm = this;
    }

})();