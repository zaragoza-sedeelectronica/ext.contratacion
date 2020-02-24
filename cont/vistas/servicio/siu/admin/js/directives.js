angular.module('app.directives', [])

.directive('modalRegistroBorrar', ['Dao', '$uibModal', 'Informer', '$route', '$location', '$http',
    function(Dao, $uibModal, Informer, $route, $location, $http) {
        return {
            controller: function($scope, $element, $attrs, $transclude) {

                var templatePath = $location.absUrl().split('#')[0];
                templatePath = '/cont/vistas/' + templatePath.split('/sede/')[1];

                $scope.lanzarModalBorrar = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: templatePath + 'templates/borrarModal.html',
                        controller: function($scope, $uibModalInstance, arrayInterno) {
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
                        for(var i = 0; i < registros.length; i++){
                            $http.delete(SERVICE + QUERY + '/' + registros[i].id + '/remove', {}).then(function(res) {
                                Informer.inform("Registro eliminado correctamente", "success");
                            }, function() {
                                Informer.inform("Se ha producido un error al eliminar el registro", "danger");
                            });
                        };
                        $scope.$parent.checkList = []; // Limpia array al finalizar
                        $location.path() === '/' ? $route.reload() : $location.path('/');
                    }, function() {
                        $scope.checkList = []; // Limpia array si pulsa cancelar
                    });

                };
            },
            restrict: 'E',
            template: '<button type="button" ng-hide="location === \'/new\'" class="btn btn-default" ng-click="lanzarModalBorrar()"><span class="fa fa-trash" aria-hidden="true"></span></button>',
        };
    }
])