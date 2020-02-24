angular.module('app.controllers', [])

	.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', function($scope, $window, $location, $filter, Informer, $route) {
	
		$scope.estadonormalizado = [
		    { id: 'S', text: 'Si' },
		    { id: 'N', text: 'No' },
		];
	
	  $scope.listar = function() {
	      $location.path('#/');
	  };
	
	  $scope.crear = function() {
	      // Si estamos copiando un punto y se quiere crear uno nuevo, tenemos que recargar la vista
	      if ($location.path('/new')) {
	          copiaMapeoSemantico = null;
	          $route.reload();
	      }
	      $location.path('/new');
	  };
	  $scope.allInfos = Informer.allInfos;
	  $scope.remove = Informer.remove;
	}])
        .controller('ListCtrl', ['$scope', 'Restangular', 'Informer', function($scope, Restangular, Informer) {
        
        $scope.order = ['id', 'id'];
        $scope.reverse = false;

        var start = 0;
        $scope.registros = [];
        var q = '';

        // $scope.busy = false;
        // $scope.loadMore = function() {
        	
            // if ($scope.busy)
                // return;
            // $scope.busy = true;
            
            Restangular.all('registro-licencia').getList({rows: 50, start: start, q: q/*, sort:'linkedResource.id asc,score desc'*/}, {}).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.totalCount = data.totalCount;
                //$scope.busy = false;
            });
            // start += 50;
        // };
//        $scope.guardar = function(index) {
//        	$scope.datos[index].put({}, {}).then(function(dato) {
//                Informer.inform("El registro se ha editado correctamente.", "success");
//                $scope.datos[index] = dato;
//            }, function() {
//                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
//            });
//        };
//        
//        $scope.preferido = function(index) {
//        	$scope.datos[index].status = 'Z';
//        	$scope.datos[index].put({}, {}).then(function(dato) {
//                Informer.inform("El registro se ha editado correctamente.", "success");
//                $scope.datos[index] = dato;
//            }, function() {
//                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
//            });
//        };
//        
//        $scope.validar = function(index) {
//        	$scope.datos[index].status = 'S';
//        	$scope.datos[index].put({}, {}).then(function(dato) {
//                Informer.inform("El registro se ha editado correctamente.", "success");
//                $scope.datos[index] = dato;
//            }, function() {
//                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
//            });
//        };
//        
//        $scope.rechazar = function(index) {
//        	$scope.datos[index].status = 'N';
//        	$scope.datos[index].put({}, {}).then(function(dato) {
//                Informer.inform("El registro se ha editado correctamente.", "success");
//                $scope.datos[index] = dato;
//            }, function() {
//                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
//            });
//        };
//        
//        $scope.borrar = function(index) {
//        	$scope.datos[index].remove(null, {}).then(function() {
//        		Informer.inform("El registro se ha eliminado correctamente.", "success");
//        		delete $scope.datos[index];
//        	}, function() {
//        		Informer.inform("Se ha producido un error al eliminar el registro.", "danger");
//        	});
//        };
        $scope.buscar = function() {
            q='';
            console.log($scope.query_portal)
            if ($scope.query_portal) {
            	q = q + 'codPortal==' + $scope.query_portal.id;
            }
            if ($scope.query_iae) {
            	if (q.length > 0) {
            		q = q + ';';
            	}
               q = q + 'idIAE==' + $scope.query_iae.id.seccion + $scope.query_iae.id.agrupacion + $scope.query_iae.id.licencia;
               
            }
            
            if ($scope.query_normalizado) {
            	if (q.length > 0) {
            		q = q + ';';
            	}
                q = q + 'normalizado==' + $scope.query_normalizado;
                
             }
            var expediente = '';
            if ($scope.query_expediente) {
                expediente = $scope.query_expediente;
            }
            
            start = 0;
            datosListados = [];
            Restangular.all('registro-licencia').getList({rows: 50, start: start, q: q, expediente: expediente}, {}).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.totalCount = data.totalCount;
            });
            start += 50;

        };

    }])
        .controller('EditCtrl', ['$scope','Dao', 'registro',  '$location', 'Restangular', 'Informer', function($scope, Dao, registro,  $location, Restangular, Informer) {
        	console.log(registro);
        	$scope.registro = registro;
        	$scope.guardar = function() {
	        	var o = Restangular.one('registro-licencia/' + $scope.registro.id);
	        	if (angular.isDefined($scope.registro.iae.identifier)) {
	        		o.iae = $scope.registro.iae.identifier;
	        	}
	        	if (angular.isDefined($scope.registro.emplazamiento.id)) {
	        		o.idVia = $scope.registro.emplazamiento.id;
	        	}
        		Dao.actualizar(o, {}).then(function() {
                    Informer.inform("El registro se ha editado correctamente.", "success");
                    // $location.path('/');
                }, function() {
                    Informer.inform("Se ha producido un error al guardar el registro.", "danger");
                    // $location.path('/');
                });
        		
        };

    }]);
