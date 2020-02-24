angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao', 
    function($scope, $window, $location, $filter, Informer, $route, Dao) {

	    $scope.listarActividades = function() {
	        Dao.limpiarFiltros();
	        $scope.checkList = [];
	        if ($location.path() == '/activities') { // ListCtrl
	            $route.reload();
	        } else {
	            $location.path('/activities'); // DetailCtrl
	        };
	    };
	    
        $scope.crearActividad = function() {
            if ($location.path == '/activities/new') {
                copiaRegistro = null;
                $route.reload();
            }
            $location.path('/activities/new');
        };
        $scope.revisar = function() {
            $location.path('/check');
        };
        
        $scope.listarAmbitos = function() {
        	if ($location.path() == '/scopes') {
        		$route.reload();
        	} else {
        		$location.path('/scopes');
        	}
        };
        
        $scope.crearAmbito = function() {
        	if ($location.path == '/scopes/new') {
        		$route.reload();
        	}
        	$location.path('/scopes/new');
        };
        
        $scope.listarEntidades = function() {
        	if ($location.path() == '/entities') {
        		$route.reload();
        	} else {
        		$location.path('/entities');
        	}
        };
        
        $scope.crearEntidad = function() {
        	if ($location.path == '/entities/new') {
        		$route.reload();
        	}
        	$location.path('/entities/new');
        };
        
        $scope.listarMonitores = function() {
        	if ($location.path() == '/monitors') {
        		$route.reload();
        	} else {
        		$location.path('/monitors');
        	}
        };
        
        $scope.crearMonitor = function() {
        	if ($location.path == '/monitors/new') {
        		$route.reload();
        	}
        	$location.path('/monitors/new');
        };
        
        $scope.crearDistrito = function() {
        	if ($location.path == '/districts/new') {
        		$route.reload();
        	}
        	$location.path('/districts/new');
        };
        
        $scope.listarDistritos = function() {
        	if ($location.path() == '/districts') {
        		$route.reload();
        	} else {
        		$location.path('/districts');
        	}
        };
        
        $scope.crearFestivo = function() {
        	if ($location.path == '/holidays/new') {
        		$route.reload();
        	}
        	$location.path('/holidays/new');
        };
        
        $scope.listarFestivos = function() {
        	if ($location.path() == '/holidays') {
        		$route.reload();
        	} else {
        		$location.path('/holidays');
        	}
        };
        
        $scope.listarSolicitudes = function() {
        	if ($location.path() == '/requests') {
        		$route.reload();
        	} else {
        		$location.path('/requests');
        	}
        };
        
        $scope.crearSolicitud = function() {
        	if ($location.path == '/requests/new') {
        		$route.reload();
        	}
        	$location.path('/requests/new');
        };
        
        $scope.formularioInforme = function() {
        	if ($location.path() == '/requests/report') {
        		$route.reload();
        	}
        	$location.path('/requests/report');
        }
        
        $scope.listarFacturas = function() {
        	if ($location.path() == '/invoices') {
        		$route.reload();
        	} else {
        		$location.path('/invoices');
        	}
        };
        
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;
        $scope.checkList = []; // Array checkbox multiple (listado)

    }

])

.controller('ScopeListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if (angular.equals({}, params)) {
            params.start = 0;
            params.rows = 20;
            params.sort = 'id asc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = false;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        loadPage();

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/ambito/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id asc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/ambito/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/scopes/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        }

    }
])

