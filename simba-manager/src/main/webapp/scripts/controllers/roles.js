'use strict';

angular.module('SimbaApp')
  .controller('RoleCtrl', ['$scope', '$modal', '$log', '$role', '$error', '$translate', '$simba_component',
                  function ($scope, $modal, $log, $role, $error, $translate, $simba_component) {

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
            $error.showError($translate('error.no.role.selected'));
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
                            $error.showError($translate('error.update.failed'));
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
              $error.showError($translate('error.no.role.selected'));
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
                              $error.showError($translate('error.update.failed'));
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
            $error.showError($translate('error.update.failed'));
        });
    }

    $scope.deleteRole = function(role) {
        $role.deleteRole(role).success(function() {
            var index = $scope.roles.indexOf(role);
            $scope.roles.splice(index,1);
        })
        .error(function() {
            $error.showError($translate('error.update.failed'));
        });
    };

    $scope.createRole = function() {
        var textbox =  $simba_component.textbox($translate('create.role'));

        textbox.result.then(function (name) {
            $role.createRole(name).success(function(data) {
                        $scope.roles.push(data);
                    })
                    .error(function() {
                        $error.showError($translate('error.create.failed'));
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
            $error.showError($translate('error.update.failed'));
        });
    }

  }]);
