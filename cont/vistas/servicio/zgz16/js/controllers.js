angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route',
    function($scope, $window, $location, $filter, Informer, $route) {

        $scope.listar = function() {
            $location.path('/list');
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            };
            $location.path('/new');
        };

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])
.controller('UserCtrl', ['$scope', '$location', 'Dao', 'Informer', '$filter', '$http','$uibModal','Restangular',
    function($scope, $location, Dao, Informer, $filter, $http, $uibModal,Restangular) {
		$scope.centros = [
			{ id: '1-01', title: 'IES Pedro de Luna' },
			{ id: '1-02', title: 'IES Francisco Grande Covián' },
			{ id: '1-03', title: 'IES Pablo Serrano' },
			{ id: '1-04', title: 'IES Andalán'},
			{ id: '1-05', title: 'IES Luis Buñuel' },
			{ id: '1-06', title: 'IES Ramón y Cajal' },
			{ id: '1-07', title: 'IES Goya'},
			{ id: '1-08', title: 'IES Miguel Servet' },
			{ id: '1-09', title: 'IES José Manuel Blecua' },
			{ id: '1-10', title: 'IES Pablo Gargallo' },
			{ id: '1-11', title: 'IES Medina Albaida' },
			{ id: '1-12', title: 'IES Jerónimo Zurita' },
			{ id: '1-13', title: 'IES Miguel Catalán' },
			{ id: '1-14', title: 'IES El Portillo' },
			{ id: '1-15', title: 'IES María Moliner' },
			{ id: '1-16', title: 'IES Miralbueno'},
			{ id: '1-17', title: 'IES Ramón Pignatelli' },
			{ id: '1-18', title: 'IES Virgen del Pilar' },
			{ id: '1-19', title: 'IES Pilar Lorengar' },
			{ id: '1-20', title: 'IES Río Gállego' },
			{ id: '1-21', title: 'IES Avempace'},
			{ id: '1-22', title: 'IES La Azucarera' },
			{ id: '1-23', title: 'IES Ítaca'},
			{ id: '1-24', title: 'IES Félix de Azara' },
			{ id: '1-25', title: 'IES Santiago Hernández' },
			{ id: '1-26', title: 'IES Parque Goya' },
			{ id: '1-27', title: 'IES Elaios'},
			{ id: '1-28', title: 'IES Miguel de Molinos' },
			{ id: '1-29', title: 'IES Tiempos Modernos' },
			{ id: '1-30', title: 'IES Valdespartera'},
			{ id: '1-31', title: 'IES Ángel Sanz Briz' },
			{ id: '2-01', title: 'Colegio Sagrado Corazón-Corazonistas' },
			{ id: '2-02', title: 'Colegio Cantín y Gamboa' },
			{ id: '2-03', title: 'Colegio San Vicente de Paúl' },
			{ id: '2-04', title: 'Colegio Santo Domingo de Silos' },
			{ id: '2-05', title: 'Colegio Escuelas Pías' },
			{ id: '2-06', title: 'Colegio La Anunciata' },
			{ id: '2-07', title: 'Colegio Nuestra Señora del Carmen y San José' },
			{ id: '2-08', title: 'Colegio Compañía de María' },
			{ id: '2-09', title: 'Colegio Santa Rosa' },
			{ id: '2-10', title: 'Colegio Calasanz'},
			{ id: '2-11', title: 'Colegio Calasancio' },
			{ id: '2-12', title: 'Colegio Montessori' },
			{ id: '2-13', title: 'Colegio La Milagrosa' },
			{ id: '2-14', title: 'Colegio Nuestra Señora de la Merced' },
			{ id: '2-15', title: 'Colegio Pompiliano'},
			{ id: '2-16', title: 'Colegio San Antonio de Padua' },
			{ id: '2-17', title: 'Colegio La Salle Franciscanas Gran Vía' },
			{ id: '2-18', title: 'Colegio Cardenal Xavierre' },
			{ id: '2-19', title: 'Colegio El Buen Pastor' },
			{ id: '2-20', title: 'Colegio Escuelas Pías de Santa Engracia' },
			{ id: '2-21', title: 'Colegio María Auxiliadora' },
			{ id: '2-22', title: 'Colegio Santa Ana' },
			{ id: '2-23', title: 'Colegio Villa Cruz' },
			{ id: '2-24', title: 'Colegio Nuestra Señora del Carmen' },
			{ id: '2-25', title: 'Colegio Inmaculada Concepción' },
			{ id: '2-26', title: 'Colegio La Salle Montemolín' },
			{ id: '2-27', title: 'Colegio San Agustín' },
			{ id: '2-28', title: 'Colegio Salesiano Nuestra Señora del Pilar' },
			{ id: '2-29', title: 'Colegio Jesús María El Salvador' },
			{ id: '2-30', title: 'Colegio Sagrado Corazón - Moncayo' },
			{ id: '2-31', title: 'Colegio La Purísima' },
			{ id: '2-32', title: 'Colegio Romareda'},
			{ id: '2-33', title: 'Colegio Santa María Reina' },
			{ id: '2-34', title: 'Colegio Montearagón'},
			{ id: '2-35', title: 'Colegio Madre Mª Rosa Molas' },
			{ id: '2-36', title: 'Colegio Romareda'},
			{ id: '2-37', title: 'Colegio Hijas de San José' },
			{ id: '2-38', title: 'Colegio Antonio Machado' },
			{ id: '2-39', title: 'Colegio Condes de Aragón' },
			{ id: '2-40', title: 'Colegio San Alberto Magno' },
			{ id: '2-41', title: 'Colegio Juan de Lanuza' },
			{ id: '2-42', title: 'Colegio María Inmaculada' },
			{ id: '2-43', title: 'Colegio Teresiano del Pilar' },
			{ id: '2-44', title: 'Colegio Sagrada Familia' },
			{ id: '2-45', title: 'Colegio Liceo Europa' },
			{ id: '2-46', title: 'Colegio Santa Magdalena Sofía' },
			{ id: '2-47', title: 'Colegio Sansueña'},
			{ id: '2-48', title: 'Colegio Marianistas Santa María del Pilar' },
			{ id: '2-49', title: 'Colegio Bajo Aragón' },
			{ id: '2-50', title: 'Colegio Agustín Gericó - Montemolín' },
			{ id: '2-51', title: 'Colegio Don Bosco' },
			{ id: '2-52', title: 'Colegio La Purísima y San Antonio' },
			{ id: '2-53', title: 'Centro de Educación Secundaria San Valero' },
			{ id: '2-54', title: 'Colegio Cristo Rey' },
			{ id: '2-55', title: 'Colegio La Concepción' },
			{ id: '2-56', title: 'Colegio Padre Enrique de Ossó' },
			{ id: '2-57', title: 'Collège Moliere'},
			{ id: '2-58', title: 'Colegio El Pilar Maristas' },
			{ id: '2-59', title: 'Colegio Sagrado Corazón de Jesús' },
			{ id: '2-60', title: 'Colegio Lycée Français Molière' },
			{ id: '2-61', title: 'Colegio Inglés La Alfranca' },
			{ id: '2-62', title: 'Colegio Británico de Aragón' },
			{ id: '3-01', title: 'Centro Sociolaboral Las Fuentes (Zaragoza Dinámica)' },
			{ id: '3-02', title: 'Centro Sociolaboral Casco Viejo (Zaragoza Dinámica)' },
			{ id: '3-03', title: 'Centro Sociolaboral Almozara (Zaragoza Dinámica)' },
			{ id: '3-04', title: 'Centro Sociolaboral Federico Ozanam' },
			{ id: '3-05', title: 'Centro Sociolaboral Torrero (Zaragoza Dinámica)' },
			{ id: '3-06', title: 'Centro Sociolaboral Oliver (Zaragoza Dinámica)' },
			{ id: '3-07', title: 'Centro Sociolaboral Valdefierro (Zaragoza Dinámica)' },
			{ id: '3-08', title: 'Centro Sociolaboral San José (Zaragoza Dinámica)' },
			{ id: '3-09', title: 'Centro Sociolaboral La Jota (Zaragoza Dinámica)' },
			{ id: '3-10', title: 'Aula Taller TOPI' },
			{ id: '3-11', title: 'Centro Sociolaboral Picarral (Zaragoza Dinámica)' },
			{ id: '3-12', title: 'Centro Sociolaboral Delicias (Zaragoza Dinámica)' },
			{ id: '3-13', title: 'Centro Sociolaboral Actur (Zaragoza Dinámica)' },
			{ id: '3-14', title: 'Centro Sociolaboral Casetas (Zaragoza Dinámica)' }
		];
		
		$scope.editPerfil = function(reg) {
            var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/zgz16/partials/modalEditPerfil.html',
                controller: function($scope, $uibModalInstance, registro) {
                    $scope.registro = Restangular.copy(registro);
                    if ($scope.registro.user.perfil.formato) {
                    	$scope.registro.user.perfil.formato = $scope.registro.user.perfil.formato.split(',');
                    }
                    if ($scope.registro.user.perfil.ocupacion) {
                    	$scope.registro.user.perfil.ocupacion = $scope.registro.user.perfil.ocupacion.split(',');
                    }
                    $scope.valores = [
                    	{ id: 1, title: 'Accesible y adaptada' },
                    	{ id: 2, title: 'Comprometida' },
                    	{ id: 3, title: 'Creativa' },
                    	{ id: 4, title: 'Divertida' },
                    	{ id: 5, title: 'Formativa' },
                    	{ id: 6, title: 'Igualdad' },
                    	{ id: 7, title: 'Innovadora' },
                    	{ id: 8, title: 'Participativa' },
                    	{ id: 9, title: 'Pensamiento crítico' },
                    	{ id: 10, title: 'Respeto' },
                    	{ id: 11, title: 'Sociabilidad' }
                    	];
                    
                    $scope.temas = [
                    	{ id: 87, title: 'Actividades vacacionales' },
                    	{ id: 11, title: 'Aire Libre y Excursiones' },
                    	{ id: 92, title: 'Artes plásticas' },
                    	{ id: 84, title: 'Ciencia y Tecnología' },
                    	{ id: 25, title: 'Deporte' },
                    	{ id: 91, title: 'Desarrollo personal' },
                    	{ id: 24, title: 'Formación' },
                    	{ id: 86, title: 'Gastronomía' },
                    	{ id: 89, title: 'Idiomas' },
                    	{ id: 90, title: 'Imagen y sonido' },
                    	{ id: 27, title: 'Medio Ambiente y Naturaleza' },
                    	{ id: 16, title: 'Música' },
                    	{ id: 93, title: 'Ocio y Juegos' },
                    	{ id: 26, title: 'Otros' },
                    	{ id: 17, title: 'Teatro y Artes Escénicas' },
                    	{ id: 88, title: 'Turismo' }
                    	];
                    
                    $scope.valoresAct = {};
                    $scope.valoresAct.selected = $scope.registro.user.perfil.valores ? $scope.registro.user.perfil.valores : [];
                    
                    
                    $scope.onSelectValores = function($item, $model){
                        if(angular.isUndefined($scope.registro.user.perfil.valores)) {
                        	$scope.registro.user.perfil.valores = [];
                        }
                        $scope.registro.user.perfil.valores.push({id: parseInt($item.id), title: $item.title});
                    };
                    $scope.onRemoveValores = function($item, $model){
                        $scope.registro.user.perfil.valores = _.reject($scope.registro.user.perfil.valores, function(v){ return v.id == $item.id; });
                    };
                    
                    $scope.temasAct = {};
                    $scope.temasAct.selected = $scope.registro.user.perfil.temas ? $scope.registro.user.perfil.temas : [];
                    
                    
                    $scope.onSelectTemas = function($item, $model){
                        if(angular.isUndefined($scope.registro.user.perfil.temas)) {
                        	$scope.registro.user.perfil.temas = [];
                        }
                        $scope.registro.user.perfil.temas.push({id: parseInt($item.id), title: $item.title});
                    };
                    $scope.onRemoveTemas = function($item, $model){
                        $scope.registro.user.perfil.temas = _.reject($scope.registro.user.perfil.temas, function(v){ return v.id == $item.id; });
                    };
                    
                    $scope.confirmarModal = function() {
                        $uibModalInstance.close($scope.registro);
                    };

                    $scope.cancelarModal = function() {
                        $uibModalInstance.dismiss('cancel');
                    };

                    $scope.datepickers = {featuredStartDate: false, featuredEndDate: false};
                    $scope.toggleOpenDatePicker = function($event, which) {
                        $event.preventDefault();
                        $event.stopPropagation();
                        $scope.datepickers[which] = true;
                    };
                },
                size: 'lg',
                resolve: {
                    registro: function() {
                        return reg;
                    }
                }
            });

            modalInstance.result.then(function(registro) {
            	var datos = angular.copy(registro)
            	if (registro.user.perfil.ocupacion && registro.user.perfil.ocupacion instanceof Array) {
            		datos.user.perfil.ocupacion = registro.user.perfil.ocupacion.join(',');
            	}
            	if (registro.user.perfil.formato && registro.user.perfil.formato instanceof Array) {
            		datos.user.perfil.formato = registro.user.perfil.formato.join(',');
            	}
            	Restangular.one('comunidad/perfil/').customPUT(datos, registro.user.id).then(function(res) {
                    Informer.inform(res.mensaje, 'success');
		    delete $scope.registro;
                }, function(res) {
                    Informer.inform(res.mensaje, 'danger');
                })
            }, function() {
                //console.log('Modal dismissed at: ' + new Date());
            });
            
        };
		
		
		$scope.buscar = function() {
			$http.get('search-user', {
                params: {
                    dni: $scope.user.dni,
                    email: $scope.user.email
                }
            }).then(function(retorno) {
            	$scope.registro = retorno.data;
            	if (!$scope.registro.user.documentoIdentificativo) {
            		// No se ha registrado por la app, ponemos el DNI del padrón (si lo tiene)
            		$scope.registro.user.documentoIdentificativo = $scope.registro.user.nif;
            	}
            	else {
            		// Se ha registrado por la app, ya ha aceptado las condiciones de participación
            		$scope.cbCondiciones = {
            				value: true,
            				disabled: true
            		}
            	}
            	if ($scope.registro.user.perfil && $scope.registro.user.perfil.fechaNacimiento) { 
            		$scope.registro.user.perfil.fechaNacimiento = new Date($scope.registro.user.perfil.fechaNacimiento);
            	}
            }, function(err) {
            	$scope.registro = null;
                Informer.inform("Se ha producido un error al consultar:" + (JSON.stringify(err.data.error) || JSON.stringify(err.data.mensaje)), "danger");
            });
            delete copiaRegistro;
        };
		
        $scope.datepickers = {
                fechaNacimiento: false
        };
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.addUser = function(user) {
        	var datos = {}; 
        	datos.id = user.user.id;
        	datos.fechaNacimiento = $filter('date')(user.user.perfil.fechaNacimiento, 'dd-MM-yyyy') || $filter('date')(user.fechaNacimiento, 'dd-MM-yyyy');
        	datos.centro = user.centroEducativo;
        	if (user.user.documentoIdentificativo && (user.user.documentoIdentificativo.trim() !== '')) {
        		datos.documentoIdentificativo = user.user.documentoIdentificativo;
        	}
        	if (user.user.perfil.genero && (user.user.perfil.genero.trim() !== '')) {
        		datos.genero = user.user.perfil.genero;
        	}
        	if (user.user.perfil.pais && (user.user.perfil.pais.trim() !== '')) {
        		datos.pais = user.user.perfil.pais;
        	}
        	if (user.user.juntaUser && (user.user.juntaUser.trim() !== '')) {
        		datos.barrio = user.user.juntaUser;
        	}
        	if (user.user.perfil.estudiosActuales && (user.user.perfil.estudiosActuales.trim() !== '')) {
        		datos.estudiosActuales = user.user.perfil.estudiosActuales;
        	}
        	Dao.crear('zgz16/adduser', datos).then(function() {
                Informer.inform("La persona se ha asociado correctamente al programa.", "success");
            }, function(err) {
            	Informer.inform("Se ha producido un error: " + JSON.stringify(err.data.mensaje), "danger");
            });
        };

    }
])
.controller('ListCtrl', ['$scope', 'Dao', '$location', 'Informer',
    function($scope, Dao, $location, Informer) {

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

        Dao.listar('zgz16', params).then(function(data) {
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
            Dao.listar('zgz16', params).then(function(data) {
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
            Dao.crear('zgz16', $scope.registro).then(function() {
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

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', '$location', 'Informer', '$http',
    function($scope, Dao, registro, Restangular, $location, Informer, $http) {

        $scope.registro = registro;

        if ($scope.permisos.LIDER) {
	        console.log(registro);
	        $http.get('/sede/servicio/zgz16/' + registro.id + '/lider', {}).then(function(res) {
	            $scope.lideres = res.data;
	        });
        };

        if(angular.isDefined($scope.registro.image)) { $scope.imagenVertical = $scope.registro.image;};
        
        $scope.guardar = function() {
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

        $scope.actosAsociados = [];
        Restangular.all('cultura').all('evento').all('admin').all('list').getList({ q: 'blockZGZ16.id==' + registro.id, fl: 'id,title', sort: 'id' }).then(function(res) {
            $scope.actosAsociados = res;
        });
        
        $scope.asociarLider = function(login) {
        	$http.get('/sede/servicio/zgz16/' + $scope.registro.id + '/lider/' + login + '/add', {}).then(function(res) {
                Informer.inform('Líder asociado correctamente', 'success');
                $http.get('/sede/servicio/zgz16/' + registro.id + '/lider', {}).then(function(res) {
    	            $scope.lideres = res.data;
    	        });
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        }
        $scope.eliminarLider = function(userId) {
        	$http.delete('/sede/servicio/zgz16/' + $scope.registro.id + '/lider/' + userId, {}).then(function(res) {
                Informer.inform('Líder eliminado correctamente', 'success');
                $scope.lideres = res.data;
            }, function(res){
                Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
            });
        }
        
    }
]);
