angular.module('SimbaApp')
    .directive('ngConfirmClick', ['$translate',
        function($translate) {
            return {
                link: function (scope, element, attr) {
                    var msg = $translate(attr.ngConfirmClick) || $translate('collectionmanager.confirmremove.text');
                    var clickAction = attr.confirmAction;
                    element.bind('click', function(event) {
                        if(window.confirm(msg)) {
                            scope.$eval(clickAction)
                        }
                    })
                }
            }
        }
     ])
