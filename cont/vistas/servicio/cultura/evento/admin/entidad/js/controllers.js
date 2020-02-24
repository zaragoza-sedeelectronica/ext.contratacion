angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao',
    function($scope, $window, $location, $filter, Informer, $route, Dao) {

        $scope.listar = function() {
            Dao.limpiarFiltros();
            if($location.path() == '/'){ // ListCtrl
                $route.reload();
            } else {
                $location.path('/'); // DetailCtrl
            };
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            }

            $location.path('/new');

        };

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.q = [];
            params.q.push({key:'validation', value:'false'});
        };
        $scope.filtrosParaListado = params.q;

        $scope.registros = [];

        $scope.busy = false;
        $scope.loadMore = function() {
            // Si params.q está definido venimos de búsqueda
            // if(params.q && !($scope.start >= $scope.totalCount)){
                if ($scope.busy) return;
                $scope.busy = true;

                Dao.listar('entidad', params).then(function(data) {
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
            // };
        };

        $scope.buscar = function(qsearch) {
            var params = {};
            params.start = 0;
            params.rows = 500;
            // params.sort = 'lastUpdated desc';
            params.q = [];
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
                if(key === 'id') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                    params.id = parseInt(value);
                    item.value = '';
                };
                if(item.value !== '') {
                	 // Si esta vacío el valor no se añade
                	params.q.push(item);
                };
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            if (params.q.length <= 0) {
            	delete params.q;
            }
            Dao.listar('entidad', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/edit/' + data[0].id);
                } else {
                    $scope.registros = data;   
                };
            });
        };


    }
])

.controller('CreateCtrl', ['$scope', '$location', 'Dao', 'Informer', 'registro',
        function($scope, $location, Dao, Informer, registro) {

            if(typeof copiaRegistro === 'undefined' || copiaRegistro === null){
                $scope.registro = registro;
            } else {
                $scope.registro = copiaRegistro;
                delete copiaRegistro;
            };

            $scope.location = $location.path();
            $scope.isCollapsedEquip = true;

            $scope.guardar = function() {
                Dao.crear('entidad', $scope.registro).then(function(res) {
                    Informer.inform("El registro se ha creado correctamente.", "success");
                    $location.path('/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro:" + err.data.error, "danger");
                    // $location.path('/');
                });
                // // TODO: Comprobar Eliminar el objeto copiaPunto, para prevenir que tenga contenido de nuevo
                // delete copiaRegistro;
            };

            $scope.generarCodigoZ16 = function() {
            	var text = "";
            	var possible = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

            	for (var i = 0; i < 8; i++) {
            		text += possible.charAt(Math.floor(Math.random() * possible.length));
            	}
            	return text;
            }
        }
    ])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', 'RestangularActo', '$location', 'Informer', '$http',
    function($scope, Dao, registro, Restangular, RestangularActo, $location, Informer,$http) {

        $scope.registro = registro;
        $scope.isCollapsedEquip = true;

        if ($scope.permisos.LIDER) {
	        $http.get('/sede/servicio/cultura/evento/admin/entidad/' + registro.id + '/lider', {}).then(function(res) {
	            $scope.lideres = res.data;
	        });
        };
        $scope.guardar = function() {
            Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                $route.reload();
                // $location.path('/');
            }, function(err) {
                Informer.inform('Se ha producido un error al guardar el registro: ' + err.data.error, 'danger');
                // $location.path('/');
            });
        };
        $scope.asociarLider = function(login) {
        	$http.get('/sede/servicio/cultura/evento/admin/entidad/' + $scope.registro.id + '/lider/' + login + '/add', {}).then(function(res) {
                Informer.inform('Líder asociado correctamente', 'success');
                $http.get('/sede/servicio/cultura/evento/admin/entidad/' + registro.id + '/lider', {}).then(function(res) {
    	            $scope.lideres = res.data;
    	        });
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        }
        
        $scope.sustituirEntidad = function(actual, nuevo) {
        	$http.get('/sede/servicio/cultura/evento/admin/entidad/' + actual + '/sustituir/' + nuevo + '/del', {}).then(function(res) {
                Informer.inform(res.data.mensaje, 'success');
                $location.path('/');
            }, function(res){
                Informer.inform('Error al sustituir: ' + res.data.mensaje, 'danger');
            });
        }
        
        $scope.eliminarLider = function(userId) {
        	$http.delete('/sede/servicio/cultura/evento/admin/entidad/' + $scope.registro.id + '/lider/' + userId, {}).then(function(res) {
                Informer.inform('Líder eliminado correctamente', 'success');
                $scope.lideres = res.data;
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        }

        $scope.actosAsociados = [];
        RestangularActo.all('list').getList({q: 'organizer.id==' + registro.id, fl: 'id,title'}).then(function(res){
            $scope.actosAsociados = res;
        });
        
        $scope.generarCodigoZ16 = function() {
        	var text = "";
        	var possible = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

        	for (var i = 0; i < 8; i++) {
        		text += possible.charAt(Math.floor(Math.random() * possible.length));
        	}
        	return text;
        }
    }
]);
