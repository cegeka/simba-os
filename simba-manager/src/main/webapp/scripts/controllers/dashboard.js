'use strict';

angular.module('SimbaApp')
  .controller('DashboardCtrl', ['$scope', '$rule', function ($scope, $rule) {
    $scope.tabs;
    $scope.init = function() {
        $scope.tabs = getTabs();
    };
    
    var getTabs = function() {
        var tabs = [
            {id: 'UserTab', title: 'dashboard.users', active: false,  resourceName: 'manage-users', operation: 'READ', url: 'views/users.html'},
            {id: 'RoleTab', title: 'dashboard.roles', active: false, resourceName: 'manage-roles', operation: 'READ', url: 'views/roles.html'},
            {id: 'GroupTab', title: 'dashboard.groups', active: false, resourceName: 'manage-groups', operation: 'READ', url: 'views/groups.html'},
            {id: 'PolicyTab', title: 'dashboard.policies', active: false, resourceName: 'manage-policies', operation: 'READ', url: 'views/policies.html'}
        ];

        tabs.forEach(function(tab) {
            tab.disabled = !$rule.evaluateRule(tab.resourceName, tab.operation);

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
