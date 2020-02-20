angular.module('app.controllers', [])

.controller('MainCtrl', ['$scope', '$window', '$location', '$filter', 'Informer', '$route', 'RestangularBasic',
    function($scope, $window, $location, $filter, Informer, $route, RestangularBasic) {

        $scope.listar = function() {
        	$scope.query = {};
            $location.path('#/');
        };

        $scope.crear = function() {
            if ($location.path('/new')) {
                copiaRegistro = null;
                $route.reload();
            }

            $location.path('/new');

        };
        
        if (sessionStorage.getItem('reqmateria') === null) {
        	RestangularBasic.all('catalogo').all('materia').getList({sort:'title asc'}).then(function(data) {
        			$scope.materia = data;
        			sessionStorage.setItem('reqmateria', JSON.stringify(data));    					
        		}, function(result) {
                	Informer.inform(result.data.error || result.data.mensaje, "error");
               });

        } else {
        	$scope.materia = JSON.parse(sessionStorage.getItem('reqmateria'));
        }
        
        $scope.datepickers = {
        		issued: false,
        		modified: false,
        		pubDate: false,
        		vigencia: false,
        		coberturaIni: false,
        		coberturaFin: false
        };
        $scope.toggleOpenDatePicker = function($event, which) {
          $event.preventDefault();
          $event.stopPropagation();
          $scope.datepickers[which] = true;
        };
        
        $scope.idioma = [
           { id: 1, title: 'es' },
           { id: 2, title: 'en' },
           { id: 3, title: 'fr' },
           { id: 4, title: 'de' },
           { id: 5, title: 'it' },
           { id: 6, title: 'pt' }              
        ];
        
        $scope.sino = [
             { id: 'S', title: 'Sí' },
             { id: 'N', title: 'No' }             
          ];
        
        $scope.tipoRepEspacial = ['vector', 'grid', 'textTable', 'tin', 'stereoModel', 'video'];
        
        $scope.materiasInspire = [
			{ id: 1, text: 'Sistemas de Cuadrículas Geográficas' },
			{ id: 2, text: 'Nombres Geográficos' },
			{ id: 3, text: 'Unidades Administrativas' },
			{ id: 4, text: 'Direcciones' },
			{ id: 5, text: 'Parcelas Catastrales' },
			{ id: 6, text: 'Redes de Transporte' },
			{ id: 7, text: 'Hidrografía' },
			{ id: 8, text: 'Lugares Protegidos' },
			{ id: 9, text: 'Elevaciones' },
			{ id: 10, text: 'Cubierta Terrestre' },
			{ id: 11, text: 'Ortoimágenes' },
			{ id: 12, text: 'Geología' },
			{ id: 13, text: 'Unidades Estadísticas' },
			{ id: 14, text: 'Edificiones' },
			{ id: 15, text: 'Uso del Suelo' },
			{ id: 16, text: 'Salud y Seguridad Humana' },
			{ id: 17, text: 'Servicios de Utilidad Pública y Estatales' },
			{ id: 18, text: 'Instalaciones de Observación del Medio Ambiente' },
			{ id: 19, text: 'Instalaciones de Producción e Industriales' },
			{ id: 20, text: 'Instalaciones Agrícolas y de Acuicultura' },
			{ id: 21, text: 'Distribución de la Población - Demografía' },
			{ id: 22, text: 'Zonas Sujetas a Ordenación, a Restricciones o Reglamentaciones y Unidades de Notificación' },
			{ id: 23, text: 'Zonas de Riesgos Naturales' },
			{ id: 24, text: 'Condiciones Atmosféricas' },
			{ id: 25, text: 'Aspectos Geográficos de Carácter Meteorológico' },
			{ id: 26, text: 'Regiones Biogeográficas' },
			{ id: 27, text: 'Hábitats y Biotipos' },
			{ id: 28, text: 'Distribución de las Especies' },
			{ id: 29, text: 'Recursos Energéticos' },
			{ id: 30, text: 'Recursos Minerales' }
        ];
        $scope.mediaType = [
			{ id: 'text/csv', text: 'CSV' },
			{ id: 'text/html', text: 'HTML' },
			{ id: 'application/xhtml+xml', text: 'XHTML' },
			{ id: 'text/plain', text: 'Texto plano' },
			{ id: 'text/xml', text: 'XML' },
			{ id: 'text/calendar', text: 'Calendario' },
			{ id: 'application/api', text: 'API' },
			{ id: 'application/sparql-query', text: 'SPARQL' },
			{ id: 'application/json', text: 'JSON' },
			{ id: 'application/ld+json', text: 'JSON-LD' },
			{ id: 'application/pdf', text: 'PDF' },
			{ id: 'application/postscript', text: 'PostScript' },
			{ id: 'application/soap+xml', text: 'SOAP' },
			{ id: 'application/sparql-results+xml', text: 'SPARQL-XML' },
			{ id: 'application/sparql-results+json', text: 'SPARQL-JSON' },
			{ id: 'application/rdf+xml', text: 'RDF-XML' },
			{ id: 'text/rdf+n3', text: 'RDF-N3' },
			{ id: 'application/x-turtle', text: 'RDF-Turtle' },
			{ id: 'application/vnd.ms-excel', text: 'Excel' },
			{ id: 'application/vnd.oasis.opendocument.spreadsheet', text: 'ODS' },
			{ id: 'application/zip', text: 'ZIP' },
			{ id: 'application/vnd.ogc.wms_xml', text: 'WMS' },
			{ id: 'application/vnd.ogc.wfs_xml', text: 'WFS' },
			{ id: 'text/xml+georss', text: 'geoRSS' },
			{ id: 'image/vnd.djvu', text: 'DjVu' },
			{ id: 'image/vnd.dwg', text: 'DWG' },
			{ id: 'application/vnd.google-earth.kml+xml]', text: 'KML' },
			{ id: 'application/vnd.google-earth.kmz]', text: 'KMZ' },
			{ id: 'application/rss+xml', text: 'RSS' },
			{ id: 'application/atom+xml', text: 'Atom' },
			{ id: 'application/solr', text: 'Solr' }
        ];
        
        $scope.periodicidad = [
	        { id: 'P0DT1S', text: 'Instantáneo' },
	        { id: 'P1D', text: 'Diario' },
	        { id: 'P1W', text: 'Semanal' },
	        { id: 'P1M', text: 'Mensual' },
	        { id: 'P3M', text: 'Trimestral' },
	        { id: 'P1Y', text: 'Anual' },
	        { id: 'P4Y', text: 'Cada 4 años' }
        ];
        
        $scope.allInfos = Informer.allInfos;
        $scope.remove = Informer.remove;

    }
])

