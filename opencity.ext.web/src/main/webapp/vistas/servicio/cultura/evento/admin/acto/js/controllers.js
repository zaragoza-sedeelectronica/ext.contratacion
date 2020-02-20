angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao', 'portales', 
    function($scope, $window, $location, $filter, Informer, $route, Dao, portales) {

        $scope.listar = function() {
            Dao.limpiarFiltros();
            $scope.checkList = [];
            if($location.path() == '/'){ // ListCtrl
                $route.reload();
            } else {
                $location.path('/'); // DetailCtrl
            }
        };
        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            }
            $location.path('/new');
        };
        $scope.destacadas = function() {
            $location.path('/featured');
        };
        $scope.revisar = function() {
            $location.path('/check');
        };
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;
        $scope.checkList = []; // Array checkbox multiple (listado)

        sessionStorage.setItem('portales', JSON.stringify(portales));

    }

])

.controller('HistoricCtrl', ['$scope', 'Restangular', 'Dao', '$location', 'Informer', '$timeout',
    function($scope, Restangular, Dao, $location, Informer, $timeout) {
        var params = Dao.getFiltros(); // Object
        // if(angular.equals({}, params)){
            params.start = 0;
            params.sort = 'endDate desc';
        // };
        $scope.filtrosParaListado = params.q;

        $scope.registros = [];
        
        params.q = _.filter(params.q, function(obj) { return obj.key !== 'busquedaHistorico' });

        Dao.listar('evento/historico', params).then(function(data) {
            $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
            if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
        }, function(err){
            if(params.q !== null){
                Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
            } else {
                Informer.inform("Error en listado: " + err.data.error, "danger");
            };
        });

        $scope.copiarDeHistorico = function(reg){
            copiaRegistro = Restangular.copy(reg);
            if(angular.isDefined(copiaRegistro.id)){delete copiaRegistro.id;};
            if(angular.isDefined(copiaRegistro.creationDate)){delete copiaRegistro.creationDate;};
            if(angular.isDefined(copiaRegistro.lastUpdate)){delete copiaRegistro.lastUpdate;};
            if(angular.isDefined(copiaRegistro.codHistorico)){delete copiaRegistro.codHistorico;};

            if(angular.isDefined(copiaRegistro.programa)){
                Dao.listar('evento/programa', {'title':copiaRegistro.programa}).then(function(res){
                    if(res.plain().length > 0) {
                        copiaRegistro.program = res.plain()[0];
                    }
                });
                delete copiaRegistro.programa;
            }
            if(angular.isDefined(copiaRegistro.codSector)){
                copiaRegistro.population = [{'id':copiaRegistro.codSector}];
                delete copiaRegistro.codSector;
            }
            if(angular.isDefined(copiaRegistro.codTema)){
                copiaRegistro.category = [{'id':copiaRegistro.codTema}];
                delete copiaRegistro.codTema;
            }

            // Lugar, fecha y horario
            if(angular.isDefined(copiaRegistro.codEquipamiento)){
                Dao.listar('evento/lugar-realizacion', {'id':copiaRegistro.codEquipamiento}).then(function(res){
                    var subEvent = {};
                    if(res.plain().length > 0) {
                        subEvent.location = res.plain()[0];
                        // En la tabla historico, los campos de ACTO_LUGAR se almacenan a nivel de tabla HISTORICO
                        if(angular.isDefined(copiaRegistro.startDate)){
                            subEvent.startDate = copiaRegistro.startDate;
                        }
                        if(angular.isDefined(copiaRegistro.endDate)){
                            subEvent.endDate = copiaRegistro.endDate;
                        }
                        if(angular.isDefined(copiaRegistro.horaInicio)){
                            subEvent.horaInicio = copiaRegistro.horaInicio;
                        }
                        if(angular.isDefined(copiaRegistro.horaFinal)){
                            subEvent.horaFinal = copiaRegistro.horaFinal;
                        }
                        if(angular.isDefined(copiaRegistro.horario)){
                            subEvent.horario = copiaRegistro.horario;
                        }
                        if(angular.isDefined(copiaRegistro.comentarios)){
                            subEvent.comment = copiaRegistro.comentarios;
                        }
                        copiaRegistro.subEvent = new Array();
                        copiaRegistro.subEvent.push(subEvent);
                    }
                    delete copiaRegistro.codEquipamiento;
                }, function(err) {
                    Informer.inform("Error al recuperar lugar de realización de histórico", "danger");
                });
            }
            // Eliminamos campos de subEvent
            delete copiaRegistro.horaInicio;
            delete copiaRegistro.horaFinal;
            delete copiaRegistro.horario;
            delete copiaRegistro.comentarios;

            if(angular.isDefined(copiaRegistro.price)){
                copiaRegistro.priceComment += '<br/>' + copiaRegistro.price;
                delete copiaRegistro.price;
            }

            if(angular.isDefined(copiaRegistro.tipoEntrada) && copiaRegistro.tipoEntrada === 'libre'){
                var price = {
                    'hasCurrencyValue': 0.00,
                    'hasCurrency': 'EUR',
                    'fareGroup': 'Gratuita',
                    'minSize': '1'
                };
                copiaRegistro.price = new Array();
                copiaRegistro.price.push(price);
                delete copiaRegistro.tipoEntrada;
            }

            if(angular.isDefined(copiaRegistro.codEntidad)){
                Dao.listar('evento/entidad', {'id':copiaRegistro.codEntidad}).then(function(res){
                    if(res.plain().length > 0) {
                        var organizer = res.plain()[0];
                        copiaRegistro.organizer = new Array();
                        copiaRegistro.organizer.push(organizer);
                    };
                }, function(err) {
                    Informer.inform("Error al recuperar entidad de histórico", "danger");
                });
                delete copiaRegistro.codEntidad;
            }

            var registration = new Object();
            if(angular.isDefined(copiaRegistro.registrationTitle)){
                registration.title = copiaRegistro.registrationTitle;
                delete copiaRegistro.registrationTitle;
            }
            if(angular.isDefined(copiaRegistro.registrationDescription)){
                registration.description = copiaRegistro.registrationDescription;
                delete copiaRegistro.registrationDescription;
            }
            if(angular.isDefined(copiaRegistro.registrationStreetAddress)){
                registration.streetAddress = copiaRegistro.registrationStreetAddress;
                delete copiaRegistro.registrationStreetAddress;
            }
            if(angular.isDefined(copiaRegistro.registrationOpeningHours)){
                registration.openingHours = copiaRegistro.registrationOpeningHours;
                delete copiaRegistro.registrationOpeningHours;
            }
            if(angular.isDefined(copiaRegistro.registrationTelephone)){
                registration.telephone = copiaRegistro.registrationTelephone;
                delete copiaRegistro.registrationTelephone;
            }
            if(angular.isDefined(copiaRegistro.registrationFaxNumber)){
                registration.faxNumber = copiaRegistro.registrationFaxNumber;
                delete copiaRegistro.registrationFaxNumber;
            }
            if(angular.isDefined(copiaRegistro.registrationUrl)){
                registration.url = copiaRegistro.registrationUrl;
                delete copiaRegistro.registrationUrl;
            }
            if(angular.isDefined(copiaRegistro.registrationEmail)){
                registration.email = copiaRegistro.registrationEmail;
                delete copiaRegistro.registrationEmail;
            }
            if(angular.isDefined(copiaRegistro.registrationStartDate)){
                registration.startDate = copiaRegistro.registrationStartDate;
                delete copiaRegistro.registrationStartDate;
            }
            if(angular.isDefined(copiaRegistro.registrationEndDate)){
                registration.endDate = copiaRegistro.registrationEndDate;
                delete copiaRegistro.registrationEndDate;
            }
            if(angular.isDefined(copiaRegistro.registrationRequisitosCanjeo)){
                registration.requisitosCanjeo = copiaRegistro.registrationRequisitosCanjeo;
                delete copiaRegistro.registrationRequisitosCanjeo;
            }
            if(angular.isDefined(copiaRegistro.registrationFechaInicioCanjeo)){
                registration.fechaInicioCanjeo = copiaRegistro.registrationFechaInicioCanjeo;
                delete copiaRegistro.registrationFechaInicioCanjeo;
            }
            if(angular.isDefined(copiaRegistro.registrationFechaFinCanjeo)){
                registration.fechaFinCanjeo = copiaRegistro.registrationFechaFinCanjeo;
                delete copiaRegistro.registrationFechaFinCanjeo;
            }
            if(!_.isEmpty(registration)){ // si no está vacío
                copiaRegistro.registration = registration;
            }

            // Damos tiempo a resolver las promises
            $timeout(function() {
                console.log(copiaRegistro);
                $location.path('/new');
            }, 1000);
            
        };

    }
])

