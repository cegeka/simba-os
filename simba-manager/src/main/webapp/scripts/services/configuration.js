angular.module('SimbaApp')
    .factory('$configuration', ['$rest', function($rest) {
        return {
            getValue: function(parameter) {
                return $rest.post('configuration/getValue', parameter);
            },
            findUniqueParameters: function() {
                return $rest.get('configuration/findUniqueParameters');
            },
            findListParameters: function() {
                return $rest.get('configuration/findListParameters');
            },
            changeParameter: function(parameter, successCallback, errorCallback) {
                return $rest.post('configuration/changeParameter', parameter)
                    .success(successCallback)
                    .error(errorCallback);
            },
            changeListParameter: function(parameter, successCallback, errorCallback) {
                 return $rest.post('configuration/changeListParameter', parameter)
                    .success(successCallback)
                    .error(errorCallback);
            },
        };
    }]);
