angular.module('Gesweb.directives', [])

.directive('whenScrolled', function() {
    return function(scope, elm, attr) {
        var raw = elm[0];
        elm.bind('scroll', function() {
            if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight) {
                scope.$apply(attr.whenScrolled);
            }
        });
    };
})

.directive('dateParser', ['$filter', function($filter) {
    return {
        require: '?ngModel',
        link: function(scope, elem, attrs, ctrl) {
            if (!ctrl) return;

            ctrl.$formatters.push(function(value) {
                if (value) {
                    var dt = new Date(value);
                    // 'undo' the timezone offset again (so we end up on the original date again)
                    dt.setMinutes(dt.getMinutes() + dt.getTimezoneOffset());
                    return dt;
                }

                return undefined;
            });
        }
    };
}])

.directive('datepickerLocaldate', ['$parse', function ($parse) {
    var directive = {
        restrict: 'A',
        require: ['ngModel'],
        link: link
    };
    return directive;

    function link(scope, element, attr, ctrls) {
        var ngModelController = ctrls[0];

        // called with a JavaScript Date object when picked from the datepicker
        ngModelController.$parsers.push(function (viewValue) {
            // undo the timezone adjustment we did during the formatting
            //viewValue.setMinutes(viewValue.getMinutes() - viewValue.getTimezoneOffset());
            // we just want a local date in ISO format
            return new Date(viewValue);//.substring(0, 10);
        });

        // called with a 'yyyy-mm-dd' string to format
        ngModelController.$formatters.push(function (modelValue) {
            if (!modelValue) {
                return undefined;
            }
            // date constructor will apply timezone deviations from UTC (i.e. if locale is behind UTC 'dt' will be one day behind)
            var dt = new Date(modelValue);
            // 'undo' the timezone offset again (so we end up on the original date again)
            //dt.setMinutes(dt.getMinutes() + dt.getTimezoneOffset());
            return dt;
        });
    }
}])
.directive('botonVisibilidad', ['Dao', 'Restangular', '$route', '$location', 'Informer', 
    function(Dao, Restangular, $route, $location, Informer) {
        return {
            restrict: 'E',
            template: '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'S\'" title="Publicar">'
                + '<span class="fa fa-eye" aria-hidden="true"></span>'
                + '</button> '
                + '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'N\'" title="Ocultar">' 
                + '<span class="fa fa-eye-slash" aria-hidden="true"></span>'
                + '</button>',
            link: function(scope, elem, attrs) {

                // Si no viene definida...
                if (angular.isUndefined(scope.registro)) {
                    scope.registro = {};
                    scope.registro.visible = '';
                };
                // Creando un nuevo registro en /new
                if(angular.isUndefined(scope.registro.visible)){
                    scope.registro.visible = 'S';
                };

                scope.visibilidad = {
                    valor: scope.registro.visible === 'S' ? true : false,
                    // texto: scope.registro.visible === 'S' ? ' Ocultar' : ' Mostrar'
                }
                scope.setVisibilidad = function(val) {

                    if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                        for(var i = 0; i < scope.checkList.length; i++){
                            scope.checkList[i].visible = scope.checkList[i].visible === 'S' ? 'N' : 'S';
                        };
                    } else {
                        scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S';    
                    };
                    
                    // Si $location.path === '/new' solo hacemos set de gcz_pub
                    if($location.path() !== '/new'){
                        // Multiples registros del listado seleccionados
                        if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                            for(var i = 0; i < scope.checkList.length; i++){
                                // Establecemos route de detalle de registro
                                Restangular.restangularizeElement('', scope.checkList[i], scope.checkList[i].route.replace('/admin/list', ''));
                                Dao.actualizar(scope.checkList[i]).then(function() {
                                    scope.visibilidad.valor = !val;
                                    Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                                }, function(err){
                                    // En caso de error volvemos al estado anterior
                                    scope.checkList[i].visible = scope.checkList[i].visible === 'S' ? 'N' : 'S'
                                    Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                });
                            };
                        }
                         }else {
                            // Establecemos route de detalle de registro
                              Restangular.restangularizeElement('', scope.registro, scope.registro.route.replace('/admin/listado', ''));
                                                        Dao.actualizar(scope.registro).then(function() {
                                                            scope.visibilidad.valor = !val;
                                                            Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                                                        }, function(err){
                                                            // En caso de error volvemos al estado anterior
                                                            scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S'
                                                            Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                                        });
                                                    };
                            }
                        }


                };

            }


]).directive('botonVisibilidadQuery', ['Dao', 'Restangular', '$route', '$location', 'Informer',
      function(Dao, Restangular, $route, $location, Informer) {

console.log("LLEGO.....");
          return {
              restrict: 'E',
              template: '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'S\'" title="Publicar">'
                  + '<span class="fa fa-eye" aria-hidden="true"></span>'
                  + '</button> '
                  + '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'N\'" title="Ocultar">'
                  + '<span class="fa fa-eye-slash" aria-hidden="true"></span>'
                  + '</button>',
              link: function(scope, elem, attrs) {

                  // Si no viene definida...
                  if (angular.isUndefined(scope.registro)) {
                      scope.registro = {};
                      scope.registro.visible = '';
                  };
                  // Creando un nuevo registro en /new
                  if(angular.isUndefined(scope.registro.visible)){
                      scope.registro.visible = 'S';
                  };

                  scope.visibilidad = {
                      valor: scope.registro.visible === 'S' ? true : false,
                      // texto: scope.registro.visible === 'S' ? ' Ocultar' : ' Mostrar'
                  }
                  scope.setVisibilidad = function(val) {
                        console.log(scope.registro.visible);
                      if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                          for(var i = 0; i < scope.checkList.length; i++){
                              scope.checkList[i].visible = scope.checkList[i].visible === 'S' ? 'N' : 'S';
                          };
                      } else {
                          scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S';
                      };

                      // Si $location.path === '/new' solo hacemos set de gcz_pub
                      if($location.path() !== '/new'){
                          // Multiples registros del listado seleccionados
                          if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                           console.log("LLEGO..... no new");

                              for(var i = 0; i < scope.checkList.length; i++){
                                  // Establecemos route de detalle de registro
                                       Restangular.restangularizeElement('', scope.checkList[i], scope.checkList[i].route.replace('/admin/list', ''));
                                         Dao.actualizar(scope.checkList[i]).then(function() {
                                             scope.visibilidad.valor = !val;
                                             Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                                         }, function(err){
                                             // En caso de error volvemos al estado anterior
                                             scope.checkList[i].visible = scope.checkList[i].visible === 'S' ? 'N' : 'S'
                                             Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                         });
                                     };

                           }else{
                            if(scope.registro.visible=='S'){
                                       Restangular.all('/admin/'+scope.registro.id +'/unlock').customGET().then(function() {
                                           scope.visibilidad.valor = !val;
                                           Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                                       }, function(err){
                                           // En caso de error volvemos al estado anterior
                                           scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S'
                                           Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                       });
                                       Restangular.restangularizeElement('', scope.registro, scope.registro.route.replace('/admin/list', ''));
                           }else{
                           Restangular.all('/admin/'+scope.registro.id +'/lock').customGET().then(function() {
                               scope.visibilidad.valor = !val;
                               Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                           }, function(err){
                               // En caso de error volvemos al estado anterior
                               scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S'
                               Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                       });

                             }
                           }




                          } else {
                              // Establecemos route de detalle de registro
                              console.log("LLEGO.....  new");
                              Restangular.restangularizeElement('', scope.registro, scope.registro.route.replace('/admin/listado', ''));
                              Dao.actualizar(scope.registro).then(function() {
                                  scope.visibilidad.valor = !val;
                                  Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                              }, function(err){
                                  // En caso de error volvemos al estado anterior
                                  scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S'
                                  Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                              });
                          };
                      }

                  }


          };
      }
  ])
