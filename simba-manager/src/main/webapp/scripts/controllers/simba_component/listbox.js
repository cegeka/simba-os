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
