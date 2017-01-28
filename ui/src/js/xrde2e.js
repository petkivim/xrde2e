var app = angular.module('xrde2e', ['ui.bootstrap']);

app.controller('mainController', function ($scope, $uibModal, $http, $timeout) {
    $scope.search = '';

    $scope.showModal = function (serverIdentifier) {
        $scope.opts = {
            backdrop: true,
            backdropClick: true,
            dialogFade: true,
            keyboard: true,
            templateUrl: 'templates/table_detailed.html',
            controller: ModalInstanceCtrl,
            resolve: {} // empty storage
        };

        $scope.opts.resolve.item = function () {
            return angular.copy({serverIdentifier: serverIdentifier});
        }

        var modalInstance = $uibModal.open($scope.opts);

        modalInstance.result.then(function () {
            //on ok button press 
        }, function () {
            //on cancel button press
        });
    };

    var update = function () {
        $http.get('/api/v1/current').then(function (data) {
            $scope.targets = data.data;
            $scope.timestamp = new Date();
        }), function (data, status, headers) {
            console.log(data);
            console.log(status);
            console.log(headers);
        };
        $timeout(update, 30000);
    };
    $timeout(update, 0);
});

var ModalInstanceCtrl = function ($scope, $http, $uibModalInstance, item) {
    $http.get('/api/v1/history/' + item.serverIdentifier).then(function (data) {
        $scope.targets = data.data;
        $scope.serverIdentifier = item.serverIdentifier;
    }), function (data, status, headers) {
        console.log(data);
        console.log(status);
        console.log(headers);
    };
    $scope.ok = function () {
        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
};