angular.module('PARCELA.directives', [])

.directive('autocompletadoiae', ['$http', 'limitToFilter', '$timeout',
    function($http, limitToFilter, $timeout) {
        return {
            restrict: 'E',
            scope: {
                filtro: '@',
                modelo: '=',
                check: '&?'
            },
            replace: true,
            template: '<input typeahead-on-select="onSelect($item, $model, $label)" class="form-control" type="text" ng-model="modelo" typeahead="registro as \'(\' + registro.identifier + \') \' + registro.{{filtro}} for registro in obtenerListado($viewValue)">',
            link: function(scope, element, attrs) {

                var uri = attrs.uri;
                var url = 'http://' + window.sessionStorage
                    .getItem('SERVIDOR') + uri;
                var filtro = attrs.filtro || 'title'; // Si no ha filtro asignado buscamos por defecto con title

                scope.obtenerListado = function(val) {
                    if (!isNaN(val)) {
                        filtro = 'identifier';
                    } else {
                        filtro = 'title';
                    }
                    var query = filtro + '==' + val + '*';
                    return $http
                        .get(url, {
                            params: {
                                q: query
                            }
                        })
                        .then(
                            function(res) {
                                var registros = [];
                                angular
                                    .forEach(
                                        res.data.result,
                                        function(
                                            item) {
                                            registros
                                                .push(item); // item[filtro]
                                        });
                                return limitToFilter(
                                    registros,
                                    10);
                            });
                };

                scope.onSelect = function($item, $model,
                    $label) {
                    //                 	console.log(scope);
                    // //                	_.defer(function(){scope.$apply();});
                    //                 	// Da error $apply already in progress
                    scope.$apply();
                    // //                	scope.$digest();
                    scope.check();
                    // //                	$timeout(scope.check());
                    // //                	scope.$evalAsync(function(scope){
                    // //                		scope.check();
                    // //                	});
                };

                // scope.format = function($model){
                //     if(attrs.formatModel)
                //         $model[attrs.formatModel];
                // };

                scope
                    .$watch(
                        'modelo',
                        function(newValue, oldValue) {
                            // Dado que estamos sustituyendo el etiquetado de la directiva mediante template
                            // No consigo utilizar el attr. typeahead-on-select='onSelect($item, $model, $label)
                            // De este modo disparamos la llamada al modal cuando la busqueda devuelve un objecto
                            // Es decir, cuando el usuario ha seleccionado un resultado (objeto)
                            if (newValue !== null && typeof newValue === 'object') {
                                //scope.$parent.$parent.lanzarModalLugar(newValue, true);
                            }
                        });
            }
        };
    }
]);
