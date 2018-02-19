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
  .controller('SessionCtrl', ['$scope', '$modal', '$log', '$session', '$error', '$translate', '$simba_component',
                  function ($scope, $modal, $log, $session, $error, $translate, $simba_component) {

    $scope.sessions =[];
    
    $scope.init = function() {
        $session.findAllActive().then(
            function(data) {
                $scope.sessions = data;
            });
    };

    $scope.deleteSession = function(session) {
        $session.remove(session).success(function() {
            var index = $scope.sessions.indexOf(session);
            $scope.sessions.splice(index,1);
        })
        .error($error.handlerWithDefault('error.update.failed'));
    };

    $scope.removeAllSessions = function() {
        $session.removeAllButMine().then(function() {
            $scope.init();
        })
        .catch($error.handlerWithDefault('error.update.failed'));
    };

    $scope.timeStampAsDate = function (UNIX_timestamp){
      var a = new Date(UNIX_timestamp);
      var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
      var year = a.getFullYear();
      var month = months[a.getMonth()];
      var date = a.getDate();
      var hour = a.getHours();
      var min = a.getMinutes();
      var sec = a.getSeconds();
      var time = date+' '+month+' '+year+' '+hour+':'+min+':'+sec ;
      return time;
  }

  }]);
