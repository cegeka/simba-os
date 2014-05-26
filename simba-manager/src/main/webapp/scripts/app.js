'use strict';

angular.module('SimbaApp', ['ui.bootstrap', 'pascalprecht.translate', 'ngRoute', 'ngResource', 'xeditable'])
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
  .config(['$translateProvider', '$translatePartialLoaderProvider', function($translateProvider, $translatePartialLoaderProvider) {
    $translateProvider.useLoader('$translatePartialLoader', {urlTemplate: '/simba-manager/resources/locale/{lang}/{part}.json'});
    $translateProvider.preferredLanguage('nl_NL');
  }])
  .config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.withCredentials = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
  }
]);
