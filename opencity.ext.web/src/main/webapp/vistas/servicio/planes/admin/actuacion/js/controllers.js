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

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'Restangular','$location', '$route', 'Informer', '$filter', '$window', '$timeout',
    function($scope, Dao, $http, registro, Restangular, $location, $route, Informer, $filter, $window, $timeout) {
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        $scope.documentoTitulo = null;
        $scope.documentoURL = null;
        $scope.datoAbiertoTitulo = null;
        $scope.datoAbiertoURL = null;

        $scope.valorTabla = null;

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

        $scope.datepickers = {startDate: false, endDate: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.cargarFicha = function(id){
            $window.open('/sede/servicio/planes/admin/' + id + '?refresh=y', '_blank');
        };

        // Tablas: discriminador y atributos predeterminados
        function getDiscriminadorEdad() {
            var params = {};
            params.start = 0;
            params.rows = 1; // El primer resultado
            params.sort = 'id asc';
            params.q = [
            	{ key: 'title',	value: 'EDAD' }
            ];
            Dao.setFiltros(params);
            Dao.listar('../atributo/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) {
                	Informer.inform("Error al obtener el atributo 'EDAD'", "warning");
                }
                else {
                    $scope.discriminadorEdad = Restangular.stripRestangular(data[0]);
                    getAtributoHombre();
                };
            });
        };
        function getAtributoHombre() {
            var params = {};
            params.start = 0;
            params.rows = 1; // El primer resultado
            params.sort = 'id asc';
            params.q = [
            	{ key: 'title',	value: 'HOMBRE' }
            ];
            Dao.setFiltros(params);
            Dao.listar('../atributo/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) {
                	Informer.inform("Error al obtener el atributo 'HOMBRE'", "warning");
                }
                else {
                    $scope.atributosGenero = [];
                    $scope.atributosGenero.push(Restangular.stripRestangular(data[0]));
                    getAtributoMujer();
                };
            });
        };
        function getAtributoMujer() {
            var params = {};
            params.start = 0;
            params.rows = 1; // El primer resultado
            params.sort = 'id asc';
            params.q = [
            	{ key: 'title',	value: 'MUJER' }
            ];
            Dao.setFiltros(params);
            Dao.listar('../atributo/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) {
                	Informer.inform("Error al obtener el atributo 'MUJER'", "warning");
                }
                else {
                    $scope.atributosGenero.push(Restangular.stripRestangular(data[0]));
                    $scope.registro.tablaDestinatariosPrevisto = {
                            discriminador: $scope.discriminadorEdad,
                            atributos: $scope.atributosGenero,
                            valores: []
                        };
                    $scope.registro.tablaDestinatariosRealizado = {
                            discriminador: $scope.discriminadorEdad,
                            atributos: $scope.atributosGenero,
                            valores: []
                        };
                };
            });
        };
        
        // Tablas: creación
        if ($scope.registro.tablaDestinatariosPrevisto === undefined) {
            getDiscriminadorEdad();
        }
        if ($scope.registro.tablaOtrosDestPrevisto === undefined) {
            $scope.registro.tablaOtrosDestPrevisto = {
                    discriminador: { id: null, title: "DESTINATARIOS", valores: [] },
                    atributos: [],
                    valores: []
                };
        }
        if ($scope.registro.tablaGastoPrevisto === undefined) {
            $scope.registro.tablaGastoPrevisto = {
                    discriminador: { id: null, title: "CONCEPTOS", valores: [] },
                    atributos: [],
                    valores: []
                };
        }
        if ($scope.registro.tablaOtrosDestRealizado === undefined) {
            $scope.registro.tablaOtrosDestRealizado = {
                    discriminador: { id: null, title: "DESTINATARIOS", valores: [] },
                    atributos: [],
                    valores: []
                };
        }
        if ($scope.registro.tablaGastoRealizado === undefined) {
            $scope.registro.tablaGastoRealizado = {
                    discriminador: { id: null, title: "CONCEPTOS", valores: [] },
                    atributos: [],
                    valores: []
                };
        }
        if ($scope.registro.tablaIndicadoresRealizado === undefined) {
            $scope.registro.tablaIndicadoresRealizado = {
                    discriminador: { id: null, title: "INDICADOR", valores: [] },
                    atributos: [],
                    valores: []
                };
        }

        // Tablas: funciones
        $scope.obtenerValorTabla = function(tabla, discriminador, atributo) {
            // Buscar valor
            var valor = tabla.valores.filter(function(valor) {
                return ((valor.codAtributo == atributo.id) && (valor.valorDiscriminador == discriminador.valorDiscriminador))
            })[0];
            if (valor != null) {
                // Obtener valor
                return valor.valorAtributo;
            }
            return null;
        }

        $scope.establecerValorTabla = function(tabla, discriminador, atributo) {
            // Buscar valor
            var valor = tabla.valores.filter(function(valor) {
                return ((valor.codAtributo == atributo.id) && (valor.valorDiscriminador == discriminador.valorDiscriminador))
            })[0];
            if (valor != null) {
                // Actualizar valor
                valor.valorAtributo = this.valorTabla;
            } else {
                // Crear valor
                if (atributo != null) {
                    tabla.valores.push({
                        codDiscriminador: discriminador.codDiscriminador,
                        valorDiscriminador: discriminador.valorDiscriminador,
                        codAtributo: atributo.id,
                        valorAtributo: this.valorTabla
                    });
                }
            }
        }

        // Adición de nuevo valor de atributo discriminador (propio)
        $scope.nuevoValorDisc = function(tabla, valor) {
            var nombre = valor.trim();
            if (nombre === "") {
                return;
            }
            for (var i = 0, len = tabla.discriminador.valores.length; i < len; i++) {
                if (nombre === tabla.discriminador.valores[i].valorDiscriminador) {
                    return;
                }
            }
        	tabla.discriminador.valores.push({ id: null, codDiscriminador: null, valorDiscriminador: valor });
        }
        
        // Adición de nuevo atributo (estandarizado o propio)
        var codAtributo = -1;   // Si es propio, identificador temporal para asociar sus posibles valores
        $scope.nuevoAtributo = function(tabla, atributo, propio) {
        	if (propio) {
        	    var title = atributo.trim();
        	    if (title === "") {
        	        return;
        	    }
        	    for (var i = 0, len = tabla.atributos.length; i < len; i++) {
        	        if (title === tabla.atributos[i].title) {
        	            return;
        	        }
        	    }
        		atributo = {
        			id: codAtributo--,
        			title: atributo
        		}
        	}
        	else {
        	    if (atributo.id === undefined) {
        	        return;
        	    }
        	    for (var i = 0, len = tabla.atributos.length; i < len; i++) {
        	        if (atributo.id === tabla.atributos[i].id) {
        	            return;
        	        }
        	    }
        	}
        	tabla.atributos.push(atributo);
        }

        // Desplazar un atributo (columna) de una tabla
        $scope.desplazarAtributo = function(tabla, index, pos) {
            tabla.atributos[index+pos] = tabla.atributos.splice(index, 1, tabla.atributos[index+pos])[0];
        };

        // Eliminar un atributo (columna) de una tabla
        $scope.eliminarAtributo = function(tablas, index) {
            if (confirm('¿Desea eliminar la columna? Se perderán los valores introducidos en ella.')) {
                for (var i = 0, len = tablas.length; i < len; i++) {
                	tablas[i].atributos.splice(index, 1);
                }
            }
        };

        // Desplazar un valor discriminador (fila) de una tabla
        $scope.desplazarValor = function(tabla, index, pos) {
            tabla.discriminador.valores[index+pos] = tabla.discriminador.valores.splice(index, 1, tabla.discriminador.valores[index+pos])[0];
        };

        // Eliminar un valor discriminador (fila) de una tabla
        $scope.eliminarValor = function(tablas, index) {
            if (confirm('¿Desea eliminar la fila? Se perderán los valores introducidos en ella.')) {
                for (var i = 0, len = tablas.length; i < len; i++) {
                	tablas[i].discriminador.valores.splice(index, 1);
                }
            }
        };

        
        // Entidades colaboradoras (asociaciones)
        $scope.asociarEntidad = function(asociacion) {
            if (($scope.registro.asociaciones === undefined) || ($scope.registro.asociaciones === null)) {
                $scope.registro.asociaciones = [];
            }
            if ($scope.registro.asociaciones.length >= 3) {
                alert("No se pueden seleccionar más de 3 entidades colaboradoras.");
            }
            else {
                var found = false;
                for (var i = 0, len = $scope.registro.asociaciones.length; i < len; i++) {
                    if ($scope.registro.asociaciones[i].id === asociacion.id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    $scope.registro.asociaciones.push(asociacion);
                }
            }
        }
        $scope.desasociarEntidad = function(index) {
            $scope.registro.asociaciones.splice(index, 1);
        }

        // Juntas municipales y vecinales
        $scope.asociarJunta = function(junta) {
            if (($scope.registro.juntas === undefined) || ($scope.registro.juntas === null)) {
                $scope.registro.juntas = [];
            }
            if ($scope.registro.juntas.length >= 4) {
                alert("No se pueden seleccionar más de 4 juntas.");
            }
            else {
                var found = false;
                for (var i = 0, len = $scope.registro.juntas.length; i < len; i++) {
                    if ($scope.registro.juntas[i].id === junta.id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    $scope.registro.juntas.push(junta);
                }
            }
        }
        $scope.desasociarJunta = function(index) {
            $scope.registro.juntas.splice(index, 1);
        }

        // Enlaces a documentos e imágenes y datos abiertos
        if ($scope.registro.documentos == undefined) {
            $scope.registro.documentos = [];
        }
        if ($scope.registro.datosAbiertos == undefined) {
            $scope.registro.datosAbiertos = [];
        }

        $scope.asociarDocumento = function(titulo, url) {
            $scope.registro.documentos.push({
                title: titulo,
                enlace: url
            });
        }

        $scope.desasociarDocumento = function(index) {
            $scope.registro.documentos.splice(index, 1);
        }

        $scope.asociarDatoAbierto = function(titulo, url) {
            $scope.registro.datosAbiertos.push({
                title: titulo,
                enlace: url
            });
        }

        $scope.desasociarDatoAbierto = function(index) {
            $scope.registro.datosAbiertos.splice(index, 1);
        }

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