.controller('ListCtrl', ['$scope', 'Restangular', '$http', 'Dao', '$location', 'Informer', 'portales',
    function($scope, Restangular, $http, Dao, $location, Informer, portales) {

        $scope.portales = portales;
        
        var params = Dao.getFiltros(); // Object
        if(angular.equals({}, params)){
            params.start = 0;
            params.rows = 50;
            params.sort = 'lastUpdated desc';
        }
        $scope.filtrosParaListado = params.q;

        // Directiva th-orden
        $scope.order = 'lastUpdated';
        $scope.reverse = true;

        $scope.registros = [];
        $scope.busy = false;

        // pagination
        $scope.viewby = 50;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        };

        // Si params.q está definido venimos de búsqueda
        if(params.q) {
            loadMore();
        }
        function loadMore() {
            
            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            //if(params.q && !($scope.start >= $scope.totalCount) && !($scope.totalCount < 50)){
                //if ($scope.busy) return;
                //$scope.busy = true;

                Dao.listar('evento/admin/list', params).then(function(data) {
                    //$scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                    $scope.registros = data;
                    if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
                    $scope.totalCount = data.totalCount;
                    $scope.start = data.start;
                    //$scope.busy = false;
                }, function(err){
                    if(params.q !== null){
                        Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                    } else {
                        Informer.inform("Error en listado: " + err.data.error, "danger");
                    };
                });   
                //params.start += 50;
            //};
        };

        $scope.pageChanged = function() {
            loadMore();
        };

        $scope.qsearch = {}; // Inicializamos el objeto para evitar undefined en el select. Bug?
        $scope.buscar = function(qsearch) {
            $scope.registros = [];

            var params = {};
            params.start = 0;
            params.rows = 50; // Número de resultados totales en la búsqueda
            params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key){
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                if(key === 'tema') {item.key = 'category.id'};
                if(key === 'entidad') {item.key = 'organizer.id'};
                if(key === 'type' && value.indexOf('Exhibición') !== -1) {
                    value = 'Exhibición' // Eliminamos 'Exhibición, proyección, competición' para que las comas no afecten a FIQL
                };
                item.value = value; // Valor del campo de busqueda actual
                if(key === 'lugar') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                    params.idLugar = parseInt(item.value);
                    item.value = '';
                };
                if(key === 'portal') { // Búsqueda manual por filter en el controlador de sede (no FIQL)
                    params.idPortal = parseInt(item.value);
                    item.value = '';
                };
                if(item.value !== '') {params.q.push(item);}; // Si esta vacío el valor no se añade
                delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('evento/admin/list', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if(data.totalCount == 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");};
                // Si devuelve un único resultado, se envía directamente al detalle.
                if(data.totalCount === 1){
                    $location.path('/edit/' + data[0].id);
                } else {
                    $scope.registros = data;
                }
            });
        };

        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });
        $scope.desasociarPortal = function(reg) {
            Restangular.all('evento/admin').one('review', reg.id).one('remove', portalUsuario.id).remove().then(function(res) {
            	var index = $scope.registros.indexOf(reg);
            	console.log(index);
            	$scope.registros.splice(index, 1);
                Informer.inform(res.mensaje, 'success');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            });
        };
        
        // Petición para mandar actividades caducadas a histórico
        if($scope.permisos.ADMIN) {
            $http.put('/sede/servicio/cultura/mover-historico', {}).then(function(res) {
                //Informer.inform("Actividades caducadas movidas correctamente a histórico", "success");
            }, function(err) {
                Informer.inform("Error al mover a histórico las actividades caducadas", "danger");
            });
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', '$http', 'registro', 'poblacion', 'valores', 'portales', 'origenes', 'Restangular','$location', '$route', 'Informer', '$filter', 'tiposPrecio', 'formatoActividad', '$window', '$timeout', 
    function($scope, Dao, $http, registro, poblacion, valores, portales, origenes, Restangular, $location, $route, Informer, $filter, tiposPrecio, formatoActividad, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;

        if($location.path() === '/new'){
            // Por defecto se enviará al historico al caducar
            $scope.registro.historico = 'S';
            // Por defecto esta actividad aparecerá en los informes
            $scope.registro.informe = true;
            // Por defecto se muestra el título del programa y su programación asociada
            //$scope.registro.programaVisible = 'S';
            // Estos campos se machacan en servidor
            $scope.registro.creationDate = new Date();
            $scope.registro.codUsuario = parseInt($scope.usuario.propiedades.usr_agenda);
            $scope.registro.usuarioAlta = $scope.usuario.login;
            $scope.registro.nombreContacto = $scope.usuario.nombre;
            $scope.registro.apellidosContacto = $scope.usuario.apellido1 + ' ' + ($scope.usuario.apellido2 || '');
            $scope.registro.emailContacto = $scope.usuario.email;
            $scope.registro.telefonoContacto = $scope.usuario.cgcz_telefono;
        }
        // Si al modificar no tiene asociado telefono se asociamos el de la propiedad cgcz_telefono
        if(angular.isUndefined($scope.registro.telefonoContacto)){$scope.registro.telefonoContacto = $scope.usuario.propiedades.cgcz_telefono};

        if(typeof copiaRegistro === 'undefined' || copiaRegistro === null){
            $scope.registro = registro;
        } else {
            // Condición por si se recupera una actividad de histórico y no se ha encontrado el equipamiento en HistoricCtrl
            if(copiaRegistro.subEvent) {
                for(var i=0; i < copiaRegistro.subEvent.length; i++){
                    delete copiaRegistro.subEvent[i].id;
                    if (copiaRegistro.subEvent[i].startDate) { copiaRegistro.subEvent[i].startDate = new Date(copiaRegistro.subEvent[i].startDate); };
                    if (copiaRegistro.subEvent[i].endDate) { copiaRegistro.subEvent[i].endDate = new Date(copiaRegistro.subEvent[i].endDate); };
                };
            };
            if (copiaRegistro.registration) {
                if (copiaRegistro.registration.startDate) { copiaRegistro.registration.startDate = new Date(copiaRegistro.registration.startDate); };
                if (copiaRegistro.registration.endDate) { copiaRegistro.registration.endDate = new Date(copiaRegistro.registration.endDate); };
                if (copiaRegistro.registration.fechaInicioCanjeo) { copiaRegistro.registration.fechaInicioCanjeo = new Date(copiaRegistro.registration.fechaInicioCanjeo); };
                if (copiaRegistro.registration.fechaFinCanjeo) { copiaRegistro.registration.fechaFinCanjeo = new Date(copiaRegistro.registration.fechaFinCanjeo); };
            };
            if(copiaRegistro.portales) {
                for(var i=0; i < copiaRegistro.portales.length; i++){
                    if (copiaRegistro.portales[i].featuredStartDate) { copiaRegistro.portales[i].featuredStartDate = new Date(copiaRegistro.portales[i].featuredStartDate); };
                    if (copiaRegistro.portales[i].featuredEndDate) { copiaRegistro.portales[i].featuredEndDate = new Date(copiaRegistro.portales[i].featuredEndDate); };
                }
            }
            if (copiaRegistro.fechaCaducidad) { copiaRegistro.fechaCaducidad = new Date(copiaRegistro.fechaCaducidad); };
            if (copiaRegistro.feHoUser) { copiaRegistro.feHoUser = new Date(copiaRegistro.feHoUser); };
            if (copiaRegistro.fechaAlternativa) { copiaRegistro.fechaAlternativa = new Date(copiaRegistro.fechaAlternativa); };
    
            angular.extend($scope.registro, copiaRegistro);
            delete copiaRegistro;
        }
        $scope.actividadPropia = ('' +$scope.registro.codUsuario) === $scope.usuario.propiedades.usr_agenda;

        if($scope.lider.length){
            for(var i = 0; i < $scope.lider.length; i++){
                if($scope.lider[i].associatedType === 'org.sede.servicio.equipamiento.entity.Equipamiento'){
                    $scope.esLiderDeEquipamiento = true;
                    var codEquipamiento = $scope.lider[i].associatedId;
                    $http.get('/sede/servicio/cultura/evento/lugar-realizacion', {
                        params: {
                            q: 'id==' + codEquipamiento 
                        }
                    }).then(function(res) {
                        if(res.data.result[0]) {
                            // console.log(res.data.result[0])
                            $scope.registro.subEvent = [];
                            var se = {};
                            se.location = res.data.result[0];
                            se.endDate = new Date();
                            $scope.registro.subEvent.push(se);
                        }
                    });
                }
            }
        }

        // Si creamos nueva actividad o {portales} no esta definido (edición de actividad gestión antigua)
        if($location.path() === '/new' || angular.isUndefined($scope.registro.portales)) {
            // No tiene portal asignado -> generamos nuevo array
//            if(angular.isUndefined($scope.registro.portales)){
            $scope.registro.portales = [];
//            };
            // Buscamos el portal asociado al usuario de {portales} en app.js
            var portal = _.find(JSON.parse(sessionStorage.getItem('portales')), function(item) {
                //return item.id == $scope.usuario.propiedades.usr_agenda;
                return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
            });
            // Si no encuentra portal asociado a usuario, asociamos por defecto Web Municipal
            if(angular.isUndefined(portal)){
                portal = portales[0];
            };
            // Eliminamos array de códigos de usuarios para establecer el portal a $scope.registro.portales
            delete portal.usr;
            delete portal.link;
            // Creamos el nuevo objeto actoPortal con el portal obtenido
            var actoPortal = {
                portal: portal,
                description: null,
                featuredStartDate: null,
                featuredEndDate: null,
                featuredOrder: null,
                visibleEncalendario: 'S'
            };
            // Buscamos si existe el portal del usuario/por defecto dentro de $scope.registro.portales
            var contienePortal = _.find($scope.registro.portales, function(ap) { return ap.portal.id == actoPortal.portal.id; });
            // Si no lo contiene, añadimos el portal correspondiente
            if(angular.isUndefined(contienePortal)){
                $scope.registro.portales.push(actoPortal);
            };
        };

        $scope.portales = portales; // app.js value
        // Objeto select-ui pobDestino. Convierte la cadena en array para recorrerlo en la directiva.
        $scope.portal = {};
        $scope.portal.selected = angular.isDefined($scope.registro.portales) ?
                _.map($scope.registro.portales, 'portal.id').toString().split(',') : [];
        $scope.onSelectPortal = function($item, $model){
            if(angular.isUndefined($scope.registro.portales)) {
                $scope.registro.portales = [];
            };
            // Creamos el nuevo objeto actoPortal con el portal obtenido
            var actoPortal = {
                portal: {
                    id: parseInt($item.id), 
                    title: $item.title
                },
                description: null,
                featuredStartDate: null,
                featuredEndDate: null,
                featuredOrder: null
            };
            $scope.registro.portales.push(actoPortal);
            //$scope.$apply();
        };
        $scope.onRemovePortal = function($item, $model){
            $scope.registro.portales = _.reject($scope.registro.portales, function(p){ return p.portal.id == $item.id; });
        };

        $scope.limpiarFechaCaducidad = function(){
            delete $scope.registro.fechaCaducidad;
        };
        
        //$scope.formatoActividad = formatoActividad;
        $scope.origenes = origenes;

        $scope.formasDePago = [
            {id: 0, title: 'Único'},
            {id: 1, title: 'Mensual'},
            {id: 2, title: 'Trimestral'}
        ];

        $scope.checkFormaPago = function(formaPago) {
            if(formaPago == null) {
                delete $scope.registro.numPagos;
            }
        };
        
        
        
        
        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });
        $scope.portalSelected = $scope.registro.portales[0];
        if (portalUsuario) {
	        for (var l = 0; l < $scope.registro.portales.length; l++) {
	        	if ($scope.registro.portales[l].portal.id == portalUsuario.id) {
	        		$scope.portalSelected = $scope.registro.portales[l];
	        	}
	        }
        }
        
        $scope.hoyEntreFechas = function(p){
            var currentDate = new Date();
            var minDate = new Date(p.featuredStartDate);
            var maxDate =  new Date(p.featuredEndDate);
            if (currentDate > minDate && currentDate < maxDate){
                return 'success';
            } else{
                return 'warning';
            };
        };

        // Establecemos la visualización previa de la imagen almacenada en los objetos imagenVertical e imagenHorizontal
        if(angular.isDefined($scope.registro.attachment)) {
            if(angular.isDefined($scope.registro.attachment.image)) { $scope.imagenVertical = $scope.registro.attachment.image;};
            if(angular.isDefined($scope.registro.attachment.imageAlt)) { $scope.imagenHorizontal = $scope.registro.attachment.imageAlt;};
        };
        // Anexo:Image Crop
        $scope.imageCrop = {
           originalImage: '',
           croppedImage: ''
        };

        $scope.category = [];
        Restangular.all('evento').all('categoria').getList().then(function(res) {
            $scope.category = res.plain();
        });

        $scope.type = [];
        Restangular.all('evento').all('formato').getList().then(function(res) {
            $scope.type = res.plain();
        });

        /* // Gestión antigua temas - subtemas
        RestangularTema.all('evento-zaragoza').getList().then(function(res) {
            $scope.temas = res.originalElement;
            for(var i=0; i < $scope.temas.length; i++){
                $scope.temas[i].subtema = false;
            };
            RestangularSubtema.all('evento-zaragoza').getList().then(function(res) {
                $scope.subtemas = res.originalElement;
                var cat = $scope.temas;
                for(var i=0; i < $scope.subtemas.length; i++){
                    var temaId = $scope.subtemas[i].tema.id;
                    if(_.contains(_.pluck(cat, 'id'), temaId)) {
                        var index = _.indexOf(_.pluck(cat, 'id'), temaId);
                        //if(cat[index].subtema === undefined) cat[index].subtema = [];
                        //delete    $scope.subtemas[i].tema;
                        $scope.subtemas[i].subtema = true;
                        cat.splice(index + 1, 0, $scope.subtemas[i]);
                        //cat[index].subtema.push($scope.subtemas[i]);
                    }
                };
                $scope.categorias = cat;
            });
        });
        */

        $scope.desasociarPortal = function() {
            Restangular.all('evento/admin').one('review', $scope.registro.id).one('remove', portalUsuario.id).remove().then(function(res) {
                Informer.inform(res.mensaje, 'success');
                $location.path('/');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            });
        };
        
        $scope.selectPrograma = function(reg) {
            // Programa antiguo, se guarda como String a nivel de tabla actos
            //$scope.registro.programa = reg.title;
        };
        $scope.selectLugar = function(reg) {
            // 20637 = Fuera de Zaragoza
            if(reg.id == 20637){
                $scope.showLocation = true;
            } else {
               $scope.showLocation = false;
            };
        };
        $scope.selectEntidadOrganiza = function(reg) {
            if(angular.isUndefined($scope.registro.organizer)) {
                $scope.registro.organizer = [];
            };
            $scope.registro.organizer.push(reg);
        };
        $scope.eliminarEntidadOrganiza = function(index) {
            $scope.registro.organizer.splice(index, 1);
        };
        $scope.selectEntidadImparte = function(reg) {
            if(angular.isUndefined($scope.registro.performer)) {
                $scope.registro.performer = [];
            };
            $scope.registro.performer.push(reg);
        };
        $scope.eliminarEntidadImparte = function(index) {
            $scope.registro.performer.splice(index, 1);
        };
        
        $scope.poblacion = poblacion; // app.js value
        // Objeto select-ui pobDestino. Convierte la cadena en array para recorrerlo en la directiva.
        $scope.pobDestino = {};
        $scope.pobDestino.selected = angular.isDefined($scope.registro.population) ?
                _.map($scope.registro.population, 'id').toString().split(',') : [];
        $scope.onSelectPobDestino = function($item, $model){
            if(angular.isUndefined($scope.registro.population)) 
                $scope.registro.population = [];
            $scope.registro.population.push({id: parseInt($item.id), title: $item.title});
        };
        $scope.onRemovePobDestino = function($item, $model){
            $scope.registro.population = _.reject($scope.registro.population, function(p){ return p.id == $item.id; });
        };
        
        $scope.valores = valores; // app.js value
        $scope.valoresAct = {};
        $scope.valoresAct.selected = angular.isDefined($scope.registro.value) ?
                _.map($scope.registro.value, 'id').toString().split(',') : [];
        $scope.onSelectValores = function($item, $model){
            if(angular.isUndefined($scope.registro.value)) 
                $scope.registro.value = [];
            $scope.registro.value.push({id: parseInt($item.id), title: $item.title});
        };
        $scope.onRemoveValores = function($item, $model){
            $scope.registro.value = _.reject($scope.registro.value, function(v){ return v.id == $item.id; });
        };

        $scope.completarInscripcion = function(inscripcion){
            $scope.registro.registration.streetAddress = inscripcion.streetAddress;
            $scope.registro.registration.telephone = inscripcion.telephone;
            $scope.registro.registration.faxNumber = inscripcion.faxNumber;
            $scope.registro.registration.email = inscripcion.email;
            // Terminamos de rellenar los campos de inscripcion por lo que dejamos
            // $scope.registro.inscripcion.title con solo título en vez de obj completo. 
            $scope.registro.registration.title = inscripcion.title;
        };
        $scope.borrarInscripcion = function() {
            $scope.registro.registration.streetAddress = '';
            $scope.registro.registration.telephone = '';
            $scope.registro.registration.faxNumber = '';
            $scope.registro.registration.email = '';
            // Terminamos de rellenar los campos de inscripcion por lo que dejamos
            // $scope.registro.inscripcion.title con solo título en vez de obj completo. 
            $scope.registro.registration.title = '';
        };
        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });
        $scope.crearInscripcion = function(r){
            r.registration.url = 'https://www.zaragoza.es/sede/servicio/cultura/evento/' + registro.id + '/inscripcion';
            if (portalUsuario && portalUsuario.id=="2") {
            	registro.cuestionario = true;
            }
        };

        $scope.datepickers = {startDate: false, endDate: false, inicioDestacado: false, finDestacado: false, inicioDestacadoAlt: false, finDestacadoAlt: false, dateCaducidad: false, startDateInscrip: false, endDateInscrip: false, fechaInicioCanjeo: false, fechaFinCanjeo: false, feHoUser: false, fechaAlternativa: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };
        
        $scope.cargarFicha = function(id, proyecto, codUsuario){
            if(proyecto === 'ciudad') {
                if(codUsuario !== 2) {
                    $window.open('/ciudad/actividades/detalle_Agenda?id=' + id + '&refresh=y', '_blank');
                } else {
                    $window.open('/ciudad/sectores/jovenes/cipaj/fichaAJ_Agenda?codigo=' + id + '&refresh=y', '_blank');
                }
            } else {
            	if (portalUsuario.link) {
            		$window.open('/sede/servicio/' + portalUsuario.link + '/' + id + '?refresh=y', '_blank');
            	} else {
            		$window.open('/sede/servicio/cultura/evento/' + id + '?refresh=y', '_blank');
            	}
            }
        };

        $scope.guardar = function() {
            var queryParams = {}; 
            var contieneAdjuntos = false;
            var pendienteValidar = false;
            // Comprobamos adjuntos antes de guardar registro
            //if($scope.files.length > 0){contieneAdjuntos = true;};
             // Comprobamos si la actividad está pendiente de validar y si se tiene permisos PUB
            if($scope.registro.validacion === 'S' && $scope.permisos.VAL){
                $scope.registro.validacion = 'N'; // Validamos la actividad
                pendienteValidar = true;
            }

            // Si la actividad tiene inscripción online (ayto) marcamos no se envía a histórico
            if ($scope.registro.id) {
                var urlInscripcion = '/sede/servicio/cultura/evento/'+ $scope.registro.id +'/inscripcion';
                if($scope.registro.registration.url && $scope.registro.registration.url.indexOf(urlInscripcion) > 0) {
                    $scope.registro.historico = 'N';
                }
            }

            // Comprobamos si han buscado entidad pero finalmente no han seleccionado una
            // TOFIX: Corregir y tratar a través de la directiva <autocompletado>
            if($scope.registro.organizer === ''){delete $scope.registro.organizer;};

            // Si hay anexo definido antes de guardar:
            if(angular.isDefined($scope.registro.attachment)){
                // Comprobamos si tiene idAnexo (copia de actividad anterior) y lo borramos para generar un nuevo anexo
                if(angular.isDefined($scope.registro.attachment.id) && $location.path() === '/new') {
                    delete $scope.registro.attachment.id;
                }
                // Añadimos la ruta correcta (el recortador no genera la ruta necesaria (imagen/nombre_fichero.ext), solo nombre_fichero.ext)
                if(angular.isDefined($scope.registro.attachment.image)) {
                    if($scope.registro.attachment.image.indexOf('imagen/') < 0) {
                        $scope.registro.attachment.image = 'imagen/' + $scope.registro.attachment.image;
                    }
                }
                // Añadimos la ruta correcta (el recortador no genera la ruta necesaria (imagen/nombre_fichero.ext), solo nombre_fichero.ext)
                if(angular.isDefined($scope.registro.attachment.imageAlt)) {
                    if($scope.registro.attachment.imageAlt.indexOf('imagen/') < 0) {
                        $scope.registro.attachment.imageAlt = 'imagen/' + $scope.registro.attachment.imageAlt;
                    }
                }
            }

            if($location.path() === '/new') {
                Dao.crear('evento', $scope.registro).then(function(res) {
                    Informer.inform('El registro se ha creado correctamente.', 'success');
                    $location.path('/edit/' + res.id);
                }, function(err) {
                    Informer.inform("Se ha producido un error al crear el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                });
            } else {
                Dao.actualizar($scope.registro, queryParams).then(function(res) {
                     Informer.inform('El registro se ha modificado correctamente.' 
                        + (pendienteValidar ? ' Actualizando pendientes de validación...' : ''), 'success');
                    if(pendienteValidar) {
                    	$scope.actualizarPendientesValidacion();
                    }
                    if(angular.isDefined($scope.registro.attachment) && angular.isUndefined($scope.registro.attachment.id)){
                        $scope.registro.attachment.id = res.attachment.id;
                    }
                    // Restangular elimina el originalElement en cada petición, se lo re-asociamos desde la respuesta
                    if(angular.isUndefined($scope.registro.originalElement) && angular.isDefined(res.originalElement)){
                        $scope.registro.originalElement = res.originalElement;
                        // FIXME: ELIMINAR AL PASAR A SEDE - Forzado de horarios por si se realiza una copia (no viene en respuesta)
                        //$scope.registro.originalElement.subEvent = $scope.registro.subEvent;
                    }
                    $route.reload();
                }, function(err) {
                    Informer.inform("Se ha producido un error al guardar el registro: " + (angular.isDefined(err.data.error) ? JSON.stringify(err.data.error) : JSON.stringify(err.data.mensaje)), "danger");
                     // $location.path('/');
                });
            }
            
        }

        $scope.borrarLugarLista = function(index) {
            $scope.registro.subEvent.splice(index, 1);
            // Eliminamos al campo location a nivel de actos los titulos de los equipamientos borrados
            var equipamientos;
            _.each($scope.registro.subEvent, function(subEvent){
                if(equipamientos === undefined) {
                    equipamientos = subEvent.location.title;
                } else {
                    equipamientos += ', ' + subEvent.location.title;
                };
            });
            $scope.registro.location = equipamientos;
        };

        $scope.abrircalendario = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.abierto = true;
        };

        $scope.tiposPrecio = tiposPrecio; // app.js value
        $scope.nPrecio = {};
        $scope.aniadirPrecio = function(nPrecio){
            var priceObj = {
                'hasCurrencyValue': (nPrecio.fareGroup === 'Gratuita' ? 0.00 : parseFloat(nPrecio.hasCurrencyValue.toFixed(2))),
                'hasCurrency': 'EUR',
                'fareGroup': nPrecio.fareGroup,
                'minSize': (angular.isUndefined(nPrecio.minSize) ? '1' : nPrecio.minSize)
            };
            if(angular.isUndefined($scope.registro.price)){
                angular.extend($scope.registro, {
                    price: [priceObj]
                });
            } else {
                $scope.registro.price.push(priceObj);
            };
            // Generamos antiguo campo precio a partir del array de precios nuevos
            // if(angular.isUndefined($scope.registro.precioEntrada)){$scope.registro.precioEntrada = '';}
            // for(var i = 0; i < $scope.registro.price.length; i++){
            //     if(i == 0){$scope.registro.precioEntrada = '';};
            //     $scope.registro.precioEntrada += ($scope.registro.precioEntrada.length == 0 ? '' : '<br/>')
            //         + $scope.registro.price[i].fareGroup + ': ' + $scope.registro.price[i].hasCurrencyValue + ' euros'
            // };
            // // Si se añade el precio con tipo 'Gratuita' añadimos antiguo campo tipoEntrada='libre'
            // if(nPrecio.fareGroup === 'Gratuita'){
            //     $scope.registro.tipoEntrada = 'libre';
            //     // Si la entrada es gratuita borramos el campo antiguo de precios
            //     if(angular.isDefined($scope.registro.precioEntrada)) {$scope.registro.precioEntrada = '';}
            // };
            $scope.nPrecio = {};
        };

        // $scope.modificarPrecio = function(precio){
        //     $scope.modificarCampo = true;
        //     $scope.verCampo = true;
        // };
        // $scope.actualizarPrecio = function(precio){
        //     $scope.modificarCampo = false;
        //     $scope.verCampo = false;
        // };
        $scope.eliminarPrecio = function(index){
            // Si se elimina el precio con tipo 'Gratuita' eliminamos antiguo campo tipoEntrada='libre'
            // if($scope.registro.price[index].fareGroup === 'Gratuita'){
            //     delete $scope.registro.tipoEntrada;
            //     // Repetimos: Generamos antiguo campo precio a partir del array de precios nuevos
            //     for(var i = 0; i < $scope.registro.price.length; i++){
            //         if(i == 0){$scope.registro.precioEntrada = '';};
            //         // Excepto tipo gratuita
            //         if($scope.registro.price[i].fareGroup !== 'Gratuita') {
            //             $scope.registro.precioEntrada += ($scope.registro.precioEntrada.length == 0 ? '' : ', ')
            //             + $scope.registro.price[i].fareGroup + ': ' + $scope.registro.price[i].hasCurrencyValue + ' euros'    
            //         };
            //     };
            // };
            $scope.registro.price.splice(index, 1);
            // Si se eliminan todos los precios se controla que el campo antiguo de precio se borra
            // if($scope.registro.price.length == 0){
            //     $scope.registro.precioEntrada = '';
            // };
        }

        // $scope.recargarEntidad = function(id){
        //     Dao.detalle('entidad/evento-zaragoza', id).then(function(res){
        //         $scope.registro.entidad = res.originalElement;
        //     });
        // };

        $scope.removeAnexo = function(id){
            if($location.path() === '/new'){
                delete $scope.registro.attachment;
            } else {
                var anexo = Restangular.all('evento').one('admin').one('attachment', id).one('remove');
                Dao.eliminar(anexo).then(function(res) {
                    delete $scope.registro.attachment;
                    Informer.inform('Ficha de anexos eliminada correctamente.', 'success');
                }, function(err) {
                    Informer.inform('Se ha producido un error al borrar la ficha de anexos: ' + JSON.stringify(err.data.error) || JSON.stringify(err.data.mensaje), "danger");
                });
            };
            $scope.imagenVertical = undefined;
            $scope.imagenHorizontal = undefined;
        };

        //$scope.files = [];
        $scope.$on('fileSelected', function(event, args) {
            // Localiza la posicion actual del array Anexos en la vista
            //var x = $scope.registro.anexo.map(function(x) {return x.codAnexo; }).indexOf(parseInt($scope.verAnexo.codAnexo));
            
            // Si estamos modificando y tiene datos anteriores asociamos los datos de registro.anexo[0]
            // if($location.path() !== '/new' && $scope.registro.anexo.length > 0) {
            //     $scope.anexo = $scope.registro.anexo[0];
            // };

            // Primera selección de adjunto
            // if(angular.isUndefined($scope.anexo)) {
            //     $scope.anexo = {};
            // };
            if(angular.isUndefined($scope.registro.attachment.file_array)) {
                $scope.registro.attachment.file_array = [];
            };

            // if($location.path() === '/new' && angular.isDefined($scope.anexo.id)){
            //     delete $scope.anexo.id;
            // };
            
            // Solo permitimos la selección de un fichero de cada tipo. Si selecciona otro del mismo tipo, lo descarta (_.reject)
            $scope.$apply(function() {
                if(_.find($scope.registro.attachment.file_array, function(file) { return file.media_type === args.tipo; })){
                    $scope.registro.attachment.file_array = _.reject($scope.registro.attachment.file_array, function(f){ return f.media_type === args.tipo; });
                };
            });

            var currFile = args.file;
            var reader = new FileReader();
            reader.onload = function (file) {
                var media_file = new Object();
                var title = file.name.replace(/[^a-z0-9\.]/gi, '_').toLowerCase();
                // Revisar al crear no existe $scope.registro.id
                //var title = $scope.registro.id + file.name.substring(file.name.lastIndexOf('.'), file.name.length)
                var description = title.substring(0, (title.indexOf(".") > 30 ? 30 : title.indexOf(".")));
                
                // if (file.type.match('image.*')) {
                //     // Cambiamos la extensión de la imagen adjunta, ya que transformamos el formato al recortar
                //     title = title.substr(0, title.lastIndexOf(".")) + ".png";
                //     $scope.anexo.imagen = 'imagen/' + title;
                // } else 
                if (file.type === 'application/pdf' || file.type === 'text/plain') {
                    $scope.registro.attachment.document = 'documento/' + title;
                } else if (file.type.match('audio.*')) {
                    $scope.registro.attachment.audio = 'sonido/' + title;
                };

                media_file.media_name = title;
                media_file.media_description = description;
                media_file.media_type = args.tipo;

                return function(evt){
                    // if (file.type.match('image.*')) {
                    //     var image = new Image();
                    //     image.src = evt.target.result;
                    //     image.onload = function() {
                    //         if(this.width < 320 || this.height < 480){
                    //             Informer.inform('Tamaño de la imagen no válido. El tamaño mínimo es 320x480 px.', 'warning');
                    //             $scope.imageCrop.originalImage = '';
                    //             $scope.imageCrop.croppedImage = '';
                    //             $('#anexoImagen').val(null);
                    //             $scope.anexo.file_array = _.reject($scope.anexo.file_array, function(f){ return f.media_type === 'anexoImagen'; });
                    //             delete $scope.registro.anexo[0].imagen;
                    //             //$scope.files = _.without($scope.files, _.findWhere($scope.files, {tipo: 'anexoImagen'}));
                    //         } else {
                    //             // $scope.imageCrop.originalImage = evt.target.result;
                    //         };
                    //     };
                    //     $scope.$apply(function($scope){
                    //         $scope.imageCrop.originalImage = evt.target.result;
                    //         if($scope.imageCrop.originalImage !== "") {
                    //             // Esperamos 500 ms para que el recortador recargue la imagen recortada
                    //             $timeout(function() {
                    //                 // Bug firefox unsupport ngImgCropFullExtended: imageCrop.croppedImage = null
                    //                 if($scope.imageCrop.croppedImage === null){
                    //                     media_file.media_body = encodeURIComponent($scope.imageCrop.originalImage);
                    //                 } else {
                    //                     media_file.media_body = encodeURIComponent($scope.imageCrop.croppedImage);    
                    //                 };
                    //             }, 500);
                    //         };
                    //     });
                    // } else {
                        media_file.media_body = encodeURIComponent(evt.target.result);
                    // };
                    $scope.$apply(function($scope){
                        $scope.registro.attachment.file_array.push(media_file);
                    });
                };
            }(currFile);
            reader.readAsDataURL(currFile);    

        });

    }
])
//TODO Eliminar
.controller('EditAltCtrl', ['$scope', 'Dao', '$http', 'registro', 'poblacion', 'valores', 'portales', 'origenes', 'Restangular','$location', '$route', 'Informer', '$filter', 'tiposPrecio', 'formatoActividad', '$window', '$timeout', 
    function($scope, Dao, $http, registro, poblacion, valores, portales, origenes, Restangular, $location, $route, Informer, $filter, tiposPrecio, formatoActividad, $window, $timeout) { 
        $scope.debug = false;
        $scope.location = $location.path();
        $scope.registro = registro;
        $scope.portales = portales;

        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });
       
        $scope.actoPortal = _.find($scope.registro.portales, function (portal) {
            return portal.portal.id === parseInt(portalUsuario.id); //TOFIX: $scope.portales.id debería ser integer
        });

        $scope.datepickers = {inicioDestacado: false, finDestacado: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        $scope.cargarFicha = function(id, proyecto, codUsuario){
            if(proyecto === 'ciudad') {
                if(codUsuario !== 2) {
                    $window.open('/ciudad/actividades/detalle_Agenda?id=' + id + '&refresh=y', '_blank');
                } else {
                    $window.open('/ciudad/sectores/jovenes/cipaj/fichaAJ_Agenda?codigo=' + id + '&refresh=y', '_blank');
                }
            } else {
            	if (portalUsuario.link) {
            		$window.open('/sede/servicio/' + portalUsuario.link + '/' + id + '?refresh=y', '_blank');
            	} else {
            		$window.open('/sede/servicio/cultura/evento/' + id + '?refresh=y', '_blank');
            	}
            }
        };

        
        
        $scope.guardar = function() {
        	var organiza = '';
        	if ($scope.registro.organizer) {
	        	for(var i = 0; i < $scope.registro.organizer.length; i++) { 
	        		  if (i > 0) {
	        			  organiza = organiza + ',';
	        		  }
	        		  organiza = organiza + $scope.registro.organizer[i].id
	        	}
        	}
        	var imparte = '';
        	if ($scope.registro.performer) {
	        	for(var i = 0; i < $scope.registro.performer.length; i++) { 
	        		  if (i > 0) {
	        			  imparte = imparte + ',';
	        		  }
	        		  imparte = imparte + $scope.registro.performer[i].id
	        	}
        	}
            Restangular.all('evento/admin').one('review', $scope.registro.id).one('add', portalUsuario.id).put({
                'description': $scope.actoPortal.description,
                'organizer': organiza,
                'performer': imparte,
                'featuredStartDate': $filter('date')($scope.actoPortal.featuredStartDate, 'dd-MM-yyyy'),
                'featuredEndDate': $filter('date')($scope.actoPortal.featuredEndDate, 'dd-MM-yyyy'),
                'featuredOrder': $scope.actoPortal.featuredOrder
            }).then(function(res) {
                Informer.inform(res.mensaje, 'success');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            })
        };
        $scope.selectEntidadOrganiza = function(reg) {
            if(angular.isUndefined($scope.registro.organizer)) {
                $scope.registro.organizer = [];
            }
            $scope.registro.organizer.push(reg);
        };
        $scope.eliminarEntidadOrganiza = function(index) {
            $scope.registro.organizer.splice(index, 1);
        };
        $scope.selectEntidadImparte = function(reg) {
            if(angular.isUndefined($scope.registro.performer)) {
                $scope.registro.performer = [];
            }
            $scope.registro.performer.push(reg);
        };
        $scope.eliminarEntidadImparte = function(index) {
            $scope.registro.performer.splice(index, 1);
        };
        $scope.desasociarPortal = function() {
            Restangular.all('evento/admin').one('review', $scope.registro.id).one('remove', portalUsuario.id).remove().then(function(res) {
                Informer.inform(res.mensaje, 'success');
                $location.path('/');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            });
        };

    }
])

