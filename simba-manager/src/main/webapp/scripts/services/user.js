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
    .factory('$user', ['$rest', function($rest) {
        return {
            getAll: function() {
                return $rest.get('user/findAll');
            },
            searchUsers: function (searchText) {
                console.log(searchText);
                return $rest.post('user/search', searchText);
            },
            add: function(user) {
                return $rest.post('user/create', user);
            },
            addRest: function(username) {
              return $rest.post('user/createRestUser', username);
            },
            findByRole: function(role) {
                return $rest.post('user/findByRole', role);
            },
            findRoles: function(user) {
                return $rest.post('user/findRoles', user);
            },
            findGroups : function(user) {
                return $rest.post('user/findGroups', user);
            },
            findRolesNotLinked: function(user) {
                return $rest.post('user/findRolesNotLinked', user);
            },
            removeRole: function(user, role) {
                return $rest.post('user/removeRole', {'user':user, 'role':role});
            },
            addRoles: function(user, roles) {
                return $rest.post('user/addRoles', {'user':user, 'roles':roles});
            },
            findPolicies: function(user) {
                return $rest.post('user/findPolicies', user);
            },
            resetPassword: function(user) {
                return $rest.post('user/resetPassword', user);
            },
            createWithRoles: function(user, roleNames) {
                return $rest.post('user/createWithRoles', user, roleNames);
            },
            update: function(user) {
                return $rest.post('user/update', user);
            },
            getSuccessUrls: function() {
                return ['/dummyURL/'];
            },
            refresh: function(user) {
                return $rest.post('user/refresh', user);
            },
        };
    }]);
