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

angular.module('SimbaApp')
    .factory('$policy', ['$rest', '$q', function($rest, $q) {
        function getDataForPolicy(url, policy) {
            var deferred = $q.defer();
                $rest.post(url, policy)
            .success(function(data){
                    deferred.resolve(data);
            })
            .error(function(){
                    deferred.reject();
            });
            return deferred.promise;
        }

        return {
            getAll: function() {
               return $rest.get('policy/findAll');
            },
            findRoles: function(policy) {
                return getDataForPolicy('policy/findRoles', policy);
            },
            findRolesNotLinked: function(policy) {
                return getDataForPolicy('policy/findRolesNotLinked', policy);
            },
            findRules: function(policy) {
                return getDataForPolicy('policy/findRules', policy);
            },
            findRulesNotLinked: function(policy) {
                return getDataForPolicy('policy/findRulesNotLinked', policy);
            },
            addRoles: function(policy, roles) {
                return $rest.post('policy/addRoles', {"policy": policy, "roles": roles});
            },
            removeRole: function(role, policy) {
                return $rest.post('policy/removeRole', {"role":role, "policy":policy});
            },
            removeRule: function(rule, policy) {
                return $rest.post('policy/removeRule', {"rule":rule, "policy":policy});
            },
            addRules: function(policy, rules) {
                return $rest.post('policy/addRules', {"policy":policy, "rules":rules});
            },
            refresh: function(policy) {
                return getDataForPolicy('policy/refresh', policy);
            },
            createPolicy: function(name) {
                return $rest.post('policy/create', name);
            },
            deletePolicy: function(policy) {
                return $rest.post('policy/delete', {"policy":policy});
            }
        };
    }]);
