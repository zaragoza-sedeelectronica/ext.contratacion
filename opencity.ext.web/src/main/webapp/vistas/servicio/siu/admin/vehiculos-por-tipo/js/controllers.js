const PAGE_SIZE = 10;

angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao', 
    function($scope, $window, $location, $filter, Informer, $route, Dao) {

        $scope.listar = function() {
            Dao.limpiarFiltros();
            $scope.checkList = [];
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
        $scope.checkList = []; // Array checkbox multiple (listado)

    }
])

.controller('ListCtrl', ['$scope', '$http', 'Dao', '$location', 'Informer',
    function($scope, $http, Dao, $location, Informer) {
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = PAGE_SIZE;
            params.sort = 'lastUpdated desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];
        $scope.busy = false;

        // pagination
        $scope.viewby = PAGE_SIZE;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        };

        // Si params.q está definido venimos de búsqueda
        if (params.q) {
            loadMore();
        };
        function loadMore() {
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            Dao.listar(QUERY, params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");}
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            }); 
        };

        $scope.pageChanged = function() {
            loadMore();
        };

        $scope.qsearch = {}; // Inicializamos el objeto para evitar undefined en el select. Bug?
        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = PAGE_SIZE; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key;
                item.value = value;
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar(QUERY, params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) {
                    Informer.inform("La b\u00FAsqueda no ha producido ning\u00FAn resultado", "warning");
                };
                $scope.registros = data;
            });
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout', 
    function($scope, Dao, $http, registro, Restangular, $location, $route, Informer, $filter, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        $scope.guardar = function() {
            var queryParams = {}; 
            // Completar entidad
            let entity = $scope.registro;
            entity.lastUpdated = new Date().toISOString();
            if (entity.tipo_vehiculo != null) {
                entity.tipoVehiculo = {
                    tipo_vehiculo: entity.tipo_vehiculo
                }
            }
            if ((entity.mes != null) && (entity.anyo != null)) {
                var anyo = "" + entity.anyo;
                var mes = "" + entity.mes;
                if (mes.length == 1) mes = "0" + mes;
                entity.idTemporal = mes + "-" + anyo;
                entity.fecha = anyo + "-" + mes + "-01";
            }
            // Realizar petición
            if($location.path() === '/new') {
                Dao.crear(QUERY, entity).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/edit/' + res.id);
                }, function(err) {
                    if (err.status == 400) {
                        Informer.inform("Se ha producido un error al crear el registro: ya hay un registro para esa fecha (a\u00F1o y mes).", "danger");
                    } else {
                        Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    }
                });
            } else {
                Dao.actualizar(entity, queryParams).then(function(res) {
                    Informer.inform('El registro se ha modificado correctamente.');
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                        // FIXME: ELIMINAR AL PASAR A SEDE - Forzado de horarios por si se realiza una copia (no viene en respuesta)
                        //$scope.registro.originalElement.subEvent = $scope.registro.subEvent;
                    };
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                });
            };
            
        };
    }
])