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
  .controller('PolicyCtrl', ['$scope', '$modal', '$log', '$policy', '$error', '$translate', '$simba_component', '$rootScope',
                  function ($scope, $modal, $log, $policy, $error, $translate, $simba_component, $rootScope) {

    $scope.roles =[];
    $scope.rules =[];
    $scope.policies =[];
    $scope.selectedPolicy;
    
    $scope.init = function() {
        $policy.getAll().then(
            function(data) {
                $scope.policies = data;
            });
    };

    $scope.selectPolicy = function(policy) {
        $policy.findRules(policy).then(function(data) {
            $scope.rules = data;
        });
        $policy.findRoles(policy).then(function(data) {
            $scope.roles = data
        });
        $scope.selectedPolicy = policy;
    };

    $scope.openAddRole = function() {
        if($scope.selectedPolicy==null) {
            $error.showError('error.no.policy.selected');
            return;
        }

        $policy.findRolesNotLinked($scope.selectedPolicy)
            .then(function(data) {
                var listbox = $simba_component.listbox($translate('addrolestopolicypopup.addrolestopolicy'), data, "name");

                listbox.result.then(function (selectedUnlinkedRoles) {
                    $policy.addRoles($scope.selectedPolicy, selectedUnlinkedRoles)
                        .success(function(){
                            $policy.findRoles($scope.selectedPolicy).then(function(data) {
                                $scope.roles = data;
                            });
                            refreshSelectedPolicy();
                        })
                        .error($error.handlerWithDefault('error.adding.rol'));
                }, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });
            });
    };

      $scope.openAddRule = function() {
          if($scope.selectedPolicy==null) {
              $error.showError('error.no.policy.selected');
              return;
          }

          $policy.findRulesNotLinked($scope.selectedPolicy)
              .then(function(data) {
                  var listbox = $simba_component.listbox($translate('addrulestopolicypopup.addrulestopolicy'), data, "name");

                  listbox.result.then(function (selectedUnlinkedRules) {
                      $policy.addRules($scope.selectedPolicy, selectedUnlinkedRules)
                          .success(function(){
                              $policy.findRules($scope.selectedPolicy).then(function(data) {
                                  $scope.rules = data
                              });
                              refreshSelectedPolicy();
                          })
                          .error($error.handlerWithDefault('error.update.failed'));
                  }, function () {
                      $log.info('Modal dismissed at: ' + new Date());
                  });

              })
      };

    var refreshSelectedPolicy = function() {
        if(typeof $scope.selectedPolicy !== 'undefined') {
            $policy.refresh($scope.selectedPolicy).then(function(data) {
                $scope.selectedPolicy = data;
            });
        }
    }

    $scope.deleteRoleFromPolicy = function(role) {
        $policy.removeRole(role, $scope.selectedPolicy).success(function() {
            $policy.findRoles($scope.selectedPolicy).then(function(data) {
                $scope.roles = data;
            });
            refreshSelectedPolicy();
        })
        .error($error.handlerWithDefault('error.remove.rol'));
    }

    $scope.deleteRuleFromPolicy = function(rule) {
        $policy.removeRule(rule, $scope.selectedPolicy).success(function() {
          $policy.findRules($scope.selectedPolicy).then(function(data) {
              $scope.rules = data;
          });
          refreshSelectedPolicy()
        })
        .error($error.handlerWithDefault('error.update.failed'));
    }

    $scope.deletePolicy = function(policy) {
        $policy.deletePolicy(policy).success(function() {
            var index = $scope.policies.indexOf(policy);
            $scope.policies.splice(index,1);
            $scope.init();
            refreshSelectedPolicy();
        })
        .error($error.handlerWithDefault('error.remove.policy'));
    };

    $scope.createPolicy = function() {
        var textbox =  $simba_component.textbox($translate('create.policy'));

        textbox.result.then(function (name) {
            $policy.createPolicy(name).success(function(data) {
                        $scope.init();
                        refreshSelectedPolicy();
                    })
                    .error($error.handlerWithDefault('error.create.failed'));
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

  }]);