.controller('ListCtrl', ['$scope', 'Dao', 'RestangularBasic', 'Informer',
    function($scope, Dao, RestangularBasic, Informer) {

        var params = {};
        params.start = 0;
        
        $scope.registros = [];
        $scope.query = {};
        $scope.busy = false;
        $scope.buscar = function() {
        	var q = obtenerQueryString($scope);
        	RestangularBasic.all('catalogo').getList({
				rows: 50,
				start: 0, 
				sort: 'lastUpdated desc',
				q : q
				}).then(function(data) {
					$scope.totalCount = data.totalCount;
		            $scope.registros = data;
		            $scope.busy = false;
                    $scope.query = {};
		        }, function(result) {
		        	Informer.inform(result.data.error || result.data.mensaje, "error");
		        });
        };
        // $scope.loadMore = function() {
            // if ($scope.busy) return;
            // $scope.busy = true;
            var q = obtenerQueryString($scope);
            RestangularBasic.all('catalogo').getList({
				rows: 50,
				start: params.start,
				sort: 'lastUpdated desc',
				q : q
				}).then(function(data) {
					$scope.totalCount = data.totalCount;
					$scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
		            $scope.busy = false;
		            
		        }, function(result) {
		        	Informer.inform(result.data.error || result.data.mensaje, "error");
		        });
//            Dao.listar('dataset', params).then(function(data) {
//                $scope.registros = $scope.registros.concat(data); //De otro modo, se pierden los datos ya listados
//                $scope.busy = false;
//            });
            
            params.start += 50;
        // };


    }
])