.directive('botonVisibilidad10', ['Dao', 'Restangular', '$route', '$location', 'Informer', 
    function(Dao, Restangular, $route, $location, Informer) {
        return {
            restrict: 'E',
            template: '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'1\'" title="Publicar">'
                + '<span class="fa fa-eye" aria-hidden="true"></span>'
                + '</button> '
                + '<button ng-show="permisos.PUB && location !== \'/new\'" type="button" class="btn btn-default" ng-click="setVisibilidad(visibilidad.valor);" ng-disabled="registro.visible === \'0\'" title="Ocultar">' 
                + '<span class="fa fa-eye-slash" aria-hidden="true"></span>'
                + '</button>',
            link: function(scope, elem, attrs) {
                // Si no viene definida...
                if (angular.isUndefined(scope.registro)) {
                    scope.registro = {};
                    scope.registro.visible = '';
                };
                // Creando un nuevo registro en /new
                if(angular.isUndefined(scope.registro.visible)){
                    scope.registro.visible = '1';
                };

                scope.visibilidad = {
                    valor: scope.registro.visible === '1' ? true : false,
                    // texto: scope.registro.visible === 'S' ? ' Ocultar' : ' Mostrar'
                }
                scope.setVisibilidad = function(val) {

                    if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                        for(var i = 0; i < scope.checkList.length; i++){
                            scope.checkList[i].visible = scope.checkList[i].visible === '1' ? '0' : '1';
                        };
                    } else {
                        scope.registro.visible = scope.registro.visible === '1' ? '0' : '1';    
                    };
                    
                    // Si $location.path === '/new' solo hacemos set de gcz_pub
                    if($location.path() !== '/new'){
                        // Multiples registros del listado seleccionados
                        if(angular.isDefined(scope.checkList) && scope.checkList.length > 0){
                            for(var i = 0; i < scope.checkList.length; i++){
                                // Establecemos route de detalle de registro
                                Restangular.restangularizeElement('', scope.checkList[i], scope.checkList[i].route.replace('/admin/list', ''));
                                Dao.actualizar(scope.checkList[i]).then(function() {
                                    scope.visibilidad.valor = !val;
                                    Informer.inform('Registro ' + (scope.registro.visible === '1' ? '' : 'des') + 'publicado correctamente.', "success");
                                }, function(err){
                                    // En caso de error volvemos al estado anterior
                                    scope.checkList[i].visible = scope.checkList[i].visible === '1' ? '0' : '1'
                                    Informer.inform('Error al ' + (scope.registro.visible === '1' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                });
                            };
                        } else {
                            // Establecemos route de detalle de registro
                            Restangular.restangularizeElement('', scope.registro, scope.registro.route.replace('/admin/list', ''));
                            Dao.actualizar(scope.registro).then(function() {
                                scope.visibilidad.valor = !val;
                                Informer.inform('Registro ' + (scope.registro.visible === '1' ? '' : 'des') + 'publicado correctamente.', "success");
                            }, function(err){
                                // En caso de error volvemos al estado anterior
                                scope.registro.visible = scope.registro.visible === '1' ? '0' : '1'
                                Informer.inform('Error al ' + (scope.registro.visible === '1' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                            });
                        };     
                    };

                };

            }
        };
    }
])
.directive('botonCopiar', ['Restangular', '$location',
    function(Restangular, $location) {
        return {
            restrict: 'E',
            template: '<button title="Copiar" ng-show="permisos.NEW && location !== \'/new\'" type="button" class="btn btn-default" ng-click="copiarRegistro()">' + '<span class="fa fa-clone" aria-hidden="true"></span><span class="sr-only">' + ' Copiar' + '</span></button>',
            link: function(scope, elem, attrs) {
                scope.copiarRegistro = function() {
                    copiaRegistro = Restangular.copy(scope.registro.originalElement);
                    if(angular.isDefined(copiaRegistro.id)){delete copiaRegistro.id;};
                    //if(angular.isDefined(copiaRegistro.service_request_id)){delete copiaRegistro.service_request_id;};
                    if(angular.isDefined(copiaRegistro.creationDate)){delete copiaRegistro.creationDate;};
                    if(angular.isDefined(copiaRegistro.lastUpdate)){delete copiaRegistro.lastUpdate;};
                    $location.path('/new');
                };
            }
        };
    }
])

.directive('thOrden', ['Dao',
    function(Dao) {
        return {
            restrict: 'A',
            transclude: true,
            template: '<a ng-click="ordenarCabecera()">' + '<span ng-transclude></span>' + ' <span class="fas" ng-class="{\'fa-sort-alpha-down\' : order === by && !reverse,  \'fa-sort-alpha-up\' : order===by && reverse}" aria-hidden="true"></span>' + '</a>',
            scope: {
                order: '=',
                by: '=',
                reverse: '='
            },
            link: function(scope, element, attrs) {

                scope.ordenarCabecera = function() {
                    var field = attrs.order.replace(/("|')/g, ''); // Eliminamos '' de las cadenas
                    // La primera ordenacion en cada cabecera, siempre son diferentes
                    var value = field;
                    if (typeof orden !== 'undefined') {
	                    if (scope.order !== scope.by) {
	                        orden = ' asc';
	                    } else {
	                        orden = orden === ' desc' ? ' asc' : ' desc';
	                    }
	                    value += orden;
                    }
                    else {
	                    if (scope.order !== scope.by) {
	                    	value += ' asc';
	                    }
	                    else {
	                    	value += scope.reverse ? ' asc' : ' desc';
	                    }
                    }

                    var param = Dao.getFiltros(); // Obtenemos los qparams del servicio Dao || {}
                    param.start = 0; // Forzamos que devuelva de nuevo desde el primer registro.
                    param.sort = value;

                    var route = scope.$parent.registros[0].route; // Obtenemos la ruta de un registro cualquiera
                    Dao.listar(route, param).then(function(data) {
                        scope.$parent.registros = data;
                        
                        scope.$parent.filtrosParaListado.start = 50;
                        
                        scope.$parent.currentPage = 1;
                        
                        scope.$parent.busy = false;
                        // Ordenamos los resultados
                        if (scope.order === scope.by) {
                            scope.reverse = !scope.reverse;
                        } else {
                            scope.by = scope.order;
                            scope.reverse = false;
                        };
                    });
                }
            }
        }
    }
])

.directive('buscador', ['Dao',
    function(Dao) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                filter: '=',
                placeholder: '@'
            },
            template: '<input type="text" class="form-control" ng-model="filter" ng-model-options="{ debounce: 1500 }" placeholder="{{placeholder}}">',
            link: function(scope, elem, attrs) {
                var route;
                scope.$watch('filter', function(newValue, oldValue) {
                    // La primera vez que $watch es llamado, newValue === oldValue,
                    // asi evitamos la primera llamada al cargar modelo.
                    if (newValue !== oldValue && newValue.length >= 3) {
                        var params = {};
                        params.q = [];
                        item = new Object();
                        item.key = attrs.filter; // Campo de busqueda. Ej: 'title' 
                        if (angular.isNumber(newValue)) {
                        	item.value = newValue; // Valor del campo de busqueda actual
                        } else {
                        	item.value = newValue + '*';
                        }
                        params.q.push(item);

                        if(angular.isUndefined(attrs.route) && angular.isUndefined(route)) {
                            route = scope.$parent.registros[0].route;
                        } else if(angular.isDefined(attrs.route)){
                            route = attrs.route;
                        } else {
                            throw "'route' no definida";
                        }
                        var datosListados = [];
                        Dao.listar(route, params).then(function(data) {
                            datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                            scope.$parent.registros = data;
                            scope.$parent.busy = false;
                            console.log(JSON.stringify(params));
                            scope.filter = "";
                        });
                    }
                });                 
            }
        };
    }
])

.directive('autocompletado', ['$http', 'limitToFilter', '$timeout',
    function($http, limitToFilter, $timeout) {
        return {
            restrict: 'E',
            scope: {
                //filtro: '@',
                modelo: '=',
                check: '&?',
                retorno: '=?'
            },
            replace: true,
            // REVISAR BUG expresiones dinámicas ngOptions https://github.com/angular-ui/bootstrap/issues/4631
            //template: '<input typeahead-on-select="onSelect($item, $model, $label)" class="form-control" type="text" ng-model="modelo" uib-typeahead="registro as registro.{{filtro}} for registro in obtenerListado($viewValue)">',
            template: '<input typeahead-on-select="onSelect($item, $model, $label)" class="form-control" type="text" ng-model="modelo" uib-typeahead="registro as registro.title for registro in obtenerListado($viewValue)">',
            link: function(scope, element, attrs) {
                // Obligatorio. Ruta del recurso a buscar
                var uri = attrs.uri;
                var url = uri;
                // var url = 'http://localhost:7777' + uri;
                //var url = '//' + window.sessionStorage.getItem('SERVIDOR') + uri;
                
                // Opcional. Campo por el que se realizará la búsqueda en el typeahead
                // REVISAR BUG expresiones dinámicas ngOptions https://github.com/angular-ui/bootstrap/issues/
                var filtro = 'title';
                var filtroId = 'id';

                scope.obtenerListado = function(val) {
                    // Formamos la query fiql con el valor del campo a buscar
                    var query;
                    if (isNaN(val)) {
                    	query = filtro + '==' + val + '*';
                    } else {
                    	query = filtroId + '==' + val;                    	
                    }
                    // Opcional. Añade otras condiciones si están definidas
                    if(attrs.fiql){query += ';' + attrs.fiql};
                    var params = {
                          q: query,
                          sort: filtro + ' asc'
                    }
                    if (attrs.campos) {
                        params.fl = attrs.campos;
                    }
                    return $http.get(url, {
                        params: params
                    }).then(function(res) {
                        var registros = [];
                        angular.forEach(res.data.result, function(item) {
                            registros.push(item); // item[filtro]
                        });
                        return limitToFilter(registros, 15);
                    });
                };
                // Opcional. Se ejecuta al seleccionar un resultado del typeahead
                scope.onSelect = function ($item, $model, $label) {
                    // Opcional. Devuelve solo el valor de un atributo en lugar del objeto completo
                    if(attrs.retorno){scope.modelo = $item[attrs.retorno];}
                    
                    if(angular.isDefined(scope.check)) {
                        // BUGFIX: https://docs.angularjs.org/error/$rootScope/inprog
                        $timeout(function() {
                          scope.check();
                        }, 0);    
                    };
                 	
                };
            }
        };
    }
])

.directive('portalero', function($http, limitToFilter) {
    return {
        restrict: 'E',
        scope: {
            filtro: '@',
            modelo: '='
        },
        replace: true,
        template: '<input class="form-control" type="text" ng-model="modelo" typeahead="registro as (registro.{{filtro}} +\',\'+ registro.numero) for registro in obtenerListado($viewValue)">',
        link: function(scope, element, attrs) {

            var uri = attrs.uri || '/opencityext/servicio/portalero/';
            var url = uri;
            //var uri = attrs.uri || "/api/recurso/urbanismo-infraestructuras/portalero";
            //var url = 'http://' + window.sessionStorage.getItem('SERVIDOR') + uri;
            var filtro = attrs.filtro || 'calle.title'; // Si no ha filtro asignado buscamos por defecto con title

            scope.obtenerListado = function(val) {
            	
            	var valor = val.split(",");
            	var query = "";
            	if (valor.length > 1) {
            		query = filtro + '==' + valor[0] + '*;numero==' + valor[1];
            		if (attrs.numero) {
            			query = query + '*';
            		}
            	} else {
            		query = filtro + '==' + val + '*';
            	}
                //console.log(query);
                return $http.get(url, {
                    params: {
                        q: query
                    }
                }).then(function(res) {
                    var registros = [];
                    angular.forEach(res.data.result, function(item) {
                        registros.push(item); // item[filtro]
                    });
                    return limitToFilter(registros, 10);
                });
            };

        }
    };
})

.directive('georrefTool', function() {

    return {
        template: '<input type="hidden" id="jsoncontent" name="jsoncontent" value=""/>',
        //      templateUrl : 'partials/iframe_visualizador.htm',
        link: function(scope, elm, attrs) {
            var cargadoPuntoX;
            var cargadoPuntoY;
            if (scope.registro) {
                if(scope.registro.x && scope.registro.y){ // Ticketing
                    cargadoPuntoX = scope.registro.x;
                    cargadoPuntoY = scope.registro.y;   
                } else if (scope.registro.geometry){ // Resto de salidas API
                    cargadoPuntoX = scope.registro.geometry.coordinates[0];
                    cargadoPuntoY = scope.registro.geometry.coordinates[1];
                }     
            }
            
            var ifrm = document.createElement("iframe");
            ifrm.setAttribute("name", "visualizador_visor");
            ifrm.setAttribute("id", "visualizador_visor");
            ifrm.setAttribute("title", "Localizacion");
            ifrm.setAttribute("scrolling", "no");
            ifrm.setAttribute("src", "/IDEZar_mapasColaborativos/index.html?VIEWERMODE=EDIT&c=" + Math.random());
            ifrm.setAttribute("frameBorder", "0");
            ifrm.setAttribute("style", "border: 0px; valign: bottom; align: left;");
            ifrm.width = "100%";
            ifrm.height = "500px";
            elm.context.appendChild(ifrm);

            scope.$watch("portaleroSeleccion", function(newvalue, oldvalue) {
            	console.log("LLEGA");
                if (newvalue && newvalue !== oldvalue && angular.isDefined(scope.portaleroSeleccion.calle)) {
                    var nuevoPuntoX = newvalue.geometry.coordinates[0];
                    var nuevoPuntoY = newvalue.geometry.coordinates[1];
                    if (parent.frames["visualizador_visor"] != null) {
                        var facada = document.getElementById("visualizador_visor").contentWindow.facade; 
                        var json = '{"type": "FeatureCollection","crs": {"type": "EPSG","properties": {"code": 23030}},"properties": {"title": "capa","icon": "","link": "","description": "capa"},"features": [{"type": "Feature","geometry": {"type": "Point","coordinates": [' + nuevoPuntoX + ',' + nuevoPuntoY + ']},"properties": {"title": "Queja","link": "","description": "","category": "","date": "","icon": ""}}]}';
                        facada.borrarCapasJSON();
                        facada.definirCapaJSON("userDefined", json, null, null, null, true);
                        facada.centrarMapa(parseFloat(nuevoPuntoX), parseFloat(nuevoPuntoY), null, null, "EPSG:23030", 8000, true);
                    } else {
                        //TODO: Revisar si es necesario
                        document.getElementById("id_port").innerHTML = document.getElementById("id_port").innerHTML + "No existe el visualizador";
                    }
                    // Asignamos los nuevos datos sobre portalero al registro actual
                    if (scope.registro) {
                        if(scope.registro.x && scope.registro.y){ // Ticketing
                            scope.registro.address_string = newvalue.calle.title;
                            scope.registro.address_id = newvalue.id;
                            scope.registro.x = nuevoPuntoX;
                            scope.registro.y = nuevoPuntoY;
                        } else if (scope.registro.geometry) { // Resto de salidas API
                            scope.registro.geometry.coordinates[0] = nuevoPuntoX;
                            scope.registro.geometry.coordinates[1] = nuevoPuntoY;
                        }     
                    }
                }
            });

            function FormComponentFacade() {
                this.visualizadorCargado = visualizadorCargado;
                function visualizadorCargado(exito, name) {
                    if (exito) {
                        if (cargadoPuntoX) {
                        	var visualizador = document.getElementById("visualizador_visor").contentWindow.facade;
                            var json = '{"type": "FeatureCollection","crs": {"type": "EPSG","properties": {"code": 23030}},"properties": {"title": "capa","icon": "","link": "","description": "capa"},"features": [{"type": "Feature","geometry": {"type": "Point","coordinates": [' + cargadoPuntoX + ',' + cargadoPuntoY + ']},"properties": {"title": "Queja","link": "","description": "","category": "","date": "","icon": ""}}]}';
                            visualizador.definirCapaJSON("userDefined", json, null, null, null, true);
                            visualizador.centrarMapa(parseFloat(cargadoPuntoX), parseFloat(cargadoPuntoY), null, null, "EPSG:23030", 8000, true);
                        }
                    } else {
                        alert('Error cargando el punto');
                    }
                }
            }
            facade = new FormComponentFacade();

        }
    };
})

/*.directive('fechaActual', ['dateFilter', 
    function(dateFilter) {
        return {
            restrict: 'E',
            link: function(scope, element, attrs) {
                var format;
                scope.$watch(attrs.formato, function(value) {
                    format = value;
                    updateTime();
                });
                function updateTime() {
                    var dt = dateFilter(new Date(), format);
                    element.text(dt);
                }
                function updateLater() {
                    setTimeout(function() {
                        updateTime();
                        updateLater();
                    }, 1000);
                }
                updateLater();
            }
        };
    }
])*/

.directive('cabecera', ['$window', '$filter', '$rootScope', 
    function($window, $filter, $rootScope) {
        return {
            restrict: 'E',
            //templateUrl: '/cont/vistas/servicio/catalogo/admin/templates/cabecera.html',
            replace: true,
            link: function($scope, elm, attrs, controller) {

                $scope.navbarCollapsed = true;

                $rootScope.usuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
                $rootScope.servicios = JSON.parse(sessionStorage.getItem('serviciosUsuario'));
				if(sessionStorage.getItem('liderUsuario')){
                    $rootScope.lider = JSON.parse(sessionStorage.getItem('liderUsuario'));
                };

                if(attrs.servicio === '' || attrs.seccion === ''){
                    elm.find("form").remove();
                } else {
                    var valorServicio = attrs.servicio;
                    var valorSeccion = attrs.seccion;

                    for (var i = 0; i < $scope.servicios.length; i++) {
                        if ($scope.servicios[i].codigoServicio === valorServicio) {
                            $scope.secciones = $scope.servicios[i].secciones;
                            $scope.serv = $scope.servicios[i];
                            for (var j = 0; j < $scope.secciones.length; j++) {
                                if ($scope.secciones[j].codigoSeccion === valorSeccion) {
                                    $scope.secc = $scope.secciones[j];
                                    var permisosUsuario = $scope.secciones[j].permisos;
                                }
                            }
                        }
                    }
                    
                    /*for (i = 0; i < permisosTotal.length; i++) {
                        if (permisosTotal.indexOf(permisosTotal[i]) !== -1) {
                            window["permiso" + permisosTotal[i]] = true;
                        }
                    }*/
                    
                    $scope.servicio = {};
                    $scope.servicio.selected = $scope.serv;
                    $scope.seccion = {};
                    $scope.seccion.selected = $scope.secc;

                    $scope.onSelectServ = function(selectedServ) {
                        $window.location.href = '/gesweb/secciones/' + $filter('lowercase')(selectedServ.codigoServicio) + "/" + $filter('lowercase')(selectedServ.codigoSeccionDefecto);
                    };
                    
                    $scope.tienePermiso = function(servicio,seccion,operacion) {
                    	for (var i = 0; i < $scope.servicios.length; i++) {
                            if ($scope.servicios[i].codigoServicio === servicio) {
                                var secciones = $scope.servicios[i].secciones;
                                for (var j = 0; j < secciones.length; j++) {
                                    if (secciones[j].codigoSeccion === seccion) {
                                        var seccionActual = $scope.secciones[j];
                                        for (var k = 0; k < seccionActual.permisos.length; k++) {
                                        	if (seccionActual.permisos[k]===operacion) {
                                        		return true;
                                        	}
                                        }
                                        return false;
                                    }
                                }
                            }
                        }
                    	
                        return false;
                    };
                    
                    $scope.onSelectSecc = function(selectedSecc) {
                        $window.location.href = '/gesweb/secciones/' + $filter('lowercase')(selectedSecc.codigoServicio) + "/" + $filter('lowercase')(selectedSecc.codigoSeccion);
                    };

                    $scope.permisos = {
                        "MAIL": false,
                        "ADMIN": false,
                        "DEL": false,
                        "DOC": false,
                        "REGALAZARAGOZA": false,
                        "DET": false,
                        "FORM_MAIL": false,
                        "COM": false,
                        "NEW": false,
                        "RES": false,
                        "VAL": false,
                        "SMS": false,
                        "IMP": false,
                        "PUB": false,
                        "CONTESTAR": false,
                        "ADJ": false,
                        "SENDINSPECTOR": false,
                        "SENDEXTERNO": false,
                        "MOD": false,
                        "MODRESTRICTED": false,
                        "MODMULTIPLE": false,
                        "ORD": false,
                        "ADMINOPERADOR": false,
                        "ANSWERREQUESTED": false,
                        "OPERADOR": false,
                        "DELMULTIPLE": false
                    };
                    for (var x = 0; x < permisosUsuario.length; x++) {
                        $scope.permisos[permisosUsuario[x]] = true;
                    };
                };

                $scope.verDatosUsuario = function(){
                    $window.location.href = '/gesweb/secciones/default/base/#/user/' + $scope.usuario.id;
                };
            }
        };
    }
])

.directive('modalBorrar', ['Dao', '$uibModal', 'Informer', '$route', '$location', 'Restangular', '$timeout', 
    function(Dao, $uibModal, Informer, $route, $location, Restangular, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            template: '<button ng-show="permisos.DEL && location !== \'/new\'" type="button" class="btn btn-default" ng-click="lanzarModalBorrar()" title="Borrar"><span class="fas fa-trash-alt" aria-hidden="true"></span></button>',
            controller: function($scope, $element, $attrs, $transclude) {

                var templatePath = $location.absUrl().split('#')[0];
                templatePath = '/cont/vistas/' + templatePath.split('/opencityext/')[1];

                $scope.lanzarModalBorrar = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: templatePath + '/templates/borrarModal.html',
                        controller: function($scope, $uibModalInstance, arrayInterno) {
                            //console.log(arrayInterno);
                            $scope.registros = arrayInterno;

                            $scope.borrar = function() {
                                $uibModalInstance.close($scope.registros);
                            };

                            $scope.cancelar = function() {
                                $uibModalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            arrayInterno: function() {
                                var array = [];
                                // Multiples registros del listado seleccionados
                                if(angular.isDefined($scope.checkList) && $scope.checkList.length > 0){
                                  array = $scope.checkList;  
                                } else {
                                    // Un único registro del listado o desde detalle
                                    array.push($scope.registro);
                                }
                                return array;
                            }
                        }
                    });

                    modalInstance.result.then(function(registros) {
                        // Si el atributo recurso="" sino obtiene el recurso de la última parte de la url
                        var recurso = $attrs.recurso || templatePath.split('/').reverse()[1];
                        console.log(recurso)
                        for(var i = 0; i < registros.length; i++){
                            Restangular.one(recurso, registros[i].id).customDELETE('remove', {}, {}).then(function(res) {
                            //Dao.eliminar(registros[i]).then(function(res) {
                                Informer.inform("Registro eliminado correctamente", "success");
                            }, function() {
                                Informer.inform("Se ha producido un error al eliminar el registro", "danger");
                            });
                        };
                        $scope.$parent.checkList = []; // Limpia array al finalizar
                        $timeout(function(){
                            $location.path() === '/' ? $route.reload() : $location.path('/');
                        }, 1000);
                    }, function() {
                        $scope.checkList = []; // Limpia array si pulsa cancelar
                    });
                };
            }
        };
    }
])

