angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route',
    function($scope, $window, $location, $filter, Informer, $route) {

        $scope.listar = function() {
            $location.path('#/');
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            }

            $location.path('/new');

        };

        $scope.modoBusqueda = 0;
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao', '$location',
    function($scope, Dao, $location) {

        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.sort = 'id desc';
        };
        $scope.filtrosParaListado = params.q;

        $scope.registros = [];

        // $scope.busy = false;
        // $scope.loadMore = function() {
        // if ($scope.busy) return;
        // $scope.busy = true;

        Dao.listar('list', params).then(function(data) {
            $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
            $scope.busy = false;
        });
        // params.start += 50;
        // };

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
            Dao.listar('noticia/list', params).then(function(data) {
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

        $scope.guardar = function() {
            if(angular.isDefined($scope.registro.image) && $scope.registro.image.indexOf('zaragoza.es') > 0) {
                $scope.registro.image = $scope.registro.image.split('/actividades/').pop();
            };
            if(angular.isDefined($scope.registro.ordenDestacado) && $scope.registro.ordenDestacado === null){
                $scope.registro.ordenDestacado = 0;
            };
            Dao.crear('programa', $scope.registro).then(function() {
                Informer.inform("El registro se ha creado correctamente.", "success");
                $location.path('/edit/' + res.id);
                // $location.path('/');
            }, function(err) {
                Informer.inform("Se ha producido un error al crear el registro:" + JSON.stringify(err.data.error) || JSON.stringify(err.data.mensaje), "danger");
                // $location.path('/');
            });
            // TODO: Comprobar Eliminar el objeto copiaPunto, para prevenir que tenga contenido de nuevo
            delete copiaRegistro;
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', '$location', 'Informer', '$window',
    function($scope, Dao, registro, Restangular, $location, Informer, $window) {

        $scope.registro = registro;

        $scope.guardar = function() {
            if(angular.isDefined($scope.registro.file_array)){
                $scope.registro.media_name = $scope.registro.file_array[0].media_name;
                $scope.registro.media_description = $scope.registro.file_array[0].media_description;
                $scope.registro.media_body = $scope.registro.file_array[0].media_body;
                delete $scope.registro.file_array;
            };
            if(angular.isDefined($scope.registro.image) && $scope.registro.image.indexOf('zaragoza.es') > 0) {
                $scope.registro.image = $scope.registro.image.split('/actividades/').pop();
            };
            if(angular.isDefined($scope.registro.ordenDestacado) && $scope.registro.ordenDestacado === null){
                $scope.registro.ordenDestacado = 0;
            };
            Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                // $location.path('/');
            }, function(err) {
                Informer.inform('Se ha producido un error al guardar el registro: ' + JSON.stringify(err.data.error) || JSON.stringify(err.data.mensaje), 'danger');
                // $location.path('/');
            });
        };

        $scope.cargarFicha = function(id){
            $window.open('/sede/servicio/noticia/' + id + '?refresh=y', '_blank');
        };

        $scope.datepickers = {dateCreated: false, expirationDate: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.category = [];
        Restangular.all('category').getList().then(function(res) {
            $scope.poblacion = res.plain();
        });
        // Objeto select-ui pobDestino. Convierte la cadena en array para recorrerlo en la directiva.

        console.log( _.map($scope.registro.category, 'title').toString().split(','));
        $scope.pobDestino = {};
        $scope.pobDestino.selected = angular.isDefined($scope.registro.category) ?
                _.map($scope.registro.category, 'title').toString().split(',') : [];
        $scope.onSelectPobDestino = function($item, $model){
            if(angular.isUndefined($scope.registro.population)) {
                $scope.registro.population = [];
            };
            $scope.registro.population.push({title: $item.title});
        };
        $scope.onRemovePobDestino = function($item, $model){
            var index = _.indexOf($scope.pobDestino.selected, $item.id.toString());
            $scope.registro.poblacion.splice(index, 1);
        };

    }
]);
