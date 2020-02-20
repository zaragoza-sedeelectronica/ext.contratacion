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
                };
                if (d.inicioDestacado) { d.inicioDestacado = new Date(d.inicioDestacado); };
                if (d.finDestacado) { d.finDestacado = new Date(d.finDestacado); };
                if (d.inicioDestacadoAlt) { d.inicioDestacadoAlt = new Date(d.inicioDestacadoAlt); };
                if (d.finDestacadoAlt) { d.finDestacadoAlt = new Date(d.finDestacadoAlt); };
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
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/banco-index.html',
            controller: 'MainCtrl'
        }).

        when('/activities', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/actividad-list.html',
            controller: 'ListCtrl'
        }).
        when('/activities/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/actividad-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/activities/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/actividad-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco');
                }
            }
        }).

        when('/scopes', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/ambito-list.html',
            controller: 'ScopeListCtrl'
        }).
        when('/scopes/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/ambito-details.html',
            controller: 'ScopeEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/scopes/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/ambito-details.html',
            controller: 'ScopeEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/ambito');
                }
            }
        }).

        when('/entities', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/entidad-list.html',
            controller: 'EntityListCtrl'
        }).
        when('/entities/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/entidad-details.html',
            controller: 'EntityEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/entities/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/entidad-details.html',
            controller: 'EntityEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/entidad');
                }
            }
        }).

        when('/monitors', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/monitor-list.html',
            controller: 'MonitorListCtrl'
        }).
        when('/monitors/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/monitor-details.html',
            controller: 'MonitorEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/monitors/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/monitor-details.html',
            controller: 'MonitorEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/monitor');
                }
            }
        }).

        when('/districts', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/distrito-list.html',
            controller: 'DistrictListCtrl'
        }).
        when('/districts/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/distrito-details.html',
            controller: 'DistrictEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/districts/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/distrito-details.html',
            controller: 'DistrictEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/distrito');
                }
            }
        }).

        when('/holidays', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/festivo-list.html',
            controller: 'HolidayListCtrl'
        }).
        when('/holidays/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/festivo-details.html',
            controller: 'HolidayEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/holidays/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/festivo-details.html',
            controller: 'HolidayEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/festivo');
                }
            }
        }).

        when('/requests', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-list.html',
            controller: 'RequestListCtrl'
        }).
        when('/requests/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-details.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/requests/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-details.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/solicitud');
                }
            }
        }).
        when('/requests/mail-entity/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-mail-ofertante.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/solicitud');
                }
            }
        }).
        when('/requests/mail-requester/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-mail-solicitante.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/solicitud');
                }
            }
        }).
        when('/requests/mail-entity-ungranted/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-mail-ofertante-no-concedida.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/solicitud');
                }
            }
        }).
        when('/requests/mail-requester-ungranted/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-mail-solicitante-no-concedida.html',
            controller: 'RequestEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/solicitud');
                }
            }
        }).
        when('/requests/report', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/solicitud-report.html',
            controller: 'RequestReportCtrl'
        }).

        when('/invoices', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/factura-list.html',
            controller: 'InvoiceListCtrl'
        }).
        when('/invoices/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/banco/partials/factura-details.html',
            controller: 'InvoiceEditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('evento/admin/banco/factura');
                }
            }
        }).

//        when('/ayuda', {
//            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/acto/partials/acto-ayuda.html',
//            controller: 'ListCtrl'
//        }).
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
value('tiposDuracion', [
    'Curso escolar',
    'Corta duraci\u00F3n',
    'Excursi\u00F3n',
    'Actividad vacacional'
]).
value('meses', [
    { id: '01', title: 'Enero' },
    { id: '02', title: 'Febrero' },
    { id: '03', title: 'Marzo' },
    { id: '04', title: 'Abril' },
    { id: '05', title: 'Mayo' },
    { id: '06', title: 'Junio' },
    { id: '07', title: 'Julio' },
    { id: '08', title: 'Agosto' },
    { id: '09', title: 'Septiembre' },
    { id: '10', title: 'Octubre' },
    { id: '11', title: 'Noviembre' },
    { id: '12', title: 'Diciembre' }
])
;


