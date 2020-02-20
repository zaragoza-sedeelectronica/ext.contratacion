angular.module('app.directives', [])
.directive('autocompletadocentro', ['$http', 'limitToFilter', '$timeout',
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
            template: '<input typeahead-on-select="onSelect($item, $model, $label)" class="form-control" type="text" ng-model="modelo" uib-typeahead="registro as registro.titleSea for registro in obtenerListado($viewValue)">',
            link: function(scope, element, attrs) {
                // Obligatorio. Ruta del recurso a buscar
                var uri = attrs.uri;
                var url = uri;
                // var url = 'http://localhost:7777' + uri;
                //var url = '//' + window.sessionStorage.getItem('SERVIDOR') + uri;
                
                // Opcional. Campo por el que se realizará la búsqueda en el typeahead
                var filtro = 'titleSea'; // 'title' valor por defecto
                var filtroId = 'id';

                scope.obtenerListado = function(val) {
                    // Formamos la query fiql con el valor del campo a buscar
                    var query;
                    query = filtro + '==' + val + '*';
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