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
    .controller('AddConditionCtrl', ['$scope', '$log', '$error', '$translate', '$configuration', '$conditions', '$modalInstance', 'policies', 'condition',
        function ($scope, $log, $error, $translate, $configuration, $conditions, $modalInstance, policies, condition) {

            $scope.newCondition = condition;
            $scope.policies = policies;

            $scope.init = function () {

            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            $scope.next = function () {
                $modalInstance.close($scope.newCondition);
            };

            $scope.validateTimeConditions = function () {
                $conditions.validateTimeCondition($scope.newCondition.condition).then(function () {
                    $scope.next();
                }).catch(function(e) {
                    $scope.validation = 'error.CRONEXPRESSION';
                });
            };

            $scope.getSelectedPolicies = function () {
                return $scope.newCondition.policies == null || $scope.newCondition.policies.length == 0
                    ? "/"
                    : $scope.newCondition.policies
                        .map(function (policy) {
                            return policy.name;
                        })
                        .join(", ");
            };

            $scope.getSelectedUsers = function () {
                return $scope.newCondition.excludedUsers == null || $scope.newCondition.excludedUsers.length == 0
                    ? "/"
                    : $scope.newCondition.excludedUsers
                        .map(function (user) {
                            return user.userName;
                        })
                        .join(", ");
            };
        }]);




