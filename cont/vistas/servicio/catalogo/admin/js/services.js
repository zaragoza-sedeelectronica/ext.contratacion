angular.module('Gesweb.services', [])

.factory("Query", function(){
    return {};
})

.factory("registroSvc", function(){
    var reg = {};
    return {
        getReg: function () {
            return reg;
        },
        setReg: function (reg) {
            this.reg = reg;
        }
    }; 
})

// .factory("UserService", ['$http', function($http){
//     return {
//         getUsuario: function() {
//             return JSON.parse(sessionStorage.getItem('datosUsuario'));
//         },
//         getServicios: function() {
//             return JSON.parse(sessionStorage.getItem('serviciosUsuario'));
//         },
//         setUsuario: function() {
//             $http({
//                 method: 'PUT',
//                 url: '/api/acceso/' + clientID,
//                 data: {
//                     "nombre": $scope.usuario.nombre,
//                     "apellido1": $scope.usuario.apellido1,
//                     "apellido2": $scope.usuario.apellido2,
//                     "email": $scope.usuario.email,
//                     //                    "servicioPorDefecto": $scope.usuario.servicioPorDefecto,
//                     //                    "seccionPorDefecto": $scope.usuario.seccionPorDefecto
//                 },
//                 headers: {}
//             }).success(function() {
//                 Informer.inform("Datos del usuario guardados correctamente.", "success");
//             }).error(function() {
//                 Informer.inform("Error al modificar datos del usuario.", "error");
//             });
//         },
//         setPassword: function() {
//             $http({
//                 method: 'PUT',
//                 url: '//' + SERVIDOR + '/api/acceso/' + clientID + '/change-password',
//                 data: {},
//                 headers: {'X-password': $scope.password}
//             }).success(function() {
//                 Informer.inform("Contraseña modificada correctamente.", "success");
//             }).error(function() {
//                 Informer.inform("Error al modificar la contraseña.", "danger");
//             });
//         }
//     }; 
// }])

.factory('Informer', ['$rootScope', '$timeout', function($rootScope, $timeout) {

    var messages = [];
    var Informer = {};

    Informer.inform = function(msg, type) {

        messages.push({
            msg: msg,
            type: type
        });

        //$rootScope.$apply();

        $timeout(function() {
            Informer.remove(messages[0]);
        }, 10000);
    };

    Informer.allInfos = function(d) {
        return messages;
    };

    Informer.remove = function(info) {
        messages.splice(messages.indexOf(info), 1);
    };

    return Informer;
}])

// Información: Cambios en la forma de devolver promises
// http://ath3nd.wordpress.com/2013/08/05/15/
// http://stackoverflow.com/questions/20982461/using-http-and-q-to-fetch-data/20982497#20982497

