//Prametros generales de configuración del arbol

//Ids de nodos del arbol
var nodoPadreId = null;
var nodoArbolId = null;
var nodoArbolBordeId = null;
var nodoArbolBordeRellenoId = null;
var nodoArbolBordeEsqSupId = null;
var nodoArbolBordeEsqInfId = null;

//Margen superior/inferior
var margenArbol = 0;

//Altura del arbol
var alturaArbol = 250;

//Tamaño en pixeles del nodo con el título de la categoría
var tamCategoria = 25;
//Agregar un borde redondeado
var agregarBordeRedondeado = true; // Si se pone a false, no calcula bien el tamaño de los acordeones
//Mostrar el arbol al inicio // Se sobreescribe en "cargaArbol"
var iniciarArbolAbierto = true;

var urlGeocoder = "//www.zaragoza.es/geocoderIDEZar"
var funcionGeocoder = "seleccionToVisualizador";//Definida en tree_functions;
/*
	informacion a cargar en el arbol
*/
var infoActualidad;
try {
	if(dataActualidad) {
		infoActualidad = dataActualidad 
	} 
} catch(ex) {
	infoActualidad = parent.dataActualidad;
}

var infoMovilidad;
try {
	if(dataMovilidad) {
		infoMovilidad = dataMovilidad 
	} 
} catch(ex) {
	infoMovilidad = parent.dataMovilidad;
}

var infoTrafico;
try {
	if(dataTrafico) {
		infoTrafico = dataTrafico 
	}
} catch(ex) {
	infoTrafico = parent.dataTrafico;
}

var infoEquipamientos;
try {
	if(dataEquipamientos) {
		infoEquipamientos = dataEquipamientos 
	}
} catch(ex) {
	infoEquipamientos = parent.dataEquipamientos;
}

var treeOrderList;
try {
	if (treeOrderDataList) {
		treeOrderList = treeOrderDataList;
	}
} catch(ex) {
	treeOrderList = parent.treeOrderDataList;
}

