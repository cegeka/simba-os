'use strict';

angular.module('SimbaApp')
  .controller('textboxCtrl', ['$scope', '$modalInstance', 'title', function ($scope, $modalInstance, title) {


    $scope.input = {};
    $scope.title = title;
    
    $scope.init = function() {
        $scope.title = title;
    };

    $scope.save = function () {
        $modalInstance.close($scope.input.text);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

  }]);
