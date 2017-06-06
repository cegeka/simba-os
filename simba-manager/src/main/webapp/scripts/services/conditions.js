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
    .factory('$conditions', ['$rest', function($rest) {
        return {
            findAll: function() {
                return $rest.newPost('condition/findAll');
            },
            findPolicies: function (condition) {
                return $rest.newPost('condition/findPolicies', condition);
            },
            findUsers: function (condition) {
                return $rest.newPost('condition/findExemptedUsers', condition);
            },
            refresh: function (condition) {
                return $rest.newPost('condition/refresh', condition);
            },
            addOrUpdate: function (conditionWithUsersAndPolicies) {
                return $rest.newPost('condition/addOrUpdate', conditionWithUsersAndPolicies);
            },
            remove: function (condition) {
                return $rest.newPost('condition/remove', condition);
            },
            validateTimeCondition: function (timeConditions) {
                return $rest.newPost('condition/validateTimeCondition', timeConditions);
            }
        };
    }]);
