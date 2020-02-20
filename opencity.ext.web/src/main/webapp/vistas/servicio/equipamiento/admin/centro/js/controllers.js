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
            params.sort = 'lastUpdated desc';
        };
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];
        $scope.busy = false;

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

        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 100; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
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
            Dao.listar('list', params).then(function(data) {
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

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'poblacion', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout',
    function($scope, Dao, $http, registro, poblacion, Restangular, $location, $route, Informer, $filter, $window, $timeout) {
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        if (($scope.registro.edificio === undefined) || ($scope.registro.edificio === null)) {
            $scope.registro.edificio = [];
        }

        if (($scope.registro.institucion === undefined) || ($scope.registro.institucion === null)) {
            $scope.registro.institucion = [];
        }

        if($location.path() === '/new'){
            // Estos campos se machacan en servidor
            $scope.registro.creationDate = new Date();
        };

        if(typeof copiaRegistro === 'undefined' || copiaRegistro === null){
            $scope.registro = registro;
        } else {
            $scope.registro = copiaRegistro;
            delete copiaRegistro;
        };

        $scope.cargarFicha = function(id){
            $window.open('/sede/servicio/equipamiento/admin/' + id + '?refresh=y', '_blank');
        };

        $scope.instituciones = [];
        // Restangular.all('institucion').getList().then(function(res) {
        Restangular.all('instituciones').customGET("", {rows: 1000, sort: "title asc"}).then(function(res) {
            // customGET: first parameter is required, so just provide empty string
            $scope.instituciones = res.result;
        });

        // Clases de persona asociadas al centro
        $scope.clasepersona = [];
        for (var c in $scope.registro.clasepersona) {
            $scope.clasepersona.push($scope.registro.clasepersona[c].id);
        }

        // Listado de clases de persona
        $scope.personas = {};
        $scope.tiposPersona = [];
        // Restangular.all('personas').getList().then(function(res) {
        Restangular.all('personas').customGET("", {rows: 1000, sort: "id asc"}).then(function(res) {
            // customGET: first parameter is required, so just provide empty string
            for (var i in res.result) {
                var persona = res.result[i];
                var tipoPersona = persona.tipoPersona.title;
                var tipoPersonaList = $scope.personas[tipoPersona];
                if (tipoPersonaList === undefined) {
                    tipoPersonaList = [];
                    $scope.personas[tipoPersona] = tipoPersonaList;
                    $scope.tiposPersona.push(tipoPersona);
                }
                tipoPersonaList.push(persona);
            }
        });

        // Trámites asociados
        $scope.asociarTramite = function(idTramite) {
            Restangular.all($scope.registro.id + '/tramite/' + idTramite + '/save').post(tramite).then(function(res) {
                $scope.registro.procs = res.procs;
                Informer.inform('Tr\u00E1mite añadido.', 'success');
            }, function(err){
                Informer.inform("Se ha producido un error al a\u00F1adir el tr\u00E1mite: " + err.data.error, "danger");
            });
        }

        $scope.desasociarTramite = function(index) {
            var idProc = $scope.registro.procs.result[index].id;
            if (idProc !== null) {
                Restangular.all($scope.registro.id + '/tramite/' + idProc + '/remove').remove().then(function(res) {
                    $scope.registro.procs.result.splice(index, 1);
                    Informer.inform('Tr\u00E1mite desasociado.', 'success');
                }, function(err){
                    Informer.inform("Se ha producido un error al desasociar el tr\u00E1mite: " + err.data.error, "danger");
                });
            }
        }

        // Temas (subtemas) asociados
        $scope.asociarSubtema = function(subtema) {
            if (($scope.registro.category === undefined) || ($scope.registro.category === null)) {
                $scope.registro.category = [];
            }
            $scope.registro.category.push(subtema);
        }

        $scope.desasociarSubtema = function(index) {
            $scope.registro.category.splice(index, 1);
        }

        // Tipos de personas asociados
        $scope.onPersonaClicked = function(persona) {
            var pos = posicionPersona(persona.id);
            if (persona.checked) {
                if (pos == -1) {
                    if (($scope.registro.clasepersona === undefined) || ($scope.registro.clasepersona === null)) {
                        $scope.registro.clasepersona = [];
                    }
                    $scope.registro.clasepersona.push(persona);
                }
            }
            else {
                if (pos != -1) {
                    $scope.registro.clasepersona.splice(pos, 1);
                }
            }
        }

        function posicionPersona(idPersona) {
            if ($scope.registro.clasepersona) {
                for (var i = 0; i < $scope.registro.clasepersona.length; i++) {
                    if ($scope.registro.clasepersona[i].id == idPersona) {
                        return i;
                    }
                }
            }
            return -1;
        }

        // Propiedades asociadas
        function resetPropiedadCentro() {
            $scope.propiedadCentro = {
                propiedad: null,
                valorBool: null,
                valorNum: null,
                valorDate: null,
                valorString: null,
                observaciones: null
            };
        }
        resetPropiedadCentro();

        $scope.asociarPropiedad = function() {
            if (($scope.registro.propiedad === undefined) || ($scope.registro.propiedad === null)) {
                $scope.registro.propiedad = [];
            }
            var prop = {
                propiedad: $scope.propiedadCentro.propiedad,
                observ: $scope.propiedadCentro.observaciones
            }
            if ($scope.propiedadCentro.propiedad.tipo === 'xsd:int' || $scope.propiedadCentro.propiedad.tipo === 'xsd:integer') {
                prop.valor = parseInt($scope.propiedadCentro.valorNum);
            }
            else if ($scope.propiedadCentro.propiedad.tipo === 'xsd:float') {
                prop.valor = $scope.propiedadCentro.valorNum;
            }
            else if ($scope.propiedadCentro.propiedad.tipo === 'xsd:boolean') {
                prop.valor = $scope.propiedadCentro.valorBool;
            }
            else if ($scope.propiedadCentro.propiedad.tipo === 'xsd:date') {
                var day = $scope.propiedadCentro.valorDate.getDate();
                if (day < 10) { day = "0" + day; }
                var month = $scope.propiedadCentro.valorDate.getMonth() + 1;
                if (month < 10) { month = "0" + month; }
                prop.valor = day + "/" + month + "/" + $scope.propiedadCentro.valorDate.getFullYear();
            }
            else {
                prop.valor = $scope.propiedadCentro.valorString;
            }
            $scope.registro.propiedad.push(prop);
            resetPropiedadCentro();
        }

        $scope.desasociarPropiedad = function(index) {
            $scope.registro.propiedad.splice(index, 1);
        }

        $scope.guardar = function() {
            var queryParams = {};

            // Quitar el atributo "checked" de los tipo de persona para evitar que falle el guardado
            if ($scope.registro.clasepersona) {
                for (var i = 0; i < $scope.registro.clasepersona.length; i++) {
                    delete $scope.registro.clasepersona[i].checked;
                }
            }

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
