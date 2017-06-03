/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

angular.module('SimbaApp')
    .factory('$simba_component', ['$modal', '$translate', function($modal, $translate) {
        var checkUndefined = function(input) {
            return typeof input === 'undefined' ? '' : input;
        };

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



