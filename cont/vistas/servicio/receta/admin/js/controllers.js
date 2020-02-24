angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', '$uibModal',
    function($scope, $window, $location, $filter, Informer, $route, $uibModal) {

    	$scope.tipoPlato = [
            { id: 1, text: 'Entremés' },
            { id: 2, text: 'Ensaladas' },
            { id: 3, text: 'Sopas, cremas y potajes' },
            { id: 4, text: 'Verduras y legumbres' },
            { id: 5, text: 'Arroces y pastas' },
            { id: 6, text: 'Patés, mousses y tartas' },
            { id: 7, text: 'Pescados y marisco' },
            { id: 8, text: 'Carnes, caza y aves' },
            { id: 9, text: 'Repostería y postres' }
        ];
    	$scope.visibilidad = [
            { id: 'S', text: 'Si' },
            { id: 'N', text: 'No' }
        ];
    	$scope.tipoCocina = [
       		{ id: 1, text: 'Vegetariana' },
    		{ id: 2, text: 'Vegana' },
    		{ id: 3, text: 'Naturista' },
    		{ id: 4, text: 'Macrobiótica' },
    		{ id: 5, text: 'Frugívora' },
    		{ id: 6, text: 'Creativa' },
    		{ id: 7, text: 'Casera' },
    		{ id: 8, text: 'Internacional' },
    		{ id: 9, text: 'Navidad' },
    		{ id: 10, text: 'Semana Santa' },
    		{ id: 11, text: 'Época colonial' },
    		{ id: 12, text: 'Medieval' },
    		{ id: 13, text: 'Budista' },
    		{ id: 14, text: 'Cristiana' },
    		{ id: 15, text: 'Rastafari' },
    		{ id: 16, text: 'Musulmana' },
    		{ id: 17, text: 'Judía' },
    		{ id: 18, text: 'Comida Rápida' },
    		{ id: 19, text: 'Ecogastronomía' },
    		{ id: 20, text: 'Diabéticos' },
    		{ id: 21, text: 'Celíacos' }
        ];

        $scope.listar = function() {
            $location.path('#/');
        };

        $scope.crear = function() {
            // Si estamos copiando un punto y se quiere crear uno nuevo, tenemos que recargar la vista
            if ($location.path('/new')) {
                copiaPunto = null;
                $route.reload();
            }

            $location.path('/new');

        };

        $scope.lanzarModalBusqueda = function() {
            var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/receta/admin/templates/busquedaModal.html',
                controller: function($scope, $uibModalInstance) {

                    $scope.busqueda = function() {
                        $uibModalInstance.close();
                    };

                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                }
            });

            modalInstance.result.then(function(selectedItem) {
                // $scope.selected = selectedItem;
            }, function() {
                //$log.info('Modal dismissed at: ' + new Date());
            });

        };

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao',
    function($scope, Dao) {

        // $scope.order = ['id', 'id'];
        // $scope.reverse = false;

		var params = {};
	    params.start = 0;
        $scope.registros = [];

        $scope.busy = false;
        $scope.loadMore = function() {
            if ($scope.busy) return;
            $scope.busy = true;

            Dao.listar('receta', params).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.busy = false;
            });
            params.start += 50;
        };


    }
])
.controller('CreateCtrl', ['$scope', '$location', 'Dao', 'Informer', '$uibModal',
    function($scope, $location, Dao, Informer, $uibModal) {
		try {
			$scope.registro = angular.isUndefined(copiaRegistro);
		} catch (e) {
			$scope.registro = {};
		}
		if (angular.isDefined($scope.registro.restaurante) && $scope.registro.restaurante === "") {
       		delete $scope.registro.restaurante;
       	}
        $scope.guardar = function() {
//            	TODO No muestra los errores!!!
        	if (angular.isUndefined($scope.registro.restaurante.id)) {
        		delete $scope.registro.restaurante;
        	}
            Dao.crear('receta?debug=s', $scope.registro).then(function(res) {
            	Informer.inform("El registro se ha creado correctamente.", "success");
                $location.path('/edit/' + res.id);
            }, function() {
            	Informer.inform("Se ha producido un error al crear el registro.", "danger");
//                    $location.path('/');
            });
            delete copiaRegistro;
        };

    }
])
.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', 'RestangularIngrediente', '$location', 'Informer', '$uibModal',
    function($scope, Dao, registro, Restangular, RestangularIngrediente, $location, Informer, $uibModal) {
        $scope.registro = Restangular.copy(registro);
        $scope.guardar = function() {
           delete $scope.registro.file;
           
           try {
        	   if (angular.isUndefined($scope.registro.images[0].body)) {
        		   delete $scope.registro.images;   
        	   }
           } catch (e) {
        	   delete $scope.registro.images;
           }
           if (angular.isDefined($scope.registro.restaurante) && $scope.registro.restaurante === "") {
          		delete $scope.registro.restaurante;
          	}
           Dao.actualizar($scope.registro).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
//                    $location.path('/');
                try {
             	 	delete $scope.registro.images[0].body;   
                } catch (e) {
             	   ;
                }
                
            }, function() {
                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
                //$location.path('/');
            });
        	
        };
        
        $scope.$on("fileSelected", function(event, args) {
            $scope.$apply(function() {
                var reader = new FileReader();
				reader.onload = function(event) {
					$scope.registro.images=[];
					$scope.registro.images[0]={};
					$scope.registro.images[0].title = args.file.name;
		    	    $scope.registro.images[0].body = encodeURIComponent(event.target.result);
				};
				reader.readAsDataURL(args.file);
            });
        });

        $scope.borrarRow = function(index) {
        	$scope.registro.ingredients.splice(index, 1);
        };
        
        $scope.gestionRow = function(index) {
        	$scope.dato = {};
        	if (!angular.isUndefined(index)) {
        		$scope.dato=angular.extend($scope.registro.ingredients[index]);
        	}
        	var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/receta/admin/templates/ingredienteModal.html',
                
                controller: function($scope, $uibModalInstance, registro, dato) {
                	$scope.registro = registro;
                	$scope.dato = dato;
                    $scope.save = function() {
                        $uibModalInstance.close($scope.dato);
                    };
                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                }, resolve: {
                	registro: function() {
                		return $scope.registro;
                   },
                   dato: function() {
               			return $scope.dato;
                  }
                }
            });

            modalInstance.result.then(function(selectedItem) {
            	
            	if (angular.isUndefined(selectedItem.ingredient.id)) {
            		RestangularIngrediente.all('ingrediente').post("{\"title\":\""+ selectedItem.ingredient +"\"}",{},{}).then(function(json){
            			selectedItem.ingredient={};
            			selectedItem.ingredient.id=json.id;
            			selectedItem.ingredient.title=json.title;
            			if (angular.isUndefined(index)) {
                    		//add al listado
                    		if (angular.isUndefined($scope.registro.ingredients)) {
                    			$scope.registro.ingredients = [];
                    		}
                    		$scope.registro.ingredients.push(selectedItem);
                    	} else {
                    		//update listado
                    		$scope.registro.ingredients[index] = selectedItem;
                    	}
            		});
            		
            	} else {
            	
                	if (angular.isUndefined(index)) {
                		//add al listado
                		if (angular.isUndefined($scope.registro.ingredients)) {
                			$scope.registro.ingredients = [];
                		}
                		$scope.registro.ingredients.push(selectedItem);
                	} else {
                		//update listado
                		$scope.registro.ingredients[index] = selectedItem;
                	}
            	}
            }, function() {
            });
        };
        
    }
]);
