var app = angular.module('app', [
    'ngRoute',
    'restangular',
    'ui.bootstrap',
    'infinite-scroll',
    'angularShamSpinner',
    'ngSanitize',
    'ui.select',
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
            console.log(what);
            var d = data;
            if (operation === 'get' && what == 'catalogo') {
                if (d.issued) { d.issued = new Date(d.issued); };
                if (d.modified) { d.modified = new Date(d.modified); };
                if (d.pubDate) { d.pubDate = new Date(d.pubDate); };
                if (d.coberturaIni) { d.coberturaIni = new Date(d.coberturaIni); };
                if (d.coberturaFin) { d.coberturaFin = new Date(d.coberturaFin); };
                if (d.vigencia) { d.vigencia = new Date(d.vigencia); };
            };
            return d;
        });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/catalogo/admin/partials/dataset-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/catalogo/admin/partials/dataset-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('catalogo');
                }
            }
        }).
        when('/new', {
            controller: 'CreateCtrl',
            templateUrl: '/cont/vistas/servicio/catalogo/admin/partials/dataset-details.html'
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');

        //RestangularProvider.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/');
        RestangularProvider.setBaseUrl('/sede/servicio/');

        RestangularProvider.setRequestInterceptor(function(elem, operation) {
            if (operation === "remove") {
                return undefined;
            }
            return elem;
        });

    }
])
.factory('RestangularBasic', function(Restangular) {
    return Restangular.withConfig(function(RestangularConfigurer) {
        // RestangularConfigurer.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/dataset');
        // RestangularConfigurer.setBaseUrl('https://www.zaragoza.es/sede/servicio/catalogo');
        RestangularConfigurer.setBaseUrl('/opencityext/servicio/');
    });
});