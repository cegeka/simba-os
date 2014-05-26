'use strict';

angular.module('SimbaApp')
  .controller('UniqueParameterCtrl', ['$scope', '$modal', '$log', '$error', '$translate', '$configuration',
                  function ($scope, $modal, $log, $error, $translate, $configuration) {

       $scope.parameters =[];
       $scope.currentValue = null;

       $scope.init = function() {
          $configuration.findUniqueParameters().then(
              function(data) {
                  $scope.parameters = data;
              });
       };

       $scope.changeParameter = function(parameter) {
            $configuration.changeParameter(parameter,
                undefined,
                function(){
                    $error.showError('error.update.failed');
                    parameter.value = $scope.currentValue;
                });
       }

       $scope.cacheCurrentValue = function(value) {
            $scope.currentValue = value;
       }

}]);