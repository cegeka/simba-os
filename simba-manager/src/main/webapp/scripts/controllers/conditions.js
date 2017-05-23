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
    .controller('ConditionsCtrl', ['$scope', '$log', '$error', '$translate', '$configuration', '$conditions', '$modal', '$policy',
        function ($scope, $log, $error, $translate, $configuration, $conditions, $modal, $policy) {

            $scope.conditions = [];
            $scope.selectedUsers = [];
            $scope.selectedPolicies = [];
            $scope.headers = [
                'condition.name',
                'condition.type',
                'condition.description'
            ];

            $scope.init = function () {
                $conditions.findAll().success(function (data) {
                    $scope.conditions = data;
                })
            };

            $scope.removeCondition = function (condition) {
                $conditions.remove(condition).then(function () {
                    $scope.init();
                });
            };

            $scope.addCondition = function () {
                var newCondition = {condition : {type: 'condition.type.timecondition'}, excludedUsers: [], policies: []};
                var modal = $modal.open({
                    templateUrl: 'views/modals/conditions/addCondition.html',
                    controller: 'AddConditionCtrl',
                    resolve: {
                        policies: function () {
                            return [];
                        },
                        condition: function () {
                            return newCondition;
                        }
                    }
                });
                modal.result.then(function () {
                    modal = $modal.open({
                        templateUrl: 'views/modals/conditions/addConditionTimeSettings.html',
                        controller: 'AddConditionCtrl',
                        resolve: {
                            policies: function () {
                                return [];
                            },
                            condition: function () {
                                return newCondition;
                            }
                        }
                    });
                    modal.result.then(function () {
                        $policy.getAll().then(function (policies) {
                            var modal = $modal.open({
                                templateUrl: 'views/modals/conditions/addConditionPolicies.html',
                                controller: 'AddConditionCtrl',
                                resolve: {
                                    policies: function () {
                                        return policies;
                                    },
                                    condition: function () {
                                        return newCondition;
                                    }
                                }
                            });
                            modal.result.then(function () {
                                modal = $modal.open({
                                    templateUrl: 'views/modals/conditions/addConditionExcludedUsers.html',
                                    controller: 'AddConditionCtrl',
                                    resolve: {
                                        policies: function () {
                                            return [];
                                        },
                                        condition: function () {
                                            return newCondition;
                                        }
                                    }
                                });
                                modal.result.then(function () {
                                    modal = $modal.open({
                                        templateUrl: 'views/modals/conditions/addConditionSummary.html',
                                        controller: 'AddConditionCtrl',
                                        resolve: {
                                            policies: function () {
                                                return [];
                                            },
                                            condition: function () {
                                                return newCondition;
                                            }
                                        }
                                    });
                                    modal.result.then(function () {
                                        $conditions.addOrUpdate(newCondition).then(function (condition) {
                                            $scope.conditions.push(condition.data);
                                        }).catch(function () {
                                            $error.showError('error.update.failed');
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            };


        }]);