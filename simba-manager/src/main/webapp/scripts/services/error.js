angular.module('SimbaApp')
    .factory('$error', function() {
        var error = {message:'', visible:false};
        return {
            getError: function() {
                return error;
            },
            showError: function(message) {
                error.message=message;
                error.visible=true;
            },
            hideError: function() {
                error.visible=false;
            }
        };
    });
