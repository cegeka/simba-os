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
    .controller('CacheCtrl', ['$scope', '$log', '$error', '$translate', '$cache', '$timeout',
        function ($scope, $log, $error, $translate, $cache, $timeout) {

            $scope.cacheEnabled = true;
            $scope.feedbackCounter = 0;

            $scope.init = function () {
                $scope.retrieveCacheStatus();
            };

            $scope.showFeedback = function (feedback) {
                $translate(feedback).then(function (translation) {
                    $scope.feedback = translation;
                    $scope.feedbackCounter++;
                });
                $timeout(function () {
                    $scope.feedbackCounter--;
                    if($scope.feedbackCounter == 0){
                        $scope.feedback = null;
                    }
                }, 3000);
            };

            $scope.refreshCache = function () {
                $cache.refresh().then(function () {
                    $scope.showFeedback('configuration.cache.refreshed');
                }).catch($error.handlerWithDefault('error.loading.data'));
            };

            $scope.enableCache = function () {
                if (!$scope.cacheEnabled) {
                    $cache.enable().then(function () {
                        $scope.showFeedback('configuration.cache.set.enabled');
                    }).catch($error.handlerWithDefault('error.loading.data'));
                }else{
                    $scope.showFeedback('configuration.cache.already.enabled');
                }
            };

            $scope.disableCache = function () {
                if ($scope.cacheEnabled) {
                    $cache.disable().then(function () {
                        $scope.showFeedback('configuration.cache.set.disabled');
                    }).catch($error.handlerWithDefault('error.loading.data'));
                }else {
                    $scope.showFeedback('configuration.cache.already.disabled');
                }
            };

            $scope.retrieveCacheStatus = function () {
                $cache.isEnabled().success(function (result) {
                    $scope.cacheEnabled = result == 'true';
                });
            }
        }]);