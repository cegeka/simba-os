'use strict';

angular.module('SimbaApp')
    .controller('MainCtrl', ['$scope', '$translate', '$translatePartialLoader', '$error', '$rule', 'Idle', '$modal', function ($scope, $translate, $translatePartialLoader, $error, $rule, Idle, $modal) {
        $scope.tabs;
        $scope.error = $error.getError();

        $scope.init = function () {
            $scope.tabs = getTabs();
            $scope.languages = [
                {id: 'nl_NL', value: 'Nederlands'},
                {id: 'fr_FR', value: 'Français'},
                {id: 'en_US', value: 'English'}
            ];
            $scope.selectedLanguage = $scope.languages[0];
            $translatePartialLoader.addPart('locale');
            $translatePartialLoader.addPart('error');
            $translate.refresh();
        };

        $scope.changeLanguage = function (language) {
            $translate.use(language.id);
        };

        $scope.hideErrorMessage = function () {
            $error.hideError();
        };

        var getTabs = function () {
            var tabs = [
                {id: 'Dashboard', title: 'menu.dashboard', active: true, url: 'views/dashboard.html', resourceName: 'manage-users', operation: 'READ'},
                {id: 'Sessions', title: 'menu.sessions', active: false, url: 'views/sessions.html', resourceName: 'manage-sessions', operation: 'READ'},
                {id: 'Configuration', title: 'menu.configuration', active: false, url: 'views/configuration.html', resourceName: 'manage-sessions', operation: 'READ'}
            ];
            tabs.forEach(function (tab) {
                tab.disabled = !$rule.evaluateRule(tab.resourceName, tab.operation);
            });
            return tabs;
        };

        function closeModals() {
            if ($scope.warning) {
                $scope.warning.close();
                $scope.warning = null;
            }
        }

        $scope.$on('IdleStart', function () {
            closeModals();

            $scope.warning = $modal.open({
                templateUrl: 'views/idledialog.html',
                windowClass: 'modal-danger'
            });
        });

        $scope.$on('IdleEnd', function () {
            closeModals();
        });

        $scope.$on('IdleTimeout', function () {
            window.location = '/simba-manager/?SimbaAction=SimbaLogoutAction';
        });

        $scope.reset = function () {
            Idle.watch();
        };
    }])
    .config(['KeepaliveProvider', 'IdleProvider', function (KeepaliveProvider, IdleProvider) {
        IdleProvider.idle(900);
        IdleProvider.timeout(30);
        KeepaliveProvider.interval(10);
    }])
    .run(['Idle', function (Idle) {
        console.log("App started");
        Idle.watch();
    }]);
