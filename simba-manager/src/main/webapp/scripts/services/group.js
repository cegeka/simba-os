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
    .factory('$group', ['$rest', '$q', function($rest, $q) {
        function getDataForGroup(url, group) {
            var deferred = $q.defer();
                $rest.post(url, group)
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
               return $rest.get('group/findAll');
            },
            findRoles: function(group) {
                return getDataForGroup('group/findRoles', group);
            },
            findRolesNotLinked: function(group) {
                return getDataForGroup('group/findRolesNotLinked', group);
            },
            findUsers: function(group) {
                return getDataForGroup('group/findUsers', group);
            },
            addRoles: function(group, roles) {
                return $rest.post('group/addRoles', {"group": group, "roles": roles});
            },
            removeRole: function(group, role) {
                return $rest.post('group/removeRole', {"group":group, "role":role});
            },
            refresh: function(group) {
                return getDataForGroup('group/refresh', group);
            }

        };
    }]);
