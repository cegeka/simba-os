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
    .factory('$rest', ['$http', '$q', function ($http, $q) {
        return {
            get: function (url) {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: '/simba-manager/rest/' + url
                }).success(function (data) {
                    deferred.resolve(data);
                }).error(function (data) {
                    deferred.reject(data);
                });
                return deferred.promise;
            },
            post: function (url, requestBody) {
                return $http({
                    method: 'POST',
                    url: '/simba-manager/rest/' + url,
                    data: requestBody,
                    headers: {'Content-Type': 'application/json'}
                });
            }

        };
    }]);