.directive('modalBusqueda', ['$location', '$route','$uibModal', 'Dao', function($location, $route, $uibModal, Dao){
   return {
       template: '<button type="button" class="btn btn-primary btn-sm" ng-show="permisos.DET" ng-click="modalBusqueda()">'
               + '<span class="fa fa-search" aria-hidden="true"></span>'
               + '<span class="hidden-xs"> B&uacute;squeda</span>'
               + '</button>',
       replace: true,
       link: function($scope, elem, attrs, controller) {

            var templatePath = $location.absUrl().split('#')[0];
            templatePath = '/cont/vistas/' + templatePath.split('/opencityext/')[1];

            $scope.modalBusqueda = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: templatePath + '/templates/modalBusqueda.html',
                    controller: function($scope, $uibModalInstance) {
                        $scope.query = {};
                        // console.log($route);
                        $scope.busqueda = function() {
                            $uibModalInstance.close($scope.query);
                        };

                        $scope.cancelar = function() {
                            $uibModalInstance.dismiss('cancel');
                        };
                    },
                    resolve: {}
                });

                modalInstance.result.then(function(selectedItem) {
                    var params = {};
                    params.q = [];
                    for (campo in selectedItem) {
                        item = new Object();
                        item.key = campo; // Campo de busqueda. Ej: 'title' 
                        item.value = selectedItem[campo]; // Valor del campo de busqueda actual
                        params.q.push(item);
                    }
                    var datosListados = [];
                    Dao.listar(attrs.path, params).then(function(data) {
                        datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                        $scope.$$childTail.registros = data;
                    });
                }, function() {
                    console.info('Modal dismissed at: ' + new Date());
                });
            };

       }
   };
}])

