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
        .error(function() {
            $error.showError($translate('error.update.failed'));
        });
    };

    $scope.removeAllSessions = function() {
        $session.removeAllButMine().then(function() {
            $scope.init();
        })
        .catch(function() {
            $error.showError($translate('error.update.failed'));
        });
    }

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
