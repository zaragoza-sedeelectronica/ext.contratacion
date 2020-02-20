angular.module('app.directives', [])

.directive('modalPeticion', ['$uibModal', '$http','$filter', 'Informer', function($uibModal, $http, $filter, Informer){
    return {
        link: function($scope, iElm, iAttrs, controller) {

            $scope.lanzarModalPeticion = function() {

                var modalInstance = $uibModal.open({
                    templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/templates/modalPeticion.html',
                    controller: function($scope, $uibModalInstance, registro) {
                        $scope.registro = angular.copy(registro.originalElement);

                        $scope.enviarPeticion = function() {
                            // var mensajePeticion = '<h1>' + $scope.registro.id + ' : ' + $scope.registro.title + '</h1>' + $scope.comentario;
                            var mensajePeticion = 'dfsdfsf'
                            $uibModalInstance.close(mensajePeticion);
                        };

                        $scope.cancelarModal = function() {
                            $uibModalInstance.dismiss('cancel');
                        };
                    },
                    size: 'lg',
                    resolve: {
                        registro: function() {
                            return $scope.registro;
                        }
                    }
                });

                modalInstance.result.then(function(mensajePeticion) {
                    $http.post('/sede/servicio/cultura/evento/admin/support', mensajePeticion).then(function(res) {
                        Informer.inform(res.data.mensaje, 'success');
                    }, function(res){
                        Informer.inform('Error en el envío: ' + res.data.mensaje, 'danger');
                    });
                }, function() {
                    //console.log('Modal dismissed at: ' + new Date());
                });
            };

        },
        restrict: 'E',
        template: '<button ng-show="permisos.MAIL && location !== \'/new\'" type="button" class="btn btn-default" ng-click="lanzarModalPeticion()"><span class="fa fa-comment" aria-hidden="true"></span> <span class="hidden-xs"> Petición</span></button>',
    };
}])
.directive('modalLugar', ['$uibModal', '$http', '$filter', function($uibModal, $http, $filter){
    return {
        link: function($scope, iElm, iAttrs, controller) {

            $scope.lanzarModalLugar = function(data, nuevoLugar, index) {
                // if nuevoLugar = true, estamos agregando un nuevo lugar(typeahead) por que lugar = data
                // else nuevoLugar = false, estamos editando un lugar ya seleccionado al acto

                // var subEvent = set<ActoLugar>
                var subEvent = new Object();
                var hInicio, hFinal;
                var hApertura, hCierre;
                subEvent.id = nuevoLugar ? undefined : data.id || undefined;
                subEvent.location = nuevoLugar ? data : data.location;
                subEvent.horario = data.horario || '';
                subEvent.comment = data.comment || '';
                subEvent.startDate = data.startDate || '';
                subEvent.endDate = data.endDate || '';
                // subEvent.startDate = data.startDate ? new Date(data.startDate) : undefined;
                // subEvent.endDate = data.endDate ? new Date(data.endDate) : undefined;
                subEvent.horaInicio = data.horaInicio || undefined;
                subEvent.horaFinal = data.horaFinal || undefined;
                // Evitamos recuperar en el modelo todos los espacios al seleccionar nuevo lugar
                subEvent.space = nuevoLugar ? undefined : data.space || undefined;
                //subEvent.space = data.space || undefined;

                subEvent.openingHours = data.openingHours || [];

                var modalInstance = $uibModal.open({
                    templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/templates/modalHorario.html',
                    controller: function($scope, $uibModalInstance, subEvent, permisos) {
                        $scope.permisos = permisos;
                        // $scope.subEvent en el modal
                        $scope.subEvent = subEvent;
                        $scope.horasNuevoCierre = false;
                        $scope.endTimeBoolean = 'N';
                        $scope.horarioSelecc = [];

                        $scope.dateOptions = {startingDay: 1};
                        $scope.horarios = [
                            {'id': 0, 'dayOfWeek':'Todos los días', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 1, 'dayOfWeek':'Lunes', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 2, 'dayOfWeek':'Martes', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 3, 'dayOfWeek':'Miércoles', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 4, 'dayOfWeek':'Jueves', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 5, 'dayOfWeek':'Viernes', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 6, 'dayOfWeek':'Sábado', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 7, 'dayOfWeek':'Domingo', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 8, 'dayOfWeek':'Lunes a Viernes', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 9, 'dayOfWeek':'Fin de semana', 'startTime': new Date(), 'endTime': new Date()},
                            {'id': 10, 'dayOfWeek':'Sin determinar', 'startTime': new Date(), 'endTime': new Date()}
                        ];

                        // $scope.espacios = [];
                        // $scope.disableSpaceList = true;
                        // var id = $scope.subEvent.location.id;
                        // var url = 'https://www.zaragoza.es/sede/servicio/equipamiento/' + id
                        // $http.get(url, {}).then(function(res) {
                        //     if(angular.isDefined(res.data.space)) {
                        //         $scope.espacios = res.data.space;
                        //         $scope.disableSpaceList = false;
                        //     };
                        // });

                        // Selecciona automáticamente el día según la fecha fin de realización.
                        $scope.dateSelected = function(date){
                            var dia = $filter('date')(date, 'EEEE');
                            dia = dia.charAt(0).toUpperCase() + dia.slice(1);
                            var index;
                            _.find($scope.horarios, function(h, idx){ if(h.dayOfWeek == dia){ index = idx; return true;}; });
                            $scope.horarioSelecc = []; // Reiniciamos array por si había alguna fecha ya marcada previamente
                            $scope.horarioSelecc.push($scope.horarios[index]);
                        };

                        if(angular.isDefined($scope.subEvent.endDate)){
                            $scope.dateSelected($scope.subEvent.endDate);
                        };
                        if(angular.isDefined($scope.subEvent.horaInicio)){
                            $scope.startTime = new Date();
                            $scope.startTime.setHours($scope.subEvent.horaInicio.split(":")[0]);
                            $scope.startTime.setMinutes($scope.subEvent.horaInicio.split(":")[1]);
                        };
                        if(angular.isDefined($scope.subEvent.horaFinal)){
                            $scope.endTime = new Date();
                            $scope.endTime.setHours($scope.subEvent.horaFinal.split(":")[0]);
                            $scope.endTime.setMinutes($scope.subEvent.horaFinal.split(":")[1]);
                            $scope.endTimeBoolean = 'S';
                        }; 

                        $scope.crearTabla = function(horariosSelecc, startTime, endTime, endTimeBoolean, maximumCapacity){
                            var temp = {};
                            // Recorremos los horarios seleccionados
                            _.each(horariosSelecc, function(horario){
                                temp = {};
                                angular.copy(horario, temp);
                                //Convertimos horas de openingHours (Date() -> HH:mm)
                                temp.startTime = $filter('date')(startTime, 'HH:mm');
                                temp.endTime = $filter('date')(endTime, 'HH:mm');
                                // Al crear sesión: plazas totales = plazas disponibles
                                if(maximumCapacity) { // Si maximumCapacity está definido
                                    temp.maximumCapacity = maximumCapacity;
                                    temp.remainingCapacity = maximumCapacity; 
                                };
                                // Actualizamos horario calendario antigua gestión
                                $scope.subEvent.horaInicio = temp.startTime;
                                $scope.subEvent.horaFinal = temp.endTime;
                                // Si el checkbox no está seleccinado no borramos endTime
                                if(endTimeBoolean === 'N'){
                                    delete temp.endTime;
                                    delete $scope.subEvent.horaFinal; // Actualizamos horario calendario antigua gestión
                                };
                                // Generamos tabla de nuevos horarios
                                switch(temp.id) {
                                    case 0:
                                        for(var i = 1; i <= 7; i++){
                                            // delete temp.id;
                                            var obj = {
                                                dayOfWeek: $scope.horarios[i].dayOfWeek,
                                                startTime: temp.startTime,
                                                endTime: temp.endTime
                                            };
                                            if(maximumCapacity) { // Si maximumCapacity está definido
                                                obj.maximumCapacity = maximumCapacity;
                                                obj.remainingCapacity = maximumCapacity; 
                                            };
                                            subEvent.openingHours.push(obj);
                                        };
                                        break;
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                        delete temp.id;
                                        subEvent.openingHours.push(temp);
                                        break;
                                    case 8:
                                        for(var i = 1; i <= 5; i++){
                                            // delete temp.id;
                                            var obj = {
                                                dayOfWeek: $scope.horarios[i].dayOfWeek,
                                                startTime: temp.startTime,
                                                endTime: temp.endTime,
                                            };
                                            if(maximumCapacity) { // Si maximumCapacity está definido
                                                obj.maximumCapacity = maximumCapacity;
                                                obj.remainingCapacity = maximumCapacity; 
                                            };
                                            subEvent.openingHours.push(obj);
                                        };
                                        break;
                                    case 9:
                                        for(var i = 6; i <= 7; i++){
                                            // delete temp.id;
                                            var obj = {
                                                dayOfWeek: $scope.horarios[i].dayOfWeek,
                                                startTime: temp.startTime,
                                                endTime: temp.endTime,
                                            };
                                            if(maximumCapacity) { // Si maximumCapacity está definido
                                                obj.maximumCapacity = maximumCapacity;
                                                obj.remainingCapacity = maximumCapacity; 
                                            };
                                            subEvent.openingHours.push(obj);    
                                        };
                                        break;
                                    case 10:
                                        delete temp.id;
                                        subEvent.openingHours.push(temp);
                                        break;
                                };
                            });
                            $scope.regenerarCampoHorario();
                        };

                        $scope.borrarHorario = function(index) {
                            $scope.subEvent.openingHours.splice(index, 1);
                            $scope.regenerarCampoHorario();
                        };

                        $scope.agregarHorario = function() {
                            $uibModalInstance.close($scope.subEvent);
                        };

                        $scope.regenerarCampoHorario = function() {
                            // Añadiamos al campo de horario (texto) los horarios de la nueva gestión
                            if($scope.subEvent.horario.length === 0 || $scope.subEvent.horario.indexOf('lista-horario') > 0) {$scope.subEvent.horario = '<ul class="lista-horario">';};
                            _.each($scope.subEvent.openingHours, function(oh) {
                                $scope.subEvent.horario += '<li>' + oh.dayOfWeek + ', ' + oh.startTime + ((angular.isDefined(oh.endTime)) ? ' a ' + oh.endTime : '') + ' h.</li>';
                            });
                            $scope.subEvent.horario = $scope.subEvent.horario.replace(/:00/g,'').replace(/:/g,'.') // Convertir: 10:00 > 10 h. y 11:30 -> 11.30 h.
                            if($scope.subEvent.horario.indexOf('lista-horario') > 0) {$scope.subEvent.horario += '</ul>';};
                        };

                        $scope.cancelarModal = function() {
                            $uibModalInstance.dismiss('cancel');
                        };

                        $scope.datepickers = {dateStart: false, dateEnd: false};
                        $scope.toggleOpenDatePicker = function($event, which) {
                            $event.preventDefault();
                            $event.stopPropagation();
                            $scope.datepickers[which] = true;
                        };
                    },
                    size: 'lg',
                    resolve: {
                        subEvent: function() {
                            return subEvent; // set<ActoLugar>
                        },
                        permisos: function(){
                            return $scope.permisos;
                        }
                    }
                });

                modalInstance.result.then(function(selectedItem) {
                    // Si no hay ningun subEvent previamente definido
                    // en el detalle del acto
                    if (angular.isUndefined($scope.registro.subEvent)) {
                        $scope.registro.subEvent = [];
                    };
                    // Hacemos copia del obj para modificar el tipo de literal de las horas
                    var subEvento = {};
                    angular.copy(selectedItem, subEvento);
                    subEvento.horaInicio = $filter('date')(subEvento.horaInicio, 'HH:mm');
                    subEvento.horaFinal = $filter('date')(subEvento.horaFinal, 'HH:mm');
                    // Si estamos creando un nuevo registro de LugarRealizacion (subEvent)
                    if (nuevoLugar) {
                        $scope.registro.subEvent.push(subEvento);
                    } else {
                        $scope.registro.subEvent[index] = subEvento;
                    };
                    // Añadimos al campo location a nivel de actos los titulos de los equipamientos añadidos
                    var equipamientos;
                    _.each($scope.registro.subEvent, function(subEvent){
                        if(equipamientos === undefined) {
                            equipamientos = subEvent.location.title;
                        } else {
                            equipamientos += ', ' + subEvent.location.title;
                        };
                    });
                    $scope.registro.location = equipamientos;
                }, function() {
                    //console.log('Modal dismissed at: ' + new Date());
                });
            };

        }
    };
}])
.directive('btnValidar', ['Dao', '$location' , '$route', function(Dao, $location, $route){
    return {
        template: '<button ng-show="permisos.VAL" class="btn btn-sm btn-primary" ng-click="listarPendientesValidacion()"><span class="fa fa-inbox" aria-hidden="true"></span> Pendientes de validación <span class="badge">{{numPendientes}}</span></button>',
        link: function($scope, iElm, iAttrs, controller) {

            // Ejecución inicial
            if($scope.permisos.VAL) {
                Dao.listar('evento/admin/list', cargarParams()).then(function(data) {$scope.numPendientes = data.totalCount;});
                // Muestra el núm de actividades pendientes de validación en el botón
                $scope.actualizarPendientesValidacion = function(){
                    Dao.listar('evento/admin/list', cargarParams()).then(function(data) {$scope.numPendientes = data.totalCount;});
                };
            };

            // Acción del botón: Devuelve el listado (ListCtrl) con las actividades pendientes de validación
            $scope.listarPendientesValidacion = function(){
                Dao.setFiltros(cargarParams())
                if($location.path() == '/'){ // ListCtrl
                    $route.reload();
                } else {
                    $location.path('/'); // DetailCtrl
                };
            };

            function cargarParams(){
                var params = {};
                params.start = 0;
                params.rows = 50;
                params.q = [];
                // Las actividades con validacion==S son las pendientes de validar,
                // las que tienen novisible==S son no publicadas, se localizan a través de la búsqueda avanzada.
                params.q.push({'key':'validacion', 'value': 'S'});
                // Si no tiene permiso ADMIN solo devuelve las pendientes de validación del portal correspondiente al usuario
                
                return params;
            };
            
        }
    };
}])
.directive('modalBusquedaAgenda', ['$route','$uibModal', 'Dao', 'poblacion', '$location', 'formatoActividad', 'portales',
    function($route, $uibModal, Dao, poblacion, $location, formatoActividad, portales){
        return {
            template: '<button class="btn btn-primary btn-sm" ng-show="permisos.DET" ng-click="modalBusqueda()">'
                   + '<span class="fa fa-search" aria-hidden="true"></span>'
                   + '<span class="hidden-xs"> Búsqueda Avanzada</span>'
                   + '</button>',
            replace: true,
            link: function($scope, elem, attrs, controller) {

                $scope.modalBusqueda = function() {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/templates/modalBusqueda.html',
                        controller: function($scope, $uibModalInstance, permisos, poblacion, formatoActividad, portales) {
                            $scope.query = {};
                            $scope.permisos = permisos;
                            $scope.poblacion = poblacion;
                            $scope.formatoActividad = formatoActividad;
                            $scope.portales = portales;
                            $scope.query.validacion='N';
                            $scope.datepickers = {startDate: false, endDate: false};
                            $scope.toggleOpenDatePicker = function($event, which) {
                                $event.preventDefault();
                                $event.stopPropagation();
                                $scope.datepickers[which] = true;
                            };

                            $scope.busqueda = function() {
                               $uibModalInstance.close($scope.query);
                            };
                            $scope.cancelar = function() {
                               $uibModalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            permisos: function(){
                                return $scope.permisos;
                            },
                            poblacion: function() {
                                return poblacion;
                            },
                            formatoActividad: function() {
                                return formatoActividad;
                            },
                            portales: function(){
                                return portales;
                            },
                        }
                    });

                    modalInstance.result.then(function(qsearch) {
                        // if(qsearch['poblacion'].length === 0){delete qsearch['poblacion'];}
                        var params = {};
                        params.start = 0;
                        params.rows = 75;
                        params.sort = 'lastUpdated desc';
                        params.q = [];
                        _.each(qsearch, function(value, key){
                            var item = new Object();
                            item.key = key; // Campo de busqueda. Ej: 'title'
                            if(key === 'programa') {item.key = 'program.title'};
                            if(key === 'programahistorico') {item.key = 'programa'};
                            if(key === 'entidad') {item.key = 'organizer.id'};
                            if(key === 'temas') {item.key = 'category.id'};
                            if(key === 'poblacion') {item.key = 'population.id'};
                            if(key === 'type' && value.indexOf('Exhibición') !== -1) {
                                value = 'Exhibición' // Eliminamos 'Exhibición, proyección, competición' para que las comas no afecten a FIQL
                            }
                            item.value = value; // Valor del campo de busqueda actual
                            if(key === 'diasParaTerminar') { 
                                item = {
                                    key: 'daysLeft',
                                    op: '=lt=',
                                    value: '0'
                                };
                            }
                            if(key === 'startDate') { 
                                item = {
                                    key: 'startDate',
                                    op: '=ge=',
                                    value: new Date(value).toISOString()
                                };
                            }
                            if(key === 'endDate') { 
                                item = {
                                    key: 'endDate',
                                    op: '=le=',
                                    value: new Date(value).toISOString()
                                };
                            }
                            if(key === 'minAge') { 
                                item = {
                                    key: 'minAge',
                                    op: '=ge=',
                                    value: value
                                };
                            }
                            if(key === 'max') { 
                                item = {
                                    key: 'maxAge',
                                    op: '=le=',
                                    value: value
                                };
                            }
                            if(key === 'lugar') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                                params.idLugar = parseInt(item.value);
                                item.value = '';
                            }
                            if(key === 'validacion') {
                            	if (value !== 'N') {
                            		console.log('reinicia');
                            		item.value = '';
                            	} else {
	                            	item = {
	                                        key: 'validacion',
	                                        op: '==',
	                                        exact:true,
	                                        value: 'N'
	                                }
                            	}
                            }
                            if(key === 'novisible') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                            	item = {
                                        key: 'novisible',
                                        op: '==',
                                        exact:true,
                                        value: 'S'
                                };
                            }
                            if(key === 'portal') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                                params.idPortal = parseInt(item.value);
                                item.value = '';
                            }
                            if(item.value !== '') {
                            	params.q.push(item);
                            }
                            delete qsearch[key];
                        });

                        Dao.setFiltros(params);

                        if(_.find(params.q, function(obj) { return obj.key === 'busquedaHistorico' })) {
                            if($location.path() == '/historic'){ // ListCtrl
                                $route.reload();
                            } else {
                                $location.path('/historic'); // DetailCtrl
                            }
                        } else {
                            if($location.path() == '/'){ // ListCtrl
                                $route.reload();
                            } else {
                                $location.path('/'); // DetailCtrl
                            }   
                        }
                        
                        
                        /*
                        var datosListados = [];
                        Dao.listar(attrs.path, params).then(function(data) {
                            datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                            $scope.$$childTail.registros = data;
                            $scope.$$childTail.totalCount = data.totalCount;
                            // $scope.busy = false;
                            delete params.q;
                            Dao.limpiarFiltros();
                            $location.path('/'); // Volvemos al listado
                       });*/
                    }, function() {
                       // console.info('Modal dismissed at: ' + new Date());
                    });
               };

            }
        };
    }
])
.directive('modalMonitores', ['$uibModal', '$http', '$filter', 'Informer', function($uibModal, $http, $filter, Informer){
    return {
        template: '<button type="button" class="btn btn-default" ng-click="lanzarModalMonitores()">'
                   + '<span class="far fa-address-card" aria-hidden="true"></span>'
                   + '<span class="hidden-xs"> Monitor</span>'
                   + '</button>',
        //replace: true,
        link: function($scope, iElm, iAttrs, controller) {

            $scope.lanzarModalMonitores = function() {
                
                var modalInstance = $uibModal.open({
                    templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/templates/modalMonitores.html',
                    controller: function($scope, $uibModalInstance, acto) {
                        $scope.acto = acto;
                        
                        $scope.asociarMonitor = function(email){
                       	
                        	var monitorAsociado = _.find($scope.acto.monitores, function(item) {
                                return _.includes(item.email, email);
                            });
                        	if (monitorAsociado) {
                        		alert('El monitor ya está asociado a la actividad');
                        	} else {
	                            $http.post('/sede/servicio/cultura/evento/admin/'+ acto.id +'/monitor/save', {'email': email}).then(function(res) {
	                                //Informer.inform(res.data.mensaje, 'success');
	                                if(angular.isUndefined($scope.acto.monitores)) {
	                                    $scope.acto.monitores = new Array();
	                                }
	                                $scope.acto.monitores.push(res.data);
	                            }, function(res){
	                                alert('Persona no encontrada');
	                            });
                        	}
                        };

                        $scope.eliminarMonitor = function(idMonitor){
                            $http.delete('/sede/servicio/cultura/evento/admin/'+ $scope.acto.id +'/monitor/' + idMonitor + '/remove', {}).then(function(res) {
                                $scope.acto.monitores = _.reject($scope.acto.monitores, function(d){ return d.id === idMonitor; });
                            }, function(res){
                                alert('Error al borrar monitor');
                            });
                        };

                        $scope.cancelarModal = function() {
                           $uibModalInstance.dismiss('cancel');
                        };       
                    },
                    size: 'lg',
                    resolve: {
                        acto: function() {
                            return $scope.registro;
                        },
                    }
                });

                modalInstance.result.then(function(selectedItem) {
                   
                }, function() {
                    //console.log('Modal dismissed at: ' + new Date());
                });
            };

        }
    };
}]);