.controller('CreateCtrl', ['$scope', '$location', 'Dao', 'Informer', 'Restangular',
    function($scope, $location, Dao, Informer, Restangular) {

        $scope.registro = (copiaRegistro === null) ? {} : copiaRegistro;
        $scope.registro.editor = $scope.registro.editor || 'Ayuntamiento de Zaragoza. Unidad de Gestión de la Web Municipal';
        $scope.registro.licencia = $scope.registro.licencia || 'http://www.zaragoza.es/ciudad/servicios/avisolegal.htm#condiciones';
        $scope.registro.lugar = $scope.registro.lugar || 'Zaragoza';
        $scope.guardar = function() {
            Dao.crear('dataset', $scope.registro).then(function(res) {
                Informer.inform("El registro se ha creado correctamente.", "success");
                if(registro.geo=='S') {
	                Restangular.one('catalogo/geonetwork/' + registro.id).get({operation: "Insert"}).then(function(respuesta) {
	                	if (respuesta.status != 200) {
	                 		Informer.inform(respuesta.mensaje, "warning");
	                 	} else {
	                 		Informer.inform("Registro guardado correctamente en geonetwork", "info");
	                 	}
	                 }, function(err) {
	                	 Informer.inform("Se ha producido un error al guardar el registro en geonetwork." + respuesta.mensaje, "danger");
	                     console.log(err);
	                 });
                }
                
                $location.path('/edit/' + res.id);
            }, function() {
                Informer.inform("Se ha producido un error al crear el registro.", "danger");
//                $location.path('/');
            });
            // TODO: Comprobar Eliminar el objeto copiaPunto, para prevenir que tenga contenido de nuevo
            delete copiaRegistro;
        };

    }
])

