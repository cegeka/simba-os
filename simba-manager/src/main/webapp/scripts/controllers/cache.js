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
    .controller('CacheCtrl', ['$scope', '$log', '$error', '$translate', '$configuration', '$cache',
        function ($scope, $log, $error, $translate, $configuration, $cache) {

            $scope.cacheEnabled = true;

            $scope.init = function () {
                $scope.retrieveCacheStatus();
            };

            $scope.refreshCache = function () {
                $cache.refresh();
            };

            $scope.enableCache = function () {
                if (!$scope.cacheEnabled) {
                    $cache.enable();
                }
            };

            $scope.disableCache = function () {
                if ($scope.cacheEnabled) {
                    $cache.disable();
                }
            };

            $scope.retrieveCacheStatus = function () {
                $cache.isEnabled().success(function (result) {
                    $scope.cacheEnabled = result == 'true';
                });
            }
        }]);