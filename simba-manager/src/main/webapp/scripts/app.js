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

angular.module('SimbaApp', ['ui.bootstrap', 'pascalprecht.translate', 'ngRoute', 'ngResource', 'xeditable', 'ngIdle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/modals/common/loading.html',
                controller: 'InitCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .config(['$translateProvider', '$translatePartialLoaderProvider', function ($translateProvider, $translatePartialLoaderProvider) {
        $translateProvider.useLoader('$translatePartialLoader', {urlTemplate: '/simba-manager/resources/locale/{lang}/{part}.json'});
        $translateProvider.preferredLanguage('nl_NL');
        $translateProvider.useSanitizeValueStrategy('escape');
    }])
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        $httpProvider.defaults.withCredentials = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];

        var interceptor = ['$rootScope', '$q', function (scope, $q) {
            function success(response) {
                return response;
            }

            function error(response) {
                var status = response.status;
                if (status === 401) {
                    //AuthFactory.clearUser();
                    window.location = "/simba-manager/";
                    return;
                }
                // otherwise
                return $q.reject(response);
            }

            return function (promise) {
                return promise.then(success, error);
            }
        }];
        $httpProvider.responseInterceptors.push(interceptor);

    }
    ]);
