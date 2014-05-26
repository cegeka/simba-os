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
