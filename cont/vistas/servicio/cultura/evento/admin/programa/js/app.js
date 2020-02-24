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
        RestangularProvider.setRequestInterceptor(function(elem, operation) {
            if (operation === 'remove') {
                return undefined;
            }
            return elem;
        });
        // Modificamos la petición del servicio para obtener la ruta relativa de las imagenes (imagen/nombre.ext)
        RestangularProvider.addRequestInterceptor(function(element, operation, what, url) {
            if (element !== null && angular.isDefined(element)) {
                // Corregimos si alguna imagen actual está mal formada
                if (angular.isDefined(element.image)) { element.image = element.image.split('/actividades/').pop(); };
                // FIXME: Si la imagen no tiene el directorio inicial se lo asignamos
                if (angular.isDefined(element.image) && element.image.indexOf('imagen/') === -1) { element.image = 'imagen/' + element.image };
                if (angular.isDefined(element.imageAlt)) { element.imageAlt = element.imageAlt.split('/actividades/').pop(); };
                if (angular.isDefined(element.imageAlt) && element.imageAlt.indexOf('imagen/') === -1) { element.imageAlt = 'imagen/' + element.imageAlt };
            };
            return element;
        });
        // Modificamos la respuesta del servicio para obtener la ruta local de las imagenes
        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            if (operation === 'get') {
                if (data.image) { data.image = '/cont/paginas/actividades/' + data.image.split('/actividades/').pop(); };
                if (data.imageAlt) { data.imageAlt = '/cont/paginas/actividades/' + data.imageAlt.split('/actividades/').pop(); };
            };
            return data;
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
            // Obtenemos el objeto 'originalElement' (unrestangularized)
            if (!angular.isArray(extractedData)) {
                extractedData.originalElement = angular.copy(extractedData);
            }
            return extractedData;
        });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/programa/partials/programa-list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/programa/partials/programa-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle('programa');
                }
            }
        }).
        when('/new', {
            templateUrl: '/cont/vistas/servicio/cultura/evento/admin/programa/partials/programa-details.html',
            controller: 'CreateCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        otherwise({ redirectTo: '/' });
        $locationProvider.hashPrefix('');

        RestangularProvider.setBaseUrl('/sede/servicio/cultura/evento/admin/');

    }
]);