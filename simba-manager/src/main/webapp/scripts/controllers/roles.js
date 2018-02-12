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
  .controller('RoleCtrl', ['$scope', '$modal', '$log', '$role', '$error', '$translate', '$simba_component', '$rootScope',
                  function ($scope, $modal, $log, $role, $error, $translate, $simba_component, $rootScope) {

    $scope.roles =[];
    $scope.users =[];
    $scope.policies =[];
    $scope.selectedRole;
    
    $scope.init = function() {
        $role.getAll().then(
            function(data) {
                $scope.roles = data;
            });
    };

    $scope.selectRole = function(role) {
        $role.findUsers(role).then(function(data) {
            $scope.users = data;
        });
        $role.findPolicies(role).then(function(data) {
            $scope.policies = data;
        });
        $scope.selectedRole = role;
    };

    $scope.openAddUser = function() {
        if($scope.selectedRole==null) {
            $error.showError('error.no.role.selected');
            return;
        }

        $role.findUsersNotLinked($scope.selectedRole)
            .success(function(data) {
                $scope.unlinkedUsers = data.data;
                var listbox = $simba_component.listbox($translate('adduserstorolepopup.adduserstorole'), data, "userName");

                listbox.result.then(function (selectedUnlinkedUsers) {
                    $role.addUsers($scope.selectedRole, selectedUnlinkedUsers)
                        .success(function(){
                            $role.findUsers($scope.selectedRole).then(function(data) {
                                $scope.users = data;
                            });
                        })
                        .error(function(){
                            $error.showError('error.adding.user');
                        });}, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });

            })
            .catch(function() {
                $error.showError('error.loading.data');
            });
    };

      $scope.openAddPolicy = function() {
          if($scope.selectedRole==null) {
              $error.showError('error.no.role.selected');
              return;
          }

          $role.findPoliciesNotLinked($scope.selectedRole)
              .then(function(data) {
                  var listbox = $simba_component.listbox($translate('adduserstorolepopup.adduserstorole'), data, "name");

                  listbox.result.then(function (selectedUnlinkedPolicies) {
                      $role.addPolicies($scope.selectedRole, selectedUnlinkedPolicies)
                          .success(function(){
                              $role.findPolicies($scope.selectedRole).then(function(data) {
                                  $scope.policies = data;
                              });
                          })
                          .error(function(){
                              $error.showError('error.adding.policy');
                          });}, function () {
                      $log.info('Modal dismissed at: ' + new Date());
                  });

              })
              .catch(function() {
                  $error.showError('error.loading.data');
              });
      };

    $scope.deleteUserFromRole = function(user) {
        $role.removeUser(user, $scope.selectedRole).success(function() {
            $role.findUsers($scope.selectedRole).then(function(data) {
                $scope.users = data;
            });
        })
        .error(function() {
            $error.showError('error.remove.user');
        });
    }

    $scope.deleteRole = function(role) {
        $role.deleteRole(role).success(function() {
            var index = $scope.roles.indexOf(role);
            $scope.roles.splice(index,1);
        })
        .error(function() {
            $error.showError('error.remove.rol');
        });
    };

    $scope.createRole = function() {
        var textbox =  $simba_component.textbox($translate('create.role'));

        textbox.result.then(function (name) {
            $role.createRole(name).success(function(data) {
                        $scope.roles.push(data);
                    })
                    .error(function() {
                        $error.showError('error.create.failed');
                    });
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.deletePolicyFromRole = function(policy) {
        $role.removePolicy($scope.selectedRole, policy).success(function() {
            $role.findPolicies($scope.selectedRole).then(function(data) {
                $scope.policies = data;
            });
        })
        .error(function() {
            $error.showError('error.remove.policy');
        });
    }

  }]);
