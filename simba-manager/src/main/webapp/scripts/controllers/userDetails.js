'use strict';

angular.module('SimbaApp')
  .controller('UserDetailsCtrl', ['$scope', '$modalInstance', 'selectedUser', '$user', '$translate', '$error', '$simba_component', '$configuration',
                        function ($scope, $modalInstance, selectedUser, $user, $translate, $error, $simba_component, $configuration) {
    $scope.tabs; 
    $scope.user;
    $scope.successUrls;
    $scope.userRoles;
    $scope.userPolicies;
    $scope.userGroups
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

    $scope.initRoles = function() {
        $user.findRoles($scope.user)
            .success(function(data) {
                $scope.userRoles = data;
            })
            .error(function() {
                $error.showError('error.loading.data');
            });
        $scope.showEditButtons=false;
    }

    $scope.initPolicies = function() {
      $user.findPolicies($scope.user)
          .success(function(data) {
              $scope.userPolicies = data;
          })
          .error(function() {
              $error.showError('error.loading.data');
          });
      $scope.showEditButtons=false;
    }

    $scope.initGroups = function() {
      $user.findGroups($scope.user)
          .success(function(data) {
              $scope.userGroups = data;
          })
          .error(function() {
              $error.showError('error.loading.data');
          });
      $scope.showEditButtons=false;
    }
    
    $scope.save = function () {
        $modalInstance.close($scope.user);
    };

    $scope.resetPassword = function() {
        $user.resetPassword($scope.user).then(function(data) {
            $scope.user = data.data;
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.addRoleToUser = function() {
        $user.findRolesNotLinked($scope.user)
            .success(function(data) {

                var listbox = $simba_component.listbox('dashboard.roles', data, "name");

                listbox.result.then(function (selectedUnlinkedRoles) {
                    $user.addRoles($scope.user, selectedUnlinkedRoles)
                        .success(function(){
                            $scope.initRoles();
                            refreshSelectedUser();
                        })
                        .error(function(){
                            $error.showError('error.update.failed');
                        });
                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });


            })
            .error(function() {
               $error.showError('error.loading.data');
            });
    }

    $scope.deleteRole = function(role) {
        $user.removeRole($scope.user, role)
        .success(function() {
            $scope.initRoles();
            refreshSelectedUser();
        })
        .error(function() {
            $error.showError('error.update.failed');
        });
    }
    
    var getTabs = function() {
        return [
            {id: 'Data', title: $translate('updateuser.data'), active: true, url: 'views/modals/user/content/data.html', clickAction: function() {$scope.initData();}},
            {id: 'Roles', title: $translate('updateuser.roles'),  active: false, url: 'views/modals/user/content/roles.html', clickAction: function() {$scope.initRoles();}},
            {id: 'Groups', title: $translate('updateuser.groups'), active: false, url: 'views/modals/user/content/groups.html', clickAction: function() {$scope.initGroups();}},
            {id: 'Policies', title: $translate('updateuser.policies'),  active: false, url: 'views/modals/user/content/policies.html', clickAction: function() {$scope.initPolicies();}}
        ];
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
    }
  }]);
