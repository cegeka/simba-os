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
                return $rest.post('condition/findAll');
            },
            findPolicies: function (condition) {
                return $rest.post('condition/findPolicies', condition);
            },
            findUsers: function (condition) {
                return $rest.post('condition/findUsers', condition);
            },
            refresh: function (condition) {
                return $rest.post('condition/refresh', condition);
            },
            addOrUpdate: function (conditionWithUsersAndPolicies) {
                return $rest.post('condition/addOrUpdate', conditionWithUsersAndPolicies);
            },
            remove: function (condition) {
                return $rest.post('condition/remove', condition);
            },
            validateTimeCondition: function (timeConditions) {
                return $rest.post('condition/validateTimeCondition', timeConditions);
            }
        };
    }]);