.controller('EditCtrl', ['$scope', 'Dao', 'registro', 'Restangular', 'RestangularBasic', '$location', 'Informer', '$uibModal',
    function($scope, Dao, registro, Restangular, RestangularBasic, $location, Informer, $uibModal) {

        $scope.registro = registro;
        idiomaselegidos = [];
        if (angular.isDefined($scope.registro.language)) {
	        for (i = 0; i < $scope.registro.language.length; i++) {
	        	if ($scope.registro.language[i].title === 'es') {
	        		idiomaselegidos.push($scope.idioma[0]);
	        	} else if ($scope.registro.language[i].title === 'en') {
	        		idiomaselegidos.push($scope.idioma[1]);
	        	} else if ($scope.registro.language[i].title === 'fr') {
	        		idiomaselegidos.push($scope.idioma[2]);
	        	} else if ($scope.registro.language[i].title === 'de') {
	        		idiomaselegidos.push($scope.idioma[3]);
	        	} else if ($scope.registro.language[i].title === 'it') {
	        		idiomaselegidos.push($scope.idioma[4]);
	        	} else if ($scope.registro.language[i].title === 'pt') {
	        		idiomaselegidos.push($scope.idioma[5]);
	        	}
	        }
	        $scope.registro.language = idiomaselegidos;
        }
        $scope.eliminarMateria = function(index) {
        	$scope.registro.materia.splice(index, 1);
        };
        
        $scope.borrarDatos = function() {
        	if (registro.geo=='N' && confirm('Se borrarán los datos sobre la georreferenciación salvo la materia')) {
//        		delete $scope.registro.materiaInspire;
        		$scope.registro.spatialRepresentationTypeCode = '';
        		$scope.registro.spatialResolution = '';
        	}
        };
        
        $scope.borrarRegistro = function() {
        	Dao.eliminar(registro).then(function(res) {
        		console.log(res);
        		Informer.inform("Se ha eliminado el registro correctamente.", "success");
        		$location.path('/');
            }, function(res) {
            	console.log(res);
                Informer.inform("Se ha producido un error al eliminar el registro.", "danger");
            });
        };
        $scope.altaGeonetwork = function() {
        	 Restangular.one('catalogo/geonetwork/' + registro.id).get({operation: "Insert"}).then(function(res) {
        		 if (res.status != 200) {
              		Informer.inform(res.mensaje, "warning");
              	} else {
              		Informer.inform("Registro guardado correctamente en geonetwork", "info");
              	}
             }, function(err) {
            	 Informer.inform("Se ha producido un error al guardar el registro en geonetwork." + res.mensaje, "danger");
                 console.log(err);
             });
        };
        $scope.asociarMateria = function() {
        	if (angular.isUndefined($scope.registro.materia)) {
        		$scope.registro.materia = [];
        	}
        	$scope.registro.materia.push($scope.registro.materiaSelec);
        	$scope.registro.materiaSelec=undefined;
        };
        
        $scope.eliminarNormativa = function(index) {
        	$scope.registro.normativa.splice(index, 1);
        };
        $scope.asociarNormativa = function() {
        	if (angular.isUndefined($scope.registro.normativa)) {
        		$scope.registro.normativa = [];
        	}
        	$scope.registro.normativa.push($scope.registro.normativaSelec);
        	$scope.registro.normativaSelec=undefined;
        };
        $scope.eliminarTag = function(index) {
        	$scope.registro.tag.splice(index, 1);
        };
        $scope.asociarTag = function() {
        	if (angular.isUndefined($scope.registro.tag)) {
        		$scope.registro.tag = [];
        	}
        	
        	if(angular.isUndefined($scope.registro.tagSelec.id)) {
        		RestangularBasic.all('tag').post("{\"title\":\""+ $scope.registro.tagSelec +"\",\"uri\":\"http://www.zaragoza.es/"+ encodeURIComponent($scope.registro.tagSelec) +"\"}",{},{}).then(function(json){
        			$scope.registro.tagSelec = {};
        			$scope.registro.tagSelec.id = json.id;
        			$scope.registro.tagSelec.title = json.title;
        			$scope.registro.tagSelec.title = json.title;
        			$scope.registro.tagSelec.uri = json.uri;
        			
	    			$scope.registro.tag.push($scope.registro.tagSelec);
	        		$scope.registro.tagSelec=undefined;
	    			
	    		});
        	
        	
        	} else {
        		$scope.registro.tag.push($scope.registro.tagSelec);
        		$scope.registro.tagSelec=undefined;
        	}
        };
        $scope.eliminarRecurso = function(index) {
        	$scope.registro.recurso.splice(index, 1);
        };
        $scope.asociarRecurso = function() {
        	if (angular.isUndefined($scope.registro.recurso)) {
        		$scope.registro.recurso = [];
        	}
        	$scope.registro.recurso.push($scope.registro.recursoSelec);
        	$scope.registro.recursoSelec=undefined;
        };
        
        $scope.borrarFormato = function(index) {
        	$scope.registro.formato.splice(index, 1);
        };
        
        $scope.gestionFormato = function(index) {
        	$scope.dato = {};
        	
        	if (!angular.isUndefined(index)) {
        		$scope.dato=angular.extend($scope.registro.formato[index]);
        	}
        	var modalInstance = $uibModal.open({
                templateUrl: '/cont/vistas/servicio/catalogo/admin/templates/formatoModal.html',
                
                controller: function($scope, $uibModalInstance, registro, dato, mediaType) {
                	$scope.registro = registro;
                	$scope.dato = dato;
                	$scope.mediaType = mediaType;
                	
                	$scope.datepickers = {
                    		issued: false,
                    		modified: false
                    };
                    $scope.toggleOpenDatePicker = function($event, which) {
                      $event.preventDefault();
                      $event.stopPropagation();
                      $scope.datepickers[which] = true;
                    };
                    $scope.dato.license = $scope.dato.license || 'http://www.zaragoza.es/ciudad/servicios/avisolegal.htm#condiciones';
                	$scope.tipoMedio=[
    		              { id: 1, title: 'application' },
    		              { id: 2, title: 'audio' },
    		              { id: 3, title: 'example' },	
    		              { id: 4, title: 'image' },
    		              { id: 5, title: 'message' },
    		              { id: 6, title: 'model' },
    		              { id: 7, title: 'multipart' },
    		              { id: 8, title: 'text' },
    		              { id: 9, title: 'video' }
    	        	];
                	if (angular.isDefined($scope.dato.tipoMedio)) {
                		tipoMedioElegido = $scope.tipoMedio[$scope.dato.tipoMedio.id - 1];
                    	$scope.dato.tipoMedio = tipoMedioElegido;
                	}
                    
                	$scope.addRel = function() {
                		if (angular.isUndefined($scope.dato.rel)) {
                			$scope.dato.rel = [];
                		}
                		$scope.dato.rel.push({id:'',title:''});
                    };
                    $scope.borrarRel = function(index) {
                    	$scope.dato.rel.splice(index, 1);
                    };
                    $scope.save = function() {
                        $uibModalInstance.close($scope.dato);
                    };
                    $scope.cancelar = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                }, resolve: {
                	registro: function() {
                		return $scope.registro;
                   },
                   mediaType: function() {
               		return $scope.mediaType;
                  },
                   dato: function() {
               			return $scope.dato;
                  }
                }
            });

            modalInstance.result.then(function(selectedItem) {
            	console.log(selectedItem);
            	if (angular.isUndefined(selectedItem.id)) {
//            		RestangularIngrediente.all('ingrediente').post("{\"title\":\""+ selectedItem.ingredient +"\"}",{},{}).then(function(json){
//            			selectedItem.ingredient={};
//            			selectedItem.ingredient.id=json.id;
//            			selectedItem.ingredient.title=json.title;
            			if (angular.isUndefined(index)) {
                    		//add al listado
                    		if (angular.isUndefined($scope.registro.formato)) {
                    			$scope.registro.formato = [];
                    		}
                    		$scope.registro.formato.push(selectedItem);
                    	} else {
                    		//update listado
                    		$scope.registro.formato[index] = selectedItem;
                    	}
//            		});
            		
            	} else {
            	
                	if (angular.isUndefined(index)) {
                		//add al listado
                		if (angular.isUndefined($scope.registro.formato)) {
                			$scope.registro.formato = [];
                		}
                		$scope.registro.formato.push(selectedItem);
                	} else {
                		//update listado
                		$scope.registro.formato[index] = selectedItem;
                	}
            	}
            }, function() {
            });
        };
        
        
        
        $scope.guardar = function() {
            Dao.actualizar($scope.registro).then(function(res) {
                Informer.inform("El registro se ha editado correctamente.", "success");
                if(registro.geo=='S') {
	                Restangular.one('catalogo/geonetwork/' + registro.id).get({operation: "Update"}).then(function(respuesta) {
	                 	if (respuesta.status != 200) {
	                 		Informer.inform(respuesta.mensaje, "warning");
	                 	} else {
	                 		Informer.inform("Registro guardado correctamente en geonetwork", "info");
	                 	}
	                 }, function(err) {
	                	 Informer.inform("Se ha producido un error al guardar el registro wn geonetowrk." + res.mensaje, "danger");
	                     console.log(err);
	                 });
                }
                
                //$scope.registro = res;
                //$location.path('/');
                $route.reload();
            }, function() {
                Informer.inform("Se ha producido un error al guardar el registro.", "danger");
//                $location.path('/');
            });
        };
    }
])

