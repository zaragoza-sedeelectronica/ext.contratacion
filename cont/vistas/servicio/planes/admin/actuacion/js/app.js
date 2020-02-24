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
    'leaflet-directive',
    'app.controllers',
    'Gesweb.filters',
    'Gesweb.services',
    'Gesweb.directives',
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

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/planes/admin/actuacion/partials/actuacion-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/planes/admin/actuacion/partials/actuacion-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('/');
                }
            }
        }).
        when('/new', {
            templateUrl: '/cont/vistas/servicio/planes/admin/actuacion/partials/actuacion-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');

        // RestangularProvider.setBaseUrl('http://' + window.sessionStorage.getItem('SERVIDOR') + '/sede/servicio');
        RestangularProvider.setBaseUrl('/sede/servicio/planes/admin/actuacion/');
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
]);