.directive('sameAs', [function() {
    return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                if (viewValue === scope[attrs.sameAs]) {
                    ctrl.$setValidity('sameAs', true);
                    return viewValue;
                } else {
                    ctrl.$setValidity('sameAs', false);
                    return undefined;
                }
            });
        }
    };
}])

/*.directive('flecha', [function() {
    return {
        restrict: 'E',
        replace: true,
        template: '<i class="icono-flecha icon-minus"></i>',
        link: function(scope, elm, attrs) {
            // Revisamos el orden por defecto establecido
            var orden = angular.lowercase(scope.order[0]);
            $('.icono-flecha + a').each(function() {
                if ($(this).text().toLowerCase() === orden) {
                    $(this).parent().find('i').removeClass('icon-minus').addClass('icon-arrow-up');
                };
            });
            // Si hace click en cualquier <th> para ordenar se ejecuta cambiarFlecha(index)
            scope.cambiarFlecha = function(reg) {
                // TODO: Realizar primera carga, con icon-minus, excepto la columna por la que ordenamos
                // Para visualizar correctamente como se filtra por defecto al cargar la app
                $('.icono-flecha').eq(reg).toggleClass(function() {
                    if ($(this).is(".icon-arrow-up")) {
                        return "icon-arrow-down";
                    } else {
                        return "icon-arrow-up";
                    }
                }).removeClass('icon-minus');
                $('.icono-flecha').not(':eq(' + reg + ')').attr('class', function(i, c) {
                    return c.replace(/\icon-a\S+/g, '');
                }).addClass('icon-minus');
            };
        }
    };
}])*/

