angular.module('SimbaApp')
    .factory('$policy', ['$rest', '$q', function($rest, $q) {
        function getDataForPolicy(url, policy) {
            var deferred = $q.defer();
                $rest.post(url, policy)
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
               return $rest.get('policy/findAll');
            },
            findRoles: function(policy) {
                return getDataForPolicy('policy/findRoles', policy);
            },
            findRolesNotLinked: function(policy) {
                return getDataForPolicy('policy/findRolesNotLinked', policy);
            },
            findRules: function(policy) {
                return getDataForPolicy('policy/findRules', policy);
            },
            findRulesNotLinked: function(policy) {
                return getDataForPolicy('policy/findRulesNotLinked', policy);
            },
            addRoles: function(policy, roles) {
                return $rest.post('policy/addRoles', {"policy": policy, "roles": roles});
            },
            removeRole: function(role, policy) {
                return $rest.post('policy/removeRole', {"role":role, "policy":policy});
            },
            removeRule: function(rule, policy) {
                return $rest.post('policy/removeRule', {"rule":rule, "policy":policy});
            },
            addRules: function(policy, rules) {
                return $rest.post('policy/addRules', {"policy":policy, "rules":rules});
            },
            refresh: function(policy) {
                return getDataForPolicy('policy/refresh', policy);
            },
            createPolicy: function(name) {
                return $rest.post('policy/create', name);
            },
            deletePolicy: function(policy) {
                return $rest.post('policy/delete', {"policy":policy});
            }
        };
    }]);
