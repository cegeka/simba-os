'use strict';

angular.module('SimbaApp')
  .controller('UserCtrl', ['$scope', '$modal', '$log', '$user', '$error', '$translate', function ($scope, $modal, $log, $user, $error, $translate) {
    $scope.tabs;  
    $scope.searchText = "";
    $scope.searchBoxPlaceholderText = "users.filter";
    
    $scope.headers = [
      'useroverview.username',
      'useroverview.active',
      'useroverview.name',
      'useroverview.firstname'
    ];
    
    $scope.users =[];
    
    $scope.init = function() {
        $user.getAll().then(
            function(data) {
                $scope.users = data;
            });
    };
    
    $scope.openUserDetails = function(selectedUser) {
        var modalInstance = $modal.open({
                                templateUrl: 'views/modals/user/userDetailTemplate.html',
                                controller: 'UserDetailsCtrl',
                                resolve: {
                                  selectedUser: function () {
                                    return jQuery.extend(true, {}, selectedUser);
                                  }
                                }
        });

        modalInstance.result.then(function (user) {
            $user.update(user)
                .success(function(data) {
                    selectedUser = data;
                })
                .error(function() {
                    $error.showError('error.update.failed');
                });
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
   
    $scope.openAddUser = function() {
        var modalInstance = $modal.open({
                                templateUrl: 'views/modals/user/addUserTemplate.html',
                                controller: 'UserDetailsCtrl',
                                resolve: {
                                  selectedUser: function () {
                                    return {};
                                  }
                                }
        });

        modalInstance.result.then(function (user) {
            $user.add(user)
                .success(function(data) {
                    $scope.users.push(data);
                })
                .error(function() {
                    $error.showError('error.create.failed');
                });
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.isUserInactive = function(user) {
        return user.status === 'INACTIVE';
    };
  }]);
