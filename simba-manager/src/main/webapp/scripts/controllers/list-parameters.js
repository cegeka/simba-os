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
  .controller('ListParameterCtrl', ['$scope', '$modal', '$log', '$error', '$translate', '$configuration', '$simba_component',
                  function ($scope, $modal, $log, $error, $translate, $configuration, $simba_component) {

       $scope.parameters =[];

       $scope.init = function() {
          $configuration.findListParameters().then(
              function(data) {
                  $scope.parameters = data;
                  $scope.parameters.forEach(function (p) {
                      p.visible = false;
                  })
              });
       };

       $scope.toggleVisibility = function (param) {
           param.visible = !param.visible;
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