angular.module('SimbaApp')
    .factory('$rest', ['$http', '$q', '$property', function($http, $q, $property) {
        return {
            get: function(url) {
                var deferred = $q.defer();
                    $http({method: 'GET', url: $property.getSimbaLocation() + '/manager/' + url })
                .success(function(data){
                        deferred.resolve(data);
                })
                .error(function(){
                        deferred.reject();
                });
                return deferred.promise;
            },
            post: function(url, requestBody) {
                return $http({ method: 'POST',
                        url: $property.getSimbaLocation() + '/manager/' + url,
                        data: requestBody,
                        headers:{'Content-Type':'application/json'}
                });
            }
        };
    }]);



