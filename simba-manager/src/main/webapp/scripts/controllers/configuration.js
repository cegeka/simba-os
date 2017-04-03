'use strict';

angular.module('SimbaApp')
  .controller('ConfigurationCtrl', ['$scope', '$rule', function ($scope, $rule) {
    $scope.tabs;
    $scope.init = function() {
        $scope.tabs = getTabs();
    };

    var getTabs = function() {
        var tabs = [
            {id: 'UniqueParametersTab', title: 'configuration.parameters.unique', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/unique-parameters.html'},
            {id: 'ListParametersTab', title: 'configuration.parameters.list', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/list-parameters.html'},
        ];

        tabs.forEach(function(tab) {
            tab.hidden = !$rule.evaluateRule(tab.resourceName, tab.operation);

        });

        for(var i = 0; i < tabs.length; i++) {
            if(!tabs[i].disabled) {
                tabs[i].active = true;
                return tabs;
            }
        }

        return tabs;
    };
  }]);