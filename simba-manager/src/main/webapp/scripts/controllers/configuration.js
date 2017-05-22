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
  .controller('ConfigurationCtrl', ['$scope', '$rule', function ($scope, $rule) {
    $scope.tabs;
    $scope.init = function() {
        $scope.tabs = getTabs();
    };

    var getTabs = function() {
        var tabs = [
            {id: 'UniqueParametersTab', title: 'configuration.parameters.unique', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/unique-parameters.html'},
            {id: 'ListParametersTab', title: 'configuration.parameters.list', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/list-parameters.html'},
            {id: 'CacheTab', title: 'configuration.cache', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/cache.html'},
            {id: 'ConditionsTab', title: 'configuration.conditions.title', active: false,  resourceName: 'manage-configuration', operation: 'READ', url: 'views/conditions.html'}
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