angular.module('SimbaApp')
    .factory('$property', ['$http', '$q', function($http, $q) {
        var simbaLocation = "";
        var managerLocation = "";
        function setManagerLocation() {
            var url = window.location.href;
            var arr = url.split("/");
            managerLocation = arr[0] + "//" + arr[2];
        }
        return {
            setSimbaLocation: function(successFunction) {
                setManagerLocation();
                $http({method: 'GET', url: managerLocation+'/simba-manager/simba-locator'}).
                    success(function(data) {
                      simbaLocation = data;
                      successFunction();
                    })
                    .error(function() {
                        successFunction();
                    });
            },
            getSimbaLocation: function() {
                return simbaLocation;
            }
        };
    }]);



