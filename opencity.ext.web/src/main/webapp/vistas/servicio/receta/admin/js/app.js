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
]).config(['$routeProvider', '$locationProvider', 'RestangularProvider', '$httpProvider',
    function($routeProvider, $locationProvider, RestangularProvider, $httpProvider) {

        $httpProvider.defaults.headers.common['Accept'] = 'application/json';
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
        // Versión 1.4.0 | setResponseExtractor (para metadata en la respuesta) y setListTypeIsArray están deprecated
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var extractedData;
            if (operation === "getList") {
                extractedData = data.result;
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

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/receta/admin/partials/receta-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/receta/admin/partials/receta-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('receta');
                }
            }
        }).
        when('/new', {
            controller: 'CreateCtrl',
            templateUrl: '/cont/vistas/servicio/receta/admin/partials/receta-details.html'
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');

        // RestangularProvider.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/turismo/gastronomia');
        RestangularProvider.setBaseUrl('/sede/servicio/');

        RestangularProvider.setRequestInterceptor(function(elem, operation) {
            if (operation === "remove") {
                return undefined;
            }
            return elem;
        });

    }
]).
factory('RestangularIngrediente', function(Restangular) {
    return Restangular.withConfig(function(RestangularConfigurer) {
        RestangularConfigurer.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/turismo/gastronomia');
    });
});