.controller('EstructuraCtrl', ['$scope', 'RestangularEstructura', 'Informer',
    function($scope, RestangularEstructura, Informer) {

        $scope.order = ['id', 'id'];
        $scope.reverse = false;

        var start = 0;
        var datosListados = [];
        var q = '';

        $scope.busy = false;
        $scope.loadMore = function() {
            if ($scope.busy)
                return;
            $scope.busy = true;

            RestangularEstructura.all('organo-publico').getList({
                rows: 50,
                start: start,
                q: q
            }, {}).then(function(data) {
                datosListados = datosListados.concat(data); //De otro modo, se pierden los datos ya listados
                $scope.registros = datosListados;
                $scope.busy = false;
            });
            start += 50;
        };

        $scope.guardar = function(index) {
            if ($scope.registros[index].nombre_recurso) {
                $scope.registros[index].put({}, {}).then(function(dato) {
                    Informer.inform("El registro se ha editado correctamente.", "success");
                    $scope.registros[index] = dato;
                }, function() {
                    Informer.inform("Se ha producido un error al guardar el registro.", "error");
                });
            } else {
                RestangularEstructura.one("organo-publico", $scope.registros[index].id).customPOST($scope.registros[index], "", "", "").then(function(dato) {
                    Informer.inform("El registro se ha asociado correctamente.", "success");
                    $scope.registros[index] = dato;
                }, function(result) {
                    Informer.inform(result.data.error || result.data.mensaje, "error");
                });
            }
        };

    }
])

function obtenerQueryString($scope) {
	var q = '';
	if (angular.isDefined($scope.query.id)) {
		q = q + 'id==' + $scope.query.id + ';';
	}
	if (angular.isDefined($scope.query.title)) {
		q = q + 'title==' + $scope.query.title + '*;';
	}
	if (angular.isDefined($scope.query.formato) && angular.isDefined($scope.query.formato.mediaType)) {
		q = q + 'formato.mediaType==' + $scope.query.formato.mediaType + '*;';
	}
	
	if (angular.isDefined($scope.query.materia) && angular.isDefined($scope.query.materia.id)) {
//		q = q + '(materiaPrimaria.id==' + $scope.query.materia.id  + ',materia.id==' + $scope.query.materia.id  + ');';
		q = q + 'materiaPrimaria.id==' + $scope.query.materia.id  + ';';
	}
	if (q.length > 0) {
		q = q.substring(0, q.length - 1);
	} 
	return q;
	
}
