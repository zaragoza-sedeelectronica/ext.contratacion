angular.module('app.controllers', [])

    .controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', function($scope, $window, $location, $filter, Informer, $route) {

        $scope.tipo = [
            { id: 'obra', text: 'obra' },
            { id: 'ocupacion', text: 'ocupacion' },
            { id: 'derribo', text: 'derribo' }
        ];
        $scope.anyo = [
            { id: '2010', text: '2010' },
            { id: '2011', text: '2011' },
            { id: '2012', text: '2013' },
            { id: '2013', text: '2013' },
            { id: '2014', text: '2014' },
            { id: '2015', text: '2015' },
            { id: '2016', text: '2016' },

        ];
        datosUsuario = JSON.parse(sessionStorage.getItem('datosUsuario'));
        $scope.linkInforme = CryptoJS.HmacSHA1("informe#" + datosUsuario.login, datosUsuario.secretKey, { asBytes: true }).toString();
        $scope.listar = function() {
            $location.path('#/');
        };

        $scope.crear = function() {
            // Si estamos copiando un punto y se quiere crear uno nuevo, tenemos que recargar la vista
            if ($location.path('/new')) {
                copiaMapeoSemantico = null;
                $route.reload();
            }
            $location.path('/new');
        };
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;
    }])

    .controller('ListCtrl', ['$scope', 'Restangular', 'Informer', function($scope, Restangular, Informer) {

        $scope.order = 'areaReferencia asc';
        $scope.reverse = false;

        var start = 0;
        var datosListados = [];
        var q = '';

        $scope.busy = false;
        $scope.loadMore = function() {

            if ($scope.busy)
                return;
            $scope.busy = true;
            if ($scope.query_portal) {
                q = q + 'id==' + $scope.query_portal.parcela.replace(',', '-');
            }
            if ($scope.query_tipo) {
                if (q.length > 0) {
                    q = q + ';';
                }
                q = q + 'expedientePrivate.tipo==' + $scope.query_tipo;

            }
            if ($scope.query_anyo) {
                if (q.length > 0) {
                    q = q + ';';
                }
                q = q + 'expedientePrivate.creationDate=ge=' + $scope.query_anyo + '-01-01T00:00:00Z;expedientePrivate.creationDate=le=' + $scope.query_anyo + '-12-31T00:00:00Z';

            }
            var expediente = '';
            if ($scope.query_expediente) {
                expediente = $scope.query_expediente.replace('-', '/');
            }
            Restangular.all('licencia-obra').getList({ rows: 50, start: start, expediente: expediente, q: q, sort: $scope.order }, {}).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.totalCount = data.totalCount;
                $scope.busy = false;
            });
            start += 50;
        };
        $scope.ordenar = function(orden) {
            $scope.order = orden;
            $scope.buscar();
        }
        $scope.buscar = function() {
            q = '';
            if ($scope.query_portal) {
                q = q + 'id==' + $scope.query_portal.parcela.replace(',', '-');
            }
            if ($scope.query_tipo) {
                if (q.length > 0) {
                    q = q + ';';
                }
                q = q + 'expedientePrivate.tipo==' + $scope.query_tipo;

            }
            if ($scope.query_anyo) {
                if (q.length > 0) {
                    q = q + ';';
                }
                q = q + 'expedientePrivate.creationDate=ge=' + $scope.query_anyo + '-01-01T00:00:00Z;expedientePrivate.creationDate=le=' + $scope.query_anyo + '-12-31T00:00:00Z';

            }
            var expediente = '';
            if ($scope.query_expediente) {
                expediente = $scope.query_expediente.replace('-', '/');
            }

            start = 0;
            datosListados = [];
            Restangular.all('licencia-obra').getList({ rows: 50, start: start, q: q, expediente: expediente, sort: $scope.order }, {}).then(function(data) {
                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.totalCount = data.totalCount;
            });
            start += 50;

        };

    }])

    .controller('EditCtrl', ['$scope', 'Dao', 'registro', '$location', 'Restangular', 'Informer', function($scope, Dao, registro, $location, Restangular, Informer) {
        console.log(registro);
        $scope.registro = registro;
        $scope.guardar = function() {
            var o = Restangular.one('licencia-obra/' + $scope.registro.id);
            if (angular.isDefined($scope.registro.iae.identifier)) {
                o.iae = $scope.registro.iae.identifier;
            }
            if (angular.isDefined($scope.registro.emplazamiento.id)) {
                o.idVia = $scope.registro.emplazamiento.id;
            }
            Dao.actualizar(o, {}).then(function() {
                Informer.inform("El registro se ha editado correctamente.", "success");
                // $location.path('/');
            }, function() {
                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
                // $location.path('/');
            });

        };

    }]);