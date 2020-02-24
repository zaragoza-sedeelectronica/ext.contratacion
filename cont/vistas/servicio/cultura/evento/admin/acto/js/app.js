var app = angular.module('app', [
    'ngRoute',
    'ngAnimate',
    'restangular',
    'ui.bootstrap',
    'infinite-scroll',
    'angularShamSpinner',
    'ngSanitize',
    'ui.select',
    'ngImgCrop',
    'checklist-model',
    'app.controllers',
    'Gesweb.filters',
    'Gesweb.services',
    'Gesweb.directives',
    'app.directives',
]).config(['$routeProvider', '$locationProvider', 'RestangularProvider', '$httpProvider', 'uiSelectConfig',
    function($routeProvider, $locationProvider, RestangularProvider, $httpProvider, uiSelectConfig) {

        uiSelectConfig.theme = 'bootstrap';
        $httpProvider.defaults.headers.common['Accept'] = 'application/json';
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
        RestangularProvider.addRequestInterceptor(function(element, operation, what, url) {
            if (operation === 'remove') {
                return undefined;
            }
            return element;
        });

        RestangularProvider.addRequestInterceptor(function(element, operation, what, url) {
            if (element !== null && angular.isDefined(element) && angular.isDefined(element.attachment)) {
                if (angular.isDefined(element.attachment.document) && element.attachment.document.indexOf('zaragoza.es') > 0) { element.attachment.document = element.attachment.document.split('/actividades/').pop() };
                if (angular.isDefined(element.attachment.image) && element.attachment.image.indexOf('zaragoza.es') > 0) { element.attachment.image = element.attachment.image.split('/actividades/').pop() };
                if (angular.isDefined(element.attachment.imageAlt) && element.attachment.imageAlt.indexOf('zaragoza.es') > 0) { element.attachment.imageAlt = element.attachment.imageAlt.split('/actividades/').pop() };
                if (angular.isDefined(element.attachment.audio) && element.attachment.audio.indexOf('zaragoza.es') > 0) { element.attachment.audio = element.attachment.audio.split('/actividades/').pop() };
            };
            return element;
        });

        // Versión 1.4.0 | setResponseExtractor (para metadata en la respuesta) y setListTypeIsArray están deprecated
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var extractedData;
            if (operation === 'getList') {
                if (data.result) {
                    extractedData = data.result;
                } else {
                    extractedData = [];
                }
                extractedData.totalCount = data.totalCount;
                extractedData.start = data.start;
                extractedData.rows = data.rows;
            } else {
                extractedData = data;
            }
            // Obtenemos el objeto 'originalElement' (unrestangularized) en detalle
            if (!angular.isArray(extractedData)) {
                extractedData.originalElement = angular.copy(extractedData);
            };
            return extractedData;
        });

        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            //console.log(what);
            var d = data;
            if (operation === 'get' && what == 'evento' && angular.isDefined(d.subEvent)) {
                for (var i = 0; i < d.subEvent.length; i++) {
                    if (d.subEvent[i].startDate) { d.subEvent[i].startDate = new Date(d.subEvent[i].startDate); };
                    if (d.subEvent[i].endDate) { d.subEvent[i].endDate = new Date(d.subEvent[i].endDate); };
                };
                if (d.registration) {
                    if (d.registration.startDate) { d.registration.startDate = new Date(d.registration.startDate); };
                    if (d.registration.endDate) { d.registration.endDate = new Date(d.registration.endDate); };
                    if (d.registration.fechaInicioCanjeo) { d.registration.fechaInicioCanjeo = new Date(d.registration.fechaInicioCanjeo); };
                    if (d.registration.fechaFinCanjeo) { d.registration.fechaFinCanjeo = new Date(d.registration.fechaFinCanjeo); };
                };
                if (d.portales) {
                    for (var i = 0; i < d.portales.length; i++) {
                        if (d.portales[i].featuredStartDate) { d.portales[i].featuredStartDate = new Date(d.portales[i].featuredStartDate); };
                        if (d.portales[i].featuredEndDate) { d.portales[i].featuredEndDate = new Date(d.portales[i].featuredEndDate); };
                    };
                };
                // if (d.inicioDestacado) { d.inicioDestacado = new Date(d.inicioDestacado); };
                // if (d.finDestacado) { d.finDestacado = new Date(d.finDestacado); };
                // if (d.inicioDestacadoAlt) { d.inicioDestacadoAlt = new Date(d.inicioDestacadoAlt); };
                // if (d.finDestacadoAlt) { d.finDestacadoAlt = new Date(d.finDestacadoAlt); };
                if (d.fechaCaducidad) { d.fechaCaducidad = new Date(d.fechaCaducidad); };
                if (d.feHoUser) { d.feHoUser = new Date(d.feHoUser); };
                if (d.fechaAlternativa) { d.fechaAlternativa = new Date(d.fechaAlternativa); };
            };
            return d;
        });

        // RestangularProvider.setResponseExtractor(function(response) {
        //     var newResponse = response;
        //     if (angular.isArray(response)) {
        //         angular.forEach(newResponse, function(value, key) {
        //             newResponse[key].originalElement = angular.copy(value);
        //         });
        //     } else {
        //         newResponse.originalElement = angular.copy(response);
        //     }
        //     return newResponse;
        // });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento');
                }
            }
        }).//TODO Eliminar
        when('/edit-alt/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-details-alt.html',
            controller: 'EditAltCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento');
                }
            }
        }).
        when('/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/historic', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-historic.html',
            controller: 'HistoricCtrl'
        }).
        when('/check', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-check.html',
            controller: 'CheckCtrl'
        }).
        // when('/report', {
        //     templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-report.html',
        //     controller: 'ReportCtrl'
        // }).
        when('/featured', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-featured.html',
            controller: 'FeaturedCtrl'
        }).
        when('/ayuda', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-ayuda.html',
            controller: 'ListCtrl'
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');

        // RestangularProvider.setBaseUrl('http://' + window.sessionStorage.getItem('SERVIDOR') + '/sede/servicio');
        RestangularProvider.setBaseUrl('/sede/servicio/cultura/');
        // RestangularProvider.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/cultura-ocio/');
        // TOFIX: Pasar protocolo http/https a variable en sessionStorage. SERVER
        // RestangularProvider.setBaseUrl('http://' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/cultura-ocio/');

        // RestangularProvider.addRequestInterceptor(function(elememt, operation, what, url) {
        //     console.log(url)
        //     if(sessionStorage.getItem('datosUsuario') && url.indexOf(sessionStorage.getItem('SERVIDOR'))>=0){
        //         console.log('entramosasdasda');
        //       var datosUsuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
        //       var clientID = datosUsuario.login;
        //       var secretKey = datosUsuario.secretKey;
        //       var path = url.substring(url.indexOf('/api'), url.length);
        //       var hash = CryptoJS.HmacSHA1(clientID + method + path + (post || ''), secretKey, {asBytes: true});
        //       headers['clientId']=clientID;
        //       headers['HmacSHA1']=hash;
        //       console.log('entramos');
        //     };
        // });

    }
]).
factory('RestangularSubtema', ['Restangular', function(Restangular) {
    return Restangular.withConfig(function(RestangularConfigurer) {
        RestangularConfigurer.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/cultura-ocio/subtema/');
        RestangularConfigurer.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var extractedData = data;
            extractedData.originalElement = angular.copy(extractedData);
            return extractedData;
        });
    });
}]).
factory('RestangularTema', ['Restangular', function(Restangular) {
    return Restangular.withConfig(function(RestangularConfigurer) {
        RestangularConfigurer.setBaseUrl('http://localhost:7777/sede/servicio/cultura/evento/');
        //RestangularConfigurer.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/cultura-ocio/tema/');
        RestangularConfigurer.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var extractedData = data;
            extractedData.originalElement = angular.copy(extractedData);
            return extractedData;
        });
    });
}]).
factory('RestangularLugar', ['Restangular', function(Restangular) {
    return Restangular.withConfig(function(RestangularConfigurer) {
        //RestangularConfigurer.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/cultura-ocio/lugar/');
        RestangularConfigurer.setBaseUrl('/sede/servicio/cultura/evento/lugar-realizacion');
    });
}]).
// factory('ProgramaService', ['$q', 'Restangular', function($q, Restangular) {
//     var result = [];
//     return {
//         comprobarPrograma: function(programa){
//             return Restangular.all('evento-zaragoza').all('programa')
//                 .getList({q: 'title==' + programa});
//         }
//     };
// }]).
// factory('Categorias', ['$q', 'RestangularTema', function($q, RestangularTema) {
//  var temas = {};
//     return {
//      getCategorias: function() {
//             return RestangularTema.all('evento-zaragoza').getList().$object;
//       //       var deferred = $q.defer();
//          // RestangularTema.all('evento-zaragoza').getList().then(function(res) {
//       //        // temas = res.originalElement;
//       //        // for(var i=0; i < temas.length; i++){
//       //        //   temas[i].subtema = false;
//       //        // };
//       //        return deferred.resolve(res);
//          // });
//      }
//     };
// }]).
value('poblacion', [
    { id: '10', title: 'Adultos' },
    { id: '9', title: 'Emprendedores' },
    { id: '8', title: 'Hombres' },
    { id: '1', title: 'Infancia' },
    { id: '5', title: 'Inmigrantes' },
    { id: '2', title: 'Jóvenes' },
    { id: '4', title: 'Mujeres' },
    { id: '7', title: 'Personas con discapacidad' },
    { id: '3', title: 'Personas Mayores' },
    { id: '6', title: 'Población en general' }
]).
value('valores', [
    { id: '1', title: 'Accesible y adaptada' },
    { id: '2', title: 'Comprometida' },
    { id: '3', title: 'Creativa' },
    { id: '4', title: 'Divertida' },
    { id: '5', title: 'Formativa' },
    { id: '6', title: 'Igualdad' },
    { id: '7', title: 'Innovadora' },
    { id: '8', title: 'Participativa' },
    { id: '9', title: 'Pensamiento crítico' },
    { id: '10', title: 'Respeto' },
    { id: '11', title: 'Sociabilidad' }
]).
value('portales', [ // usr se corresponde con usr_agenda (Tabla ACTIVIDADES.USUARIOS)
    {
        id: '1',
        title: 'Web Municipal',
        usr: [
            '1',
            '167' /* semzaragoza */
        ]
    },
    { id: '2', title: 'CIPAJ', usr: ['2'], link: 'actividades/juvenil' },
    { id: '3', title: 'Zaragoza Activa', usr: ['3'] },
    { id: '4', title: 'Zaragoza Congresos', usr: ['4'] },
    { id: '5', title: 'Intranet', usr: ['1000'] },
    { id: '6', title: 'Mayor', usr: ['76', '77'] },
    {
        id: '7',
        title: 'Centros Cívicos',
        usr: [
            '35', /* cservicio: bvaldearcos, participacion, programacion */
            '51', /* brurales */
            '131', /* ccasablanca */
            '132', /* ccasetas */
            '133', /* cesquinas */
            '135', /* cgarrapinillos */
            '136', /* cjuslibol */
            '137', /* calmozara */
            '138', /* ccartuja */
            '139', /* clajota */
            '140', /* cmiralbueno */
            '143', /* coliver */
            '144', /* cpenaflor */
            '145', /* cebro */
            '147', /* callende */
            '148', /* csanjose */
            '149', /* cisabel */
            '150', /* cjorge */
            '151', /* ctorrero */
            '153', /* cvaldefierro */
            '154', /* centrouniversidad */
            '155', /* cnorte */
            '168', /* casamujer */
            '163', /* cdelicias */
        ]
    },
    { id: '8', title: 'Comercio', usr: ['8'] },
    { id: '9', title: 'Mercados', usr: [] },
    { id: '10', title: 'ZCultura', usr: [] },
    { id: '11', title: 'Fiestas del Pilar', usr: [] },
    { id: '12', title: 'Navidad', usr:[] },
    { id: '13', title: 'Bibliotecas', usr:['24'] },
    { id: '14', title: 'Medio Ambiente', usr:['158'] },
    { id: '15', title: 'Primera Página', usr:[] },
    { id: '16', title: 'Verano', usr:[] },
    { id: '17', title: 'Museos', usr:['42'] },
    { id: '18', title: 'PICH', usr:[] },
    { id: '20', title: 'Zaragoza Deporte', usr:['169'] },
    { id: '21', title: 'Etopia', usr:['156'] },
    { id: '22', title: 'Educación', usr:['33'] },
    { id: '19', title: 'Casa Culturas', usr:['32'] }
]).
value('tiposPrecio', [
    { title: 'Gratuita' },
    { title: 'Anticipada' },
    { title: 'Taquilla' },
    { title: 'Normal' },
    { title: 'Bonificada' },
    { title: 'Grupo' },
    { title: 'Jóvenes' },
    { title: 'Niños/as' },
    { title: 'Jubilados/Pensionistas' },
    { title: 'Desempleados' },
    { title: 'Visita Guiada' },
    { title: 'Centros Educativos' },
    { title: 'Familiar' },
    { title: 'Estudiantes' }
]).
value('formatoActividad', [
    'Conferencias',
    'Congresos y Jornadas',
    'Exposiciones',
    'Visitas Guiadas',
    'Campamentos y colonias',
    'Colonias urbanas',
    'Campos de voluntariado',
    'Cursos y talleres',
    'Excursiones y viajes',
    'Exhibición, proyección, competición',
    'Ferias',
    'Fiestas',
    'Festivales',
    'Universidades de verano',
    'Otras'
]).
value('origenes', [
    'Banco de actividades'
]);