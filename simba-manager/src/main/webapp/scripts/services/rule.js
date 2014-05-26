angular.module('SimbaApp')
    .factory('$rule', ['$session', '$property', function($session, $property) {
        var rules = {};

        return {
            evaluateRule: function(resourceName, operation) {
                var key = resourceName + operation;
                if(typeof rules[key] !== 'undefined') {
                    return rules[key];
                }

                var transport = new Thrift.Transport($property.getSimbaLocation() + '/authorizationService');
                var protocol  = new Thrift.Protocol(transport);

                var client = new AuthorizationServiceClient(protocol);

                rules[key] = client.isResourceRuleAllowed($session.getCurrentUserName(), resourceName, operation).allowed;
                return rules[key];
            }
        };
    }]);
