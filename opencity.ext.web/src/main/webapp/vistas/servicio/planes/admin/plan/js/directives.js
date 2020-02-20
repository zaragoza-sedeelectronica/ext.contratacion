angular.module('app.directives', [])

.directive('botonPlanesVisibilidad', ['Dao', 'Restangular', '$route', '$location', 'Informer',
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
                                Restangular.restangularizeElement('', scope.checkList[i], scope.checkList[i].route.replace('/list', ''));
                                Dao.actualizar(scope.checkList[i]).then(function() {
                                    scope.visibilidad.valor = !val;
                                    Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                                }, function(err){
                                    // En caso de error volvemos al estado anterior
                                    scope.checkList[i].visible = scope.checkList[i].visible === 'S' ? 'N' : 'S'
                                    Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                                });
                            };
                        } else {
                            // Establecemos route de detalle de registro
                            Restangular.restangularizeElement('', scope.registro, scope.registro.route.replace('/list', ''));
                            Dao.actualizar(scope.registro).then(function() {
                                scope.visibilidad.valor = !val;
                                Informer.inform('Registro ' + (scope.registro.visible === 'S' ? '' : 'des') + 'publicado correctamente.', "success");
                            }, function(err){
                                // En caso de error volvemos al estado anterior
                                scope.registro.visible = scope.registro.visible === 'S' ? 'N' : 'S'
                                Informer.inform('Error al ' + (scope.registro.visible === 'S' ? 'des' : '') + 'publicar: ' + err.data.error, "danger");
                            });
                        };     
                    };

                };

            }
        };
    }
]);
