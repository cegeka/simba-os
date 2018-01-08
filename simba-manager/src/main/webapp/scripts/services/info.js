angular.module('SimbaApp')
    .factory('$info', function() {
        var info = {message:'', visible:false};
        return {
            getInfo: function() {
                return info;
            },
            showInfo: function(message) {
                info.message=message;
                info.visible=true;
            },
            hideInfo: function() {
                info.visible=false;
            }
        };
    });