//Configuracion de estructura del arbol
function configuraArbol() {
	dojo.addOnLoad(function() {
		if (!document.getElementById(nodoArbolId)) {
			//Crear el nodo del arbol
			var treeNode = document.createElement('div');
			treeNode.setAttribute('id', nodoArbolId);
			if (nodoPadreId) {
				document.getElementById(nodoPadreId).appendChild(treeNode);
			} else {
				document.body.appendChild(treeNode);
			}
			
			if (agregarBordeRedondeado) {
				//Agrega el borde redondeado
				try {
					mostrarBordeRedondeado(alturaArbol, nodoPadreId, 
							nodoArbolBordeId, nodoArbolBordeRellenoId,
							nodoArbolBordeEsqSupId, nodoArbolBordeEsqInfId);
				} catch(e) {
				}
			}
			
			//REDEFINIR COMPORTAMIENTO DE DOJO
			try {
				redefinirFuncionesDojo();
			} catch(e) {
			}
		}
					
		//CONFIGURACION DEL ARBOL
		var storeActualidad;

		if(infoActualidad != "") {
			storeActualidad = new dojo.data.ItemFileReadStore({
		            data: {
		                identifier: 'id',
		                label: 'title',
		                items: eval("[" + infoActualidad + "]")
		            }
		        });
		}
		
		var storeMovilidad;
		if ((infoMovilidad != "") || infoTrafico != "") {
			storeMovilidad = new dojo.data.ItemFileReadStore({
		            data: {
		                identifier: 'id',
		                label: 'title',
		                items: eval("[" + infoMovilidad + infoTrafico + "]")
		            }
		        });
		}
		var storeEquipamientos;
		if (infoEquipamientos) {
			 storeEquipamientos = new dojo.data.ItemFileReadStore({
		            data: {
		                identifier: 'id',
		                label: 'title',
		                items: eval("[" + infoEquipamientos+ "]")
		            }
		        });
		}
		aContainer = new dijit.layout.AccordionContainer({
				style: "height: " + alturaArbol
			},
			nodoArbolId);

		var treeActualidad;
		if (storeActualidad) {
			treeActualidad = new dijit.tree.ForestStoreModel({
		            store: storeActualidad
		        });
		}
		var treeMovilidad;
		if (storeMovilidad) {
			treeMovilidad = new dijit.tree.ForestStoreModel({
		            store: storeMovilidad
		        });
		}
		var treeEquipamientos;
		if (storeEquipamientos) {
			treeEquipamientos = new dijit.tree.ForestStoreModel({
		            store: storeEquipamientos
		        });
		}

		treeControlActualidad = null;
		treeControlActualidadOrder = null;
                if (treeActualidad) {
			treeControlActualidad = new dijit.Tree({
			   title:'De inter\u00E9s',
		            model: treeActualidad,
		            showRoot: false,
			    getIconClass : getIcono,
			    getIconStyle : getStyle,
			    openOnClick: true,
			    onClick: treeOnClick,
			    onLoad : treeOnLoad,
				onShow: itemOnShow,
				onHide: itemOnHide
		        },
		        "actualidad");
		}

		treeControlMovilidad = null;
		treeControlMovilidadOrder = null;
		if (treeMovilidad) {
			treeControlMovilidad = new dijit.Tree({
			   title:'Movilidad',
		            model: treeMovilidad,
		            showRoot: false,
			    getIconClass : getIcono,
			    getIconStyle : getStyle,
			    openOnClick: true,
			    onClick : treeOnClick,
			    onLoad : treeOnLoad,
				onShow: itemOnShow,
				onHide: itemOnHide
		        },
		        "movilidad");
		}
		
		treeControlEquipamientos = null;
		treeControlEquipamientosOrder = null;
		if (treeEquipamientos) {
			treeControlEquipamientos = new dijit.Tree({
			   title:'Equipamientos',
		            model: treeEquipamientos,
		            showRoot: false,
			    getIconClass : getIcono,
			    getIconStyle : getStyle,
			    openOnClick: true,
			    onClick: treeOnClick,
			    onLoad : treeOnLoad,
				onShow: itemOnShow,
				onHide: itemOnHide
		        },
		        "equipamientos");
		}

		var selectSize = (screen.width <= 480)?5:10;
		
		panelLocalizarDireccionOrder = null;
		var geor = "<strong>Dirección:</strong> (calle, n° <strong>o</strong> calle)<br><input name=\"direccion\" id=\"direccion\" size=\"150\" style=\"font-size:110%;width:60%\" onkeypress=\"georefer.enterbuscar(event);\" type=\"text\">&nbsp;&nbsp;<input id=\"botonBuscarCalle\" class=\"boton\" value=\"Buscar\" style=\"width:25%\" onclick=\"georefer.buscar()\" type=\"button\"><br><br><div id=\"load\" style=\"visibility: hidden; float: left; position: absolute;\"><img src=\"//www.zaragoza.es/geocoderIDEZar/images/load.gif\"></div><select name=\"resultados\" id=\"resultados\" size=\""+ selectSize +"\" onchange=\"georefer.cogercalle()\" style=\"font-size:100%;width: 100%;\"></select>";
		panelLocalizarDireccion = 
	        new dijit.layout.AccordionPane({
	            style:'height:100px',
	            content : geor,
	            title:'Localizar Dirección',
				onShow: itemOnShow,
				onHide: itemOnHide
            });
		
		panelRutasOrder = null;
		var georRutas = "<div style='align-items:center;display:flex;margin-bottom:5px;'><input type='radio' id='route_origin_geocoder' onclick='route_origin(this)'><span><strong>Origen:</strong> (calle, n° <strong>o</strong> calle)</span></div>";
		georRutas += "<input name=\"rutas_direccion\" id=\"rutas_direccion\" size=\"150\" style=\"font-size:110%;width:60%\" onkeypress=\"georeferRutas.enterbuscar(event);\" type=\"text\">&nbsp;&nbsp;<input id=\"rutas_botonBuscarCalle\" class=\"boton\" value=\"Buscar\" style=\"width:25%\" onclick=\"georeferRutas.buscar()\" type=\"button\"><br><br><div id=\"rutas_load\" style=\"visibility: hidden; float: left; position: absolute;\"><img src=\"//www.zaragoza.es/geocoderIDEZar/images/load.gif\"></div><select name=\"rutas_resultados\" id=\"rutas_resultados\" size=\""+ selectSize +"\" onchange=\"georeferRutas.cogercalle()\" style=\"font-size:100%;width: 100%;\"></select>";
		georRutas += "<div style='align-items:center;display:flex;margin-top:5px;margin-bottom:5px;'><input type='radio' id='route_origin_location' onclick='route_origin(this)'> <strong>Mi localización</strong></div>";
		georRutas += "<strong>Tipo de transporte:</strong>";
		georRutas += "<ul style='list-style:outside none none;margin:0;padding-left:0;'><li><input type='checkbox' id='tte_bus' onclick='tte_check(this)'>Autobuses urbanos</li><li><input type='checkbox' id='tte_tram' onclick='tte_check(this)'>Tranvía</li><li><input type='checkbox' id='tte_rural_bus' onclick='tte_check(this)'>Autobuses barrios rurales</li></ul>";
		georRutas += "<br><input id='rutas_recalcularRuta' class='boton' value='Calcular' style='width:25%;position:absolute;right:40px;' onclick='recalculateCurrentRoute()' type='button'>";
		georRutas += "<br><br><div style='font-size:11px;font-style:italic;' id='rutas_message'></div>";
		panelRutas = 
	        new dijit.layout.AccordionPane({
	            style:'height:100px',
	            content : georRutas,
	            title:'Cómo llegar',
				onShow: itemOnShow,
				onHide: itemOnHide
            });

		if (!treeOrderList) {
			var treeOrderIndex = 0;
			if (treeControlActualidad) {
				aContainer.addChild(treeControlActualidad);
				treeControlActualidadOrder = treeOrderIndex;
				treeOrderIndex += 1;
			}
			if (treeControlMovilidad) {
				aContainer.addChild(treeControlMovilidad);
				treeControlMovilidadOrder = treeOrderIndex;
				treeOrderIndex += 1;
			}
			if (treeControlEquipamientos) {
				aContainer.addChild(treeControlEquipamientos);
				treeControlEquipamientosOrder = treeOrderIndex;
				treeOrderIndex += 1;
			}
			if (panelLocalizarDireccion) {
				aContainer.addChild(panelLocalizarDireccion);
				panelLocalizarDireccionOrder = treeOrderIndex;
				treeOrderIndex += 1;
			}
			if (panelRutas) {
				aContainer.addChild(panelRutas);
				panelRutasOrder = treeOrderIndex;
				treeOrderIndex += 1;
			}
		} else {
			var treeControl;
			for (var i=0, iLen=treeOrderList.length; i < iLen; i++) {
				switch(treeOrderList[i]) {
					case "actualidad": treeControl = treeControlActualidad; treeControlActualidadOrder = i; break;
					case "movilidad": treeControl = treeControlMovilidad; treeControlMovilidadOrder = i; break;
					case "equipamientos": treeControl = treeControlEquipamientos; treeControlEquipamientosOrder = i; break;
					default: treeControl = null; break;
				}
				if(treeControl) {
					aContainer.addChild(treeControl);
				}
			}
			if (panelLocalizarDireccion) {
				aContainer.addChild(panelLocalizarDireccion);
				panelLocalizarDireccionOrder = treeOrderList.length;
			}
			if (panelRutas) {
				aContainer.addChild(panelRutas);
				if (panelLocalizarDireccion) {
					panelRutasOrder = panelLocalizarDireccionOrder + 1;
				} else {
					panelRutasOrder = treeOrderList.length;
				}
			}
		}

		// Interfaz Rutas
		var route_origin_geocoder = document.getElementById("route_origin_geocoder");
		if (route_origin_geocoder) route_origin_geocoder.checked = true;
		var tte_bus = document.getElementById("tte_bus");
		if (tte_bus) tte_bus.checked = true;
		var tte_tram = document.getElementById("tte_tram");
		if (tte_tram) tte_tram.checked = true;
		var tte_rural_bus = document.getElementById("tte_rural_bus");
		if (tte_rural_bus) tte_rural_bus.checked = false;
		
		var categoriasArbol = [];
		if (treeControlActualidad) {
        		categoriasArbol.push(treeControlActualidad);
		}
		if (treeControlMovilidad) {
        		categoriasArbol.push(treeControlMovilidad);
		}
		if (treeControlEquipamientos) {
			categoriasArbol.push(treeControlEquipamientos);
		}
		if (panelLocalizarDireccion) {
			categoriasArbol.push(panelLocalizarDireccion);
		}
		if (panelRutas) {
			categoriasArbol.push(panelRutas);
		}
		
		try {
	       		aContainer.startup();
		} catch (e) {
			//Se captura un error que se da en la función startup
		}
		
		try{
			inicializarArbol(iniciarArbolAbierto, categoriasArbol, alturaArbol , tamCategoria, margenArbol);
			// Ocultar inicialmente el panel "Cómo llegar"
			if (panelRutasOrder) {
				hideRoutesPanel();
			}
		} catch (e) {
		}
	});
}

