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
  .controller('UserCtrl', ['$scope', '$modal', '$log', '$user', '$error', '$translate', '$timeout', '$rootScope', function ($scope, $modal, $log, $user, $error, $translate, $timeout, $rootScope) {
    $scope.tabs;
    $scope.searchText = "";
    $scope.searchBoxPlaceholderText = "users.filter";

    $scope.headers = [
      'useroverview.username',
      'useroverview.active',
      'useroverview.blocked',
      'useroverview.name',
      'useroverview.firstname'
    ];

    $scope.users =[];

    $scope.init = function() {
    };

    $scope.findUsers = function () {
      $rootScope.loading++;
      if ($scope.searchText.length === 0) {
          $user.getAll().then(
              function (data) {
                  $scope.users = data;
              })
              .finally(function () {
                      $rootScope.loading--;
                  }
              );
      } else {
          $user.searchUsers($scope.searchText).success(
              function (data) {
                  $scope.users = data;
              })
              .finally(function () {
                      $rootScope.loading--;
                  }
              );
      }
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
                    var i = $scope.users.indexOf(selectedUser);
                    $scope.users[i] = data;
                })
                .error(function() {
                    $error.showError('error.update.failed');
                });
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
            $user.refresh(selectedUser)
                .success(function(data) {
                    var i = $scope.users.indexOf(selectedUser);
                    $scope.users[i] = data;
                })
                .error(function() {
                    $error.showError('error.refresh.failed');
                });
        });
    };

    $scope.openAddUser = function() {
        var modalInstance = $modal.open({
                                templateUrl: 'views/modals/user/addUserTemplate.html',
                                controller: 'UserCreationCtrl',
                                resolve: {
                                  selectedUser: function () {
                                    return {};
                                  }
                                }
        });

        modalInstance.result.then(function (addUserResult) {
            if(addUserResult.type == 'webservices') {
                addWebservicesUser(addUserResult.data);
            }else if(addUserResult.type == 'rest'){
                addRestUser(addUserResult.data);
            }else{
                $error.showError('error.create.invalid.type');
            }
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.isUserInactive = function(user) {
        return user.status === 'INACTIVE';
    };

    $scope.isUserBlocked = function(user) {
        return user.status === 'BLOCKED';
    };

    var addWebservicesUser = function (creationData) {
        $user.add(creationData)
            .success(function (data) {
                $scope.users.push(data);
            })
            .error(function () {
                $error.showError('error.create.failed');
            });
    }

    var addRestUser = function (creationData) {
        $user.addRest(creationData)
            .success(function (data) {
                $modal.open({
                    templateUrl: 'views/modals/user/generatedPasswordTemplate.html',
                    controller: 'GeneratedPasswordCtrl',
                    resolve: {
                        password: function () {
                            return data;
                        }
                    }
                });
                $scope.users.push(creationData);
            })
            .error(function () {
                $error.showError('error.create.failed');
            });
    }

  }]);
