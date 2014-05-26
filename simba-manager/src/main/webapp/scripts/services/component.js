angular.module('SimbaApp')
    .factory('$simba_component', ['$modal', '$translate', function($modal, $translate) {
        var checkUndefined = function(input) {
            return typeof input === 'undefined' ? '' : input;
        }

        return {
            textbox: function(title, prefill) {
                return $modal.open({templateUrl: 'views/modals/common/textbox.html',
                                    controller: 'textboxCtrl',
                                    resolve: {
                                        title: function() {
                                            return checkUndefined(title);
                                        },
                                        prefill: function() {
                                            return checkUndefined(prefill);
                                        }
                                    }
                                   });
            },
            listbox: function(title, items, itemKey) {
                return $modal.open({templateUrl: 'views/modals/common/listbox.html',
                                    controller: 'listboxCtrl',
                                    resolve: {
                                        items: function () {
                                            return items;
                                        },
                                        title: function() {
                                            return checkUndefined(title);
                                        },
                                        itemKey: function() {
                                            return itemKey;
                                        }
                                    }
                                    });
            }
        };
    }]);



