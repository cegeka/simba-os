angular.module('SimbaApp')
    .factory('$session', ['$rest', function($rest) {
        var currentUserName = "";
        return {
            getCurrentUser: function() {
                return $rest.get('session/getCurrentUser');
            },
            setCurrentUserName: function(userName) {
                currentUserName = userName;
            },
            getCurrentUserName: function() {
                return currentUserName;
            },
            findAllActive : function() {
                return $rest.get('session/findAllActive');
            },
            removeAllButMine : function() {
                return $rest.get('session/removeAllButMine');
            },
            remove : function(session) {
                return $rest.post('session/remove', session);
            },
        };
    }]);
