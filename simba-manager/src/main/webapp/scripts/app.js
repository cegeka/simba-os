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
                if (status == 401) {
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