.controller('CheckCtrl', ['$scope', 'Restangular', 'Dao', '$location', 'Informer', 'portales', '$filter', '$uibModal', 
    function($scope, Restangular, Dao, $location, Informer, portales, $filter, $uibModal) {

        $scope.isCollapsedLRAnt = true;
        $scope.isCollapsedSinLR = true;
        $scope.isCollapsedSinTema = true;
        $scope.isCollapsedSinTipo = true;
        $scope.isCollapsedDPTNegativo = true;
        $scope.isCollapsedNoVisible = true;
        $scope.isCollapsedSinPoblacion = true;
        $scope.isCollapsedSinEntidad = true;
        $scope.isCollapsedNombreDuplicado = true;

        var params = Dao.getFiltros(); // Object
        // if(angular.equals({}, params)){
        //     params.start = 0;
        //     params.rows = 2500;
        //     params.sort = 'lastUpdated desc';
        // };
        // $scope.filtrosParaListado = params.q;

        //params.q = _.filter(params.q, function(obj) { return obj.key !== 'busquedaHistorico' });
        // $scope.registros = [];

        $scope.portales = portales;

        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });

        $scope.registros = [];

        // pagination
        $scope.viewby = 10;
        $scope.currentPage = 1;
        $scope.maxSizePage = 5;
        $scope.itemsPerPage = $scope.viewby;

        $scope.setItemsPerPage = function(num) {
            $scope.itemsPerPage = num;
            $scope.currentPage = 1; //reset to first page
        };

        $scope.listarPorServicio = function(portal){
            params = {};
            params.q = [];

            params.start = ($scope.currentPage - 1) * $scope.itemsPerPage;
            params.rows = $scope.itemsPerPage;

            var actualDate = new Date();
            actualDate.setDate(actualDate.getDate() + 1); // TOFIX: EL filtro =le= no toma el día actual
            var oneWeekAgoDate = new Date();
            oneWeekAgoDate.setDate(oneWeekAgoDate.getDate() - 40); // temporalmente 1 mes
            console.log($scope.showReviewed);
            console.log($scope.showReviewed);
            params.showReviewed = $scope.showReviewed; 
            params.id = [parseInt(portal.id)];
            params.q.push({'key':'creationDate', 'value': $filter('date')(oneWeekAgoDate, 'yyyy-MM-dd'), 'op': '=ge='});
            params.q.push({'key':'creationDate', 'value': $filter('date')(actualDate, 'yyyy-MM-dd'), 'op': '=le='});
            Dao.setFiltros(params);

            Dao.listar('evento/admin/review', params).then(function(data) {
                $scope.registros = data; //De otro modo, se pierden los datos ya listados
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
                if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
            }); 
            //$location.path('/');
        };

        $scope.pageChanged = function() {
            $scope.listarPorServicio($scope.portal);
        };

        $scope.asociadoAPortal = function(portalesActividad) {
            return _.some(portalesActividad, function (portal) {
                return portal.portal.id === parseInt(portalUsuario.id) && 'S' === portal.visible; //TOFIX: $scope.portales.id debería ser integer
            });
        };
        $scope.descartadoDePortal = function(portalesActividad) {
            return _.some(portalesActividad, function (portal) {
                return portal.portal.id === parseInt(portalUsuario.id) && 'N' === portal.visible; //TOFIX: $scope.portales.id debería ser integer
            });
        };
        $scope.asociarPortal = function(reg) {
            var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/templates/modalAsociarPortal.html',
                controller: function($scope, $uibModalInstance, registro) {
                    $scope.registro = Restangular.copy(registro);
                    $scope.actoPortal = {};

                    $scope.confirmarModal = function() {
                        $uibModalInstance.close($scope.actoPortal);
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

            modalInstance.result.then(function(actoPortal) {

                Restangular.all('evento/admin').one('review', reg.id).one('add', portalUsuario.id).put({
                    'description': actoPortal.description,
                    'featuredStartDate': $filter('date')(actoPortal.featuredStartDate, 'dd-MM-yyyy'),
                    'featuredEndDate': $filter('date')(actoPortal.featuredEndDate, 'dd-MM-yyyy'),
                    'featuredOrder': actoPortal.featuredOrder
                }).then(function(res) {
                    $scope.listarPorServicio($scope.portal);
                    Informer.inform(res.mensaje + ' <a href="/sede/servicio/cultura/evento/admin/acto/#/edit/' + reg.id + '" target="_blank">' + reg.id + ' ' + reg.title + '</a>', 'success');
                }, function(res) {
                    Informer.inform(res.mensaje, 'danger');
                })
            }, function() {
                //console.log('Modal dismissed at: ' + new Date());
            });
            
        };

        $scope.desasociarPortal = function(reg) {
            Restangular.all('evento/admin').one('review', reg.id).one('remove', portalUsuario.id).remove().then(function(res) {
                $scope.listarPorServicio($scope.portal);
                Informer.inform(res.mensaje, 'success');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            });
        };
        $scope.descartarPortal = function(reg) {
            Restangular.all('evento/admin').one('review', reg.id).one('discard', portalUsuario.id).remove().then(function(res) {
                $scope.listarPorServicio($scope.portal);
                Informer.inform(res.mensaje, 'success');
            }, function(res) {
                Informer.inform(res.mensaje, 'danger');
            });
        };

        // $scope.totalErrores = $scope.registrosSinLR.length 
        //     + $scope.registrosLRAnt.length 
        //     + $scope.registrosSinTema.length
        //     + $scope.registrosSinTipo.length;
        //     + $scope.registroDPTNegativo.length;

        $scope.comenzarRevision = function() {
            params = {};
            params.start = 0;
            params.rows = 2500;
            params.sort = 'lastUpdated desc';
            Dao.listar('evento/admin/list', params).then(function(res) {
                // $scope.registros = res;
                inicializarArrays();
                comprobar(res);
            }, function(err){
                Informer.inform("Error al consultar el listado global: " + err.data.error, "danger");
            });
        };

        function inicializarArrays() {
            $scope.registrosSinLR = [];
            // $scope.registrosLRAnt = [];
            $scope.registrosSinTema = [];
            // $scope.registrosSinTipo = [];
            $scope.registrosDPTNegativo = [];
            $scope.registrosNoVisible = [];
            $scope.registrosSinPoblacion = [];
            $scope.registrosSinEntidad = [];
            $scope.registrosNombreDuplicado = [];
        };

        function comprobar(res){
            for(var i=0; i < res.length; i++){

                /* -- Lugares de Realización-- */
                if(angular.isDefined(res[i].subEvent)){ // Tiene lugar de realización antiguo
                    for(var j = 0; j < res[i].subEvent.length; j++){
                        // if(res[i].subEvent[j].lugar.id.indexOf('lug-') !== -1){ //lug-
                        //     $scope.registrosLRAnt.push(res[i]);
                        //     break;
                        // };
                    };
                } else { // No tiene lugar de realización
                    $scope.registrosSinLR.push(res[i]);
                };
                
                /* -- Temáticas -- */
                if(angular.isUndefined(res[i].category)){
                    $scope.registrosSinTema.push(res[i]);
                };

                /* -- Tipo de acto -- */
                // if(angular.isUndefined(res[i].tipoActo)){
                //     $scope.registrosSinTipo.push(res[i]);
                // };

                /* -- Días para terminar -- */
                if(res[i].diasParaTerminar < 0){
                    $scope.registrosDPTNegativo.push(res[i]);
                };

                /* -- Visible -- */
                if(res[i].visible === 'N'){
                    $scope.registrosNoVisible.push(res[i]);
                };

                /* -- Sector de población -- */
                if(angular.isUndefined(res[i].population)){
                    $scope.registrosSinPoblacion.push(res[i]);
                };

                /* -- Entidad -- */
                if(angular.isUndefined(res[i].organizer)){
                    $scope.registrosSinEntidad.push(res[i]);
                };

            };

            /* -- Title Duplicado -- */
            //$scope.registrosNombreDuplicado = _.chain(res).groupBy('title').filter(function(v){return v.length > 1}).flatten().value();
            $scope.registrosNombreDuplicado = _.chain(res).groupBy('title').filter(function(v){return v.length > 1}).uniq().value()

        };

    }
])