.factory('Dao', ['$route', 'Restangular', '$q', function($route, Restangular, $q){
    var filters = {};
    return {
        getFiltros: function(){
            return filters;
        },
        setFiltros: function(f){
            filters = f;
        },
        limpiarFiltros: function(){
            filters = {};
            console.log('FiltrosDao limpios: ' + angular.toJson(filters));
        },
        listar: function(uri, param){
        	
        	var queryParams = generateQueryParams(param);
            
            //console.log(queryParams);
            var deferred = $q.defer();
            Restangular.all(uri).getList(queryParams)
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        detalle: function(uri, id){
            // Si no hay id, obtiene el de la uri. Ver app.js: $routeProvider
            if(id === undefined) {id = $route.current.params.id}; 
            var deferred = $q.defer();
            Restangular.one(uri, id).get({refresh: 's'})
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        detalleWgs84: function(uri, id){
            // Si no hay id, obtiene el de la uri. Ver app.js: $routeProvider
            if(id === undefined) {id = $route.current.params.id}; 
            var deferred = $q.defer();
            Restangular.one(uri, id).get({refresh: 'a',srsname: 'wgs84'})
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        detallePorId: function(uri, id){
            var deferred = $q.defer();
            Restangular.one(uri, id).get()
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        detalleConQuery: function(uri, id) {
            // Si no hay id, obtiene el de la uri. Ver app.js: $routeProvider
            if(id === undefined) {id = $route.current.params.id}; 
            var deferred = $q.defer();
            Restangular.one(uri, id).get($route.current.params)
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        crear: function(uri, obj){
            var deferred = $q.defer();
            Restangular.all(uri).post(obj, {}, {})
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        actualizar: function(obj, queryParams, headers){
            var deferred = $q.defer();
            delete obj.originalElement;
            obj.put(queryParams || {}, headers || {})
            .then(function(res){
                deferred.resolve(res);
               // console.log('actualizar success' + angular.toJson(res));
            }, function(res){
            	// console.log('actualizar error' + angular.toJson(res));
                deferred.reject(res);
            });
            
            return deferred.promise;
        },
         actualizarPorId: function(obj, queryParams, headers){
                    var deferred = $q.defer();
                    delete obj.originalElement;
                    console.log(obj);
                    obj.route="/admin/"+obj.estacion.id;
                    obj.put(queryParams || {}, headers || {})
                    .then(function(res){
                        deferred.resolve(res);
                       // console.log('actualizar success' + angular.toJson(res));
                    }, function(res){
                    	// console.log('actualizar error' + angular.toJson(res));
                        deferred.reject(res);
                    });

                    return deferred.promise;
                },
        eliminar: function(obj){
            var deferred = $q.defer();
            obj.remove()
            .then(function(res){
                deferred.resolve(res);
            }, function(res){
                deferred.reject(res);
            });
            return deferred.promise;
        },
        buscar: function(uri, params){
            alert('metodo no implementado');  
        }
    };
 
}])

.run(['$rootScope', '$window', 'Restangular', function($rootScope, $window, Restangular){

    $rootScope.$on('$locationChangeStart', function(event, next, current) {
        
        // if (sessionStorage.getItem('datosUsuario') === null) {
        //     $window.location.href = '/gesweb';
        // } else {
        //     datosUsuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
        //     serviciosUsuario = JSON.parse(sessionStorage.getItem('serviciosUsuario'));
        //     $rootScope.usuario = datosUsuario;
        //     $rootScope.servicios = serviciosUsuario;
        //     clientID = datosUsuario.login;
        //     secretKey = datosUsuario.secretKey;
        // }

    });
    
//    Restangular.addFullRequestInterceptor(function(element, operation, route, url, headers, params, httpConfig){
//        if(sessionStorage.getItem('datosUsuario')){
//            var datosUsuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
//            var clientID = datosUsuario.login;
//            var secretKey = datosUsuario.secretKey;
//            var path = url.substring(url.indexOf('/api'), url.length);
//
//            if(operation === 'getList' || operation === 'get'){
//                operation = 'get';
//                element = '';
//            } else {
//            	element = $locationProvider.queryString(element);
//            }
//
//            if(!_.isEmpty(params)){
//                path = path + '?' + EncodeQueryData(params);
//            }
//            
//            function EncodeQueryData(data) {
//                var ret = [];
//                for (var d in data) {
//                	if (angular.isDefined(data[d]) && data[d] !== null) {
//                		ret.push(encodeURIComponent(d).replace(/%20/g, "+") + "=" + encodeURIComponent(data[d]).replace(/%20/g, "+").replace(/%2C/g, ","));
//                	}
//                }
//                return ret.join("&");
//            }
//            var hash = CryptoJS.HmacSHA1(clientID + angular.uppercase(operation) + path + (element || ''), secretKey, {asBytes: true});
//            console.log(clientID + angular.uppercase(operation) + path + (element || ''));
//            console.log(hash.toString());
//            headers['clientId']=clientID;
//            headers['HmacSHA1']=hash;
//        }
//    });
    
}])



function generateQueryParams(param) {
	
	var queryParams = {};
    _.each(param, function(value, key) {
        if(key === 'q') {
            // Comprobamos si param q es un Array (obligatorio)                    
            if(angular.isArray(param.q)){
                var fiqlString = '';
                for(item in param.q){
                    // Preparamos el string para consulta FIQL
                    // Si el valor de param.q es string añadimos *
                    if(typeof param.q[item].value === 'string'
                    	&& param.q[item].value.indexOf('*') <= 0
                    	&& !param.q[item].exact) {
                    	param.q[item].value += '*';
                    }
                    if(angular.isUndefined(param.q[item].op)){
                        // fiqlString[param.q[item].key] = param.q[item].key + '==' + param.q[item].value;
                        if (angular.isArray(param.q[item].value)) {
                            // Una comparación por cada valor
                            for (var i = 0; i < param.q[item].value.length; i++) {
                                if (i > 0) { fiqlString += ','; }
                                fiqlString += param.q[item].key + '==' + param.q[item].value[i];
                            }
                        } else {
                            fiqlString += param.q[item].key + '==' + param.q[item].value; //+ "*";
                        }
                    } else { // Viene param op (operator FIQL). Ejemplo: '=lt='
                        // fiqlString[param.q[item].key] = param.q[item].key + param.q[item].op + param.q[item].value;
                        fiqlString += param.q[item].key + param.q[item].op + param.q[item].value;
                    }
                    if (param.operator && param.operator === 'OR') {
                    	fiqlString += ',';
                    } else {
                    	fiqlString += ';';
                    }
                }
                // Eliminamos último ';'
                fiqlString = fiqlString.substring(0, fiqlString.length - 1);     
            } else {
                throw 'Error DAO: << q >> no es array';
            }
            // Underscore.js | Extrae los values del array
            // queryParams["q"] = _.values(fiqlString).toString(); 
            queryParams['q'] = fiqlString;
        } else {
        	if (key !== 'operator') {
        		queryParams[key] = value;
        	}
        }
    });
	
	return queryParams;
}

function generateQueryString(param) {
	var queryParams = generateQueryParams(param);
	var queryString = "";
	_.each(queryParams, function(v, o) {
		if (o !== 'start' && o !== 'rows' && o !== 'sort') {
    		queryString = queryString + "&" + o + "=" + v;
    	}
	});
	
	return queryString;
}
