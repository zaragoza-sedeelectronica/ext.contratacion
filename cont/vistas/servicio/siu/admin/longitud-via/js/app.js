const SERVICE = '/sede/servicio/urbanismo-infraestructuras/equipamiento/';
const QUERY = 'datos-movilidad/longitud-por-tipo-via';
const FOLDER = 'longitud-via';

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

        RestangularProvider.addRequestInterceptor(function(element, operation, what, url) {
            if (operation === 'remove') {
                return undefined;
            }
            return element;
        });

        // Extraer resultados
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
            // Listado de datos obtenido
            return extractedData;
        });

        $routeProvider.
        when('/', {
            templateUrl: '/cont/vistas/servicio/siu/admin/' + FOLDER + '/partials/list.html',
            controller: 'ListCtrl'
        }).
        when('/edit/:id', {
            templateUrl: '/cont/vistas/servicio/siu/admin/' + FOLDER + '/partials/details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function(Dao, $route) {
                    return Dao.detalle(QUERY);
                }
            }
        }).
        when('/new', {
            templateUrl: '/cont/vistas/servicio/siu/admin/' + FOLDER + '/partials/details.html',
            controller: 'EditCtrl',
            resolve: {
                registro: function() {
                    return {};
                }
            }
        }).
        when('/ayuda', {
            templateUrl: '/cont/vistas/servicio/siu/admin/partials/ayuda.html',
            controller: 'ListCtrl'
        }).
        otherwise({
            redirectTo: '/'
        });
        $locationProvider.hashPrefix('');
        RestangularProvider.setBaseUrl(SERVICE);
    }
]);