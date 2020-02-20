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

        if (sessionStorage.getItem('portalesAviso') === null) {
        	Restangular.all('aviso').all('admin').all('portal').getList({sort:'title asc',fl:'id,title',showAll:'true'}).then(function(data) {
        			$scope.portalesAviso = data;
        			sessionStorage.setItem('portalesAviso', JSON.stringify(data));    					
        		}, function(result) {
                	Informer.inform(result.data.error || result.data.mensaje, "error");
               });

        } else {
        	$scope.portalesAviso = JSON.parse(sessionStorage.getItem('portalesAviso'));
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

        var params = {};
        params.start = 0;
        $scope.registros = [];
        
        $scope.busy = false;
        $scope.results = true;
        $scope.buscar = function(qsearch) {
        	$scope.registros = [];
        	console.log(qsearch)
            var params = {};
            params.start = 0;
            params.rows = 100; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(key === 'portal') {
                	item.key = 'portal.id';
                }
                if(key === 'preferential' || key === 'visible') {
                    item = {
                       key: key,
                       op: '==',
                       exact:true,
                       value: value
                	}
                }
                if(item.value !== '' && item.value != null) {params.q.push(item);}; // Si esta vacío el valor no se añade
               // delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('aviso/admin', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/edit/' + data[0].id);
                } else {
                    $scope.registros = data;    
                }
            });
        };
        $scope.loadMore = function() {
            // Si params.q está definido venimos de búsqueda
            if(params.q && !($scope.start >= $scope.totalCount) && !($scope.totalCount < 50)){
                if ($scope.busy) return;
                $scope.busy = true;

                Dao.listar('list', params).then(function(data) {
                    $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                    if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
                    $scope.totalCount = data.totalCount;
                    $scope.start = data.start;
                    $scope.busy = false;
                }, function(err){
                    if(params.q !== null){
                        Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                    } else {
                        Informer.inform("Error en listado: " + err.data.error, "danger");
                    };
                });   
                params.start += 50;
            };
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
            
            Dao.crear('aviso/admin', $scope.registro).then(function() {
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
            }, function() {
                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
            });
        };
        $scope.imageShown = null;
        $scope.clearThumbs = function() {
        	$scope.imageShown = null;
        	$scope.registro.image = null;
        	jQuery('#anexoImagen').val('');
        }
     // Establecemos la visualización previa de la imagen almacenada en los objetos imagenVertical e imagenHorizontal
        if(angular.isDefined($scope.registro)) {
            if(angular.isDefined($scope.registro.image)) { $scope.imagenVertical = $scope.registro.image;};
        };
        // Anexo:Image Crop
        $scope.imageCrop = {
           originalImage: '',
           croppedImage: ''
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
