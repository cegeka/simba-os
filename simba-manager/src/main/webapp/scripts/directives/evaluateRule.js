angular.module('SimbaApp')
    .directive('ngEvaluateRule', ['$rule',
        function($rule) {
            return {
                link: function (scope, element, attr) {
                    var ruleName = attr.ngEvaluateRule;
                    var ruleParts = ruleName.split(",");
                    var display = 'display:none'

                    if(ruleParts.length == 2) {
                        if($rule.evaluateRule(ruleParts[0], ruleParts[1])) {
                            element.removeAttr('style');
                            return;
                        }
                    }

                    element.attr('style', display);

                }
            }
        }
     ])
