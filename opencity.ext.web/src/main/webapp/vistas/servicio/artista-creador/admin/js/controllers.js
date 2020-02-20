angular.module('app.controllers', ['ngSanitize'])

    .directive('fgSelectUriInModal', ['$http', '$uibModal', function($http, $uibModal) {
        return {
            restrict: 'E',
            templateUrl: '/cont/vistas/servicio/artista-creador/admin/partials/fgSelectUri.html',
            scope: {
                uri: '=ngModel',
                por: '=',
                modo: '@'
            },
            link: function(scope, element, attrs) {
                scope.uri = scope.uri || "";

                scope.consultaSPARQL = function(p) {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/cont/vistas/servicio/artista-creador/admin/templates/modalSelectURI.html',
                        resolve: {
                            uri: function() {
                                return scope.uri;
                            }
                        },
                        controller: function($scope, $uibModalInstance, uri) {
                            var qFilters = "";
                            angular.forEach(
                                p.split(/\s+/),
                                function(r, index) {
                                    console.log(r);
                                    qFilters += " FILTER (REGEX(STR(?obj), \"" + r + "\", \"i\"))";
                                });
                            $scope.uri = uri;
                            var qSPARQL;
                            var qArtist = "select distinct ?uriCont as ?uri str(?obj) as ?nombre str(?img) as ?img" +
                                "   where  {" +
                                "   ?uriCont a dbpedia-owl:Artist." +
                                "      ?uriCont rdfs:label ?obj." +
                                "      OPTIONAL {" +
                                "         ?uriCont <http://es.dbpedia.org/property/imagen> ?img." +
                                "      }" +
                                //                          "      FILTER (REGEX(STR(?obj), \""+"jos"+"\", \"i\"))"+
                                //                          "      FILTER (REGEX(STR(?obj), \""+"cela"+"\", \"i\"))"+
                                qFilters +
                                "   }" +
                                "   LIMIT 10";
                            var qWork = "select distinct ?uriCont as ?uri str(?obj) as ?nombre str(?img) as ?img" +
                                "   where  {" +
                                "   ?uriCont a dbpedia-owl:Work." +
                                "      ?uriCont rdfs:label ?obj." +
                                "      OPTIONAL {" +
                                "         ?uriCont <http://es.dbpedia.org/property/imagen> ?img." +
                                "      }" +
                                //                          "      FILTER (REGEX(STR(?obj), \""+"jos"+"\", \"i\"))"+
                                //                          "      FILTER (REGEX(STR(?obj), \""+"cela"+"\", \"i\"))"+
                                qFilters +
                                "   }" +
                                "   LIMIT 10";
                            if (scope.modo == 'artist') {
                                qSPARQL = qArtist;
                            } else if (scope.modo == 'work') {
                                qSPARQL = qWork;
                            }
                            $http.get('http://dbpedia.org/sparql', {
                                params: {
                                    format: 'application/json',
                                    query: qSPARQL,
                                }
                            }).success(function(res) {
                                $scope.resSPARQL = angular.copy(res.results.bindings);
                                //                              angular.forEach(
                                //                                  res.results.bindings,
                                //                                  function(r, index) {
                                //                                      console.log(r.nombre.value, r.uri.value);
                                //                                  });
                                $scope.namedgraphs = res.data;
                            });
                            $scope.save = function() {
                                $uibModalInstance.close($scope.uri);
                            };
                            $scope.cancelar = function() {
                                $uibModalInstance.dismiss('cancel');
                            };
                        }
                    });

                    modalInstance.result.then(
                        function(dato) {
                            scope.uri = dato;
                        },
                        function() {
                            //alert('Modal dismissed at: ' + new Date());
                        });
                };

            }
        };
    }])

    .directive('tabla1n', ['$filter', function($filter) {

        return {
            restrict: 'E',
            templateUrl: '/cont/vistas/servicio/artista-creador/admin/partials/tabla.html',
            scope: {
                registros: '=para',
                path: '@',
                atributos: '='
            },

            controller: function($scope, $element, $attrs, $transclude) {

                console.debug($scope.atributos);
                $scope.pathFIQL = $scope.path + "?fl=" + crear($scope.atributos);

                function crear(atributos) {
                    var i, arrLen = atributos.length;
                    fl = "";
                    for (i = 0; i < arrLen; i++) {
                        fl += ("," + atributos[i].data);
                    }
                    return fl.substr(1);
                };
            },

            link: function(scope, element, attrs) {

                scope.anadirRegistro = function(registro) {
                    scope.registros = scope.registros || [];
                    var existe = $filter('filter')(
                        scope.registros, {
                            id: registro.id
                        }, true).length;
                    !existe ? scope.registros.push(angular.copy(registro)) : alert("El registro ya estaba asociado");
                    scope.regTempToAdd = null;
                };

                scope.eliminarRegistro = function(index) {
                    scope.registros.splice(index, 1);
                };

                scope.buscarPropiedad = function(registro, concatenadas) {
                    temp = concatenadas.split('.');
                    propiedad = angular.copy(registro);
                    var i, arrLen = temp.length;
                    for (i = 0; i < arrLen; i++) {
                        propiedad = propiedad[temp[i]];
                    }
                    return propiedad;
                };
            }
        };
    }])

    .controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', '$uibModal',
        function($scope, $window, $location, $filter, Informer,
            $route, $uibModal) {

            $scope.listar = function() {
                $location.path('#/');
            };

            $scope.crear = function() {
                // Si estamos copiando un punto y se quiere
                // crear uno nuevo, tenemos que recargar la
                // vista
                if ($location.path('/new')) {
                    copiaRegistro = null;
                    $route.reload();
                }

                $location.path('/new');

            };

            $scope.lanzarModalBusqueda = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: '/cont/vistas/servicio/artista-creador/admin/templates/modalBusqueda.html',
                    controller: function($scope,
                        $uibModalInstance) {

                        $scope.busqueda = function() {
                            $uibModalInstance.close();
                        };

                        $scope.cancelar = function() {
                            $uibModalInstance
                                .dismiss('cancel');
                        };
                    }
                });

                modalInstance.result.then(
                    function(selectedItem) {
                        // $scope.selected = selectedItem;
                    },
                    function() {
                        // $log.info('Modal dismissed at: '
                        // + new Date());
                    });
            };

            $scope.modoBusqueda = 0;
            $scope.allInfos = Informer.allInfos;
            $scope.remove = Informer.remove;

        }
    ])

    .controller('ListCtrl', ['$scope', 'Dao', function($scope, Dao) {

        var params = {};
        params.start = 0;
        $scope.registros = [];

        $scope.busy = false;
        // $scope.loadMore = function() {
        // if ($scope.busy)
        // return;
        // $scope.busy = true;
        Dao.listar('list', params).then(function(data) {
            $scope.registros = $scope.registros.concat(data);
            // De otro modo, se pierden los datos ya listados
            $scope.busy = false;
        });
        // params.start += 50;
        // };

    }])

    //Sobra RestangularTiposArtista?
    .controller('EditCtrl', ['$scope', 'Dao', 'registro', '$http', 'Restangular', 'RestangularTiposArtista', '$location', '$filter', 'Informer', '$modal',
        function($scope, Dao, registro, $http, Restangular, RestangularTiposArtista, $location, $filter, Informer, $modal) {

            $scope.registro = registro;
            $scope.groupTempToAdd = null;
            $scope.memberTempToAdd = null;

            Restangular.all('artista').all('tipo_artista')
                .getList().then(function(reg) {
                    $scope.types = reg;
                });

            Restangular.all('artista').all('red').getList()
                .then(function(reg) {
                    $scope.redes = reg;
                });

            $scope.nuevoEnlace = function(red, uri) {
                console.log("Nuevo enlace a", red);
                //$scope.registro.enlaces.push({red: red, uri: uri});
            };

            $scope.checkNewEnlace = function(red, uri) {
                console.log("Validar y guardar el enlace");
                if (uri != null && uri != '') {
                    $scope.registro.enlaces = $scope.registro.enlaces || [];
                    $scope.registro.enlaces.push({
                        red: red,
                        uri: uri
                    });
                }
            };

            $scope.eliminarGrupo = function(index) {
                $scope.registro.groups.splice(
                    $scope.registro.groups[index], 1);
            };

            $scope.anadirGrupo = function(group) {
                $scope.registro.groups = $scope.registro.groups || [];
                var existe = $filter('filter')(
                    $scope.registro.groups, {
                        id: group.id
                    }, true).length;
                !existe ? $scope.registro.groups.push(angular
                    .copy(group)) : alert("El grupo ya estaba asociado");
                $scope.groupTempToAdd = null;
            };

            $scope.eliminarMiembro = function(index) {
                $scope.registro.members.splice(
                    $scope.registro.members[index], 1);
            };

            $scope.anadirMiembro = function(member) {
                $scope.registro.members = $scope.registro.members || [];
                var existe = $filter('filter')(
                    $scope.registro.members, {
                        id: member.id
                    }, true).length;
                !existe ? $scope.registro.members.push(angular
                    .copy(member)) : alert("El miembro ya estaba asociado");
                $scope.memberTempToAdd = null;
            };

            $scope.getIndexEnlace = function(red) {
                var indexEnlace = -1;
                angular.forEach(
                    $scope.registro.enlaces,
                    function(enlace, index) {
                        if (enlace.id.red.id == red.id) {
                            indexEnlace = index;
                        }
                    });
                return indexEnlace;
            };

            $scope.checkEnlace = function(index) {
                var enlace = $scope.registro.enlaces[index];
                console.log("Validar y guardar index enlace", index, enlace);
                var uri = enlace.uri;
                if (uri == null || uri == '' || uri.length == 0) {
                    $scope.registro.enlaces.splice(index, 1);
                }
            };

            $scope.createEnlaceIfNotExist = function(red) {
                if ($scope.getIndexEnlace(red) < 0) {
                    $scope.registro.enlaces = $scope.registro.enlaces || [];
                    $scope.registro.enlaces.push({
                        id: {
                            red: {
                                id: red.originalElement.id
                            },
                            artistaBase: {
                                id: registro.id
                            }
                        }
                    });
                    indexEnlace = $scope.registro.enlaces.length;
                }
            };

            $scope.files = [];
            $scope.$on("fileSelected", function(event, args) {
                $scope.$apply(function() {
                    $scope.files.push(args.file);
                });
            });

            $scope.getFile = function(file, type) {
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function(event) {
                    var title = file.name.replace(/[^a-z0-9\.]/gi, '_').toLowerCase();
                    var len = title.indexOf(".");
                    if (len > 30) {
                        len = 30;
                    }
                    var description = title.substring(0, len);
                    $scope.imgTemp64 = event.target.result;
                    $scope.registro.media_name = title;
                    $scope.registro.media_description = description;
                    $scope.registro.media_body = encodeURIComponent($scope.imgTemp64);
                    console.log(event.target.result);
                };
            };

            $scope.guardar = function() {
                console.log($scope.registro);
                if ($location.path() === "/new") {
                    Dao
                        .crear('artista', $scope.registro)
                        .then(
                            function() {
                                Informer
                                    .inform(
                                        "El registro se ha editado correctamente.",
                                        "success");
                                $location.path('/');

                            },
                            function() {
                                Informer
                                    .inform(
                                        "Se ha producido un error al guardar el registro.",
                                        "danger");
                                //                      $location.path('/');
                            });
                } else {
                    Dao
                        .actualizar($scope.registro)
                        .then(
                            function() {
                                Informer
                                    .inform(
                                        "El registro se ha editado correctamente.",
                                        "success");
                                $location.path('/');

                            },
                            function() {
                                Informer
                                    .inform(
                                        "Se ha producido un error al guardar el registro.",
                                        "danger");
                                $location.path('/');
                            });
                }
            };

            $scope.anadirObra = function(registro) {
                $scope.registro.works = $scope.registro.works || [];
                if (!angular.isUndefined(registro.id)) {
                    var existe = $filter('filter')(
                        $scope.registro.works, {
                            id: registro.id
                        },
                        true
                    ).length;
                    existe ? alert("El registro ya estaba asociado") : $scope.registro.works.push(angular.copy(registro));
                    console.log("registro.id", registro.id);

                } else {
                    console.log("Petición crear nueva obra");
                    registro.artists = [{
                        id: $scope.registro.id
                    }];

                    Dao.crear('artista/obra', registro).then(function(res) {
                        $scope.registro.works.push(angular.copy(res.originalElement));
                        console.log(res);
                        //            Informer.inform("El registro se ha creado correctamente.", "success");
                        //                          $serlocation.path('/');
                    }, function() {
                        Informer.inform("Se ha producido un error al crear la obra.", "danger");
                        //                          $location.path('/');
                    });
                }
            };

            $scope.lanzarModalGestionObra = function(modo, dato) {

                var modalInstance = $modal.open({
                    templateUrl: 'templates/obraModal.html',
                    controller: function($scope, $modalInstance, registro) {
                        $scope.agnoVal = /^\d{4}$/;
                        $scope.registro = registro;
                        $scope.save = function() {
                            $modalInstance.close($scope.dato);
                        };
                        $scope.borrar = function() {
                            if ($scope.eliminarObra == 'completamente') {
                                var obra = Restangular.all('artistas').one('obra', $scope.dato.id);
                                Dao.eliminar(obra).then(function() {
                                    Informer.inform("El registro se ha editado correctamente.", "success");
                                    $location.path('/');
                                }, function() {
                                    Informer.inform("Se ha producido un error al guardar el registro.", "danger");
                                    $location.path('/');
                                });
                            } else {
                                registro.works.splice(dato, 1);
                            }
                            $modalInstance.close($scope.dato);
                        };
                        $scope.cancelar = function() {
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.modo = modo;

                        if (modo == 'edit' || modo == 'delete') {
                            $scope.dato = registro.works[dato];
                        }
                    },
                    resolve: {
                        registro: function() {
                            return $scope.registro;
                        }
                    }
                });

                modalInstance.result.then(
                    function(dato) {
                        if (modo == 'add') {
                            $scope.anadirObra(dato);
                        }
                    },
                    function() {
                        //alert('Modal dismissed at: ' + new Date());
                    }
                );
            };

        }
    ]);