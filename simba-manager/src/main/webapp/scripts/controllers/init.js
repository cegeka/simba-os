'use strict';

angular.module('SimbaApp')
    .controller("InitCtrl", ['$session', '$property', '$scope', '$resource', function($session, $property ,$scope, $resource) {
        var setCurrentUserName = function(successFunction) {
            return function() {
                if($session.getCurrentUserName() == "") {
                    $session.getCurrentUser().then(function(currentUser) {
                        $session.setCurrentUserName(currentUser.userName);
                        successFunction();
                    }).catch(function() {
                		$scope.templateUrl = '401.html';
           	 		});
                } else {
                    successFunction();
                }
            }
        }

        var setSimbaLocation = function(successFunction) {
            return function() {
                if($property.getSimbaLocation() == "") {
                    $property.setSimbaLocation(successFunction);
                } else {
                    successFunction();
                }
            }
        }

        var setTemplateUrl = function() {
            $scope.templateUrl = 'views/main.html';
        }

        setSimbaLocation(setCurrentUserName(setTemplateUrl))();

    }]);