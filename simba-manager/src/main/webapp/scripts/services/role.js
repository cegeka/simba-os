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
    .factory('$role', ['$rest', '$q', function($rest, $q) {
        function getDataForRole(url, role) {
            var deferred = $q.defer();
                $rest.newPost(url, role)
            .success(function(data){
                    deferred.resolve(data);
            })
            .error(function(){
                    deferred.reject();
            });
            return deferred.promise;
        }

        function getIdsOfTOs(tos) {
            result = [];
            tos.forEach(function(to) {
                result.push(to.id);
            });
            return result;
        }

        return {
            getAll: function() {
               return $rest.newGet('role/findAll');
            },
            findPolicies: function(role) {
                return getDataForRole('role/findPolicies', role);
            },
            findPoliciesNotLinked: function(role) {
                return getDataForRole('role/findPoliciesNotLinked', role);
            },
            findUsers: function(role) {
                return getDataForRole('role/findUsers', role);
            },
            findUsersNotLinked: function(role) {
                return $rest.newPost('role/findUsersNotLinked', role);
            },
            addPolicy: function(role, policy) {
                return $rest.newPost('role/addPolicy', {"role":role, "policy":policy});
            },
            addPolicies: function(role, policies) {
                return $rest.newPost('role/addPolicies', {"role": role, "policies": policies});
            },
            removePolicy: function(role, policy) {
                return $rest.newPost('role/removePolicy', {"role":role, "policy":policy});
            },
            removeUser: function(user, role) {
                return $rest.newPost('role/removeUser', {"user":user, "role":role});
            },
            addUsers: function(role, users) {
                return $rest.newPost('role/addUsers', {"role":role, "users":users});
            },
            refresh: function(role) {
                return getDataForRole('role/refresh', role);
            },
            createRole: function(name) {
                return $rest.newPost('role/createRole', {"roleName":name});
            },
            deleteRole: function(role) {
                return $rest.newPost('role/deleteRole', {"role":role});
            }
        };
    }]);
