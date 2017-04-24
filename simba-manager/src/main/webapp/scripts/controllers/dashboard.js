/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
