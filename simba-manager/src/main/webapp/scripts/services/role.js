angular.module('SimbaApp')
    .factory('$role', ['$rest', '$q', function($rest, $q) {
        function getDataForRole(url, role) {
            var deferred = $q.defer();
                $rest.post(url, role)
            .success(function(data){
                    deferred.resolve(data);
            })
            .error(function(){
                    deferred.reject();
            });
            return deferred.promise;
        }

        function getIdsOfTOs(tos) {
            result = [];
            tos.forEach(function(to) {
                result.push(to.id);
            });
            return result;
        }

        return {
            getAll: function() {
               return $rest.get('role/findAll');
            },
            findPolicies: function(role) {
                return getDataForRole('role/findPolicies', role);
            },
            findPoliciesNotLinked: function(role) {
                return getDataForRole('role/findPoliciesNotLinked', role);
            },
            findUsers: function(role) {
                return getDataForRole('role/findUsers', role);
            },
            findUsersNotLinked: function(role) {
                return $rest.post('role/findUsersNotLinked', role);
            },
            addPolicy: function(role, policy) {
                return $rest.post('role/addPolicy', {"role":role, "policy":policy});
            },
            addPolicies: function(role, policies) {
                return $rest.post('role/addPolicies', {"role": role, "policies": policies});
            },
            removePolicy: function(role, policy) {
                return $rest.post('role/removePolicy', {"role":role, "policy":policy});
            },
            removeUser: function(user, role) {
                return $rest.post('role/removeUser', {"user":user, "role":role});
            },
            addUsers: function(role, users) {
                return $rest.post('role/addUsers', {"role":role, "users":users});
            },
            refresh: function(role) {
                return getDataForRole('role/refresh', role);
            },
            createRole: function(name) {
                return $rest.post('role/createRole', {"roleName":name});
            },
            deleteRole: function(role) {
                return $rest.post('role/deleteRole', {"role":role});
            }
        };
    }]);