.directive('ckEditor', [function() {
    return {
        require: '?ngModel',
        link: function(scope, elm, attr, ngModel) {
            var ck = CKEDITOR.replace(elm[0], {
                toolbar: [{
                        name: 'format',
                        items: ['Source', 'Format', 'Templates']
                    },
                    '/', {
                        name: 'document',
                        items: ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink', '-', 'Maximize', 'PasteText', 'PasteFromWord']
                    }
                ],
                height: '130px',
                width: '94%'
            });

            if (!ngModel)
                return;

            //loaded didn't seem to work, but instanceReady did
            //I added this because sometimes $render would call setData before the ckeditor was ready
            ck.on('instanceReady', function() {
                ck.setData(ngModel.$viewValue);
            });

            ck.on('pasteState', function() {
                scope.$apply(function() {
                    ngModel.$setViewValue(ck.getData());
                });
            });

            ngModel.$render = function(value) {
                ck.setData(ngModel.$viewValue);
            };

        }
    };
}])

.directive('ckEditorNew', function() {
    return {
        require: '?ngModel',
        link: function(scope, elm, attr, ngModel) {
            var ck = CKEDITOR.replace(elm[0], {
                toolbar: [{
                        name: 'format',
                        items: ['Source', 'Format', 'Templates']
                    },
                    '/', {
                        name: 'document',
                        items: ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink', '-', 'Maximize', 'PasteText', 'PasteFromWord']
                    }
                ],
                height: '130px',
                //width: '94%',
                templates_files: [ '/cont/assets/js/ckeditor/ckeditor-template.js' ],
                templates_replaceContent: false,
                //stylesSet: 'my_styles:../../../js/ckeditor/ckeditor-styles.js',
                contentsCss: '/cont/assets/css/main-sede.min.css',
                allowedContent: true,
                //basicEntities: false,
                bodyId: 'ckeditor-body-id',
                allowedContent: true,
                entities: false,
                protectedSource: [/<span[^>]*><\/span>/g]
            });
            if (!ngModel) return;
            ck.on('instanceReady', function() {
                ck.setData(ngModel.$viewValue);
            });

            function updateModel() {
                scope.$apply(function() {
                    ngModel.$setViewValue(ck.getData());
                });
            }
            ck.on('change', updateModel);
            ck.on('key', updateModel);
            ck.on('dataReady', updateModel);
            ck.on('paste', updateModel);
            ck.on('selectionChange', updateModel);

            ngModel.$render = function(value) {
                ck.setData(ngModel.$viewValue);
            };
        }
    };
})

