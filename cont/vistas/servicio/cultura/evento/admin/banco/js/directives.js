angular.module('app.directives', [])

.directive('modalBancoBorrar', ['Dao', '$uibModal', 'Informer', '$route', '$location', 'Restangular', '$timeout', 
    function(Dao, $uibModal, Informer, $route, $location, Restangular, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            template: '<button ng-show="permisos.DEL && !location.endsWith(\'/new\')" type="button" class="btn btn-default" ng-click="lanzarModalBorrar()" title="Borrar"><span class="fas fa-trash-alt" aria-hidden="true"></span></button>',
            controller: function($scope, $element, $attrs, $transclude) {

                var templatePath = $location.absUrl().split('#')[0];
                templatePath = '/cont/vistas/' + templatePath.split('/sede/')[1];

                $scope.lanzarModalBorrar = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: templatePath + 'templates/borrarModal.html',
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
                        	var entityPath = '/';
                        	var splittedPath = $location.path().split('/');
                        	if (splittedPath.length > 1) {
                        		entityPath += splittedPath[1];
                        	}
                        	$location.path() === entityPath ? $route.reload() : $location.path(entityPath);
                        }, 1000);
                    }, function() {
                        $scope.checkList = []; // Limpia array si pulsa cancelar
                    });
                };
            }
        };
    }
])

.directive('botonBancoVisibilidad', ['Dao', 'Restangular', '$route', '$location', 'Informer', 
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
