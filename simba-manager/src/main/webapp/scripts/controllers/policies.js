'use strict';

angular.module('SimbaApp')
  .controller('PolicyCtrl', ['$scope', '$modal', '$log', '$policy', '$error', '$translate', '$simba_component',
                  function ($scope, $modal, $log, $policy, $error, $translate, $simba_component) {

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
            $error.showError($translate('error.no.policy.selected'));
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
                        .error(function(){
                            $error.showError($translate('error.update.failed'));
                        });}, function () {
                    $log.info('Modal dismissed at: ' + new Date());
                });

            });
    };

      $scope.openAddRule = function() {
          if($scope.selectedPolicy==null) {
              $error.showError($translate('error.no.policy.selected'));
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
                          .error(function(){
                              $error.showError($translate('error.update.failed'));
                          });}, function () {
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
        .error(function() {
            $error.showError($translate('error.update.failed'));
        });
    }

    $scope.deleteRuleFromPolicy = function(rule) {
        $policy.removeRule(rule, $scope.selectedPolicy).success(function() {
          $policy.findRules($scope.selectedPolicy).then(function(data) {
              $scope.rules = data;
          });
          refreshSelectedPolicy()
        })
        .error(function() {
          $error.showError($translate('error.update.failed'));
        });
    }

    $scope.deletePolicy = function(policy) {
        $policy.deletePolicy(policy).success(function() {
            var index = $scope.policies.indexOf(policy);
            $scope.policies.splice(index,1);
            $scope.init();
            refreshSelectedPolicy();
        })
        .error(function() {
            $error.showError($translate('error.update.failed'));
        });
    };

    $scope.createPolicy = function() {
        var textbox =  $simba_component.textbox($translate('create.policy'));

        textbox.result.then(function (name) {
            $policy.createPolicy(name).success(function(data) {
                        $scope.init();
                        refreshSelectedPolicy();
                    })
                    .error(function() {
                        $error.showError($translate('error.create.failed'));
                    });
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

  }]);