.controller('ScopeEditCtrl', ['$scope', 'Dao', '$http', 'registro', '$location', '$route', 'Informer', '$filter', '$window', '$timeout', 
    function($scope, Dao, $http, registro, $location, $route, Informer, $filter, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        $scope.guardar = function() {
            var queryParams = {}; 
            if($location.path() === '/scopes/new') {
                Dao.crear('evento/admin/banco/ambito', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/scopes/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
    }
])


.controller('EntityListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'id asc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = false;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        loadPage();

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/entidad/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id asc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/entidad/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/entities/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('EntityEditCtrl', ['$scope', 'Dao', '$location', 'registro', 'Informer', 'Restangular', '$route',
	function($scope, Dao, $location, registro, Informer, Restangular, $route) {
		$scope.debug = false;
		$scope.location = $location.path();
		$scope.registro = registro;
		
        $scope.actividadesOfertadas = [];
        if ($location.path() !== '/entities/new') {
        	Restangular.all('evento').all('admin').all('banco').all('list').getList({q: 'entity.id==' + registro.id, fl: 'id,title', sort: 'id'}).then(function(res){
                $scope.actividadesOfertadas = res;
            });
        }
        
        $scope.seleccionarMonitor = function(reg) {
            if(angular.isUndefined($scope.registro.monitors)) {
                $scope.registro.monitors = [];
            };
            var found = false;
            for (var i=0; i < $scope.registro.monitors.length; i++) {
            	if ($scope.registro.monitors[i].id === reg.id) {
            		found = true;
            		break;
            	}
            }
            if (!found) {
            	$scope.registro.monitors.push(reg);
            }
        };
        $scope.eliminarMonitor = function(index) {
            $scope.registro.monitors.splice(index, 1);
        };
        
        function forceProtocol(url) {
        	if (url) {
	        	var tmp = url.toLowerCase();
	        	if (!tmp.startsWith('http://') && !tmp.startsWith('https://')) {
	        		return 'http://' + url;
	        	}
        	}
        }
        
        $scope.guardar = function() {
            var queryParams = {};
            
            //Forzar a que las URLs tengan protocolo, para que funcionen bien los enlaces
            $scope.registro.url = forceProtocol($scope.registro.url);
            $scope.registro.twitter = forceProtocol($scope.registro.twitter);
            forceProtocol($scope.registro.facebook);
            forceProtocol($scope.registro.instagram);
            
            if ($location.path() === '/entities/new') {
                Dao.crear('evento/admin/banco/entidad', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/entities/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
	}
])


.controller('MonitorListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'id asc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = false;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        loadPage();

        
        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/monitor/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        };

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id asc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/monitor/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/monitors/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('MonitorEditCtrl', ['$scope', 'Dao', '$location', 'registro', 'Informer', 'Restangular', '$route',
	function($scope, Dao, $location, registro, Informer, Restangular, $route) {
		$scope.debug = false;
		$scope.location = $location.path();
		$scope.registro = registro;
		
        $scope.entidadesRelacionadas = [];
        Restangular.all('evento').all('admin').all('banco').all('entidad').all('list').getList({q: 'monitors.id==' + registro.id, fl: 'id,title'}).then(function(res){
            $scope.entidadesRelacionadas = res;
        });
        
        $scope.actividadesRelacionadas = [];
        Restangular.all('evento').all('admin').all('banco').all('list').getList({q: 'monitors.id==' + registro.id, fl: 'id,title'}).then(function(res){
            $scope.actividadesRelacionadas = res;
        });
        
        $scope.guardar = function() {
            var queryParams = {}; 
            if($location.path() === '/monitors/new') {
                Dao.crear('evento/admin/banco/monitor', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/monitors/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
	}
])


.controller('DistrictListCtrl', ['$scope', 'Dao', '$location', 'Informer', 'Restangular',
    function($scope, Dao, $location, Informer, Restangular) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'id asc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = false;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        Dao.listar('evento/admin/banco/distrito/list', params).then(function(data) {
            $scope.registros = data;
            
            // Obtención del precio por km
            for (var i in $scope.registros) {
            	if (($scope.registros[i].kmPrice !== undefined) && ($scope.registros[i].kmPrice !== null)) {
            		$scope.kmPrice = $scope.registros[i].kmPrice;
            		break;
            	}
            }
        });
        
        $scope.updateKmPrice = function() {
        	if ($scope.kmPrice) {
        		Restangular.all('evento').all('admin').all('banco').all('distrito')
        			.customGET('', { preciokm: $scope.kmPrice}).then(function(res) {
        				Informer.inform(res.mensaje, 'success');
        			}, function(err) {
        				Informer.inform('Precio no actualizado. ' + (angular.isDefined(err.data.mensaje) ? err.data.mensaje : ''), 'danger');
        			});
        	}
        	else {
        		alert('Importe "Desplazamiento" incorrecto. Utilice coma decimal y dos decimales como m\u00E1ximo.')
        	}
        };

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/distrito/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id asc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/distrito/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/distrito/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('DistrictEditCtrl', ['$scope', 'Dao', '$http', 'registro', '$location', '$route', 'Informer', '$filter', '$window', '$timeout', 
    function($scope, Dao, $http, registro, $location, $route, Informer, $filter, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        $scope.guardar = function() {
            var queryParams = {}; 
            if($location.path() === '/districts/new') {
                Dao.crear('evento/admin/banco/distrito', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/districts/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
    }
])


.controller('HolidayListCtrl', ['$scope', 'Dao', '$location', 'Informer', 'Restangular',
    function($scope, Dao, $location, Informer, Restangular) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'id asc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = false;

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }
        
        $scope.registros = [];
        Dao.listar('evento/admin/banco/festivo/list', params).then(function(data) {
            $scope.registros = data;
            $scope.busy = false;

            // Obtención del suplemento por hora
            for (var i = 0; i < $scope.registros.length; i++) {
            	if (($scope.registros[i].hourPrice !== undefined) && ($scope.registros[i].hourPrice !== null)) {
            		$scope.hourPrice = $scope.registros[i].hourPrice;
            		break;
            	}
            }
        });

        $scope.updateHourPrice = function() {
        	if ($scope.hourPrice) {
        		Restangular.all('evento').all('admin').all('banco').all('festivo')
        			.customGET('', { preciohora: $scope.hourPrice}).then(function(res) {
        				Informer.inform(res.mensaje, 'success');
        			}, function(err) {
        				Informer.inform('Precio no actualizado. ' + (angular.isDefined(err.data.mensaje) ? err.data.mensaje : ''), 'danger');
        			});
        	}
        	else {
        		alert('Importe "Suplemento hora" incorrecto. Utilice coma decimal y dos decimales como m\u00E1ximo.')
        	}
        };

        loadPage();

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/festivo/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id asc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/festivo/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/festivo/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('HolidayEditCtrl', ['$scope', 'Dao', '$http', 'registro', '$location', '$route', 'Informer', '$filter', '$window', '$timeout', 
    function($scope, Dao, $http, registro, $location, $route, Informer, $filter, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;
        
        // Para que se muestre en el campo de fecha
        $scope.registro.date = new Date($scope.registro.date);

        $scope.datepickers = {date: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.guardar = function() {
            var queryParams = {}; 
            if($location.path() === '/holidays/new') {
                Dao.crear('evento/admin/banco/festivo', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/holidays/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                    Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
    }
])

.controller('ListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'lastUpdated desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        };

        loadMore();

        function loadMore() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;

            Dao.listar('evento/admin/banco/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        };

        $scope.pageChanged = function() {
            loadMore();
        };

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                if(key === 'entidad') {item.key = 'entity.title'};
                if(key === 'ambito') {item.key = 'scope.id'};
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/activities/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout', 'tiposDuracion', 'meses',
    function($scope, Dao, $http, registro, Restangular, $location, $route, Informer, $filter, $window, $timeout, tiposDuracion, meses) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;
        
        $scope.tiposDuracion = tiposDuracion;
        $scope.meses = meses;
        
        $scope.imageShown = null;
        $scope.clearThumbs = function() {
        	$scope.imageShown = null;
        	$scope.registro.image = null;
        	jQuery('#anexoImagen').val('');
        }
        
        $scope.selection = {};
        
        if ($scope.registro.months != null) {
        	$scope.selection.months = $scope.registro.months.split(',');
        }
        else {
        	$scope.selection.months = [];
        }
        
        $scope.selection.priceType = '';
        if ($scope.registro.hourPrice != null) {
        	$scope.selection.priceType = 'hourPrice';
        }
        else if ($scope.registro.totalPrice != null) {
        	$scope.selection.priceType = 'totalPrice';
        }
        else if ($scope.registro.participantPrice != null) {
        	$scope.selection.priceType = 'participantPrice';
        }

        $scope.seleccionarMonitor = function(reg) {
            if(angular.isUndefined($scope.registro.monitors)) {
                $scope.registro.monitors = [];
            };
            var found = false;
            for (var i=0; i < $scope.registro.monitors.length; i++) {
            	if ($scope.registro.monitors[i].id === reg.id) {
            		found = true;
            		break;
            	}
            }
            if (!found) {
            	$scope.registro.monitors.push(reg);
            }
        };
        $scope.eliminarMonitor = function(index) {
            $scope.registro.monitors.splice(index, 1);
        };

        
        if($location.path() === '/new'){
            $scope.registro.codUsuario = parseInt($scope.usuario.propiedades.usr_agenda);
            $scope.registro.usuarioAlta = $scope.usuario.login;
            $scope.registro.nombreContacto = $scope.usuario.nombre;
            $scope.registro.apellidosContacto = $scope.usuario.apellido1 + ' ' + ($scope.usuario.apellido2 || '');
            $scope.registro.emailContacto = $scope.usuario.email;
            $scope.registro.telefonoContacto = $scope.usuario.cgcz_telefono;
        };
        
        // Si al modificar no tiene asociado telefono se asociamos el de la propiedad cgcz_telefono
        if(angular.isUndefined($scope.registro.telefonoContacto)){$scope.registro.telefonoContacto = $scope.usuario.propiedades.cgcz_telefono};

        if(typeof copiaRegistro === 'undefined' || copiaRegistro === null){
            $scope.registro = registro;
        } else {
            $scope.registro = copiaRegistro;
            delete copiaRegistro;
        };


        // Establecemos la visualización previa de la imagen almacenada en los objetos imagenVertical e imagenHorizontal
        if(angular.isDefined($scope.registro.attachment)) {
            if(angular.isDefined($scope.registro.attachment.image)) { $scope.imagenVertical = $scope.registro.attachment.image;};
            if(angular.isDefined($scope.registro.attachment.imageAlt)) { $scope.imagenHorizontal = $scope.registro.attachment.imageAlt;};
        };
        // Anexo:Image Crop
        $scope.imageCrop = {
           originalImage: '',
           croppedImage: ''
        };

        $scope.scopes = [];
//        Restangular.all('evento').all('ambito').getList().then(function(res) {
//            $scope.scopes = res.plain();
//        });
        var params = {
        		start: 0,
        		sort: 'title asc'
        };
        Dao.listar('evento/admin/banco/ambito/list', {start: 0, sort: 'title asc'}).then(function(data) {
            $scope.scopes = data.plain();
        });

        $scope.solicitudesConcedidas = [];
        // Conjunto de solicitudes concedidas de la presente actividad del Banco
        if ($location.path() !== '/entities/new') {
        	Restangular.all('evento').all('admin').all('banco').all('solicitud').all('list').getList({q: 'actividad.id==' + registro.id, fl: 'id,nombreSolicitante', sort: 'id'}).then(function(res){
                $scope.solicitudesConcedidas = res;
            });
        }
        
        $scope.datepickers = {startDate: false, endDate: false, inicioDestacado: false, finDestacado: false, inicioDestacadoAlt: false, finDestacadoAlt: false, dateCaducidad: false, startDateInscrip: false, endDateInscrip: false, feHoUser: false, fechaAlternativa: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.cargarFicha = function(id){
       		$window.open('/sede/servicio/actividades/juvenil/banco/' + id, '_blank');
        };


        $scope.guardar = function() {
        	// Meses seleccionados
        	$scope.registro.months = '';
        	jQuery("input[name=selectedMonths]").each(function() {
        		if (this.checked) {
        			$scope.registro.months += this.value + ' ';
        		}
        	});
        	
        	// Coste actividad (opciones disjuntas)
        	if ($scope.selection.priceType === 'hourPrice') {
        		$scope.registro.totalPrice = null;
        		$scope.registro.participantPrice = null;
        	}
        	else if ($scope.selection.priceType === 'totalPrice') {
        		$scope.registro.hourPrice = null;
        		$scope.registro.participantPrice = null;
        	}
        	else if ($scope.selection.priceType === 'participantPrice') {
            	$scope.registro.hourPrice = null;
            	$scope.registro.totalPrice = null;
        	}
        	
            var queryParams = {}; 
            var contieneAdjuntos = false;
            var pendienteValidar = false;
            // Comprobamos adjuntos antes de guardar registro
            //if($scope.files.length > 0){contieneAdjuntos = true;};
             // Comprobamos si la actividad está pendiente de validar y si se tiene permisos PUB
//            if($scope.registro.validacion === 'S' && $scope.permisos.VAL){
//                $scope.registro.validacion = 'N'; // Validamos la actividad
//                pendienteValidar = true;
//            };

            // Si la actividad tiene inscripción online (ayto) marcamos no sé envía a histórico
//            if ($scope.registro.id) {
//	            var urlInscripcion = '/sede/servicio/cultura/evento/'+ $scope.registro.id +'/inscripcion';
//	            if($scope.registro.registration.url && $scope.registro.registration.url.indexOf(urlInscripcion) > 0) {
//	                $scope.registro.historico = 'N';
//	            };
//            }

            // Comprobamos si han buscado entidad pero finalmente no han seleccionado una
            // TOFIX: Corregir y tratar a través de la directiva <autocompletado>
//            if($scope.registro.organizer === ''){delete $scope.registro.organizer;};

            // Si hay anexo definido antes de guardar:
//            if(angular.isDefined($scope.registro.attachment)){
//                // Comprobamos si tiene idAnexo (copia de actividad anterior) y lo borramos para generar un nuevo anexo
//                if(angular.isDefined($scope.registro.attachment.id) && $location.path() === '/new') {
//                    delete $scope.registro.attachment.id;
//                };
//                // Añadimos la ruta correcta (el recortador no genera la ruta necesaria (imagen/nombre_fichero.ext), solo nombre_fichero.ext)
//                if(angular.isDefined($scope.registro.attachment.image)) {
//                    if($scope.registro.attachment.image.indexOf('imagen/') < 0) {
//                        $scope.registro.attachment.image = 'imagen/' + $scope.registro.attachment.image;
//                    };
//                };
//                // Añadimos la ruta correcta (el recortador no genera la ruta necesaria (imagen/nombre_fichero.ext), solo nombre_fichero.ext)
//                if(angular.isDefined($scope.registro.attachment.imageAlt)) {
//                    if($scope.registro.attachment.imageAlt.indexOf('imagen/') < 0) {
//                        $scope.registro.attachment.imageAlt = 'imagen/' + $scope.registro.attachment.imageAlt;
//                    };
//                };
//            };

            if($location.path() === '/activities/new') {
                Dao.crear('evento/admin/banco/', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/activities/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.' 
                        + (pendienteValidar ? ' Actualizando pendientes de validación...' : ''), 'success');
                    if(pendienteValidar){$scope.actualizarPendientesValidacion();};
                     // $location.path('/');
//                    if(angular.isDefined($scope.registro.attachment) && angular.isUndefined($scope.registro.attachment.id)){
//                        $scope.registro.attachment.id = res.anexo.id;
//                    };
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
            
        };

        $scope.abrircalendario = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.abierto = true;
        };


        $scope.removeAnexo = function(id){
            if($location.path() === '/new'){
                delete $scope.registro.attachment;
            } else {
                var anexo = Restangular.all('evento').one('admin').one('attachment', id).one('remove');
                Dao.eliminar(anexo).then(function(res) {
                    delete $scope.registro.attachment;
                    Informer.inform('Ficha de anexos eliminada correctamente.', 'success');
                }, function(err) {
                    Informer.inform('Se ha producido un error al borrar la ficha de anexos: ' + JSON.stringify(err.data.error) || JSON.stringify(err.data.mensaje), "danger");
                });
            };
            $scope.imagenVertical = undefined;
            $scope.imagenHorizontal = undefined;
        };

        //$scope.files = [];
        $scope.$on('fileSelected', function(event, args) {
            // Localiza la posicion actual del array Anexos en la vista
            //var x = $scope.registro.anexo.map(function(x) {return x.codAnexo; }).indexOf(parseInt($scope.verAnexo.codAnexo));
            
            // Si estamos modificando y tiene datos anteriores asociamos los datos de registro.anexo[0]
            // if($location.path() !== '/new' && $scope.registro.anexo.length > 0) {
            //     $scope.anexo = $scope.registro.anexo[0];
            // };

            // Primera selección de adjunto
            // if(angular.isUndefined($scope.anexo)) {
            //     $scope.anexo = {};
            // };
            if(angular.isUndefined($scope.registro.attachment.file_array)) {
                $scope.registro.attachment.file_array = [];
            };

            // if($location.path() === '/new' && angular.isDefined($scope.anexo.id)){
            //     delete $scope.anexo.id;
            // };
            
            // Solo permitimos la selección de un fichero de cada tipo. Si selecciona otro del mismo tipo, lo descarta (_.reject)
            $scope.$apply(function() {
                if(_.find($scope.registro.attachment.file_array, function(file) { return file.media_type === args.tipo; })){
                    $scope.registro.attachment.file_array = _.reject($scope.registro.attachment.file_array, function(f){ return f.media_type === args.tipo; });
                };
            });

            var currFile = args.file;
            var reader = new FileReader();
            reader.onload = function (file) {
                var media_file = new Object();
                var title = file.name.replace(/[^a-z0-9\.]/gi, '_').toLowerCase();
                // Revisar al crear no existe $scope.registro.id
                //var title = $scope.registro.id + file.name.substring(file.name.lastIndexOf('.'), file.name.length)
                var description = title.substring(0, (title.indexOf(".") > 30 ? 30 : title.indexOf(".")));
                
                // if (file.type.match('image.*')) {
                //     // Cambiamos la extensión de la imagen adjunta, ya que transformamos el formato al recortar
                //     title = title.substr(0, title.lastIndexOf(".")) + ".png";
                //     $scope.anexo.imagen = 'imagen/' + title;
                // } else 
                if (file.type === 'application/pdf' || file.type === 'text/plain') {
                    $scope.registro.attachment.document = 'documento/' + title;
                } else if (file.type.match('audio.*')) {
                    $scope.registro.attachment.audio = 'sonido/' + title;
                };

                media_file.media_name = title;
                media_file.media_description = description;
                media_file.media_type = args.tipo;

                return function(evt){
                    // if (file.type.match('image.*')) {
                    //     var image = new Image();
                    //     image.src = evt.target.result;
                    //     image.onload = function() {
                    //         if(this.width < 320 || this.height < 480){
                    //             Informer.inform('Tamaño de la imagen no válido. El tamaño mínimo es 320x480 px.', 'warning');
                    //             $scope.imageCrop.originalImage = '';
                    //             $scope.imageCrop.croppedImage = '';
                    //             $('#anexoImagen').val(null);
                    //             $scope.anexo.file_array = _.reject($scope.anexo.file_array, function(f){ return f.media_type === 'anexoImagen'; });
                    //             delete $scope.registro.anexo[0].imagen;
                    //             //$scope.files = _.without($scope.files, _.findWhere($scope.files, {tipo: 'anexoImagen'}));
                    //         } else {
                    //             // $scope.imageCrop.originalImage = evt.target.result;
                    //         };
                    //     };
                    //     $scope.$apply(function($scope){
                    //         $scope.imageCrop.originalImage = evt.target.result;
                    //         if($scope.imageCrop.originalImage !== "") {
                    //             // Esperamos 500 ms para que el recortador recargue la imagen recortada
                    //             $timeout(function() {
                    //                 // Bug firefox unsupport ngImgCropFullExtended: imageCrop.croppedImage = null
                    //                 if($scope.imageCrop.croppedImage === null){
                    //                     media_file.media_body = encodeURIComponent($scope.imageCrop.originalImage);
                    //                 } else {
                    //                     media_file.media_body = encodeURIComponent($scope.imageCrop.croppedImage);    
                    //                 };
                    //             }, 500);
                    //         };
                    //     });
                    // } else {
                        media_file.media_body = encodeURIComponent(evt.target.result);
                    // };
                    $scope.$apply(function($scope){
                        $scope.registro.attachment.file_array.push(media_file);
                    });
                };
            }(currFile);
            reader.readAsDataURL(currFile);    

        });

    }

])

.controller('RequestListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'lastUpdated desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        loadPage();

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/solicitud/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                if (key === 'codigoActividad') {item.key = 'actividad.id'};
                if (key === 'nombreActividad') {item.key = 'actividad.title'};
                if (key === 'entidad') {item.key = 'actividad.entity.id'};
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/solicitud/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/requests/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };

    }
])

.controller('RequestEditCtrl', ['$scope', 'Dao', '$location', '$http', 'registro', 'Informer', '$window', '$route',
	function($scope, Dao, $location, $http, registro, Informer, $window, $route) {
		$scope.debug = false;
		$scope.location = $location.path();
		$scope.registro = registro;
		
		// Carga de barrios rurales
        $scope.distritos = [];
        Dao.listar('evento/admin/banco/distrito/list', {start: 0, sort: 'title asc'}).then(function(data) {
            $scope.distritos = data.plain();
        });

		// Carga de días festivos
        Dao.listar('evento/admin/banco/festivo/list', { start: 0, sort: 'id asc' }).then(function(data) {
            $scope.festivos = data.plain();

            // Obtención del suplemento por hora
            for (var i = 0; i < $scope.festivos.length; i++) {
            	if (($scope.festivos[i].hourPrice !== undefined) && ($scope.festivos[i].hourPrice !== null)) {
            		$scope.hourPrice = $scope.festivos[i].hourPrice;
            		break;
            	}
            }
            
            // Cálculo de la información de facturación
            $scope.calculaFacturacion();
        });

        // Carga de facturas
        $scope.facturas = [];
        Dao.listar('evento/admin/banco/factura/list', {start: 0, sort: 'id asc', q: [{key: 'solicitud.id', op: '==', value: registro.id}]}).then(function(data) {
            $scope.facturas = data.plain();
        });

        // Estados de los botones para enviar e-mail a las entidades
        $scope.sendMailDisabled = (!registro.estado || (registro.estado == 'EN_ESTUDIO'));

		var semana = [ "do", "lu", "ma", "mi", "ju", "vi", "s" ];	// Domingo es el día 0 en JavaScript
		$scope.calculaFacturacion = function() {
			// Obtención de horas y sesiones a partir de horario y calendario
			$scope.tramos = [];
			var diasSemana = [];
			if ($scope.registro.horario) {
				var tramos = $scope.registro.horario.split('\n');
				var tramosHoras;
				for (var i = 0; i < tramos.length; i++) {
					var tramo = tramos[i].trim();
					if (tramo === '') { continue; }
					try {
						var tramoSplitted = tramo.split(' ');
						var diaStr = tramoSplitted[0].toLowerCase();
						var horas = tramoSplitted[1].split('-');
						var horaIni = horas[0].split(/\D/);
						var minutoIni = horaIni[1] !== undefined ? horaIni[1] : "00";
						horaIni = horaIni[0];
						var horaFin = horas[1].split(/\D/);
						var minutoFin = horaFin[1] !== undefined ? horaFin[1] : "00";
						horaFin = horaFin[0];
						var hIni = horaIni + ":" + minutoIni;
						var hFin = horaFin + ":" + minutoFin;
						
						var tramoExistente = false;
						for (var t = 0; t < $scope.tramos.length; t++) {
							if (($scope.tramos[t].dia === diaStr) 
									&& ($scope.tramos[t].horaIni === hIni)
									&& ($scope.tramos[t].horaFin === hFin)) {
								tramoExistente = true;
								break;
							}
						}
						if (!tramoExistente) {
							$scope.tramos.push({
								dia: diaStr,
								horaIni: hIni,
								horaFin: hFin
							});
						}
						
						var inicio = parseInt(horaIni) + (parseInt(minutoIni) / 60);
						var fin = parseInt(horaFin) + (parseInt(minutoFin) / 60);
						if (isNaN(inicio) || isNaN(fin)) {
							$scope.errorEnHorario = true;
							throw "Error en tramo horario: " + tramo;
						}
						// Si la hora de finalización es anterior a la de inicio, se entiende que es del día siguiente (madrugada)
						if (fin < inicio) {
							fin += 24;
						}
						var duracion = fin - inicio;
						
						for (var d = 0; d < semana.length; d++) {
							if (diaStr.toLowerCase().indexOf(semana[d]) === 0) {
								var dia = diasSemana[d];
								if ((dia === undefined) || (dia === null)) {
									dia = { horasNormales: 0, horasNocturnas: 0, horasFestivas: 0 };
									diasSemana[d] = dia;
								}
								dia.horasNormales += duracion;
								
								if (d == 0) {	// Es domingo
									dia.horasFestivas += duracion;
								}
								else {
									// Horas nocturnas (a partir de las 22:00)
									if (fin > 22) {
										dia.horasNocturnas += (fin - 22);
									}
								}
							}
						}
					}
					catch (err) {
						alert(err);
						continue;
					}
				}

	        	$scope.tramos.sort(comparaTramo);
			}
	
			$scope.meses = {};
			$scope.numSesiones = 0;
			$scope.horasNormales = 0;
			$scope.horasNocturnas = 0;
			$scope.horasFestivas = 0;
			$scope.calendario = [];
			if ($scope.registro.calendario) {
				var diasCalendario = $scope.registro.calendario.split('\n');
				for (var i = 0; i < diasCalendario.length; i++) {
					var dia = diasCalendario[i].trim();
					dia = dia.replace(/[^0-9/\-]/g, "");
					dia = dia.replace(/-/g, "/");
					if (dia === '') { continue; }
					try {
						var diaSplitted = dia.split('/');	// formato 'dd/MM/yyyy'
						var fecha = new Date(diaSplitted[2], diaSplitted[1] - 1, diaSplitted[0]);

						var diaSemana = diasSemana[fecha.getDay()];
						if ((diaSemana === undefined) || (diaSemana === null)) {
							$scope.errorEnCalendario = true;
							throw "Fecha incorrecta o sin horario definido: " + dia;
						}
						
						// Preparamos las horas mensuales
						var mesStr = diaSplitted[2] + " / " + diaSplitted[1];
						var mes = $scope.meses[mesStr];
						if ((mes === undefined) || (mes === null)) {
							mes = { nombre: mesStr, horasNormales: 0, horasNocturnas: 0, horasFestivas: 0, numSesiones: 0 };
							$scope.meses[mesStr] = mes;
						}

						$scope.horasNormales += diaSemana.horasNormales;
						mes.horasNormales += diaSemana.horasNormales;

						var festivo = compruebaFestivo(fecha);
						if (diaSemana.horasFestivas > 0) {
							// El día de la semana ya tiene horas festivas (es domingo): todas las horas son festivas
							$scope.horasFestivas += diaSemana.horasFestivas;
							mes.horasFestivas += diaSemana.horasFestivas;
						}
						else {
							if (festivo) {
								// Si es festivo, añadir todas las horas como festivas
								$scope.horasFestivas += diaSemana.horasNormales;
								mes.horasFestivas += diaSemana.horasNormales;
							}
							else {
								// Si no es festivo, añadir horas nocturnas
								$scope.horasNocturnas += diaSemana.horasNocturnas;
								mes.horasNocturnas += diaSemana.horasNocturnas;
							}
						}
						
						var fechaExistente = false;
						for (var f = 0; f < $scope.calendario.length; f++) {
							if ($scope.calendario[f].fecha.getTime() === fecha.getTime()) {
								fechaExistente = true;
								break;
							}
						}
						if (!fechaExistente) {
							$scope.calendario.push({
								fecha: fecha,
								festivo: festivo
							});
						}
						
						// Sólo una sesión por día, no se dará el caso de que haya más de una sesión al día (para calcular desplazamientos)
						$scope.numSesiones++;
						mes.numSesiones++;
					}
					catch (err) {
						alert(err);
						continue;
					}
				}
				
                $scope.calendario.sort(comparaFecha);
			}
		}
		
		function compruebaFestivo(fecha) {
			if (fecha.getDay() == 0) {
				// Es domingo
				return true;
			}
			var year = fecha.getFullYear();
			var month = fecha.getMonth();
			var day = fecha.getDate();
			for (var f = 0; f < $scope.festivos.length; f++) {
				var date = new Date($scope.festivos[f].date);
				if ((date.getFullYear() === year) && (date.getMonth() === month) && (date.getDate() === day)) {
					return true;
				}
			}
			return false;
		}
		        
        // Valores selectores
        $scope.selector = {
        		dias: [ 'lunes', 'martes', 'mi\u00E9rcoles', 'jueves', 'viernes', 's\u00E1bado', 'domingo' ],
        		horas: []
        }
        for (var h = 0; h < 24; h++) {
        	$scope.selector.horas.push((h < 10 ? '0' : '') + h);
        }
        
        // Gestión horario
        $scope.tramo = {
        		dia: 'lunes',
        		horaIni: '09',
        		minutoIni: '00',
        		horaFin: '10',
        		minutoFin: '00'
        };
        $scope.addTime = function() {
        	var found = false;
        	var dia = $scope.tramo.dia;
        	var horaIni = $scope.tramo.horaIni + ':' + $scope.tramo.minutoIni;
        	var horaFin = $scope.tramo.horaFin + ':' + $scope.tramo.minutoFin;
        	for (var t = 0; t < $scope.tramos.length; t++) {
        	    var tramo = $scope.tramos[t];
        	    if ((tramo.dia === dia) && (tramo.horaIni === horaIni) && (tramo.horaFin === horaFin)) {
        	        found = true;
        	        break;
        	    }
        	}
        	if (!found) {
	        	$scope.tramos.push({
	        		dia: dia,
	        		horaIni: horaIni,
	        		horaFin: horaFin
	        	});
	        	$scope.tramos.sort(comparaTramo);
        	}
        }
        $scope.removeTime = function(index) {
        	$scope.tramos.splice(index, 1);
        }
        function comparaTramo(a, b) {
            var diaA = getDiaSemana(a.dia);
            var diaB = getDiaSemana(b.dia);
            if (diaA < diaB) return -1;
            if (diaA > diaB) return 1;
            if (a.horaIni < b.horaIni) return -1;
            if (a.horaIni > b.horaIni) return 1;
            if (a.horaFin < b.horaFin) return -1;
            if (a.horaFin > b.horaFin) return 1;
            return 0;
        }
        function getDiaSemana(dia) {
            for (var i = 0; i < semana.length; i++) {
                if (dia.toLowerCase().indexOf(semana[i]) === 0) {
                    // Corrección domingo
                    if (i == 0) return 7;
                    return i;
                }
            }
            return 7;
        }

        // Gestión calendario
        $scope.diaSeleccionado = {
        		fecha: null
        };
        $scope.addDate = function() {
        	var found = false;
        	var fechaSel = $scope.diaSeleccionado.fecha;
        	if (fechaSel != null) {
                for (var d = 0; d < $scope.calendario.length; d++) {
                    var dia = $scope.calendario[d];
                    if (dia.fecha.getTime() === fechaSel.getTime()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    $scope.calendario.push({
                        fecha: fechaSel,
                        festivo: compruebaFestivo(fechaSel)
                    });
                    $scope.calendario.sort(comparaFecha);
                }
            }
        }
        $scope.removeDate = function(index) {
        	$scope.calendario.splice(index, 1);
        }
        function formatFecha(fecha) {
        	var dia = fecha.getDate();
        	var mes = fecha.getMonth() + 1;
        	return (dia < 10 ? "0" : "") + dia + "/" + (mes < 10 ? "0" : "") + mes + "/" + fecha.getFullYear();
        }
        function comparaFecha(a, b) {
            if (a.fecha < b.fecha) return -1;
            if (a.fecha > b.fecha) return 1;
            return 0;
        }

        $scope.totalParticipantes = 0;
        if ($scope.registro.numParticipantes) {
        	$scope.totalParticipantes += $scope.registro.numParticipantes;
        }
        // Actividad de Agenda
        $scope.infoActo = function() {
        	if ($scope.registro.codActo > 0) {
            	Dao.detalle('evento/admin/banco/acto/', $scope.registro.codActo).then(function(res) {
            		$scope.nombreActo = res.nombre;
            		$scope.numInscritosOnline = res.numInscritos;
            		$scope.totalParticipantes += res.numInscritos;
            	}, function(err) {
            		Informer.inform("Error al obtener información de la actividad. Compruebe que el código corresponde a una actividad de Agenda.", "danger");
            	});
        	}
        };
        $scope.infoActo();
        
        $scope.limpiarActo = function() {
        	$scope.registro.codActo = null;
        	$scope.nombreActo = null;
        	$scope.numInscritosOnline = null;
        }
        
        $scope.abrirActo = function() {
        	$window.open('/sede/servicio/cultura/evento/admin/acto/#/edit/' + $scope.registro.codActo, '_blank');
        }
        
        $scope.guardar = function() {
        	// Ampliación solicitud
        	if (!$scope.registro.ampliacion) {
        		$scope.registro.idSolicitudAmpliada = null;
        	}
        	
        	// Si se deselecciona Barrio rural
        	if ($scope.registro.distrito && $scope.registro.distrito.id === '') {
        		delete $scope.registro.distrito;
        	}

        	// Tramos horarios
        	if (!$scope.errorEnHorario) {
	        	$scope.registro.horario = "";
	        	for (var t = 0; t < $scope.tramos.length; t++) {
	        	    var tramo = $scope.tramos[t];
	        	    $scope.registro.horario += tramo.dia + " " + tramo.horaIni + "-" + tramo.horaFin + "\n";
	        	}
        	}

        	// Días de realización
        	if (!$scope.errorEnCalendario) {
	        	$scope.registro.calendario = "";
	        	for (var d = 0; d < $scope.calendario.length; d++) {
	        	    var dia = $scope.calendario[d];
	        	    $scope.registro.calendario += formatFecha(dia.fecha) + "\n";
	        	}
        	}

            var queryParams = {}; 

            if($location.path() === '/requests/new') {
                Dao.crear('evento/admin/banco/solicitud', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/requests/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                    Informer.inform('El registro se ha modificado correctamente.', 'success');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            };
        };
        
        // Abrir detalle de factura
        $scope.abrirFactura = function(id) {
            $window.open('/sede/servicio/cultura/evento/admin/banco/#/invoices/edit/' + id, '_self');
        };

        // Abrir página de envio de mail
        $scope.previewRequestMail = function(id, isRequester) {
            var url;
            var ungrantedRequest = (registro.estado != 'CONCEDIDA');
            if (isRequester) {
                if (ungrantedRequest) {
                    url = '/sede/servicio/cultura/evento/admin/banco/#/requests/mail-requester-ungranted/' + id;
                } else {
                    url = '/sede/servicio/cultura/evento/admin/banco/#/requests/mail-requester/' + id;
                }
            } else {
                if (ungrantedRequest) {
                    url = '/sede/servicio/cultura/evento/admin/banco/#/requests/mail-entity-ungranted/' + id;
                } else {
                    url = '/sede/servicio/cultura/evento/admin/banco/#/requests/mail-entity/' + id;
                }
            }
            $window.open(url);
        };

        // Enviar mail
        $scope.sendMail = function(isRequester) {
            var email = isRequester ? $scope.registro.email : $scope.registro.actividad.entity.email;

            var subject = "";
            var activityStr = "'" + $scope.registro.actividad.id + " - " +$scope.registro.actividad.title + "'";
            if ($scope.registro.estado === 'CONCEDIDA') {
            	subject = "Colaboraci\u00F3n del Servicio de Juventud en la realizaci\u00F3n de la actividad propuesta " + activityStr;
            }
            else if ($scope.registro.estado === 'DENEGADA') {
            	subject = "Rechazada colaboraci\u00F3n del Servicio de Juventud en la realizaci\u00F3n de la actividad propuesta " + activityStr;
            } 
            else if ($scope.registro.estado === 'ANULADA') {
            	subject = "Anulada la actividad " + activityStr + " realizada en colaboraci\u00F3n con el Servicio de Juventud";
            } 
            
            var content = $("#mail-content")[0].innerHTML;

            var body = {
                "address": email,
                "subject": subject,
                "content": content
            }
            $http.post('send-mail', body).then(function(res) {
            	$window.scrollTo(0, 0);
                if (res.status == 200) {
                    Informer.inform('El e-mail se ha enviado correctamente a ' + email + '.', 'success');
                } else {
                    Informer.inform('Error enviando e-mail a ' + email + '.', 'danger');
                }
            })
        };
	}
	
])
.controller('RequestReportCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {
	
	    $scope.datepickers = {fechaDesde: false, fechaHasta: false};
	    $scope.toggleOpenDatePicker = function($event, which) {
	        $event.preventDefault();
	        $event.stopPropagation();
	        $scope.datepickers[which] = true;
	    };

		$scope.qsearch = {};
        
        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var query = '';
            _.each(qsearch, function(value, key) {
                if (key === 'entidad') {
                	if ((value !== null) && (value !== '')) {
                    	query += (query !== '') ? ';' : '';
                		query += 'actividad.entity.id==' + value;
                	}
                }
                else {
                	query += (query !== '') ? ';' : '';
                	query += key + '==' + value + '*';
                }

//               	delete qsearch[key];
            });
            var reportForm = $("#requestReportForm");
            var queryInput = reportForm.find("#report-query");
            queryInput.val(query);
            reportForm.submit();
        };
        
        $scope.limpiarFiltros = function() {
        	$scope.qsearch = {};
        	$scope.toDate = null;
        	$scope.fromDate = null;
        }

    }
])


.controller('InvoiceListCtrl', ['$scope', 'Dao', '$location', 'Informer', '$window',
    function($scope, Dao, $location, Informer, $window) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 20;
            params.sort = 'lastUpdated desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];

        // pagination
        $scope.viewby = 20;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        }

        loadPage();

        function loadPage() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar('evento/admin/banco/factura/list', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la b\u00FAsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });   
        }

        $scope.pageChanged = function() {
            loadPage();
        }

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if (key === 'nombreActividad') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                    params.nombreActividad = item.value;
                    item.value = '';
                };
                if(key === 'entidad') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                    params.idEntidad = parseInt(item.value);
                    item.value = '';
                };
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/banco/factura/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/invoices/edit/' + data[0].id);
                } else {
                    $scope.currentPage = 1; //reset to first page
                    $scope.registros = data;    
                };
            });
        };
        
        $scope.abrirFactura = function(id) {
            $window.open('/sede/servicio/cultura/evento/admin/banco/#/invoices/edit/' + id, '_self');
        };
        
        $scope.exportarCSV = function() {
            //Dao.listar('evento/admin/banco/factura/list.csv', params).then(function(data) {});
            $window.open('/sede/servicio/cultura/evento/admin/banco/factura/list.csv', '_blank');
        };

    }
])

.controller('InvoiceEditCtrl', ['$scope', 'Dao', '$location', 'registro', 'Informer', '$window', '$route',
	function($scope, Dao, $location, registro, Informer, $window, $route) {
		$scope.debug = false;
		$scope.location = $location.path();
        $scope.registro = registro;
        
        if ($scope.registro.invoiceDate) {
            $scope.registro.invoiceDate = new Date($scope.registro.invoiceDate);
        }
        if ($scope.registro.processingDate) {
            $scope.registro.processingDate = new Date($scope.registro.processingDate);
        }

        $scope.guardar = function() {
        	var queryParams = {};
        	Dao.actualizar($scope.registro, queryParams).then(function(res) {
                Informer.inform('El registro se ha modificado correctamente.', 'success');
                // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                    $scope.registro.originalElement = res.originalElement;
                };
                $route.reload();
            }, function(err) {
                Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                 // $location.path('/');
            });
        }
        
	}
]);
