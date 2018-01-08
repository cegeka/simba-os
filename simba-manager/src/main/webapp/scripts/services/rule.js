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
    .factory('$rule', ['$session', '$rest', function ($session, $rest) {
        var rules = {};

        return {
            evaluateRule: function (resourceName, operation) {
                var key = resourceName + operation;
                if (typeof rules[key] !== 'undefined') {
                    return rules[key];
                }

                rules[key] = $rest.post('authorization/isResourceRuleAllowed', {"username": $session.getCurrentUserName(), "resourcename": resourceName, "operation": operation});
                return rules[key];
            }
        };
    }]);
