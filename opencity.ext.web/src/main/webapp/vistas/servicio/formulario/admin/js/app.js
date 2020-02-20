var app = angular.module('app', [
    'ngRoute',
    'ngAnimate',
    'restangular',
    'ui.bootstrap',
    'infinite-scroll',
    'ngSanitize',
    'ui.select',
    'leaflet-directive',
    'angularShamSpinner',
    'app.controllers',
    'Gesweb.filters',
    'Gesweb.services',
    'Gesweb.directives',
    'checklist-model',
    'app.directives'
]).config(['$routeProvider', '$locationProvider', 'RestangularProvider', '$httpProvider', 'uiSelectConfig',
    function($routeProvider, $locationProvider, RestangularProvider, $httpProvider, uiSelectConfig) {
		uiSelectConfig.theme = 'bootstrap';
        $httpProvider.defaults.headers.common['Accept'] = 'application/json';
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
        // Version 1.4.0 | setResponseExtractor (para metadata en la respuesta) y setListTypeIsArray están deprecated
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

        RestangularProvider.setRestangularFields({
            id: 'service_request_id'
        });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-list.html',
            controller: 'ListCtrl'
        }).
        when('/ayuda', {
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-ayuda.html',
            controller: 'ListCtrl'
        }).
        when('/estadistica', {
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-estadistica.html',
            controller: 'EstadisticaCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalleWgs84('quejas-sugerencias');
                }
            }
        }).
        when('/new', {
            controller: 'CreateCtrl',
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-details.html'
        }).
        when('/newOperador', {
            controller: 'CreateCtrl',
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-details-operator.html'
        }).
        when('/newParques', {
            controller: 'CreateCtrl',
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/ticketing-details-parques.html'
        }).
        when('/group', {
            controller: 'GroupCtrl',
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/group-list.html'
        }).
        when('/category', {
            controller: 'CategoryCtrl',
            templateUrl: '/cont/vistas/servicio/quejas-sugerencias/admin/partials/category-list.html'
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');

        //RestangularProvider.setBaseUrl('//' + window.sessionStorage.getItem('SERVIDOR') + '/api/recurso/open311');
        RestangularProvider.setBaseUrl('/sede/servicio/');
    }
]).factory('RestangularEntidades', ['Restangular',
    function(Restangular) {
        return Restangular
            .withConfig(function(RestangularConfigurer) {
            	RestangularConfigurer.setBaseUrl('/sede/servicio/quejas-sugerencias/entidad-externa');
            });
    }
]).factory('RestangularPortalero', ['Restangular',
    function(Restangular) {
        return Restangular
            .withConfig(function(RestangularConfigurer) {
            	RestangularConfigurer.setBaseUrl('/sede/servicio/portalero');
            });
    }
]).factory('CategoriesService', ['Restangular', function(Restangular) {
		
        var defaults = {};
        Restangular.all('category').getList({results_only:false}).then(function(data) {
            defaults = data;
            sessionStorage.setItem('reqcategories', JSON.stringify(data));                        
        });
        
        
        var service = {
            categorias: {},
            save: function() {
                sessionStorage.reqcategories = angular.toJson(service.categorias);
            },
            restore: function() {
                // Pull from sessionStorage
                service.categorias = angular.fromJson(sessionStorage.reqcategories) || defaults
                return service.categorias;
            }
        };
        // Immediately call restore from the session storage
        // so we have our user data available immediately
        service.restore();
        return service;
    }])
