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

'use strict';

angular.module('SimbaApp')
  .controller('listboxCtrl', ['$scope', '$modalInstance', 'items', 'title', 'itemKey', function ($scope, $modalInstance, items, title, itemKey) {
    $scope.selectedItems = [];
    $scope.items = [];
    $scope.title= 'Items';

    $scope.init = function() {
         initItems(items);
         $scope.title = title;
    };

    $scope.save = function () {
        $modalInstance.close(extractItems());
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.selectionChanged = function(selectedItems) {
        $scope.selectedItems = selectedItems;
    }

    var initItems = function(items) {
        items.forEach(function(item) {
            $scope.items.push({"name": item[itemKey], "item":item});
        })
    }

    var extractItems = function() {
        var items = [];
        $scope.selectedItems.forEach(function(item) {
            items.push(item.item);
        })

        return items;
    }

    }]);