//Carga del arbol
function cargaArbol(margen, altura, nodoRootId, nodoId, bordeId, bordeRellenoId, bordeEsquinaSupId, bordeEsquinaInfId, abrirArbolAlCargar) {
	try {
		if(margen) {
			margenArbol = margen;
		}
		if(altura) {
			alturaArbol = altura;
		}
		if (nodoRootId) {
			nodoPadreId = nodoRootId;
		}
		if (nodoId) {
			nodoArbolId = nodoId;
		}
		if (bordeId) {
			nodoArbolBordeId = bordeId;
		}
		if (bordeRellenoId) {
			nodoArbolBordeRellenoId = bordeRellenoId;
		}
		if (bordeEsquinaSupId) {
			nodoArbolBordeEsqSupId = bordeEsquinaSupId;
		}
		if (bordeEsquinaInfId) {
			nodoArbolBordeEsqInfId = bordeEsquinaInfId;
		}
		if ((abrirArbolAlCargar != undefined) && (abrirArbolAlCargar != null)) {
			iniciarArbolAbierto = abrirArbolAlCargar;
		}
		configuraArbol();
	} catch (ex) {
		setTimeout("cargaArbol();",150);
	}
}
//La llamada se hace desde homeMapa

//Dependencias de dojo
function loadDependencies() {
	try {
		if (dojo.require) {
			dojo.require("dijit.layout.AccordionContainer");
			dojo.require("dijit.layout.ContentPane");
			dojo.require("dojo.data.ItemFileReadStore");
			dojo.require("dijit.Tree");
		}
	} catch (e) {
		setTimeout("loadDependencies()", 100);
	}
}
loadDependencies();

//Cargar Geocoder
function cargaGeocoder() {
    try {
        if (GUI) {
            funcionGeocoder = seleccionToVisualizador;
            //Si todavía no se ha cargado el script de tree_functions.js se capturará el error antes de definir el GUI del geocoder
            //y se repetirá la llamada con el timeout
            georefer = new GUI("georefer", urlGeocoder);
            georefer.onChange = funcionGeocoder;
            georeferRutas = new GUI("georeferRutas", urlGeocoder);
            georeferRutas.putHash("rutas_");
            georeferRutas.onChange = seleccionToRutometro;
        }
    } catch (e) {
    setTimeout("cargaGeocoder()", 100);
    }
}

function getGeocoder() {
	try {
		return georefer;
	} catch (e) {
		return null;
	}
}

cargaGeocoder();
