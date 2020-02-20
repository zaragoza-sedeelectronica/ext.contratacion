angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'Dao',
    function($scope, $window, $location, $filter, Informer, $route, Dao) {

        $scope.listar = function() {
            Dao.limpiarFiltros();
            if ($location.path() == '/') { // ListCtrl
                $route.reload();
            } else {
                $location.path('/'); // DetailCtrl
            };
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            }

            $location.path('/new');

        };

        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao', 'Informer', '$location', 'Restangular', 'RestangularTema', 'origenes',
    function($scope, Dao, Informer, $location, Restangular, RestangularTema, origenes) {

		$scope.origenes = origenes;

        var params = Dao.getFiltros(); // Object
        if (angular.equals({}, params)) {
            params.start = 0;
        };
        $scope.filtrosParaListado = params.q;

        $scope.registros = [];

        $scope.busy = false;
        $scope.loadMore = function() {
            if ($scope.busy) return;
            $scope.busy = true;

            Dao.listar('tema', params).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                if (data.totalCount === 0) { Informer.inform("La búsqueda no ha producido ningún resultado", "warning"); }
                $scope.totalCount = data.totalCount;
                $scope.start = data.start;
                $scope.busy = false;
            }, function(err) {
                if (params.q !== null) {
                    Informer.inform("Se ha producido un error en la búsqueda: " + err.data.error, "danger");
                } else {
                    Informer.inform("Error en listado: " + err.data.error, "danger");
                };
            });
            params.start += 50;
        };
        
        $scope.category = [];
        RestangularTema.all('evento').all('categoria').getList().then(function(res) {
            //console.log(res.plain());
            $scope.category = res.plain();
        });
        
        $scope.datepickers = {fromDate: false, untilDate: false};
        $scope.toggleOpenDatePicker = function($event, which) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datepickers[which] = true;
        };


        $scope.limpiarFiltros = function(){
            $scope.qsearch = {};
        };

        $scope.buscar = function(qsearch) {
            var params = {};
            params.start = 0;
            params.rows = 2500;
            // params.sort = 'lastUpdated desc';
            params.q = [];
            _.each(qsearch, function(value, key) {
                var item = new Object();
                item.key = key; // Campo de busqueda. Ej: 'title'
                item.value = value; // Valor del campo de busqueda actual
                if(key === 'tema') {
                	item.key = 'category.id';
                	item.value = [];
                	for (var i = 0; i < value.length; i++) {
                		item.value.push(value[i].id);
                	}
                };
                if (key === 'lugar') {item.key = 'subEvent.location.title'};
                if (key === 'programa') {item.key = 'program.title'};
                if (key === 'organizer') {item.key = 'organizer.title'};
                if (key === 'performer') {item.key = 'performer.title'};
                if (key === 'blockZGZ16') {item.key = 'blockZGZ16.title'};
                if (key === 'fecha_desde' || key === 'fecha_hasta') {
                	var mm = item.value.getMonth() + 1;
                	if (mm <= 9) { mm = '0' + mm; }
                	var dd = item.value.getDate();
                	if (dd <= 9) { dd = '0' + dd; }
                	item.value = item.value.getFullYear() + '-' + mm + '-' + dd;
                };
                if (item.value !== '') { 
                	if (typeof item.value === "string") {
                		item.value = item.value.replace(";", "");
                	}
                	params.q.push(item); }; // Si esta vacío el valor no se añade
                //delete qsearch[key];
            });
            Dao.setFiltros(params);
            $scope.filtrosParaListado = params.q;
            Dao.listar('estadisticas', params).then(function(data) {
                $scope.totalCount = data.totalCount;
                if (data.totalCount == 0) {
                	Informer.inform("La búsqueda no ha producido ningún resultado", "warning");
                }
            	$scope.registros = data;
                $scope.totales = {
                		inscritos: 0,
                		asistentes: 0,
                		finalizados: 0,
                		valoracion: 0
                };
            	if ($scope.registros.length > 0) {
            		var actValoradas = 0;
                	for (var i = 0; i < $scope.registros.length; i++) {
                		$scope.totales.inscritos += $scope.registros[i].inscritos;
                		$scope.totales.asistentes += $scope.registros[i].asistentes;
                		$scope.totales.finalizados += $scope.registros[i].finalizados;
                		if ($scope.registros[i].valoracion != null) {
                			actValoradas++;
                			$scope.totales.valoracion += parseFloat($scope.registros[i].valoracion);
                		}
                	}
                	$scope.totales.valoracion = $scope.totales.valoracion / actValoradas;
            	}
            });
        };

        $scope.detalle = function(id, qsearch) {
            var param = {};
//            params.start = 0;
//            params.rows = 500;
            // params.sort = 'lastUpdated desc';
            param.q = [];
            _.each(qsearch, function(value, key) {
            	if (key.startsWith("user_")) {
                    var item = new Object();
                    item.key = key; // Campo de busqueda
                    item.value = value; // Valor del campo de busqueda actual
                    if (item.value !== '') { 
                    	param.q.push(item);
                    } // Si esta vacío el valor no se añade
            	}
            });
            
            var fiqlString = '';
            for(item in param.q){
                // Preparamos el string para consulta FIQL
                // Si el valor de param.q es string añadimos *
                if(typeof param.q[item].value === "string") {param.q[item].value += '*';};
                if(angular.isUndefined(param.q[item].op)){
                    // fiqlString[param.q[item].key] = param.q[item].key + '==' + param.q[item].value;
                	if (angular.isArray(param.q[item].value)) {
                		// Una comparación por cada valor
                		for (var i = 0; i < param.q[item].value.length; i++) {
                			if (i > 0) { fiqlString += ','; }
                			fiqlString += param.q[item].key + '==' + param.q[item].value[i];
                		}
                	}
                	else {
                		fiqlString += param.q[item].key + '==' + param.q[item].value; //+ "*";
                	}
                } else { // Viene param op (operator FIQL). Ejemplo: '=lt='
                    // fiqlString[param.q[item].key] = param.q[item].key + param.q[item].op + param.q[item].value;
                    fiqlString += param.q[item].key + param.q[item].op + param.q[item].value;
                };
                fiqlString += ';';
            }
            // Eliminamos último ';'
            fiqlString = fiqlString.substring(0, fiqlString.length - 1);     

            var url = 'detail/' + id;
            if (fiqlString != '') {
            	url += '?q=' + fiqlString;
            }
            
            $location.url(url);
        };

    }
])

.controller('DetailCtrl', ['$scope', '$http', 'Dao', 'Informer', '$location', 'Restangular',
    function($scope, $http, Dao, Informer, $location, Restangular) {
		Dao.detalleConQuery('estadisticas').then(function(data) {
			$scope.registro = data.result[0];
		});
	}
]);


// .controller('SubtemasCtrl', ['$scope', 'RestangularSubtema', 'limitToFilter',
//     function($scope, RestangularSubtema, limitToFilter) {

//         $scope.subtemasArray = [];
//         RestangularSubtema.all('evento-zaragoza').getList({
//             q: 'tema.id==' + $scope.registro.id
//         }).then(function(response) {
//             $scope.subtemasArray = response;
//         });

//     }
// ]);
