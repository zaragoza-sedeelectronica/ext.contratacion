angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao',
    function($scope, $window, $location, $filter, Informer, $route, Dao) {

        $scope.listar = function() {
            Dao.limpiarFiltros();
            if ($location.path() == '/') { // ListCtrl
                $route.reload();
            } else {
                $location.path('/'); // DetailCtrl
            };
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            };
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
            params.sort = 'id desc';
        };
        $scope.filtrosParaListado = params.q;

        $scope.registros = [];

        //$scope.busy = false;
        //$scope.loadMore = function() {
            //if ($scope.busy) return;
            //$scope.busy = true;
            Dao.listar('tema', params).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                if (data.totalCount === 0) { Informer.inform("La búsqueda no ha producido ningún resultado", "warning"); }
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
                $scope.busy = false;
            }, function(err) {
                if (params.q !== null) {
                    Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });
            //params.start += 50;
        //};

        $scope.buscar = function(qsearch) {
            var params = {};
            params.start = 0;
            params.rows = 500;
            // params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key) {
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if (item.value !== '') { params.q.push(item); }; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento-zaragoza', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) { Informer.inform("La búsqueda no ha producido ningún resultado", "warning"); }
                // Si devuelve un único resultado, se envía directamente al detalle.
                if (data.totalCount === 1) {
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

        $scope.registro = copiaRegistro === null ? {} : copiaRegistro;

        $scope.guardar = function() {
            Dao.crear('evento-zaragoza', $scope.registro).then(function(res) {
                Informer.inform("El registro se ha creado correctamente.", "success");
                $location.path('/edit/' + res.id);
            }, function(err) {
                Informer.inform("Se ha producido un error al crear el registro:" + err.data.error, "danger");
                // $location.path('/');
            });
            // // TODO: Comprobar Eliminar el objeto copiaPunto, para prevenir que tenga contenido de nuevo
            delete copiaRegistro;
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', '$location', 'Informer',
    function($scope, Dao, registro, Restangular, $location, Informer) {

        $scope.registro = registro;

        if(angular.isDefined($scope.registro.image)) { $scope.imagenVertical = $scope.registro.image;};
        if(angular.isDefined($scope.registro.imageAlt)) { $scope.imagenHorizontal = $scope.registro.imageAlt;};

        $scope.guardar = function() {
            Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                // $location.path('/');
            }, function(err) {
                Informer.inform('Se ha producido un error al guardar el registro: ' + err.data.error, 'danger');
                // $location.path('/');
            });
        };

    }
])

// .controller('SubtemasCtrl', ['$scope', 'RestangularSubtema', 'limitToFilter',
//     function($scope, RestangularSubtema, limitToFilter) {

//         $scope.subtemasArray = [];
//         RestangularSubtema.all('evento-zaragoza').getList({
//             q: 'tema.id==' + $scope.registro.id
//         }).then(function(response) {
//             $scope.subtemasArray = response;
//         });

//     }
// ]);