.directive('fileUpload', [function() {
    return {
        scope: true, //create a new scope
        link: function(scope, el, attrs) {
            el.bind('change', function(event) {
                var files = event.target.files;
                //iterate files since 'multiple' may be specified on the element
                for (var i = 0; i < files.length; i++) {
                    //emit event upward
                    scope.$emit("fileSelected", {
                        tipo: attrs.tipoAdjunto || attrs.name || attrs.id,
                        file: files[i]
                    });
                };
            });
        }
    };
}])

.directive('validFile',function(){
  return {
    require:'ngModel',
    link:function(scope,el,attrs,ngModel){
      //change event is fired when file is selected
      el.bind('change',function(){
        scope.$apply(function(){
          ngModel.$setViewValue(el.val());
          ngModel.$render();
        });
      });
    }
  }
})

.directive('dependSel', [function() {
    return function(scope, element, attrs) {

        scope.$watch("category", function() {
            angular.element('.dependent-select').dependentSelects();
        });

    };
}])

.directive('focusMe', ['$timeout', function($timeout) {
    return {
        link: function(scope, element, attrs) {
            scope.$watch(attrs.focusMe, function(value) {
                if(value === true) { 
                    // console.log('value=',value);
                    $timeout(function() {
                        element[0].focus();
                        scope[attrs.focusMe] = false;
                    },10);
                }
            });
        }
    };
}])

.directive('botonDebug', ['$http', function($http){
    return {
        scope: {
        	bindModel:'=ngModel'
        },
		restrict: 'EA', 
		template: '<div class="btn-group" uib-dropdown>'
			+ '<button type="button" class="btn btn-default btn-default dropdown-toggle" uib-dropdown-toggle>'
			+ '<span class="fa fa-code" aria-hidden="true"></span>'
			+ '</button>'
			+ '<div class="dropdown-menu" role="menu" style="padding: 5px; min-width: 560px; max-width: 800px;">'
			+ '<pre>{{modelo | json}}</pre>'
			+ '</div>'
			+ '</div>',
        link: function($scope, iElm, iAttrs) {
            var a = angular.extend($scope.bindModel);
            var b = a.originalElement;
            $scope.$watch('bindModel', function(val) {
                $scope.modelo = _.pick(a,_.intersection(_.keys(a),_.keys(b)));
            }, true);            
        }
    };
}])

