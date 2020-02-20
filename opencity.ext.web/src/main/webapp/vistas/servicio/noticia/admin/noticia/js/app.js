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
    'app.controllers',
    'Gesweb.filters',
    'Gesweb.services',
    'Gesweb.directives'
]).config(['$routeProvider', '$locationProvider', 'RestangularProvider', '$httpProvider', 'uiSelectConfig',
    function($routeProvider, $locationProvider, RestangularProvider, $httpProvider, uiSelectConfig) {

        uiSelectConfig.theme = 'bootstrap';
        $httpProvider.defaults.headers.common['Accept'] = 'application/json';
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
        // Versión 1.4.0 | setResponseExtractor (para metadata en la respuesta) y setListTypeIsArray están deprecated
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var extractedData;
            if (operation === "getList") {
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
            // Obtenemos el objeto 'originalElement' (unrestangularized)
            if (!angular.isArray(extractedData)) {
                extractedData.originalElement = angular.copy(extractedData);
            }
            return extractedData;
        });

        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            //console.log(what);
            var d = data;
            if (operation === 'get' && what === '/') {
                if (d.dateCreated) { d.dateCreated = new Date(d.dateCreated); };
                if (d.expirationDate) { d.expirationDate = new Date(d.expirationDate); };
            };
            return d;
        });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/noticia/admin/noticia/partials/noticia-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/noticia/admin/noticia/partials/noticia-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('/');
                }
            }
        }).
        when('/new', {
            templateUrl: '/cont/vistas/servicio/noticia/admin/noticia/partials/noticia-details.html',
            controller: 'CreateCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        otherwise({ redirectTo: '/' });
        $locationProvider.hashPrefix('');

        RestangularProvider.setBaseUrl('/sede/servicio/noticia/admin/');

        RestangularProvider.setRequestInterceptor(function(elem, operation) {
            if (operation === "remove") {
                return undefined;
            }
            return elem;
        });

    }
]);