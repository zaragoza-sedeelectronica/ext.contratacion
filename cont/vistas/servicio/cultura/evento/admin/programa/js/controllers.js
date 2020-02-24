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

        // $scope.busy = false;
        // $scope.loadMore = function() {
            //if ($scope.busy) return;
            //$scope.busy = true;
            Dao.listar('programa', params).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
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
            params.rows = 100;
            params.q = [];
            _.each(qsearch, function(value, key) {
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if (item.value !== '') { params.q.push(item); }; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.limpiarFiltros();
            Dao.listar('programa', params).then(function(data) {
                if (data.totalCount == 0) { Informer.inform("La búsqueda no ha producido ningún resultado", "warning"); }
                // Si devuelve un único resultado, se envía directamente al detalle.
                if (data.totalCount === 1) {
                    $location.path('/edit/' + data[0].id);
                } else {
                    $scope.registros = data;
                };
                // $scope.busy = false;
                delete params.q;
                Dao.limpiarFiltros();
            });
        };

    }
])

.controller('CreateCtrl', ['$scope', '$location', 'Dao', 'Informer',
    function($scope, $location, Dao, Informer) {

        $scope.registro = copiaRegistro === null ? {} : copiaRegistro;

        $scope.registro.grupoAsociado = true;

        $scope.guardar = function() {
            if(angular.isDefined($scope.registro.ordenDestacado) && $scope.registro.ordenDestacado === null){
                $scope.registro.ordenDestacado = 0;
            };
            Dao.crear('programa', $scope.registro).then(function() {
                Informer.inform("El registro se ha creado correctamente.", "success");
                $location.path('/edit/' + res.id);
                // $location.path('/');
            }, function(err) {
                Informer.inform('Se ha producido un error al guardar el registro: ' + JSON.stringify(err.data.mensaje), 'danger');
                // $location.path('/');
            });
            // TODO: Comprobar Eliminar el objeto copiaPunto, para prevenir que tenga contenido de nuevo
            delete copiaRegistro;
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', '$location', 'Informer', '$http',
    function($scope, Dao, registro, Restangular, $location, Informer, $http) {

        $scope.registro = registro;

        if ($scope.permisos.LIDER) {
	        console.log(registro);
	        $http.get('/sede/servicio/cultura/evento/admin/programa/' + registro.id + '/lider', {}).then(function(res) {
	            $scope.lideres = res.data;
	        });
        };
        
        if(angular.isDefined($scope.registro.image)) { $scope.imagenVertical = $scope.registro.image;};
        if(angular.isDefined($scope.registro.imageAlt)) { $scope.imagenHorizontal = $scope.registro.imageAlt;};

        $scope.guardar = function() {
            if(angular.isDefined($scope.registro.ordenDestacado) && $scope.registro.ordenDestacado === null){
                $scope.registro.ordenDestacado = 0;
            };
            Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                // $location.path('/');
            }, function(err) {
                Informer.inform('Se ha producido un error al guardar el registro: ' + JSON.stringify(err.data.mensaje), 'danger');
                // $location.path('/');
            });
        };

        // Obtiene un listado de actividades con el programa asociado
        $scope.actosAsociados = [];
        Restangular.all('list').getList({ q: 'program.id==' + registro.id, fl: 'id,title' }).then(function(res) {
            $scope.actosAsociados = res;
        });
 
        $scope.asociarLider = function(login) {
        	$http.get('/sede/servicio/cultura/evento/admin/programa/' + $scope.registro.id + '/lider/' + login + '/add', {}).then(function(res) {
                Informer.inform('Líder asociado correctamente', 'success');
                $http.get('/sede/servicio/cultura/evento/admin/programa/' + registro.id + '/lider', {}).then(function(res) {
    	            $scope.lideres = res.data;
    	        });
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        };
        $scope.eliminarLider = function(userId) {
        	$http.delete('/sede/servicio/cultura/evento/admin/programa/' + $scope.registro.id + '/lider/' + userId, {}).then(function(res) {
                Informer.inform('Líder eliminado correctamente', 'success');
                $scope.lideres = res.data;
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        };
        
    }
]);