.directive('portal', ['$http', 'limitToFilter', 'leafletData', '$timeout', '$location', function($http, limitToFilter, leafletData, $timeout, $location){
    return {
        scope: {
            registro:'=',
            // markers:'=',
            // modelo:'='
        },
        controller: function($scope, $element, $attrs, $transclude) {
            var titulo, longitud, latitud, zoom;
            // if undefined: solo un registro, no array
            var crsETRS89 = L.CRS.proj4js('EPSG:25830', '+proj=utm +zone=30 +ellps=GRS80 +units=m +no_defs',
                new L.Transformation(1, -4590500.0, -1, 694100.0), {
                    resolutions: [53.125201382285255, 26.562600691142627, 14.062553307075499, 6.718775468936064,
                        3.7500142152201517, 1.7187565153092277, 0.9375035538050343, 0.5000018953626857,
                        0.26375099980381617, 0.131875499901908
                    ],
                    //Origen de servicio tileado
                    origin: [651500.0, 4590500.0]
                }
            );
            if(angular.isUndefined($scope.registro.length)){
                if (angular.isDefined($scope.registro.geometry)){
                    titulo = $scope.registro.title || "Zaragoza";
                    longitud = $scope.registro.geometry.coordinates[0] || -0.8856546878814697;
                    latitud = $scope.registro.geometry.coordinates[1] || 41.64718195751736;
                } else if((angular.isDefined($scope.registro.long) && angular.isDefined($scope.registro.lat)) || $attrs.latlong) { // Ticketing
                    titulo = $scope.registro.title || "Zaragoza";
                    longitud = $scope.registro.long;
                    latitud = $scope.registro.lat;
                }
                $scope.center = {
                    lat: latitud || 41.65597,
                    lng: longitud || -0.87746,
                    zoom: 22
                };
                $scope.markers = {
                    main_marker: {
                        lat: latitud,
                        lng: longitud,
                        focus: true,
                        title: titulo,
                        draggable: true
                    }
                };
            } else { // $scope.registro === array de registros
				console.log($scope.registro);
                $scope.markers = [];
                for(var i=0; i < $scope.registro.length; i++){
                    if (angular.isDefined($scope.registro[i].geometry)){
                        titulo = '<strong>' + $scope.registro[i].title + '</strong><br/>' + ($scope.registro[i].description || '');
                        longitud = $scope.registro[i].geometry.coordinates[0];
                        latitud = $scope.registro[i].geometry.coordinates[1];
                    } else if(angular.isDefined($scope.registro[i].long) && angular.isDefined($scope.registro[i].lat)) { // Ticketing
                    	titulo = '<strong>' + $scope.registro[i].title + '</strong><br/>' + ($scope.registro[i].description || '');
                        longitud = $scope.registro[i].long;
                        latitud = $scope.registro[i].lat;
                    }
                    if (longitud && latitud) {
	                    $scope.markers.push({
	                        lat: latitud,
	                        lng: longitud,
	                        
	                        message: titulo,
	                        draggable: false
	                    });
                    }
                }
            }

            $scope.defaults = {
        			center: {
        						lat:41.65597, 
        						lng: -0.87746,
        						zoom: 2
        					}, //Centro de zaragoza en coordenadas geograficas
        			//Configuracion del mapa inicial para visualizar la capa base IDEZar
        			crs: crsETRS89,
        			minZoom: 0, 
        			maxZoom: 9,
        			controls: {
        				layers: {
        					control: L.control.baselayerselector,
        					visible: true
        				}
        			}
        		};

                $scope.layers = {
                    baselayers: {
                        wms: {
                            name: 'IDEzar',
                            type: 'wms',
                            visible: true,
                            url: 'http://idezar.zaragoza.es/IDEZar_Base_Tiled/WMSTileCache',
                            zoom: 2,
                            // minZoom: 0,
                            // maxZoom: 22,
                            layerParams: {
                                crs: crsETRS89,
                                layers: 'base_25830',
                                format: 'image/png',
                                transparent: false,
        						version: '1.1.0',
                                attribution: '&copy; <a href="http://idezar.zaragoza.es">IDEZar</a> contributors, ' +
                                    ' &copy; <a href="https://www.zaragoza.es/">Ayuntamiento de Zaragoza</a>',
        						//Definición de configuracion correcta de niveles de zoom
                                zoom: 2,
                                minZoom: 0,
                                maxZoom: 9,
                                continuousWorld: true
                            }
                        },
                        osm: {
                            name: 'OpenStreetMap (XYZ)',
                            url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                            type: 'xyz',
        					//Definición de configuracion correcta de niveles de zoom y srs a emplear
        					layerParams: {
        						crs: L.CRS.EPSG3857,
        						zoom: 13,
        						minZoom:11,
        						maxZoom: 18,
        						//Propiedad necesaria para que se visualice el servcio
        						continuousWorld: true
        					}
                        },
                        googleRoadmap: {
                            name: 'Google Street',
                            layerType: 'ROADMAP',
                            type: 'google',
        					//Definición de configuracion correcta de niveles de zoom y srs a emplear
        					layerParams: {
        						crs: L.CRS.EPSG3857,
        						zoom: 13,
        						minZoom:11,
        						maxZoom: 18
        					}
                        },
                        googleSatellite: {
                            name: 'Google Satellite',
                            layerType: 'SATELLITE',
                            type: 'google',
        					//Definición de configuracion correcta de niveles de zoom y srs a emplear
        					layerParams: {
        						crs: L.CRS.EPSG3857,
        						zoom: 13,
        						minZoom:11,
        						maxZoom: 18
        					}
                        }
                    }
                };


        },
        restrict: 'EA', // E = Element, A = Attribute, C = Class, M = Comment
        template: function(element, attrs){
            var htmlText = '';
            if(attrs.buscador === 'S') htmlText += '<div class="form-group" style="margin-bottom:1em;"><label for="bus">Buscador: </label><input id="bus" placeholder="Introduce un nombre y/o número de calle (Formato: calle,num)" class="form-control" type="text" ng-model="modelo" uib-typeahead="registro as (registro.calle.title +\',\'+ registro.numero) for registro in obtenerListado($viewValue)" typeahead-on-select="onSelectCoords($item, $model, $label)"></div>';
            if(attrs.mapa === 'S') htmlText += '<button type="button" class="btn btn-default btn-street-view" ng-hide="hideStreetViewButton" ng-click="clickStreetView();">StreetView</button><div ng-show="showStreetView" class="panorama-street-view" id="pano"></div><leaflet id="mapa" center="center" markers="markers" height="380px" style="width:100%" layers="layers" geojson="geojson" defaults="defaults"></leaflet>'
            return htmlText;
        },
        link: function($scope, element, attrs, controller) {
            
            $scope.hideStreetViewButton = $location.path() === '/';

            // Hay que añadir el evento 'invalida' en la tab de localización:
            // <tab heading="Localizaci&oacute;n" select="$broadcast('invalida');">
            var bounds;
            $scope.$on("invalida", function(event, args) {
                leafletData.getMap('mapa').then(function(map) {
                    setTimeout(function(){ map.invalidateSize()}, 100);
                    bounds = map.getBounds();
                });
            });

            $scope.$watch("markers.main_marker.lat", function(newValue, oldValue) {
                if(newValue !== oldValue){
                    if($scope.registro.lat && $scope.registro.long){ // Ticketing
                        $scope.registro.long = $scope.markers.main_marker.lng;
                        $scope.registro.lat = $scope.markers.main_marker.lat;
                    } else { // Resto de salidas API
                        $scope.registro.geometry.coordinates[0] = $scope.markers.main_marker.lng;
                        $scope.registro.geometry.coordinates[1] = $scope.markers.main_marker.lat;
                    }
                }
            });

            $scope.showStreetView = false;
            $scope.clickStreetView = function(){
                var myPano = new google.maps.StreetViewPanorama(document.getElementById("pano"), {
                    position: bounds._northEast,
                    pov: {
                      pitch: -10,
                      heading: 0,
                      zoom: 1
                    }
                });
                $scope.showStreetView = !$scope.showStreetView;
            };

            if(attrs.buscador === 'S') {
                var uri = attrs.uri || "/opencityext/servicio/portalero/list";
                var url = uri;
                var filtro = attrs.filtro || 'calle.title'; // Si no ha filtro asignado buscamos por defecto con title

                $scope.obtenerListado = function(val) {
                    var valor = val.split(",");
                    var query = "";
                    if (valor.length > 1) {
                        query = filtro + '==' + valor[0] + '*;numero==' + valor[1];
                        if (attrs.numero) {
                            query = query + '*';
                        }
                    } else {
                        query = filtro + '==' + val + '*';
                    }
                    // console.log(query);
                    return $http.get(url, {
                        params: {
                            q: query,
                            srsname: "wgs84"
                        }
                    }).then(function(res) {
                        var registros = [];
                        angular.forEach(res.data.result, function(item) {
                            registros.push(item); // item[filtro]
                        });
                        return limitToFilter(registros, 10);
                    });
                };
            };
            if(attrs.buscador === 'S' && attrs.mapa === 'S'){
                $scope.onSelectCoords = function($item, $model, $label){
                    var nuevoPuntoX = $item.geometry.coordinates[0];
                    var nuevoPuntoY = $item.geometry.coordinates[1];
                    if(($scope.registro.lat && $scope.registro.long) || attrs.latlong){ // Ticketing
                        $scope.registro.address_string = $item.calle.title + ($item.etiqueta ? ', ' + $item.etiqueta : '');
                        $scope.registro.address_id = $item.id;
                        $scope.registro.long = nuevoPuntoX;
                        $scope.registro.lat = nuevoPuntoY;
                    } else { // Resto de salidas API
                        $scope.registro.geometry.coordinates[0] = nuevoPuntoX;
                        $scope.registro.geometry.coordinates[1] = nuevoPuntoY;
                    }
                    $scope.center.lng = nuevoPuntoX;
                    $scope.center.lat = nuevoPuntoY;
                    $scope.markers.main_marker.lng = nuevoPuntoX;
                    $scope.markers.main_marker.lat = nuevoPuntoY;
                };
            }
            
        }
    };
}])

