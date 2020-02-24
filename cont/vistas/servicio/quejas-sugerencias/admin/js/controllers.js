angular.module('app.controllers', [])

    .controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', 'Restangular', 'RestangularEntidades','RestangularInternos', '$route', '$templateCache', function($scope, $window, $location, $filter, Informer, Restangular, RestangularEntidades, RestangularInternos, $route, $templateCache) {

        $scope.selectedIds = [];

        $scope.datepickers = {
            expectedRes: false
        };
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };
        
       
        $scope.estados = [
			 { id: 1, text: 'Pendiente' },
			 { id: 2, text: 'Cerrada' },
			 { id: 3, text: 'No Asignada' },
			 { id: 4, text: 'Resuelta' },
			 { id: 5, text: 'Borrada' },
			 { id: 6, text: 'Solicitada información' },
			 { id: 7, text: 'Derivada a Externo' },
			 { id: 15, text: 'Finalizada por Externo' },
			 { id: 8, text: 'Rechazada' },
			 { id: 9, text: 'Archivada' },
			 { id: 10, text: 'Derivada a Interno' },
			 { id: 11, text: 'Rechazada por externo' },
			 { id: 12, text: 'Ampliar información a externo' },
			 { id: 13, text: 'Realizada por valoración' },
			 { id: 14, text: 'Pendiente por valoracíon' },
			 { id: 16, text: 'Recibida información' }
		   ];
		$scope.origin = [
			 { id: 4, text: 'Correo Convencional' },
			 { id: 2, text: 'E-mail' },
			 { id: 240386048, text: 'Inspección' },
			 { id: 1, text: 'Teléfono' },
			 { id: 5, text: 'Fax' },
			 { id: 233275393, text: 'Juntas' },
			 { id: 233275392, text: 'Policía' },
			 { id: 233275394, text: '010' },
			 { id: 241139712, text: 'Aplicaciones terceros' },
			 { id: 242712576, text: 'Registro' },
			 { id: 6, text: 'Atención presencial' },
			 { id: 7, text: 'CMSS. Comunitarios (Hoja H03-1)' },
			 { id: 8, text: 'CMMS. Especializados(Hoja H03-1)' },
			 { id: 9, text: 'Oficina de Morlanes (Hoja H03-1)' },
			 { id: 10, text: 'Información Plza. Pilar y Seminario (Hoja H03-1)' },
			 { id: 3, text: 'Web' }
		   ];
		$scope.catsip = [
			{ id: 1, text : 'Información institucional y organizativa (organigrama, competencias, perfil y trayectoria profesional, etc.)'},
			{ id: 2, text : 'Planes y programas operativos (plan de gobierno y otros planes/programas de actuación, resultados de su evaluación, etc.)'},
			{ id: 3, text : 'Normativa (normas locales y documentos emitidos en el procedimiento de elaboración, etc.)'},
			{ id: 4, text : 'Información económica (presupuestos y su ejecución, cuentas, informes de control externo, patrimonio, etc.)'},
			{ id: 5, text : 'Ayudas y subvenciones'},
			{ id: 6, text : 'Urbanismo'},
			{ id: 7, text : 'Contratación administrativa'},
			{ id: 8, text : 'Medio Ambiente'},
			{ id: 9, text : 'Recursos humanos (retribuciones, compatibilidades, procesos selectivos, provisión de puestos de trabajo, bolsas de empleo,etc.)'},
			{ id: 10, text : 'Relaciones con la ciudadanía y participación (cartas de servicio, sugerencias y reclamaciones, inventario de procedimientos, etc.)'},
			{ id: 11, text : 'Otros'}
		   ];
		
		$scope.validated = [
		     { id: 'S', text: 'Publicada' },
		     { id: 'N', text: 'Pendiente' },
		     { id: 'R', text: 'No publicada' },
		   ];
		$scope.answer_requested = [
   		     { id: 'S', text: 'Pendiente' },
   		     { id: 'N', text: 'No solicitada' },
   		     { id: 'P', text: 'Contestada' },
   		   ];
		
		$scope.priority = [
			 { id: 2, text: 'Normal' },
			 { id: 3, text: 'Alta' }
		   ];
		
		$scope.acciones = [
		   { id: 1, text: 'Resolver', respuestaTipo:'' },
		   { id: 2, text: 'Contestar', respuestaTipo:'' },
		   { id: 3, text: 'Archivar', respuestaTipo:'' },
		   { id: 4, text: 'Eliminar', respuestaTipo:'' },
		   { id: 5, text: 'Solicitar más información', respuestaTipo:'' },
		   { id: 6, text: 'Rechazar', respuestaTipo:'' },
		   { id: 7, text: 'Derivar a Web', respuestaTipo:'' },
		   { id: 8, text: 'Derivar a Externo', respuestaTipo:'' },
		   { id: 11, text: 'Derivar a Interno', respuestaTipo:'' },
		   { id: 9, text: 'Informar al ciudadano', respuestaTipo:'' },
		   
		   { id: 13, text: 'Realizada por valoración', respuestaTipo:'' },
		   { id: 14, text: 'Pendiente por valoración', respuestaTipo:'' },
		   { id: 20, text: 'Rechazada por externo', respuestaTipo:'' },
		   { id: 21, text: 'Ampliar información', respuestaTipo:'' },
		   
		   { id: 22, text: 'Cambiar categoría', respuestaTipo:'' },
		   
		   { id: 10, text: 'Anotar información', respuestaTipo:'' }
		];
		
        if (sessionStorage.getItem('datosUsuario') === null) {
            $window.location.href = '/gesweb';
        } else {
            datosUsuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
            if (!datosUsuario.propiedades) {
            	datosUsuario.propiedades=[];
            }
            serviciosUsuario = JSON.parse(sessionStorage.getItem('serviciosUsuario'));
            $scope.usuario = datosUsuario;
            $scope.servicios = serviciosUsuario;
            
            clientID = datosUsuario.login;
            secretKey = datosUsuario.secretKey;
            
        }
        $scope.types = [
   			 { id: 12812288, text: 'Agradecimiento' },
   			 { id: 4587520, text: 'Información' },
   			 { id: 1, text: 'Queja' },
   			 { id: 2, text: 'Sugerencia' },
   			 { id: 240418816, text: 'Aviso' },
   			 { id:244219904, text: 'Planificación' },
   			 { id:245268480, text: 'Inspección contractual' },
   			 { id:244219905, text: 'Aviso planificación'},
   			 { id:250544128, text: 'Solicitud Información Pública'},
   			 { id:3, text: 'Cambio de profesional'}
   		   ];
        
        rootcat = (datosUsuario.propiedades != null && datosUsuario.propiedades.rootcat_ticketing != null) 
    		? datosUsuario.propiedades.rootcat_ticketing 
    				: null;
        var permisos = [];
        for (var i = 0; i < $scope.servicios.length; i++) {
            if ($scope.servicios[i].codigoServicio === 'TICKETING') {
                $scope.secciones = $scope.servicios[i].secciones;
                $scope.serv = $scope.servicios[i];
                for (var j = 0; j < $scope.secciones.length; j++) {
                    if ($scope.secciones[j].codigoSeccion === 'REQUESTS') {
                        $scope.secc = $scope.secciones[j];
                        permisos = $scope.secciones[j].permisos;
                        if ($scope.secciones[j].permisos.indexOf("REQUERIMIENTO") > 0) {
                        	$scope.types.push({ id: 241532928, text: 'Requerimiento' });
                        } else if ($scope.secciones[j].permisos.indexOf("PLZ") > 0) {
                        	rootcat = '3899392';
                        }
                        
                    }
                }
            }
        }
        
        
     // sino tiene asociada categoria de quejas y es policia se asocia la 2
        
        var serviciosUsuario = JSON.parse(sessionStorage.getItem('serviciosUsuario'));
	   	for (var i = 0; i < serviciosUsuario.length; i++) {
	       if (serviciosUsuario[i].codigoServicio === 'TICKETING') {
	           secciones = serviciosUsuario[i].secciones;               
	           for (var j = 0; j < secciones.length; j++) {
	               if (secciones[j].codigoSeccion === 'REQUESTS') {
	                   for (var l = 0; l < secciones[j].permisos.length; l++) {
	                	   if (secciones[j].permisos[l] === 'ADJ') {
	                		   Restangular.all('category-adjacent').getList({
	                			   results_only:false, 
		       					}).then(function(data) {
		       						sessionStorage.setItem('categoryAdjacent', JSON.stringify(data));
		       					}, function(result) {
		       						Informer.inform('Error categorias adyacentes ' + (result.data.error || result.data.mensaje), "danger");
		       					});
	                	   }
	                   }
	                   
	                   break;
	               }
	           }
	       }
	   	}
	   	
	   	$scope.estados_internos = [];
        /*
        $scope.estados_internos = [
               { id: 1, text: 'Obra Ejecutada' },
               { id: 0, text: 'Sin Intervención' }
        ];*/
        if (sessionStorage.getItem('respuestaTipo') === null) {
        	if (permisos.ADMIN) {
	    		Restangular.all('quejas-sugerencias/respuestas-tipo').getList({
	    			results_only:false, 
					}).then(function(data) {
						for (i = 0; i < data.length; i++) {
			    			$scope.estados_internos.push({id: data[i].code, text: data[i].text});
			    		}
						sessionStorage.setItem('respuestaTipo', JSON.stringify(data));
					}, function(result) {
		            	Informer.inform(result.data.error || result.data.mensaje, "danger");
		            });
        	}
    	} else {
    		var data = JSON.parse(sessionStorage.getItem('respuestaTipo'));
    		for (i = 0; i < data.length; i++) {
    			$scope.estados_internos.push({id: data[i].code, text: data[i].text});
    		}
    		console.log($scope.estados_internos);
    	}
        
        
        if (sessionStorage.getItem('reqcategories') === null) {
            if (rootcat != null) {
                Restangular.all('quejas-sugerencias/category').getList({
                    results_only: false,
                }).then(function(data) {
                    $scope.categories = data;
                    sessionStorage.setItem('reqcategories', JSON.stringify(data));
                }, function(result) {
                    sessionStorage.setItem('reqcategories', JSON.stringify([]));
                });
            } else {
                sessionStorage.setItem('reqcategories', JSON.stringify([]));
            }
        } else {
            $scope.categories = JSON.parse(sessionStorage.getItem('reqcategories'));
        }
        if (sessionStorage.getItem('reqentidadexterna') === null) {
        	if (permisos.SENDEXTERNO) {
	        	RestangularEntidades.all('').getList({
	    			results_only:false, 
	    			sort: 'name asc'
					}).then(function(data) {
						$scope.entidadExterna = data;
						sessionStorage.setItem('reqentidadexterna', JSON.stringify(data));    					
					}, function(result) {
		            	Informer.inform(result.data.error || result.data.mensaje, "danger");
		            });
        	}
    	} else {
    		$scope.entidadExterna = JSON.parse(sessionStorage.getItem('reqentidadexterna'));
    	}
        if (sessionStorage.getItem('reqentidadinterna') === null) {
        	if (permisos.SENDINSPECTOR) {
	        	RestangularInternos.all('').getList({
	    			results_only:false, 
	    			sort: 'name asc'
					}).then(function(data) {
						$scope.entidadInterna = data;
						sessionStorage.setItem('reqentidadinterna', JSON.stringify(data));    					
					}, function(result) {
		            	Informer.inform(result.data.error || result.data.mensaje, "danger");
		            });
        	}
    	} else {
    		$scope.entidadInterna = JSON.parse(sessionStorage.getItem('reqentidadinterna'));
    	}
        $scope.listar = function() {
            $location.path('#/');
        };

        $scope.group = function() {
            $location.path('group');
        };

        $scope.category = function() {
            $location.path('category');
        };

        $scope.crear = function() {
            // Si estamos copiando un punto y se quiere crear uno nuevo, tenemos que recargar la vista
            // if ($location.path('/new')) {
            //     copiaPunto = null;
            //     $route.reload();
            // }
            if ($scope.permisos.OPERADOR) {
                $location.path('/newOperador');
            } else if ($scope.permisos.ZONAINSPECCION) {
                $location.path('/newParques');
            } else if ($scope.permisos.PLZ) {
                $location.path('/newPlz');
            } else {
                $location.path('/new');
            }

        };

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }])

    .controller('ListCtrl', ['$scope', '$filter', 'Restangular', 'Informer', 'Dao', 'Query', '$location', function($scope, $filter, Restangular, Informer, Dao, Query, $location) {

        var busquedaDeOtroControlador = false;
        var accountId = null;
        if (sessionStorage.getItem('accountId') !== null) {
            accountId = sessionStorage.getItem('accountId');
            sessionStorage.removeItem('accountId');
            busquedaDeOtroControlador = true;
        }
        var relacionadas = null;
        if (sessionStorage.getItem('title') !== null) {
            relacionadas = sessionStorage.getItem('title');
            sessionStorage.removeItem('title');
            busquedaDeOtroControlador = true;
        }

        var start = 0;
        var datosListados = [];
        var contenidoHash = '';
        var q = '';
        $scope.query = {};
        $scope.selectedIds = [];
        $scope.mostrar010 = true;

        $scope.busy = false;
        $scope.loadMore = function() {
            if ($scope.busy /*|| (datosListados.length >= 0 && datosListados.length < 40)*/ )
                return;
            $scope.busy = true;
            if(angular.isUndefined($scope.query.estado) && busquedaDeOtroControlador === false){
            	if(angular.isDefined(datosUsuario.propiedades.junta_ticketing)) {
            		$scope.query.estado = [$scope.estados[0].id,$scope.estados[2].id, $scope.estados[3].id];
            		$scope.textoQuery=' Filtro: Pendiente, No Asignada, Resuelta';
            	} else if(datosUsuario.propiedades.usr_ticketing=="2" || $scope.permisos.PLZ) {
            		$scope.mostrar010 = false;
                    $scope.query.estado = [$scope.estados[2].id, $scope.estados[3].id, $scope.estados[15].id];
            		$scope.textoQuery=' Filtro: Pendiente, No Asignada, Recibida información';
            	} else if (datosUsuario.login=='alperezp') {
            			$scope.query.estado = [$scope.estados[0].id];
            			$scope.textoQuery=' Filtro: Pendiente';
            	} else if (datosUsuario.propiedades.usr_ticketing=="4816906") {
            		// Limpieza publica
            		$scope.query.estado = [$scope.estados[0].id, $scope.estados[3].id, $scope.estados[6].id];
        			$scope.textoQuery=' Filtro: Pendiente, Resuelta, Derivada a externo';
            	} else {
            		$scope.query.estado=[];
            		if ($scope.permisos.ADMINOPERADOR) {
            			$scope.query.group_operator=$scope.usuario.propiedades.usr_ticketing;
            			$scope.query.estado = [$scope.estados[2].id];
            			$scope.textoQuery=' Filtro: no asignada, introducidas por los operadores';
            		} else if ($scope.permisos.OPERADOR) {
            			$scope.query.operator=$scope.usuario.login;
            			var f = new Date();
            			$scope.query.start_date=new Date(f.getFullYear(), f.getMonth(), f.getDate(), 0, 0, 0, 0);
            			$scope.textoQuery=' Filtro: introducidas por mi hoy';
            		} else if ($scope.permisos.INSPECTOR) {
            			$scope.query.estado = [$scope.estados[10].id,$scope.estados[6].id, $scope.estados[11].id, $scope.estados[7].id];
            			$scope.textoQuery=' Filtro: Derivada a interno, Derivada a externo, Rechazada por externo, Finalizada por externo';
            		} else if (datosUsuario.externo_ticketing) {
            			$scope.query.estado = [$scope.estados[6].id];
            			$scope.textoQuery=' Filtro: Derivada a externo';
            		
            		} else {
            			$scope.query.estado = [$scope.estados[0].id, $scope.estados[3].id, $scope.estados[7].id];
            			$scope.textoQuery=' Filtro: Pendiente, Resuelta';
            		}
            	}
        	} else {
        		$scope.textoQuery='';
        		if (angular.isUndefined($scope.query.estado)) {
        			$scope.query.estado='';
        		} else {
        			$scope.query.estado = $scope.query.estado.toString();
        		}
        	}

            var titulo = $scope.query.title;
            if (relacionadas !== null) {
            	titulo = relacionadas;
            }
            
            var sort_ticketing = 'status asc,requested_datetime desc';
            if ($scope.usuario.sort_ticketing) {
            	sort_ticketing = $scope.usuario.sort_ticketing;
            }

            if (start < 300 && (datosListados.length == 0 || datosListados.length > 40)) {
                Restangular.all('quejas-sugerencias').getList({
                    results_only: false,
                    rows: 50,
                    sort: sort_ticketing,
                    start: start,
                    srsname: 'wgs84',
                    title: titulo,
                    service_code: $scope.query.service_code,
                    externo_code: $scope.query_externo_code,
                    start_date: $filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    end_date: $filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    type: $scope.query.tipo,
                    answer_requested: $scope.query.answer_requested,
                    barrio_code: $scope.query.barrio_code,
                    service_request_id: $scope.query.id,
                    origin: $scope.query.origin,
                    validated: $scope.query.validated,
                    group_operator: $scope.query.group_operator,
                    operator: $scope.query.operator,
                    status: $scope.query.estado.toString(),
                    account_id: accountId
                }).then(function(data) {
                    if (data.totalCount > 0) {
                        $scope.totalCount = data.totalCount;
                    }
                    datosListados = $scope.registros || [];
                    datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                    $scope.registros = datosListados;

                    $scope.busy = false;
                    start += 50;
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });

            } else {
                if (angular.isUndefined($scope.mensajevisto) && datosListados.length > 40) {
                    Informer.inform("Solo se muestran 300 registros, si lo desea puede acotar la busqueda", "danger");
                    $scope.mensajevisto = 'S';
                }
                $scope.busy = false;
            }
        };

        $scope.buscar = function(identificador) {
            if (identificador) {
                $location.path('/edit/' + identificador);
            } else {
                start = 0;
                datosListados = [];
                $scope.textoQuery = '';
                $scope.query = $scope.queryForm || {};

                if (angular.isUndefined($scope.query.estado)) {
                    $scope.query.estado = [];
                }
                var sort_ticketing = 'status asc,requested_datetime desc';
                if ($scope.usuario.sort_ticketing) {
                    sort_ticketing = $scope.usuario.sort_ticketing;
                }
                Restangular.all('quejas-sugerencias').getList({
                    results_only: false,
                    rows: 50,
                    sort: sort_ticketing,
                    start: start,
                    srsname: 'wgs84',
                    title: $scope.query.title,
                    service_code: $scope.query.service_codes,
                    externo_code: $scope.query.externo_code,
                    start_date: $filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    end_date: $filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    type: $scope.query.tipo,
                    answer_requested: $scope.query.answer_requested,
                    barrio_code: $scope.query.barrio_code,
                    service_request_id: $scope.query.id,
                    origin: $scope.query.origin,
                    validated: $scope.query.validated,
                    //group_operator:$scope.query.group_operator,
                    operator: $scope.query.operator,
                    status: $scope.query.estado.toString()
                }).then(function(res) {
                    $scope.totalCount = res.totalCount;
                    datosListados = datosListados.concat(res); //De otro modo, se pierden los datos ya listados
                    $scope.registros = datosListados;
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });
                start += 50;
            }
        };

    }])

    .controller('CategoryCtrl', ['$scope', '$filter', 'Restangular', 'Informer', 'Dao', 'Query', '$uibModal', function($scope, $filter, Restangular, Informer, Dao, Query, $uibModal) {
        $scope.registros = JSON.parse(sessionStorage.getItem('reqcategories'));

        $scope.gestion = function(index) {
            $scope.dato = {};

            if (!angular.isUndefined(index)) {
                $scope.dato = angular.extend($scope.registros[index]);
            }

            var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/categoryModal.html',

                controller: function($scope, $uibModalInstance, registro, dato) {
                    $scope.registro = registro;
                    $scope.dato = dato;
                    $scope.save = function() {
                        $uibModalInstance.close($scope.dato);
                    };
                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                },
                resolve: {
                    registro: function() {
                        return $scope.registro;
                    },
                    dato: function() {
                        return $scope.dato;
                    }
                }
            });

            modalInstance.result.then(function(elemento) {

                var selectedItem = {};

                selectedItem.service_name = elemento.service_name;
                selectedItem.service_description = elemento.service_description;
                selectedItem.service_parent_code = elemento.service_parent_code;
                selectedItem.agency_responsible_code = elemento.agency_responsible_code;
                selectedItem.group_code = elemento.group_code;
                selectedItem.service_level = elemento.service_level;
                selectedItem.autoassign = elemento.autoassign;

                if (angular.isUndefined(elemento.service_code)) {

                    Restangular.one("quejas-sugerencias/category", selectedItem.service_code).customPOST(selectedItem, "", "", "").then(function(dato) {
                        Informer.inform("El registro se ha creado.", "success");
                        $scope.registros.push(dato);
                    }, function(result) {
                        Informer.inform(result.data.error || result.data.mensaje, "danger");
                    });

                } else {
                    selectedItem.service_code = elemento.service_code;
                    Restangular.one("quejas-sugerencias/category", selectedItem.service_code).customPUT(selectedItem, "", "", "").then(function(dato) {
                        Informer.inform("El registro se ha modificado correctamente.", "success");
                        $scope.registros[index] = dato;
                    }, function(result) {
                        Informer.inform(result.data.error || result.data.mensaje, "danger");
                    });

                }
            }, function() {});
        };





    }])
    .controller('GroupCtrl', ['$scope', '$filter', 'Restangular', 'Informer', 'Dao', 'Query', '$uibModal', function($scope, $filter, Restangular, Informer, Dao, Query, $uibModal) {

        Restangular.all('quejas-sugerencias/group').getList({
            results_only: false,
        }).then(function(data) {
            $scope.registros = data;
        }, function(result) {
            Informer.inform(result.data.error || result.data.mensaje, "danger");
        });


        $scope.gestion = function(index) {
            $scope.dato = {};

            if (!angular.isUndefined(index)) {
                $scope.dato = angular.extend($scope.registros[index]);
            }

            var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/groupModal.html',

                controller: function($scope, $uibModalInstance, registro, dato) {
                    $scope.registro = registro;
                    $scope.dato = dato;
                    $scope.save = function() {
                        $uibModalInstance.close($scope.dato);
                    };
                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                },
                resolve: {
                    registro: function() {
                        return $scope.registro;
                    },
                    dato: function() {
                        return $scope.dato;
                    }
                }
            });

            modalInstance.result.then(function(selectedItem) {

                if (angular.isUndefined(selectedItem.group_code)) {

                    Restangular.one("quejas-sugerencias/group", $scope.registros[index]).customPOST(selectedItem, "", "", "").then(function(dato) {
                        Informer.inform("El registro se ha creado.", "success");
                        $scope.registros.push(dato);
                    }, function(result) {
                        Informer.inform(result.data.error || result.data.mensaje, "danger");
                    });

                } else {
                    Restangular.one("quejas-sugerencias/group", selectedItem.group_code).customPUT(selectedItem, "", "", "").then(function(dato) {
                        Informer.inform("El registro se ha modificado correctamente.", "success");
                        $scope.registros[index] = dato;
                    }, function(result) {
                        Informer.inform(result.data.error || result.data.mensaje, "danger");
                    });

                }
            }, function() {});
        };






    }])
    .controller('EstadisticaCtrl', ['$scope', '$filter', 'Restangular', 'Query', 'Informer', function($scope, $filter, Restangular, Query, Informer) {

        // Query = Objeto compartido entre controllers.
        // Query.data contiene la información del modal estadistica
        $scope.query = Query.data;

        var service_codes;
        var datosListados = [];
        $scope.query = $scope.query || {};
        if (angular.isDefined($scope.query.service_code)) { // && $scope.query.service_code.length>0) {
            service_codes = '';
            for (var i = 0; i < $scope.query.service_code.length; i++) {
                if (i > 0) {
                    service_codes = service_codes + ",";
                }
                service_codes = service_codes + $scope.query.service_code[i].service_code;
            }

        }
        Restangular.all('quejas-sugerencias/estadistica').getList({
            results_only: false,
            service_code: service_codes,
            start_date: $filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
            end_date: $filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
        }).then(function(data) {
            $scope.totalCount = data.totalCount;
            $scope.registros = data;
        }, function(result) {
            Informer.inform(result.data.error || result.data.mensaje, "danger");
        });


        $scope.estadisticaCategoria = function(pcategoria) {

            // solo concatener cuando no existe la(s) categoria(s), si la(s) categori�a(s) existe(n) borrar
            var existeCategoria = false;
            for (var i in $scope.registros) {
                var item = $scope.registros[i] || {};
                if ((angular.isDefined(item.id) && pcategoria == item.id) || (angular.isDefined(item.id) && item.id == 1 && angular.isUndefined(pcategoria))) {
                    existeCategoria = true;
                    $scope.registros.splice(i, 1);
                    break;
                }
            }
            if (!existeCategoria) {
                Restangular.all('quejas-sugerencias/estadistica').getList({
                    results_only: false,
                    service_code: pcategoria,
                    start_date: $filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    end_date: $filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                }).then(function(data) {
                    $scope.totalCount = data.totalCount;
                    datosListados = datosListados.concat(data);
                    $scope.registros = datosListados;
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });
            }
        };

        $scope.porCategoria = function() {
            if (angular.isDefined($scope.resultado)) {
                $scope.resultado = undefined;
            } else {
                Restangular.all('quejas-sugerencias/datos').getList({
                    results_only: false,
                    start_date: $filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                    end_date: $filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                }).then(function(data) {
                    $scope.resultado = data;
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });
            }
        };



    }])
    .controller('CreateCtrl', ['$scope', '$location', 'Restangular', 'RestangularEntidades', 'Informer', 'Dao', '$filter', function($scope, $location, Restangular, RestangularEntidades, Informer, Dao, $filter) {
        $scope.$watch("registro.address_string", function(newValue, oldValue) {
            if (angular.isDefined($scope.registro.address_id)) {
                //              var address = $scope.registro.address_string.replace(/[, ]+/g, " ").replace(/[  ]+/g,' ');
                if ($scope.registro.address_id > 0) {
                    Restangular.all('quejas-sugerencias').getList({
                        results_only: false,
                        rows: 5,
                        sort: 'requested_datetime desc',
                        start: 0,
                        //                  srsname: 'utm30n',
                        title: '//' + $scope.registro.address_id,
                        start_date: $filter('date')($scope.query_start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                        end_date: $filter('date')($scope.query_end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\'')
                    }).then(function(data) {
                        if (data.totalCount > 0) {
                            $scope.busy = false;
                            $scope.mismaLocalizacion = data;
                        } else {
                            $scope.mismaLocalizacion = [];
                        }

                    }, function(result) {
                        // No hay
                    });
                }
            }
        });

        if (sessionStorage.getItem('reqcategories') === null) {
            Restangular.all('category').getList({
                results_only: false,
            }).then(function(puntos) {
                $scope.categories = puntos;
                sessionStorage.setItem('reqcategories', JSON.stringify(puntos));
            }, function(result) {
                sessionStorage.setItem('reqcategories', JSON.stringify([]));
            });
        } else {
            $scope.categories = JSON.parse(sessionStorage.getItem('reqcategories'));
        }

        if (typeof copiaRegistro != 'undefined' && copiaRegistro != null) {
            $scope.registro = copiaRegistro;
            $scope.registro.title = $scope.registro.title + '_COPIA DE q/s num: ' + $scope.registro.service_request_id;
            delete copiaRegistro.service_request_id;
            delete $scope.registro.service_request_id;
            copiaRegistro = null;
        } else {
            $scope.registro = {};
        }
        if ($scope.permisos.PLZ) {
        	$scope.registro.first_name = $scope.usuario.nombre + ' ' + $scope.usuario.apellido1 + ' ' + $scope.usuario.apellido2;
        	$scope.registro.email = $scope.usuario.email;
        }
        $scope.seleccionadaCategoria = function(item) {
            $scope.category = item;
            if (angular.isUndefined($scope.registro.title) || $scope.registro.title.length <= 3) {
                $scope.registro.title = $scope.category.service_name;
            }
        };

        $scope.guardar = function() {
            var service_code = null;
            var agency_responsible_code = null;
            var colaborador = null;
            try {
                service_code = $scope.category.service_code;
            } catch (e) {;
            }

            if (angular.isUndefined(service_code) && $scope.asociar_mi_cat === true) {
                service_code = datosUsuario.propiedades.rootcat_ticketing;
            }

            try { agency_responsible_code = $scope.category.agency_responsible_code; } catch (e) {}
            try { colaborador = $scope.tienecolaborador; } catch (e) {}

            if ($scope.registro) {
                var queryString = generarQueryStringCrear($scope.registro, service_code, agency_responsible_code, colaborador, $filter('date')($scope.registro.expected_resolution_datetime, 'yyyy-MM-ddTHH:mm:ss\'Z\''));
                Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customPOST(queryString, "", {}, {}).then(function(json) {
                    Informer.inform("El registro se ha creado correctamente con identificador: " + json.service_request_id + "", "success");
                    if ($scope.permisos.OPERADOR) {
                        $scope.registro = {};
                        $scope.portaleroSeleccion = undefined;
                        delete $scope.portaleroSeleccion;
                        delete $scope.markers;
                        delete $scope.calleSel;
                        console.log($scope.markers);
                        $location.path('/newOperador');
                    } else {
                        $scope.registro = json;
                        $location.path('/edit/' + $scope.registro.service_request_id);
                    }
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });
            } else {
                Informer.inform("Introduzca informacion de la queja.", "danger");
            }
        };

    }])
    .controller('EditCtrl', ['$routeParams', '$scope', 'Restangular', 'RestangularPortalero', 'RestangularEntidades', 'registro', '$location', 'limitToFilter', 'Informer', 'Dao', '$window', '$filter', function($routeParams, $scope, Restangular, RestangularPortalero, RestangularEntidades, registro, $location, limitToFilter, Informer, Dao, $window, $filter) {


        $scope.categories = JSON.parse(sessionStorage.getItem('reqcategories'));

        var original = registro;
        $scope.registro = Restangular.copy(original);
        $scope.selectedIds = [];
        if (!angular.isUndefined($scope.registro.agency_responsible_code)) {
            $scope.tienecolaborador = true;
        }
        for (i = 0; i < $scope.categories.length; i++) {
            if ($scope.registro.service_code == $scope.categories[i].service_code) {
                $scope.category = $scope.categories[i];
                break;
            }
        }


        if ($scope.registro.service_code == 110) {
            // Si la categoría es fuentes ornamentales
            $scope.registro.zona_inspeccion = 21;
        }

        // Buscamos las quejas que hay en la misma localización
        $scope.init = function() {
            if (angular.isDefined($scope.registro.address_id)) {
                //          var address = $scope.registro.address_string.replace(/[, ]+/g, " ").replace(/[  ]+/g,' ');
                if ($scope.registro.address_id > 0) {
                    Restangular.all('quejas-sugerencias').getList({
                        results_only: false,
                        rows: 5,
                        sort: 'requested_datetime desc',
                        start: 0,
                        //            srsname: 'utm30n',
                        title: '//' + $scope.registro.address_id
                        /*, 
                                    start_date:$filter('date')($scope.query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                                    end_date:$filter('date')($scope.query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\'')*/
                    }).then(function(data) {
                        if (data.totalCount > 0) {
                            $scope.busy = false;
                            $scope.mismaLocalizacion = data;
                        } else {
                            $scope.mismaLocalizacion = [];
                        }

                    }, function(result) {
                        // No hay
                    });
                }
            }


            if (angular.isDefined($scope.registro.id_cita)) {
                if ($scope.registro.id_cita > 0) {
                    Restangular.one('cita/' + $scope.registro.service_request_id).get().then(function(res) {
                        $scope.cita = res;
                    }, function(err) {
                        console.log('error' + err.toString());
                    });
                }
            }


        };

        // $scope.visible = function(valid) {
        //     Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customPUT("validated=" +  valid +"&visible=" + $scope.registro.visible, "", {}, {}).then(function(json) {
        //         Informer.inform("El registro se ha modificado correctamente.", "success");
        //         $scope.registro=json;
        //     }, function(result) {
        //      Informer.inform(result.data.error || result.data.mensaje, "danger");
        //     });
        // };     

        $scope.seleccionadaCategoria = function(item) {
            $scope.category = item;
        };


        $scope.verRelacionadas = function() {
            sessionStorage.setItem('title', 'COPIA DE ' + $scope.registro.service_request_id);
            $window.location.path('#/');
        };
        $scope.verDeLocalizacion = function() {
            sessionStorage.setItem('title', $scope.registro.address_string.replace(/[, ]+/g, " ").replace(/[  ]+/g, ' '));
            $window.location.path('#/');
        };

        $scope.guardar = function() {


            var service_code = null;
            var agency_responsible_code = null;
            var colaborador = null;

            try { service_code = $scope.category.service_code; } catch (e) {}
            try { agency_responsible_code = $scope.category.agency_responsible_code; } catch (e) {}
            try { colaborador = $scope.tienecolaborador; } catch (e) {}


            //          var newElement = _.pick($scope.registro, _.keys($scope.registro.originalElement));
            //          try {
            //            var facade = parent.frames["visualizador_visor"].getFacade();
            //            for(var propt in facade.changeList){
            //              var geom = facade.changeList[propt];
            //              //TODO: Revisar $$childTail AngularUI Bootstrap Tabs child scope
            //              try {
            //                newElement.address_id = $scope.$$childTail.portaleroSeleccion.id;
            //                newElement.address_string = $scope.$$childTail.portaleroSeleccion.calle.title + ", " + $scope.$$childTail.portaleroSeleccion.numero;
            //              } catch (e){;}
            //              newElement.x = geom.geometry.x;
            //              newElement.y = geom.geometry.y;
            //                break;
            //            }
            //
            //          } catch (e) {
            //            console.log(e.message);
            //        }

            // Nuevo element sin restangular

            //          delete newElement.actions;
            //          // Original element sin restangular, generado en app.js vía response interceptor
            var originalElement = $scope.registro.originalElement;
            delete originalElement.actions;
            //
            //
            //            if(_.isEqual(originalElement, newElement)){
            //                Informer.inform("No se ha detectado ningún cambio.", "success");
            //            } else {

            var queryString = generarQueryString($scope.registro, originalElement, service_code, agency_responsible_code, colaborador, $filter('date')($scope.registro.expected_resolution_datetime, 'yyyy-MM-ddTHH:mm:ss\'Z\''));
            if (queryString.length > 2) {
                // delete puntoSinRestangular.route;
                // delete puntoSinRestangular.restangularCollection;
                // delete puntoSinRestangular.parentResource; // anyadido en Restangular v. 1.0.2
                console.log(queryString);
                Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customPUT(queryString, "", {}, {}).then(function(data) {
                    Informer.inform("El registro se ha modificado correctamente.", "success");
                    $scope.registro = data;

                    if ($scope.registro.service_code == 110) {
                        // Si la categoría es fuentes ornamentales
                        $scope.registro.zona_inspeccion = 21;
                    }

                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                });
            } else {
                Informer.inform("No se ha detectado ningun cambio.", "success");
            }
            //            }
        };

        $scope.derivarAServicio = function() {
            var queryString = "&service_code=" + $scope.usuario.sup_service_code + "&agency_responsible_code=" + $scope.usuario.sup_agency_responsible_code;
            Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customPUT(queryString, "", {}, {}).then(function(data) {
                Informer.inform("El registro se ha modificado correctamente.", "success");
                $scope.registro = data;
            }, function(result) {
                Informer.inform(result.data.error || result.data.mensaje, "danger");
            });
        };

        $scope.informe = function() {
            Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customGET("informe", { Base64: "S" }, { accept: "application/pdf" }).then(function(data) {
                Informer.inform("Informe correcto.", "success");
                window.open("data:application/pdf;base64," + escape(data));
            }, function(json) {
                Informer.inform(json.data.mensaje, "danger");
            });
        };

    }])
    .controller('CoordinatesCtrl', ['$scope', 'RestangularPortalero', 'limitToFilter', function($scope, RestangularPortalero, limitToFilter) {

        //TODO: Solucionar borrado parametro en buscador en document.ready y no onclick
        $("#buscadorPortalero").click(function() {
            $("#buscadorPortalero").val("");
        });

        //TODO AL CREAR NUEVO REGISTRO NO PUEDE ACCEDER EL CONTROLADOR!!!!

        //TODO: REVISAR SI AL GUARDAR LOS PUNTOS, ES INT,STRING,FLOAT ...
        $scope.portaleroSeleccionado = [];
        $scope.portaleros = function(registro) {
            nombreCalle = registro.split(',')[0];
            numeroCalle = registro.split(',')[1];
            // Si todavi�a no han introducido una ',' (separador nombreCalle,numeroCalle)
            if (numeroCalle === undefined) {
                return RestangularPortalero.all('').getList({ rows: 50, start: 0, q: 'calle.title==' + nombreCalle + '*' }).then(function(response) {
                    $scope.portaleroSeleccionado.length = 0;
                    angular.forEach(response, function(value, key) {
                        this.push(value);
                    }, $scope.portaleroSeleccionado);
                    return limitToFilter($scope.portaleroSeleccionado, 15);
                });
            } else {
                return RestangularPortalero.all('').getList({ rows: 50, start: 0, q: 'calle.title==' + nombreCalle + '*;numero==' + numeroCalle }).then(function(response) {
                    $scope.portaleroSeleccionado.length = 0;
                    angular.forEach(response, function(value, key) {
                        this.push(value);
                    }, $scope.portaleroSeleccionado);
                    return limitToFilter($scope.portaleroSeleccionado, 15);
                });
            }
        };

    }])
    .controller('UserCtrl', ['$scope', '$http', '$location', 'Informer', 'usuario', 'servicios', function($scope, $http, $location, Informer, usuario, servicios) {

        $scope.usuario = usuario;
        $scope.servicios = servicios;

        $scope.btnGuardarDatosUsuario = function() {
            $http({
                method: 'PUT',
                url: '/api/acceso/' + clientID,
                data: {
                    "nombre": $scope.usuario.nombre,
                    "apellido1": $scope.usuario.apellido1,
                    "apellido2": $scope.usuario.apellido2,
                    "email": $scope.usuario.email,
                    //                    "servicioPorDefecto": $scope.usuario.servicioPorDefecto,
                    //                    "seccionPorDefecto": $scope.usuario.seccionPorDefecto
                },
                headers: {}
            }).success(function() {
                Informer.inform("Datos del usuario guardados correctamente.", "success");
            }).error(function() {
                Informer.inform("Error al modificar datos del usuario.", "danger");
            });
        };

        $scope.btnGuardarPassword = function() {
            $http({
                method: 'PUT',
                url: '//' + SERVIDOR + '/api/acceso/' + clientID + '/change-password',
                data: {

                },
                headers: {
                    'X-password': $scope.password
                }
            }).success(function() {
                Informer.inform("Contraseña modificada correctamente.", "success");
            }).error(function() {
                Informer.inform("Error al modificar la contraseña.", "danger");
            });
        };

    }]);