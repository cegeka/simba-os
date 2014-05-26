'use strict';

angular.module('SimbaApp')
  .controller('MainCtrl', ['$scope', '$translate', '$translatePartialLoader', '$error', '$rule', function ($scope, $translate, $translatePartialLoader, $error, $rule) {
    $scope.tabs;
    $scope.error =  $error.getError();

    $scope.init = function() {
        $scope.tabs = getTabs();
        $scope.languages = [
            {id:'nl_NL', value:'Nederlands'},
            {id:'fr_FR', value:'Français'},
            {id:'en_US', value:'English'}
        ];
        $scope.selectedLanguage = $scope.languages[0];
        $translatePartialLoader.addPart('locale');
        $translatePartialLoader.addPart('error');
        $translate.refresh();
    };

    $scope.changeLanguage = function(language)  {
        $translate.uses(language.id);
    };

    $scope.hideErrorMessage = function() {
        $error.hideError();
    };

    var getTabs = function() {
        var tabs = [
            {id: 'Dashboard', title: 'menu.dashboard', active: true, url: 'views/dashboard.html', resourceName: 'manage-users', operation: 'READ'},
            {id: 'Sessions', title: 'menu.sessions', active: false, url: 'views/sessions.html', resourceName: 'manage-sessions', operation: 'READ'},
            {id: 'Configuration', title: 'menu.configuration',  active: false, url: 'views/configuration.html', resourceName: 'manage-sessions', operation: 'READ'}
        ];
        tabs.forEach(function(tab) {
            tab.disabled = !$rule.evaluateRule(tab.resourceName, tab.operation);
        });
        return tabs;
    };
  }]);
