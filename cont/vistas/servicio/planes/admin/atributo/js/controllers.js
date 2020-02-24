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

.controller('ListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {

        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.sort = 'id desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'id';
        $scope.reverse = true;

        $scope.registros = [];
        $scope.busy = false;

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 20; // Número de resultados totales en la búsqueda
            params.sort = 'id desc';
            params.q = [
            ];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('/list', params).then(function(data) {
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

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout',
    function($scope, Dao, $http, registro, Restangular, $location, $route, Informer, $filter, $window, $timeout) {
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        $scope.valor = null;

        if(typeof copiaRegistro === 'undefined' || copiaRegistro === null){
            $scope.registro = registro;
        } else {
            $scope.registro = copiaRegistro;
            delete copiaRegistro;
        };

        $scope.agregarValor = function(nuevoValor) {
            if ($scope.registro.valores == undefined) {
                $scope.registro.valores = [];
            }
            $scope.registro.valores.push({
                valorDiscriminador: nuevoValor
            });
        };

        $scope.quitarValor = function(index) {
            $scope.registro.valores.splice(index, 1);
        };

        $scope.subirValor = function(index) {
            if (index == 0) {
                return;
            }
            $scope.registro.valores[index-1] = $scope.registro.valores.splice(index, 1, $scope.registro.valores[index-1])[0];
        };

        $scope.bajarValor = function(index) {
            if (index == ($scope.registro.valores.length - 1)) {
                return;
            }
            $scope.registro.valores[index] = $scope.registro.valores.splice(index+1, 1, $scope.registro.valores[index])[0];
        };

        $scope.guardar = function() {
            var queryParams = {};

            if($location.path() === '/new') {
                Dao.crear('', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                    // $location.path('/');
                });
            } else {
                delete $scope.registro.procs;
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
]);
