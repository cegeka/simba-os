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
    .controller('UserCreationCtrl', ['$scope', '$modalInstance', 'selectedUser', '$user', '$translate', '$error', '$simba_component', '$configuration', '$rule', '$role',
        function ($scope, $modalInstance, selectedUser, $user, $translate, $error, $simba_component, $configuration, $rule, $role) {
            $scope.tabs;
            $scope.user;
            $scope.successUrls;
            $scope.userRoles = [];
            $scope.userPolicies = [];
            $scope.changePassword = true;
            $scope.showEditButtons=true;
            $scope.error = $error.getError();

            $scope.initData = function() {
                getSuccessUrls();
                $scope.showEditButtons=true;
            }

            $scope.init = function() {
                $scope.tabs = getTabs();
                $scope.user = selectedUser;
            };

            $scope.save = function () {
                $modalInstance.close({"user": $scope.user, "roles": $scope.userRoles});
            };

            $scope.resetPassword = function() {
                $user.resetPassword($scope.user).then(function(data) {
                    $scope.user = data.data;
                });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            var getTabs = function() {
                var tabs = [
                    {id: 'Gegevens', title: 'updateuser.data', active: true, url: 'views/modals/user/addUserData.html', clickAction: function() {$scope.initData();}},
                    {id: 'Rollen', title: 'updateuser.roles', active: false, url: 'views/modals/user/addUserRoles.html', clickAction: function() {}},
                    {id: 'Policies', title: 'updateuser.policies', active: false, url: 'views/modals/user/addUserPolicies.html', clickAction: function() {}}
                ];
                tabs.forEach(function (tab) {
                    if(typeof tab.resourceName !== 'undefined') {
                        tab.hidden = !$rule.evaluateRule(tab.resourceName, tab.operation);
                    }
                });
                return tabs;
            };

            var getSuccessUrls = function() {
                $configuration.getValue('SUCCESS_URL').success(function(data) {
                    $scope.successUrls=data;
                });
            };

            $scope.hideErrorMessage = function() {
                $error.hideError();
            };

            var refreshSelectedUser = function () {
                if (typeof $scope.user !== 'undefined') {
                    $user.refresh($scope.user)
                        .success(function (data) {
                            $scope.user = data;
                        });
                }
            };

            $scope.addRoleToUser = function () {
                $role.getAll().then(function(data) {
                        data = data.filter(function (role) {
                            return $scope.userRoles.map(function (linkedRole) {
                                    return linkedRole.name;
                                }).indexOf(role.name) == -1
                        });
                        var listbox = $simba_component.listbox($translate('dashboard.roles'), data, "name");

                        listbox.result.then(function (roles) {
                            $scope.userRoles = $scope.userRoles.concat(roles);
                            updateUserPolicies();
                        });
                    })
                    .catch(function() {
                        $error.showError('error.loading.data');
                    });
            };

            var updateUserPolicies = function () {
                $scope.userPolicies = [];
                $scope.userRoles.forEach(function (role) {
                    $role.findPolicies(role).then(function (data) {
                        $scope.userPolicies = $scope.userPolicies.concat(data);
                    });
                });
            };

            $scope.deleteRole = function (role) {
                var i = $scope.userRoles.indexOf(role);
                $scope.userRoles.splice(i, 1);
                updateUserPolicies();
            };
        }]);
