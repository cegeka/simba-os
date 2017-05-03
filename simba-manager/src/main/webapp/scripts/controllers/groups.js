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
  .controller('GroupCtrl', ['$scope', '$modal', '$log', '$group', '$error', '$translate', '$simba_component',
                  function ($scope, $modal, $log, $group, $error, $translate, $simba_component) {

    $scope.groups =[];
    $scope.users =[];
    $scope.roles =[];
    $scope.selectedGroup;
    
    $scope.init = function() {
        $group.getAll().then(
            function(data) {
                $scope.groups = data;
            });
    };

    $scope.selectGroup = function(group) {
        $group.findUsers(group).then(function(data) {
            $scope.users = data;
        });
        $group.findRoles(group).then(function(data) {
            $scope.roles = data;
        });
        $scope.selectedGroup = group;
    };

      $scope.openAddRole = function() {
          if($scope.selectedGroup==null) {
              $error.showError($translate('error.no.group.selected'));
              return;
          }

          $group.findRolesNotLinked($scope.selectedGroup)
              .then(function(data) {
                  var listbox = $simba_component.listbox($translate('addRolesToGroup'), data, "name");

                  listbox.result.then(function (selectedUnlinkedRoles) {
                      $group.addRoles($scope.selectedGroup, selectedUnlinkedRoles)
                          .success(function(){
                              $group.findRoles($scope.selectedGroup).then(function(data) {
                                  $scope.roles = data;
                              });
                              $group.refresh($scope.selectedGroup).then(function(data) {
                                  $scope.selectedGroup = data;
                              });
                          })
                          .error(function(){
                              $error.showError($translate('error.update.failed'));
                          });}, function () {
                      $log.info('Modal dismissed at: ' + new Date());
                  });

              })
              .catch(function() {
                  $error.showError('error.loading.data');
              });
      };

    $scope.deleteRoleFromGroup = function(role) {
        $group.removeRole($scope.selectedGroup, role).success(function() {
            $group.findRoles($scope.selectedGroup).then(function(data) {
               $scope.roles = data;
            });
            $group.refresh($scope.selectedGroup).then(function(data) {
                $scope.selectedGroup = data;
            });
        })
        .error(function() {
            $error.showError($translate('error.update.failed'));
        });
    }

  }]);