// .controller('ReportCtrl', ['$scope', 'Restangular', 'Dao', '$location', 'Informer',
//     function($scope, Restangular, Dao, $location, Informer) {
//         var params = Dao.getFiltros(); // Object
//         params.start = 0;
//         params.sort = 'endDate desc';
//         $scope.filtrosParaListado = params.q;

//         $scope.registros = [];
        
//         params.q = _.filter(params.q, function(obj) { return obj.key !== 'busquedaHistorico' });

//         Dao.listar('actividades/historico', params).then(function(data) {
//             $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
//             if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
//         }, function(err){
//             if(params.q !== null){
//                 Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
//             } else {
//                 Informer.inform("Error en listado: " + err.data.error, "danger");
//             };
//         });

//     }
// ])

.controller('FeaturedCtrl', ['$scope', 'Restangular', 'Dao', '$location', 'Informer', 'portales', '$filter', 
    function($scope, Restangular, Dao, $location, Informer, portales, $filter) {
        var params = Dao.getFiltros(); // Object
        params.start = 0;
        params.sort = 'lastUpdated desc';

        $scope.portales = portales;

        $scope.getCampoPortalAsociado = function(registro, campo) {
            var dest = _.find(registro.portales, function(item) {
                return item.portal.id == portalUsuario.id;
            });
            return dest[campo];
        };

        $scope.dateSelected = new Date();//
        $scope.datepickers = {dateSelected: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };

        var portalUsuario = _.find($scope.portales, function(item) {
            return _.includes(item.usr, $scope.usuario.propiedades.usr_agenda);
        });

        $scope.registros = [];
        $scope.selectDate = function(date){
            params.idPortal = [parseInt(portalUsuario.id)];
            params.dateSelected = $filter('date')(date, 'dd-MM-yyyy');
            Dao.listar('evento/admin/featured', params).then(function(data) {
                $scope.registros = data;
                if(data.totalCount === 0) {Informer.inform("La búsqueda no ha producido ningún resultado", "warning");}
            }, function(err){
                if(params.q !== null){
                    Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });
        };

        $scope.selectDate($scope.dateSelected);

    }
]);