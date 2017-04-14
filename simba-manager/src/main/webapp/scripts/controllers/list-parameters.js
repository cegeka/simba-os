'use strict';

angular.module('SimbaApp')
  .controller('ListParameterCtrl', ['$scope', '$modal', '$log', '$error', '$translate', '$configuration', '$simba_component',
                  function ($scope, $modal, $log, $error, $translate, $configuration, $simba_component) {

       $scope.parameters =[];

       $scope.init = function() {
          $configuration.findListParameters().then(
              function(data) {
                  $scope.parameters = data;
              });
       };

       $scope.deleteValueFromParameter = function(parameter, value) {
            var index = parameter.value.indexOf(value);
            parameter.value.splice(index,1);
            $scope.changeParameter(parameter);
       }

       $scope.addParameter = function(parameter) {
           var textbox =  $simba_component.textbox($translate('add.parameter'));

           textbox.result.then(function (value) {
               parameter.value.push(value);
               $scope.changeParameter(parameter);
           }, function () {
               $log.info('Modal dismissed at: ' + new Date());
           });
       };

       $scope.changeParameter = function(parameter) {
            $configuration.changeListParameter(parameter,
                undefined,
                function(){
                    $error.showError('error.update.failed');
                    init();
                });
       }

}]);