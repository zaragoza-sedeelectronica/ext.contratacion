angular.module('app.directives', [])

.directive('modalAccion', ['$uibModal', 'Restangular', 'Informer', '$filter', '$http',
    function($uibModal, Restangular, Informer, $filter, $http) {
        return {
            restrict: 'E',
            scope: {
                accion: '=',
                title: '@',
                icon: '@',
                desactivar: '@',
                ayuda: '@'
            },
            // replace: true,
            template: '<button class="btn btn-default" ng-click="lanzarModalAccion()" title="{{ayuda}}" ng-disabled="{{desactivar}}"><span class="fa {{icon}}" aria-hidden="true"></span> <span class="hidden-xs">{{title}}</span></button>',
            controller: function($scope, $element, $attrs, $transclude) {
            	
            	
                $scope.lanzarModalAccion = function() {
                	
                    var modalInstance = $uibModal.open({
                        templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/accionModal.html',
                        controller: function($scope, $uibModalInstance, regInterno, accionesBase, entidadExterna, entidadInterna, estados_internos, permisos, selectedIds) {
                        	
                            $scope.registro = regInterno;
                            $scope.entidadExterna = entidadExterna;
                            $scope.entidadInterna = entidadInterna;
                            $scope.permisos = permisos;
                            $scope.selectedIds = selectedIds;
                            $scope.estados_internos = estados_internos;
                            $scope.categoryAdjacent = angular.fromJson(sessionStorage.categoryAdjacent);
                            if ($scope.usuario.otsel_ticketing=='S') {
                            	$scope.registro.incluir_orden = true;
                            }
                            $scope.accion = {};
                            if (angular.isDefined($scope.usuario.externo_defecto_tick)) {
                            	for (var i in entidadExterna) {
                            		var item = entidadExterna[i];
                            		if (item.id == $scope.usuario.externo_defecto_tick) {
                            			$scope.accion.entidadExternaSelected = item;
                            			break;
                            		}
                            	}
                            }
                            
                            var elem = parseInt($attrs.accion, 10);
                            
                            $scope.linkOt = CryptoJS.HmacSHA1("preview-orden-trabajo#" + clientID + "#"+ datosUsuario.rootcat_ticketing, secretKey, {asBytes: true}).toString();
                            
                            
                            $scope.attachment = [];
                            $scope.cerrarSinContestar = false;
                            // $("#entidadExternaSel").select2();
                            for (var i in accionesBase) {
                                var item = accionesBase[i];

                                if (elem === item.id) {

                                    $scope.accion.id = item.id;
                                    $scope.accion.title = item.text;
                                    $scope.accion.description = '';

                                    // 2: Contestar al ciudadano
                                    if (elem === 2 && $scope.selectedIds.length <= 0) {
                                        for (var j = 0; j < $scope.registro.actions.length; j++) {
                                            var elemento = $scope.registro.actions[j];
                                            if (elemento.substate == 1 && elemento.version == 1 && elemento.type == 1) {
                                                $scope.accion.description = elemento.description;
                                                break;
                                            }
                                        }
                                    }

                                    if ($scope.accion.description === '') {
                                        var tipos = JSON.parse(sessionStorage.getItem('respuestaTipo')) || [];
                                        for (var j = 0; j < tipos.length; j++) {
                                            if (item.id == tipos[j].id) {
                                                $scope.accion.description = tipos[j].text;
                                                break;
                                            }
                                        }
                                    }

                                    break;
                                }


                            }

                            $scope.abrirCalendario = function($event) {
                                $event.preventDefault();
                                $event.stopPropagation();

                                $scope.opened = true;
                            };

                            $scope.ordenTrabajoPreview = function(elem) {
                                Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customGET("preview-orden-trabajo", {
                                    Base64: "S",
                                    externo_name: $scope.accion.entidadExternaSelected.name
                                }, {
                                    accept: "application/pdf"
                                }).then(function(data) {
                                    //                Informer.inform("Informe correcto.", "success");
                                    window.open("data:application/pdf;base64," + escape(data));
                                }, function(json) {
                                    Informer.inform(json.data.mensaje, "danger");
                                });
                            };
                            $scope.cambioCategoria = function() {
                            	console.log("cambiocatgori");
                            	if ($scope.registro.category.service_code == 30008) {
                            		console.log("solicitud sotano, pedir cita");
                            		
                            		$http({
                            			  method: 'GET',
                            			  url: 'https://www.zaragoza.es/sede/servicio/cita-previa/1021/next-days'
                            			}).then(function successCallback(response) {
                            			    $scope.citaprevia = response.data.result;
                            			  }, function errorCallback(response) {
                            			    console.log(response);
                            			  });
                            		
                            	}
                            }
                            $scope.hacerAcciones = function() {
                                // Creamos un objeto con todo lo que tenemos que pasar
                                // $uibModalInstance.close() solo permite un objeto
                                var result = {};
                                result.registro = $scope.registro;
                                result.accion = $scope.accion;
                                result.attachment = $scope.attachment;
                                result.entidadInterna = $scope.entidadInterna;
                                result.estados_internos = $scope.estados_internos;
                                result.permisos = $scope.permisos;
                                result.entidadExternaSelected = $scope.accion.entidadExternaSelected;
                                result.selectedIds = $scope.selectedIds;
                                
                                $uibModalInstance.close(result);
                            };

                            $scope.cancelar = function() {
                                $uibModalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            //Devolvemos el valor de $scope.registro (general) creando un nuevo objeto regInterno
                            regInterno: function() {
                                return $scope.$parent.registro;
                            },
                            accionesBase: function() {
                                return $scope.$parent.acciones;
                            },
                            entidadInterna: function() {
                                return $scope.$parent.entidadInterna;
                            },
                            estados_internos: function() {
                            	return $scope.$parent.estados_internos;
                            },
                            permisos: function() {
                                return $scope.$parent.permisos;
                            },
                            entidadExterna: function() {
                                return $scope.$parent.entidadExterna;
                            },
                            selectedIds: function() {
                                return $scope.$parent.selectedIds;
                            }
                            
                        }
                    });

                    modalInstance.result.then(function(result) {
                    	var fecha = "";
                        var hora = "";
                        var internalStatus = "";
                        var accion = result.accion;
                        var description = accion.description;
                        var datosCategory = "";
                        if (accion.id == 1) {
                        	
                            if (!angular.isUndefined(accion.fecha)) {
                                if (!angular.isUndefined(accion.time)) {
                                    hora = $filter('date')(accion.time, 'HH:mm') + ":00.0Z";
                                } else {
                                    hora = "00:00:00.0Z";
                                }
                                fecha = "&fecha=" + $filter('date')(accion.fecha, 'yyyy-M-dd') + "T" + hora;
                            }


                        }
                        var idAccion = accion.id;
                    	if (result.selectedIds.length <= 0) {
                    	
	                        var registro = result.registro;
	                        var attachment = result.attachment;
	                        var entidadExternaSelected = result.entidadExternaSelected;
	                        
	                        var mailExterno = "";
	                        var idExterno = "";
	                        var incluirOrden = "";
	                        
	                        if (accion.id == 8) {
	                            if (angular.isDefined(entidadExternaSelected)) {
	                                idExterno = "&idExterno=" + entidadExternaSelected.id;
	                            } else {
	                                Informer.inform("Seleccione una entidad a la que derivar la queja", "danger");
	                                return;
	                            }
	
	                            if (!angular.isUndefined(entidadExternaSelected.email)) {
	                                mailExterno = "&mailExterno=" + entidadExternaSelected.email;
	                            }
	                            if (registro.incluir_orden) {
	                                incluirOrden = "&incluir_orden=S";
	                            }
	                        }
	
	                        if (accion.id == 11) {
	                            if (angular.isDefined(registro.entidadInterna)) {
	                                idExterno = "&idInterno=" + registro.entidadInterna.id;
	                            } else {
	                                Informer.inform("Seleccione un interno al que derivar la queja", "danger");
	                                return;
	                            }
	                        }
	                        if (accion.id == 22) {
	                        	if (angular.isDefined(registro.category)) {
	                        		datosCategory = "&service_code=" + registro.category.service_code + "&agency_responsible_code=" + registro.category.agency_responsible_code;
	                        		if (registro.category.service_code == 30008) {
	                        			if (angular.isDefined(registro.expediente) && angular.isDefined(registro.year) && angular.isDefined(registro.citaprevia)) {
	                        				datosCategory = datosCategory + "&expediente=" + registro.expediente + "&year=" + registro.year + "&diaCita=" + ($filter('date')(registro.citaprevia.dia, 'yyyy-MM-dd')) + "&horaCita=" + registro.citaprevia.hora; 
	                        			} else {
	                        				Informer.inform("Debe introducir el número de expediente, el año y seleccionar día para la cita previa", "danger");
	                                    	return;
	                        			}
	                        		}
	                        	} else {
	                        		Informer.inform("Seleccione categoría", "danger");
                                	return;
	                        	}
	                        
	                        }
	                        if (attachment.length > 0) {
	                            description = description + '\n\nFicheros adjuntos al mensaje:';
	                            for (elem in attachment) {
	                                var el = attachment[elem].media_url;
	                                el = el.replace('="http:', 'http:').replace('"', '');
	                                description = description + '\n- ' + el;
	                            }
	
	                        }
	                        
	                        if (angular.isDefined(registro.cerrarSinContestar) && registro.cerrarSinContestar) {
	                        	idAccion = 12;
	                        }
	                        if (angular.isDefined(registro.internal_status) && registro.internal_status) {
	                        	internalStatus = "&internalStatus=" + registro.internal_status;
	                        }
	                        console.log(idAccion);
	                        Restangular.one("quejas-sugerencias", registro.service_request_id).customPUT("description=" + encodeURIComponent(description) + fecha + idExterno + mailExterno + incluirOrden + internalStatus + datosCategory, "acciones/" + idAccion, {}, {}).then(function(json) {
	                            Informer.inform("El registro se ha modificado correctamente.", "success");
	                            $scope.$parent.registro = json;
	                        }, function(result) {
	                            Informer.inform(result.data.error || result.data.mensaje, "danger");
	                        });
	                        
                    	} else {
                        	// Accion sobre multiple quejas
                    		for (l = 0; l < result.selectedIds.length; l++) {
                    			var req = result.selectedIds[l];
                    			setTimeout(Restangular.one("quejas-sugerencias", req.service_request_id).customPUT("description=" + encodeURIComponent(description) + fecha, "acciones/" + idAccion, {}, {}).then(function(json) {
    	                            Informer.inform("Operación realizada correctamente. Actualice el listado.", "success");
    	                        }, function(result) {
    	                            Informer.inform(result.data.error || result.data.mensaje, "danger");
    	                        }), 10000);
                    		}
                        }
                    }, function() {
                        
                    });
                };
            }
        };
    }
])
    .directive('modalEntidad', ['$uibModal', 'Restangular', 'RestangularEntidades', 'RestangularInternos', 'Informer',
        function($uibModal, Restangular, RestangularEntidades, RestangularInternos, Informer) {
            return {
                restrict: 'EA',
                scope: {
                    modelo: '=',
                    icon: '@',
                },
                template: '<button class="btn btn-default" ng-click="lanzarModalEntidad()"><span class="glyphicon {{icon}}"></span> </button>',
                controller: function($scope, $element, $attrs, $transclude) {

                    $scope.lanzarModalEntidad = function() {

                        var modalInstance = $uibModal.open({
                            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/entidadModal.html',
                            controller: function($scope, $uibModalInstance, entidadExternaSelected, tipoaccion) {
				$scope.tipoaccion = tipoaccion;                                
				$scope.accion={};
                                $scope.accion.entidadExternaSelected = entidadExternaSelected || {};
                                
                                $scope.btnModalGuardarEntidad = function() {
                                    $uibModalInstance.close($scope.accion.entidadExternaSelected);
                                };

                                $scope.btnModalCancelar = function() {
                                    $uibModalInstance.dismiss('cancel');
                                };

                            },
                            resolve: {
                                entidadExternaSelected: function() {
                                    return $scope.modelo;
                                },
                                tipoaccion: function() {
                                    return $attrs.accion;
                                }
                            }
                        });

                        modalInstance.result.then(function(result) {
                        	if ($attrs.accion == "interno") {
                        		var entidadExterna = result;
	                            if ($attrs.icon.indexOf('plus') >= 0) {
	                            	RestangularInternos.all("").post(entidadExterna, {}, {}).then(function(data) {
	                                    //TODO: Eliminar peticion GET y anyadir 'data' a sessionStorage.reqentidadinterna restangularizando el objeto
	                            		RestangularInternos.all("").getList({
	                                        results_only: false,
	                                        refresh: 'S',
	                                    }).then(function(data) {
	                                        $scope.$parent.entidadInterna = data;
	                                        sessionStorage.setItem('reqentidadinterna', JSON.stringify(data));
	                                    }, function(result) {
	                                        Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                    });
	                                    Informer.inform("El registro se ha creado correctamente.", "success");
	                                    // $location.path('/');
	                                }, function(result) {
	                                    Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                    // $location.path('/');
	                                });
	                            } else {
	                                var entidadExternaRestangular = RestangularInternos.copy(entidadExterna);
	                                RestangularInternos.one("").customPUT(entidadExternaRestangular, entidadExternaRestangular.id).then(function(data) {
	                                    Informer.inform("El registro se ha modificado correctamente.", "success");
	                                    sessionStorage.setItem('reqentidadinterna', JSON.stringify(data));
	                                }, function(result) {
	                                    Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                });
	                            }
                        	} else {
                        		var entidadExterna = result;
	                            if (angular.isUndefined(entidadExterna.id)) {
	                                RestangularEntidades.all("").post(entidadExterna, {}, {}).then(function(data) {
	                                    //TODO: Eliminar peticion GET y anyadir 'data' a sessionStorage.reqentidadexterna restangularizando el objeto
	                                    RestangularEntidades.all("").getList({
	                                        results_only: false,
	                                        refresh: 'S',
	                                    }).then(function(data) {
	                                        $scope.$parent.entidadExterna = data;
	                                        sessionStorage.setItem('reqentidadexterna', JSON.stringify(data));
	                                    }, function(result) {
	                                        Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                    });
	                                    Informer.inform("El registro se ha creado correctamente.", "success");
	                                    // $location.path('/');
	                                }, function(result) {
	                                    Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                    // $location.path('/');
	                                });
	                            } else {
	                                var entidadExternaRestangular = RestangularEntidades.copy(entidadExterna);
	                                RestangularEntidades.one("").customPUT(entidadExternaRestangular, entidadExternaRestangular.id).then(function(data) {
	                                    Informer.inform("El registro se ha modificado correctamente.", "success");
	                                    sessionStorage.setItem('reqentidadexterna', JSON.stringify(data));
	                                }, function(result) {
	                                    Informer.inform(result.data.error || result.data.mensaje, "danger");
	                                });
	                            }
                        	}

                        }, function() {

                        });
                    };
                }
            };
        }
    ])
    .directive('modalEstadistica', ['$uibModal', 'Restangular', '$location', 'Informer', '$filter', 'Query', '$route',
        function($uibModal, Restangular, $location, Informer, $filter, Query, $route) {
            return {
                restrict: 'E',
                template: '<button class="btn btn-sm btn-primary" ng-click="lanzarModalEstadistica()"><span class="fa fa-chart-bar" aria-hidden="true"></span> Estadística</button>',
                controller: function($scope, $element, $attrs, $transclude) {
                    $scope.lanzarModalEstadistica = function() {

                        var modalInstance = $uibModal.open({
                            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/estadisticaModal.html',
                            controller: function($scope, $uibModalInstance, categories) {

                                $scope.query = {};
                                $scope.categories = categories;

                                $scope.datepickers = {
                                    dateStart: false,
                                    dateEnd: false
                                };
                                $scope.toggleOpenDatePicker = function($event, which) {
                                    $event.preventDefault();
                                    $event.stopPropagation();
                                    $scope.datepickers[which] = true;
                                };

                                $scope.btnModalCargarEstadistica = function() {
                                    $uibModalInstance.close($scope.query);
                                };

                                $scope.btnModalCancelar = function() {
                                    $uibModalInstance.dismiss('cancel');
                                };

                            },
                            resolve: {
                                categories: function() {
                                    return $scope.categories;
                                }
                            }
                        });

                        modalInstance.result.then(function(query) {
                            Query.data = query;
                            $location.path('/estadistica');
                            $route.reload();
                        }, function() {

                        });
                    };
                }
            };
        }
    ])
    .directive('modalBusquedaTicketing', ['$uibModal', 'Restangular', 'Informer', '$filter', '$location',
        function($uibModal, Restangular, Informer, $filter, $location) {
            return {
                restrict: 'E',
                // scope: {
                //     accion: '=',

                // },
                // replace: true,
                template: '<button class="btn btn-sm btn-primary" ng-click="lanzarModalBusqueda()"><span class="fa fa-search" aria-hidden="true"></span> Busqueda</button>',
                controller: function($scope, $element, $attrs, $transclude) {
                    $scope.lanzarModalBusqueda = function() {

                        var modalInstance = $uibModal.open({
                            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/templates/busquedaModal.html',
                            controller: function($scope, $uibModalInstance, types, estados, origin, entidadExterna, entidadInterna, estados_internos, permisos, categories) {

                            	$scope.linkExcel = CryptoJS.HmacSHA1("excel#" + clientID + "#"+ datosUsuario.rootcat_ticketing, secretKey, {asBytes: true}).toString();
                            	$scope.linkCsv = CryptoJS.HmacSHA1("csv#" + clientID + "#"+ datosUsuario.rootcat_ticketing, secretKey, {asBytes: true}).toString();
                            	$scope.linkAvisosCsv = CryptoJS.HmacSHA1("avisoscsv#" + clientID + "#"+ datosUsuario.rootcat_ticketing, secretKey, {asBytes: true}).toString();
                            	
                                $scope.query = {};
                                $scope.types = types;
                                $scope.estados = estados;
                                $scope.origin = origin;
                                $scope.entidadExterna = entidadExterna;
                                $scope.entidadInterna = entidadInterna;
                                $scope.estados_internos = estados_internos;

                                $scope.permisos = permisos;
                                $scope.categories = categories;

                                $scope.datepickers = {
                                    dateStart: false,
                                    dateEnd: false
                                };
                                $scope.toggleOpenDatePicker = function($event, which) {
                                    $event.preventDefault();
                                    $event.stopPropagation();
                                    $scope.datepickers[which] = true;
                                };

                                $scope.btnModalbuscar = function() {
                                    // var result = {};
                                    // result.query = $scope.query;
                                    // result.types = $scope.types;
                                    // result.estados = $scope.estados;
                                    $uibModalInstance.close($scope.query);
                                };

                                $scope.btnModalCancelar = function() {
                                    $uibModalInstance.dismiss('cancel');
                                };

                            },
                            resolve: {
                                //Devolvemos el valor de $scope.registro (general) creando un nuevo objeto regInterno
                            	types: function() {
                                    return $scope.types;
                                },
                                estados: function() {
                                    return $scope.estados;
                                },
                                origin: function() {
                                    return $scope.origin;
                                },
                                entidadExterna: function() {
                                    return $scope.entidadExterna;
                                },
                                entidadInterna: function() {
                                    return $scope.entidadInterna;
                                },
                                estados_internos: function() {
                                    return $scope.estados_internos;
                                },
                                permisos: function() {
                                    return $scope.permisos;
                                },
                                categories: function() {
                                    return $scope.categories;
                                }
                            }
                        });

                        modalInstance.result.then(function(query) {
                            start = 0;
                            datosListados = [];
                            $scope.textoQuery = '';
                            if (angular.isUndefined(query.estado)) {
                                query.estado = [];
                            }
                            console.log(query);
                            Restangular.all('quejas-sugerencias').getList({
                                results_only: false,
                                rows: 50,
                                srsname: 'utm30n',
                                sort: 'requested_datetime desc',
                                start: start,
                                title: query.title,
                                service_code: angular.isDefined(query.service_code) ? query.service_code.toString() : '',
                                externo_code: query.externo_code,
                                interno_code: query.interno_code,
                                internal_status: query.internal_status,
                                start_date: $filter('date')(query.start_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                                end_date: $filter('date')(query.end_date, 'yyyy-MM-ddTHH:mm:ss\'Z\''),
                                type: angular.isDefined(query.tipo) ? query.tipo.toString() : '',
                                answer_requested: query.answer_requested,
                                barrio_code: query.barrio_code,
                                group_operator:query.group_operator,
            					operator:query.operator,
                                service_request_id: query.idInterno,
                                origin: query.origin,
                                validated: query.validated,
                                status: angular.isDefined(query.estado) ? query.estado.toString() : ''
                            }).then(function(data) {
                            	$scope.$$childTail.totalCount = data.totalCount;
                            	$scope.$$childTail.textoQuery ='';
                            	$scope.$$childTail.query = query;
                                datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                                $scope.$$childTail.registros = datosListados;
                            }, function(result) {
                                Informer.inform(result.data.error || result.data.mensaje, "danger");
                            });
                            start += 50;

                        }, function() {
                            console.log('busquedaModal cerrado');
                        });
                    };
                }
            };
        }
    ])
    .directive('botonValidacion', ['Dao', 'Restangular', 'Informer', 
        function(Dao, Restangular, Informer) {
            return {
                restrict: 'E',
//                template: '<button class="btn btn-default" ng-show="{{registro.visible==\'S\' && permisos.PUB}}" ng-click="setVisibilidad(visibilidad.valor);">' + '<span class="glyphicon" ' + 'ng-class="{\'glyphicon-eye-close\': visibilidad.valor, \'glyphicon-eye-open\':!visibilidad.valor}">' + '</span><span class="hidden-xs">' + ' {{visibilidad.texto}}' + '</span></button>',
                template: '<button class="btn btn-default" ng-show="{{registro.visible==\'S\' && permisos.PUB}}" ng-click="setVisibilidad(\'S\');" title="Publicar">' + '<span class="fa fa-eye" aria-hidden="true">' + '</span></button> <button class="btn btn-default" ng-show="{{registro.visible==\'S\' && permisos.PUB}}" ng-click="setVisibilidad(\'R\');" title="Ocultar">' + '<span class="fa fa-eye-slash" aria-hidden="true">' + '</span> </button>',
                link: function(scope, elem, attrs) {
                    // Si no viene definida...
                    if (angular.isUndefined(scope.registro)) {
                        scope.registro = {};
                        scope.registro.validated = '';
                    };

                    scope.visibilidad = {
                        valor: scope.registro.validated === 'S' ? true : false,
                        texto: scope.registro.validated === 'S' ? ' Ocultar' : ' Mostrar'
                    }
                    scope.setVisibilidad = function(val) {

                        var temp = Restangular.restangularizeElement(null, {
                            id: scope.registro.service_request_id,
                            visible: scope.registro.visible,
                            validated: scope.registro.validated === val
                        }, scope.registro.route);

                        
                        var queryString = 'visible=S&validated=' + val;  
                        
                        Restangular.one("quejas-sugerencias", scope.registro.service_request_id).customPUT(queryString, "", {}, {}).then(function(data) {
                            Informer.inform("El registro se ha modificado correctamente.", "success");
//                            scope.visibilidad.texto = data.validated === 'S' ? ' Ocultar' : ' Mostrar';
                            scope.registro = data;
                        }, function(result){
                         Informer.inform(result.data.error || result.data.mensaje, "danger");
                        });

                    };

                }
            };
        }
    ])
    .directive('botonSubirFichero', ['Dao', 'Restangular', 'Informer',
        function(Dao, Restangular, Informer) {
            return {
                restrict: 'E',
                template: '<button class="btn btn-default" ng-click="subirFichero();">Subir</button>',
                link: function($scope, elem, attrs) {
                    $scope.files = [];

                    $scope.$on("fileSelected", function(event, args) {
                        $scope.$apply(function() {
                        	$scope.files = $scope.files || [];
                            $scope.files.push(args.file);
                        });
                    });

                    $scope.subirFichero = function() {
                        var j = 0;
                        var k = 0;
                        for (var i = 0; i < $scope.files.length; i++) {

                            var reader = new FileReader();
                            reader.readAsDataURL($scope.files[i]);
                            reader.onload = function(event) {
                                var title = $scope.files[j].name.replace(/[^a-z0-9\.]/gi, '_').toLowerCase();
                                var len = title.indexOf(".");
                                if (len > 30) {
                                    len = 30;
                                }
                                var description = title.substring(0, len);
                                j++;
                                Restangular.one("quejas-sugerencias", $scope.registro.service_request_id).customPUT("media_name=" + title + "&media_description=" + description + "&media_body=" + encodeURIComponent(event.target.result), "", {}, {}).then(function(json) {

                                    if ($scope.files.length <= 1) {
                                    	try {
                                        document.getElementById("ficheros_Adj").form.reset();
                                    	} catch (e) {
                                    		;
                                    	}
                                        $("#ficheros_Adj").val("");
                                        $scope.files = [];
                                        $("#listaUpload").empty();
                                        delete $scope.files;
                                    } else {
                                        delete $scope.files[k];
                                    }
                                    k++;
                                    Informer.inform("El registro se ha modificado correctamente.", "success");
                                    $scope.registro = json;
                                }, function(result) {
                                    Informer.inform(result.data.error || result.data.mensaje, "danger");
                                });

                            };

                        };
                    };

                }
            };
        }
    ]);
