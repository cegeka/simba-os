angular.module('SimbaApp')
    .factory('$group', ['$rest', '$q', function($rest, $q) {
        function getDataForGroup(url, group) {
            var deferred = $q.defer();
                $rest.post(url, group)
            .success(function(data){
                    deferred.resolve(data);
            })
            .error(function(){
                    deferred.reject();
            });
            return deferred.promise;
        }


        return {
            getAll: function() {
               return $rest.get('group/findAll');
            },
            findRoles: function(group) {
                return getDataForGroup('group/findRoles', group);
            },
            findRolesNotLinked: function(group) {
                return getDataForGroup('group/findRolesNotLinked', group);
            },
            findUsers: function(group) {
                return getDataForGroup('group/findUsers', group);
            },
            addRoles: function(group, roles) {
                return $rest.post('group/addRoles', {"group": group, "roles": roles});
            },
            removeRole: function(group, role) {
                return $rest.post('group/removeRole', {"group":group, "role":role});
            },
            refresh: function(group) {
                return getDataForGroup('group/refresh', group);
            }

        };
    }]);
