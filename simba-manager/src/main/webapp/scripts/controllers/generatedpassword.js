'use strict';

angular.module('SimbaApp')
    .controller('GeneratedPasswordCtrl', ['$scope', '$modalInstance', 'password',
        function ($scope, $modalInstance, password) {
            $scope.init = function() {
                $scope.password = password;
            };

            $scope.close = function () {
                $scope.password = null;
                $modalInstance.close();
            };
        }]);