.directive('modalLocalizacion', ['Dao', '$uibModal', 'Informer', '$route', '$location',
    function(Dao, $uibModal, Informer, $route, $location) {
        return {
            restrict: 'E',
            template: '<button type="button" class="btn btn-primary btn-sm" ng-click="lanzarModalLocalizacion()">'
                + '<span class="fas fa-map-marker-alt" aria-hidden="true"></span>'
                + '<span class="hidden-xs"> Geolocalización</span>'
                + '</button>',
            controller: function($scope, $element, $attrs, $transclude) {

                var templatePath = $location.absUrl().split('#')[0];
                templatePath = '/cont/vistas/' + templatePath.split('/opencityext/')[1];

                $scope.lanzarModalLocalizacion = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: templatePath + '/templates/localizacionModal.html',
                        controller: function($scope, $uibModalInstance, reg) {
                            $scope.title = $attrs.title || 'registros';

                            $scope.registro = reg;

                            $scope.cerrar = function() {
                                $uibModalInstance.close($scope.registro);
                            };

                            // $scope.cancelar = function() {
                            //     $uibModalInstance.dismiss('cancel');
                            // };
                        },
                        size: 'lg',
                        resolve: {
                            reg: function() {
                                return $location.path() === '/' ? $scope.$$childTail.registros : $scope.$$childTail.registro; 
                            }
                        }
                    });

                    modalInstance.result.then(function(reg) {
                        // result success
                    }, function() {
                        // result error
                    });

                };
            }
            
        };
    }
])

.directive('copyToClipboard', function () {
    return {
        restrict: 'A',
        link: function (scope, elem, attrs) {
            elem.click(function () {
                if (attrs.copyToClipboard) {
                    var $temp_input = $("<input>");
                    $("body").append($temp_input);
                    $temp_input.val(attrs.copyToClipboard).select();
                    document.execCommand("copy");
                    $temp_input.remove();
                };
            });
        }
    };
})

.directive('imgCut', ['$uibModal', '$timeout', '$location', 'Informer', function($uibModal, $timeout, $location, Informer){
    return {
        scope: {
            ngModel: '=',
            imgCutResultImage: '=', // src de la imagen en base64
            imgCutSize: '@',
            imgCutResultFiles: '=', // tiene que ser un array
        },
        link: function($scope, element, attrs, controller) {
            
            var originalImgCutResultFiles;
            var imgSize = $scope.imgCutSize;
            var widthVal = parseInt(imgSize.split('x')[0]);
            var heightVal = parseInt(imgSize.split('x')[1]);
            var media_file = new Object();
            media_file.media_type = attrs.name; // Atributo name="anexoImagen" del <input type="file"/>

            element.bind('change', function(evt) {
                if(evt.target.files.length > 0) {
                    var dataFile = evt.target.files[0];
                    // Guardamos una copia de media_file por si cancela el recortador
                    originalImgCutResultFiles = angular.copy(media_file);

                    var reader = new FileReader();
                    reader.onload = (function (file) {
                        return function(evt){
                            media_file.media_name = file.name.replace(/[^a-z0-9\.]/gi, '_').toLowerCase();
                            // Cambiamos la extensión de la imagen adjunta, ya que transformamos el formato al recortar
                            media_file.media_name = media_file.media_name.substr(0, media_file.media_name.lastIndexOf(".")) + ".png";
                            media_file.media_description = file.name.substring(0, (file.name.indexOf(".") > 30 ? 30 : file.name.indexOf(".")));
                            var image = new Image();
                            image.src = evt.target.result;
                            image.onload = function() {
                                // Comparamos las medidas de la imagen con las minímas exigidas en la directiva img-cut-size
                                if(this.width < widthVal || this.height < heightVal){
                                    element.val(null); // Eliminamos la imagen seleccionada en el input
                                    Informer.inform('Tamaño de la imagen no válido. El tamaño mínimo es ' + widthVal + 'x' + heightVal + ' px.', 'warning');
                                } else {
                                    $scope.lanzarModalLugar(media_file, evt.target.result); 
                                };
                            };
                        };
                    })(dataFile);
                    reader.readAsDataURL(dataFile);
                };
            });

            $scope.lanzarModalLugar = function(mediaFile, imgOriginalSrc) {

                var templatePath = $location.absUrl().split('#')[0];
                templatePath = '/cont/vistas/' + templatePath.split('/opencityext/')[1];

                var modalInstance = $uibModal.open({
                    templateUrl: templatePath + '/templates/modalRecortadorImagen.html',
                    controller: function($scope, $uibModalInstance, mediaFile, imgOriginalSrc) {

                        $scope.imageCrop = {
                            originalImage: '',
                            croppedImage: ''
                        };

                        $scope.imgSize = {w: widthVal,h: heightVal};

                        $scope.imageCrop.originalImage = imgOriginalSrc;

                        $scope.aceptarImagen = function() {
                            $uibModalInstance.close($scope.imageCrop.croppedImage);
                        };

                        $scope.cancelarModal = function() {
                            $uibModalInstance.dismiss('cancel');
                        };

                    },
                    //size: 'lg',
                    resolve: {
                        mediaFile: function() {
                            return mediaFile;
                        },
                        imgOriginalSrc: function() {
                            return imgOriginalSrc;
                        }
                    }
                });

                modalInstance.result.then(function(croppedImage) {
                    // Asociamos el src (encoded) de la imagen recortada al media_file
                    media_file.media_body = encodeURIComponent(croppedImage);
                    if(angular.isUndefined($scope.imgCutResultFiles)) {
                        $scope.imgCutResultFiles = new Array();
                    } else {
                        // Solo permitimos una imagen por input según su tipo (Atributo <input name="..."/>. Si selecciona otra imagen, descarta la anterior (_.reject)
                        if(_.find($scope.imgCutResultFiles, function(file) { return file.media_type === attrs.name; })){
                           $scope.imgCutResultFiles = _.reject($scope.imgCutResultFiles, function(f){ return f.media_type === attrs.name; });
                        }
                        $scope.imgCutResultFiles = new Array();
                    }
                    $scope.imgCutResultFiles.push(media_file);
                    
                    // Asociamos el src de la imagen recortada a la propiedad del modelo img-cut-result
                    $scope.imgCutResultImage = croppedImage;
                    // Asociamos el nombre del fichero a la propiedad del modelo ng-model
                    $scope.ngModel = media_file.media_name;
                }, function() { // cancel
                    // Devolvemos al estado anterior el array de media_files
                    $scope.imgCutResultFiles = originalImgCutResultFiles;
                    // Eliminamos la imagen seleccionada en el input
                    element.val(null);
                });
            };

        }
    };
}])

function getFacade() {
    return facade;
}
