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
  .controller('UserDetailsCtrl', ['$scope', '$modalInstance', 'selectedUser', '$user', '$translate', '$error', '$info', '$simba_component', '$configuration', '$log', '$filter',
                        function ($scope, $modalInstance, selectedUser, $user, $translate, $error, $info, $simba_component, $configuration, $log, $filter) {
    $scope.tabs;
    $scope.user;
    $scope.successUrls;
    $scope.userRoles;
    $scope.userPolicies;
    $scope.userGroups;
    $scope.showEditButtons=true;
    $scope.error = $error.getError();
    $scope.info = $info.getInfo();

    $scope.initData = function() {
        getSuccessUrls();
        $scope.showEditButtons=true;
    };

    $scope.init = function() {
        $scope.tabs = getTabs();
        $scope.user = selectedUser;
    };

    $scope.initRoles = function() {
        $user.findRoles($scope.user)
            .success(function(data) {
                $scope.userRoles = data;
            })
            .error(function() {
                $error.showError('error.loading.data');
            });
        $scope.showEditButtons=false;
    };

    $scope.initPolicies = function() {
      $user.findPolicies($scope.user)
          .success(function(data) {
              $scope.userPolicies = data;
          })
          .error(function() {
              $error.showError('error.loading.data');
          });
      $scope.showEditButtons=false;
    };

    $scope.initGroups = function() {
      $user.findGroups($scope.user)
          .success(function(data) {
              $scope.userGroups = data;
          })
          .error($error.handlerWithDefault('error.loading.data'));
      $scope.showEditButtons=false;
    };

    $scope.save = function () {
        $user.update($scope.user)
            .success(function (data) {
                $modalInstance.close(data);
            })
            .error(function (data) {
                $error.showError(data.errorkey);
            });
    };

    $scope.reset = function () {
        $user.resetPassword($scope.user)
            .then(function () {
                $info.showInfo('password.reset.successful');
            }).catch($error.handlerWithDefault('error.password.reset.failed'));
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.addRoleToUser = function() {
        $user.findRolesNotLinked($scope.user)
            .success(function(data) {

                data = $filter('orderBy')(data, 'name');

                var listbox = $simba_component.listbox($translate('dashboard.roles'), data, "name");

                listbox.result.then(function (selectedUnlinkedRoles) {
                    $user.addRoles($scope.user, selectedUnlinkedRoles)
                        .success(function(){
                            $scope.initRoles();
                            refreshSelectedUser();
                        })
                        .error(function(){
                            $error.showError('error.adding.rol');
                        });
                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });


            })
            .error(function() {
               $error.showError('error.loading.data');
            });
    };

    $scope.deleteRole = function(role) {
        $user.removeRole($scope.user, role)
        .success(function() {
            $scope.initRoles();
            refreshSelectedUser();
        })
        .error(function() {
            $error.showError('error.remove.rol');
        });
    };

    var getTabs = function() {
        return [
            {id: 'Data', title: 'updateuser.data', active: true, url: 'views/modals/user/content/data.html', clickAction: function() {$scope.initData();}},
            {id: 'Roles', title: 'updateuser.roles',  active: false, url: 'views/modals/user/content/roles.html', clickAction: function() {$scope.initRoles();}},
            {id: 'Groups', title: 'updateuser.groups', active: false, url: 'views/modals/user/content/groups.html', clickAction: function() {$scope.initGroups();}},
            {id: 'Policies', title: 'updateuser.policies',  active: false, url: 'views/modals/user/content/policies.html', clickAction: function() {$scope.initPolicies();}}
        ];
    };

    var getSuccessUrls = function() {
        $configuration.getListValue('SUCCESS_URL').success(function(data) {
            $scope.successUrls=data;
        });
    };

        $scope.hideErrorMessage = function() {
            $error.hideError();
        };

        $scope.hideInfoMessage = function () {
            $info.hideInfo();
        };

    var refreshSelectedUser = function () {
        if (typeof $scope.user !== 'undefined') {
            $user.refresh($scope.user)
            .success(function (data) {
                $scope.user = data;
            });
        }
    }
  }]);
