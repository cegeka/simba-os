angular.module('SimbaApp')
    .factory('$user', ['$rest', function($rest) {
        return {
            getAll: function() {
                return $rest.get('user/findAll');
            },
            add: function(user) {
                return $rest.post('user/create', user);
            },
            findByRole: function(role) {
                return $rest.post('user/findByRole', role);
            },
            findRoles: function(user) {
                return $rest.post('user/findRoles', user);
            },
            findGroups : function(user) {
                return $rest.post('user/findGroups', user);
            },
            findRolesNotLinked: function(user) {
                return $rest.post('user/findRolesNotLinked', user);
            },
            removeRole: function(user, role) {
                return $rest.post('user/removeRole', {'user':user, 'role':role});
            },
            addRoles: function(user, roles) {
                return $rest.post('user/addRoles', {'user':user, 'roles':roles});
            },
            findPolicies: function(user) {
                return $rest.post('user/findPolicies', user);
            },
            resetPassword: function(user) {
                return $rest.post('user/resetPassword', user);
            },
            createWithRoles: function(user, roleNames) {
                return $rest.post('user/createWithRoles', user, roleNames);
            },
            update: function(user) {
                return $rest.post('user/update', user);
            },
            getSuccessUrls: function() {
                return ['/dummyURL/'];
            },
            refresh: function(user) {
                return $rest.post('user/refresh', user);
            },
        };
    }]);
