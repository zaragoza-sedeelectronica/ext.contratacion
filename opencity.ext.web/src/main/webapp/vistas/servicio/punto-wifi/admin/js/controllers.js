angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', '$uibModal',
    function($scope, $window, $location, $filter, Informer, $route, $uibModal) {

        $scope.listar = function() {
            $location.path('#/');
        };

        $scope.crear = function() {
            // Si estamos copiando un punto y se quiere crear uno nuevo, tenemos que recargar la vista
            if ($location.path('/new')) {
            	copiaRegistro = null;
                $route.reload();
            }

            $location.path('/new');

        };

        $scope.lanzarModalBusqueda = function() {
            var modalInstance = $uibModal.open({
                templateUrl: 'templates/busquedaModal.html',
                controller: function($scope, $uibModalInstance) {

                    $scope.busqueda = function() {
                        $uibModalInstance.close();
                    };

                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                }
            });

            modalInstance.result.then(function(selectedItem) {
                // $scope.selected = selectedItem;
            }, function() {
                //$log.info('Modal dismissed at: ' + new Date());
            });

        };

        $scope.modoBusqueda = 0;
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao',
    function($scope, Dao) {

        var params = {};
        params.start = 0;
        $scope.registros = [];
        
        $scope.busy = false;
        $scope.results = true;
        $scope.loadMore = function() {
            if ($scope.busy || !$scope.results) return;
            $scope.busy = true;
            Dao.listar('punto-wifi', params).then(function(data) {
            	if (data.length > 0) {
            		$scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                	$scope.busy = false;
            	} else {
            		$scope.results = false;
            		$scope.busy = false;
            	}
            });
            params.start += 50;
        };


    }
])

.controller('CreateCtrl', ['$scope', '$location', 'Dao', 'Informer',
    function($scope, $location, Dao, Informer) {
        // Si copiaRegistro === null estamos creando un registro nuevo
        $scope.registro = copiaRegistro === null ? {} : copiaRegistro;
        
        if (angular.isUndefined($scope.registro.geometry)) {
            $scope.registro.geometry = {};
            var coordinates = [0,0];
            $scope.registro.geometry.type = "Point";
            $scope.registro.geometry.coordinates = coordinates;
        }

        $scope.guardar = function() {
            
            Dao.crear('punto-wifi', $scope.registro).then(function() {
                Informer.inform("El registro se ha creado correctamente.", "success");
                $location.path('/');
            }, function() {
                Informer.inform("Se ha producido un error al crear el registro.", "danger");
                $location.path('/');
            });
            delete copiaRegistro;
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', '$location', 'Informer',
    function($scope, Dao, registro, Restangular, $location, Informer) {

        $scope.registro = registro;

        $scope.guardar = function() {
            Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                $location.path('/');
            }, function() {
                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
                $location.path('/');
            });
        };
    }
])
