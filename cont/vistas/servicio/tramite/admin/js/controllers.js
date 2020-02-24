angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', '$uibModal', 'Restangular',
    function($scope, $window, $location, $filter, Informer, $route, $uibModal, Restangular) {

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

        if (sessionStorage.getItem('materiasProcedimiento') === null) {
        	Restangular.all('admin').all('materia').getList({sort:'title asc',fl:'id,title',showAll:'true'}).then(function(data) {
        			$scope.materiasProcedimiento = data;
        			sessionStorage.setItem('materiasProcedimiento', JSON.stringify(data));    					
        		}, function(result) {
                	Informer.inform(result.data.error || result.data.mensaje, "error");
               });

        } else {
        	$scope.materiasProcedimiento = JSON.parse(sessionStorage.getItem('materiasProcedimiento'));
        }
        
        
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

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {

	$scope.params = Dao.getFiltros(); // Object
    if(angular.equals({}, $scope.params)){
    	$scope.params.start = 0;
    	$scope.params.fl='id,title,plazoInicio,plazoFin,enLinea,certificado,presencial,telefono,correo';
    	$scope.params.sort = 'lastUpdated desc';
//        params.q = [];
//        params.q.push({key:'validation', value:'false'});
    };
    $scope.filtrosParaListado = $scope.params.q;
    $scope.params.start= 0;
    $scope.params.q= [];
    $scope.registros = [];

    $scope.busy = false;
    $scope.loadMore = function() {
    	if(!($scope.start >= $scope.totalCount) && !($scope.totalCount < 50)){
	        if ($scope.busy) return;
	        $scope.busy = true;
	        Dao.listar('admin', $scope.params).then(function(data) {
	            $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
	            if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
	            $scope.totalCount = data.totalCount;
	            $scope.start = data.start;
	            $scope.busy = false;
	        }, function(err){
	            if($scope.params.q !== null){
	                Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
	            } else {
	                Informer.inform("Error en listado: " + err.data.error, "danger");
	            };
	        });
	        $scope.params.start += 50;
    	}
    };

    $scope.buscar = function(qsearch) {
//        var params = {};
    	$scope.params.start = 0;
    	$scope.params.rows = 50;
    	$scope.params.fl='id,title,plazoInicio,plazoFin,enLinea,certificado,presencial,telefono,correo';
    	$scope.params.sort = 'lastUpdated desc';
    	$scope.params.q = [];
        _.each(qsearch, function(value, key){
            var item = new Object();
            item.key = key; // Campo de busqueda. Ej: 'title'
            
            if (value === 'true') {
            	value = true;
            }
            if (value === 'false') {
            	value = false;
            }
            item.value = value; // Valor del campo de busqueda actual

            if(key === 'top' || key==='id') {
                item = {
                   key: key,
                   op: '==',
                   exact:true,
                   value: value
            	}
            }
            if(key === 'materia' && !isNaN(value)) {
            	$scope.params.materia = parseInt(item.value);
                item.value = '';
            } else {
            	$scope.params.materia = '';
            }
            
            if(item.value && item.value !== '') {
            	 // Si esta vacío el valor no se añade
            	$scope.params.q.push(item);
            }
        });
        Dao.setFiltros($scope.params);
        $scope.filtrosParaListado = $scope.params.q;
        Dao.listar('admin', $scope.params).then(function(data) {
            $scope.totalCount = data.totalCount;
            if(data.totalCount == 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
            // Si devuelve un único resultado, se envía directamente al detalle.
            if(data.totalCount === 1){
                $location.path('/edit/' + data[0].id);
            } else {
                $scope.registros = data;   
            };
        });
        $scope.params.start += 50;
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
            
            Dao.crear('admin', $scope.registro).then(function() {
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

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout',
    function($scope, Dao, $http, registro, Restangular, $location, $route, Informer, $filter, $window, $timeout) {
		
        $scope.registro = registro;

        $scope.guardar = function() {
            Dao.actualizar($scope.registro).then(function(data) {
                Informer.inform("El registro se ha editado correctamente.", "success");
                $route.reload();
            }, function(mensaje) {
                Informer.inform(mensaje.data.mensaje, "danger");
            });
        };
      
        $scope.portalSelected = $scope.registro.portal;
        
        $scope.onSelectPortal = function($item, $model){
            if(angular.isUndefined($scope.registro.portal)) {
                $scope.registro.portal = [];
            }

            var avisoPortal = {
                id: parseInt($item.id), 
                title: $item.title
            }
            $scope.registro.portal.push(avisoPortal);
        };
        $scope.onRemovePortal = function($item, $model){
            $scope.registro.portal = _.reject($scope.registro.portal, function(p){
            	console.log(p.id + ':' + $item.id + ':' + (p.id == $item.id));
            	return p.id == $item.id; }
            );
        };
    }
])
