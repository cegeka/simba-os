'use strict';

angular.module('SimbaApp')
    .controller('UserCreationCtrl', ['$scope', '$modalInstance', 'selectedUser', '$user', '$translate', '$error', '$simba_component', '$configuration', '$rule',
        function ($scope, $modalInstance, selectedUser, $user, $translate, $error, $simba_component, $configuration, $rule) {
            $scope.tabs;
            $scope.user;
            $scope.successUrls;
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

            $scope.save = function () {
                $modalInstance.close({"type": getCreationType(), "data": $scope.user});
            };

            $scope.resetPassword = function() {
                $user.resetPassword($scope.user).then(function(data) {
                    $scope.user = data.data;
                });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            var getCreationType = function () {
                for(var tabId in $scope.tabs){
                    var tab = $scope.tabs[tabId];
                    console.log(tab);
                    if(tab.active){
                        return tab.id.toLowerCase();
                    }
                }
                return null;
            };

            var getTabs = function() {
                var tabs = [
                    {id: 'Webservices', title: 'users.add.webservices.title', active: true, url: 'views/modals/user/addUserWebservices.html', clickAction: function() {$scope.initData();}},
                    {id: 'REST', title: 'users.add.rest.title',  active: false, url: 'views/modals/user/addUserRest.html', resourceName: 'manage-configuration', operation: 'WRITE', clickAction: function() {}}
                ];
                tabs.forEach(function (tab) {
                    if(typeof tab.resourceName !== 'undefined') {
                        tab.hidden = !$rule.evaluateRule(tab.resourceName, tab.operation);
                    }
                });
                return tabs;
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
