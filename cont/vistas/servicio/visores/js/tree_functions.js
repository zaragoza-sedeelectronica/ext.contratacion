/*************************************************************************************/
/**Variables globales								    **/
/*************************************************************************************/
//Directorio de imagenes de bordes redondeados del arbol
/*
	iaaa: 08-03-2013
	Variable para facilitar la introduccion de rutas absolutas
	Si se define como "" se emplean relativas
*/
var myHomeIP;
try{
	if (homeIP) {
		myHomeIP = homeIP;
	} else {
		if (parent.homeIP) {
			myHomeIP = parent.homeIP;
		} else {
			myHomeIP = "";
		}
	}
} catch(e) {
	try {
		if (parent.homeIP) {
			myHomeIP = parent.homeIP;
		} else {
			myHomeIP = "";
		}
	} catch(e) {
		myHomeIP = "";
	}
} 
var directorioRecursos = myHomeIP + "/img";
//Margen de seguridad para el cÃ¡lculo de la altura del arbol
var safeMargin = 50;
//Listado de categorias del arbol se emplea para calcular la altura del mismo
var treeCategories = [];

/*************************************************************************************/
/**Funciones definidas en homeMapa						    **/
/*************************************************************************************/
function getIcono(item, opened) {
        if(item.root){
            return "diagramIcon";
        } else {
    	if(!item.children){
		return "icono";
	}
        }

    }

function getStyle(item, opened){
    if(!item.root){
    	if(!item.children){
    	    return {backgroundImage: "url(//www.zaragoza.es/contenidos/iconos/" + item.claseIcono + ".png)"};
        }
    }
        return '';
}

function treeOnClick(item) {
	var node = this.selectedNode;
	if(!item.root && item.url != undefined){
		try{
			javascript:cambiarVisibilidad(item, node);
		} catch(ex) {
			try{
				javascript:parent.cambiarVisibilidad(item, node);
			} catch(ex1) {
				
			}
		}
	}
}

function getAccordionElementImageNode(item) {
	var index;
	var elem = 0;
	if (treeControlActualidad) {
		elem = elem + 1;
	}
	if (treeControlMovilidad) {
		elem = elem + 1;
	}
	if (treeControlEquipamientos) {
		elem = elem + 1;
	}
	if (treeControlActualidad && item.domNode == treeControlActualidad.domNode) {
		index = ((treeControlActualidadOrder != null) ? treeControlActualidadOrder : 0);
	} else if (treeControlMovilidad && item.domNode == treeControlMovilidad.domNode) {
		index = ((treeControlMovilidadOrder != null) ? treeControlMovilidadOrder : 1);
	} else if (treeControlEquipamientos && item.domNode == treeControlEquipamientos.domNode) {
		index = ((treeControlEquipamientosOrder != null) ? treeControlEquipamientosOrder : 2);
	} else if (item.domNode == panelLocalizarDireccion.domNode) {
		index = ((panelLocalizarDireccionOrder != null) ? panelLocalizarDireccionOrder : elem);
	} else if (item.domNode == panelRutas.domNode) {
		index = ((panelRutasOrder != null) ? panelRutasOrder : elem);
	} else {
		return;
	}
	return aContainer.domNode.childNodes[index].children[0].children[0].children[0];
}

function itemOnShow() {
	var node = getAccordionElementImageNode(this);
	var className = node.className;
	className = className.replace("dijitAccordionArrow", "dijitAccordionArrowOn");
	node.className = className;
}

function itemOnHide() {
	var node = getAccordionElementImageNode(this);
	var className = node.className;
	className = className.replace("dijitAccordionArrowOn", "dijitAccordionArrow");
	node.className = className;
}

/*
	iaaa: 08-03-2013
	Se modifica la funcion para que se puedan actualizar los nodos expandibles
*/
function treeOnLoad() {
	var elem;
	var nodoId;
	for (var itera in this._itemNodesMap) {
		var nodo = this._itemNodesMap[itera];
		if (nodo && nodo[0] && nodo[0].item) {
			if (nodo[0].item.clase=='visible') {
				nodo[0].contentNode.children[0].style.filter = 'alpha(opacity=100)';
				nodo[0].contentNode.children[0].style.opacity = 1;
			} else {
				nodo[0].contentNode.children[0].style.filter = 'alpha(opacity=20)';
				nodo[0].contentNode.children[0].style.opacity = 0.2;
			}
		}
	}
}

/*************************************************************************************/
/**Funciones auxiliares								    **/
/*************************************************************************************/
//Funcion de geocoder, busca la función definida en homeMapa "seleccion2visualizador"
//si no la encuentra busca en el facade del visor
function seleccionToVisualizador(calle,x,y){
	// "calle" puede ser un objeto que contenga toda la información; en ese caso, el resto de parámetros están vacíos
	var srs;
	var CRS_PREFIX = "EPSG:";
	if (getGeocoder()) {
		srs = getGeocoder().getSrs();
		if (srs.toLowerCase().indexOf(CRS_PREFIX) != 0) {
			srs = CRS_PREFIX+srs;
		}
	}
	try{
		seleccion2visualizador(calle,x,y,srs);
	}catch(e){
		try{
			parent.seleccion2visualizador(calle,x,y,srs);
		} catch (ex) {
			try {
				getFacade().georreferenciarCalle(calle, x, y, srs?srs:"EPSG:23030");
			} catch(ex2) {
			}
		}
	}
}

// Función del geocoder de Rutas
function seleccionToRutometro(calle,x,y) {
	// Extraer parámetros de todos los tipos posibles de respuesta del geocoder
	var originX, originY, originName;
	if (typeof calle == "object") {
		if (Object.prototype.toString.call(calle) === '[object Array]') {
			originX = calle[1];
			originY = calle[2];
			originName = calle[0];
		} else {
			originX = calle.longitude;
			originY = calle.latitude;
			originName = calle.name;
		}
	} else {
		originX = x;
		originY = y;
		originName = calle;
	}
	geocoderRouteOrigin = [originX, originY, originName];
	doCalculateCurrentRoute(originX, originY, originName);
}

// Solicitar ruta
function doCalculateCurrentRoute(originX, originY, originName) {
	if (typeof calculateCurrentRoute != "undefined") {
		currentRouteOrigin = [originX, originY, originName];
		calculateCurrentRoute();
		if (currentRouteDestination != null) {
			showRouteLink(currentRouteDestination[0], currentRouteDestination[1], currentRouteDestination[2]);
		}
	} else if (parent.calculateCurrentRoute) {
		parent.currentRouteOrigin = [originX, originY, originName];
		parent.calculateCurrentRoute();
		if (parent.currentRouteDestination != null) {
			showRouteLink(parent.currentRouteDestination[0], parent.currentRouteDestination[1], parent.currentRouteDestination[2]);
		}
	}
}

// Calcuar ruta al pulsar el botón de Calcular
function recalculateCurrentRoute() {
	if (typeof calculateCurrentRoute != "undefined") {
		if (!currentRouteOrigin) {
			alert("Debe seleccionar una direcci\u00F3n de origen");
			return;
		}
		calculateCurrentRoute();
	} else if (parent.calculateCurrentRoute) {
		if (!parent.currentRouteOrigin) {
			alert("Debe seleccionar una direcci\u00F3n de origen");
			return;
		}
		parent.calculateCurrentRoute();
	}
}

// Hacer visible panel con opciones de Rutas
function showRoutesPanel() {
	dojo.style(aContainer.domNode.childNodes[panelRutasOrder].id, "visibility", "visible");
}

// Ocultar panel con opciones de Rutas
function hideRoutesPanel() {
	dojo.style(aContainer.domNode.childNodes[panelRutasOrder].id, "visibility", "hidden");
}

// Abrir panel con opciones de Rutas
function openRoutesPanel() {
	aContainer.selectChild(aContainer.getChildren()[panelRutasOrder]);
}

// Acción al seleccionar el origen de la Ruta
function route_origin(radio) {
	// Determinar tipo de origen
	var route_origin_geocoder = document.getElementById("route_origin_geocoder")
	var route_origin_location = document.getElementById("route_origin_location")
	var geocoderActive;
	if (radio == route_origin_geocoder) {
		geocoderActive = true;
	} else {
		geocoderActive = false;
	}
	// Actualizar interfaz
	var geocoder_input_text = document.getElementById("rutas_direccion");
	var geocoder_input_button = document.getElementById("rutas_botonBuscarCalle");
	var geocoder_input_results = document.getElementById("rutas_resultados");
	if (route_origin_geocoder) route_origin_geocoder.checked = geocoderActive;
	if (route_origin_location) route_origin_location.checked = !geocoderActive;
	if (geocoder_input_text) geocoder_input_text.disabled = !geocoderActive;
	if (geocoder_input_button) geocoder_input_button.disabled = !geocoderActive;
	if (geocoder_input_results) geocoder_input_results.disabled = !geocoderActive;
	// Calcular ruta si se selecciona la opción de geolocalizar
	if (geocoderActive == false) {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(geolocationToRutometro, geolocationError);
		} else {
			geolocationError(null, "Geolocalización no soportada por el navegador");
		}
	} else if (geocoderRouteOrigin) {
		if (typeof calculateCurrentRoute != "undefined") {
			currentRouteOrigin = geocoderRouteOrigin;
		} else if (parent.calculateCurrentRoute) {
			parent.currentRouteOrigin = geocoderRouteOrigin;
		}
	}
}

// Calcular Ruta a partir de la posición del usuario
function geolocationToRutometro(position) {
	var coords = getFacade().transformarCoordenadas("EPSG:4326", null, position.coords.longitude, position.coords.latitude);
	doCalculateCurrentRoute(coords[0], coords[1], "Origen");
}

// Error al tratar de geolocalizar al usuario para obtener el origen de la Ruta a calcular
function geolocationError(error, errorMsg) {
	if (errorMsg == null) {
		switch(error.code) {
		    case error.PERMISSION_DENIED:
		        errorMsg = "El usuario no da permiso para geolocalizarse"
		        break;
		    case error.POSITION_UNAVAILABLE:
		        errorMsg = "Posición no disponible"
		        break;
		    case error.TIMEOUT:
		        errorMsg = "Imposible obtener la localización (demasiado tiempo de espera)"
		        break;
		    case error.UNKNOWN_ERROR:
		        errorMsg = "Imposible obtener la localización (error desconocido)"
		        break;
	        default:
	        	errorMsg = "";
	        	break;
		}
	}
	if ((typeof console != "undefined") && console.log) console.log(errorMsg);
	alert("No es posible obtener su localización");
}

// Acción al tratar de seleccionar un tipo de transporte
function tte_check(checkbox) {
	var tte_bus_check = document.getElementById("tte_bus");
	var tte_tram_check = document.getElementById("tte_tram");
	var tte_rural_bus_check = document.getElementById("tte_rural_bus");
	var anyChecked = (tte_bus_check.checked == true) || (tte_tram_check.checked == true) || (tte_rural_bus_check.checked == true);
	if (anyChecked == false) {
		checkbox.checked = true;
	}
}

function showRouteLink(x, y, name) {
	if (typeof name == "string") {
		name = name.replace(/ /g,"%20");
	} else {
		name = "Destino";
	}
	var link = 'http://www.zaragoza.es/ciudad/viapublica/movilidad/como-ir/?nd=' + name + '&xd=' + x + '&yd=' + y;
	var text = 'Se ha calculado la ruta más rápida en transporte público. Para más opciones ir a <a href=' + link + ' target="_blank">"Cómo moverse en tte público"</a>';
	var rutas_message = document.getElementById("rutas_message");
	if (rutas_message) {
		rutas_message.innerHTML = text;
	}
}

/*************************************************************************************/
/**ModificaciÃ³n de comportamiento del Ã¡rbol de capas				    **/
/*************************************************************************************/
//Abrir todos los nodos
expandAll = function()
{
    var _this = this;
    function expand(node){
        var def = new dojo.Deferred();
        // Expand the node
        _this._expandNode(node).addCallback(function(){
            // When node has expanded, call expand() recursively on each non-leaf child
            var childBranches = dojo.filter(node.getChildren() || [], function(node){
                return node.isExpandable;
            }),
            defs = dojo.map(childBranches, expand);
            // And when all those recursive calls finish, signal that I'm finished
            new dojo.DeferredList(defs).addCallback(function(){
                def.callback();
            });
        });
        return def;
    }
    return expand(this.rootNode);
};

//Cerrar todos los nodos
collapseAll = function(){
    var _this = this;
    function collapse(node) {
        // never collapse root node, otherwise hides whole tree !
        if ( _this.showRoot==false)
                            if(node != _this.rootNode)
            _this._collapseNode(node);
        var childBranches = dojo.filter(node.getChildren() || [], function(node)
        {
            return node.isExpandable;
        });
        var def = new dojo.Deferred();
        defs = dojo.map(childBranches, collapse);
    }
    return collapse( _this.rootNode);
};

/*
	iaaa:
		Se redefine una funciÃ³n de la librerÃ­a de dojo para permitir el soporte de uso de 
		scroll en los elementos del Ã¡rbol en los navegadores de dispositivos mÃ³viles que lo
		soporten, solucionando un problema que surge en Opera
*/
dojoOverwriteFunctions = {};
dojoOverwriteFunctions["dijit.layout.AccordionContainer"] = {};
dojoOverwriteFunctions["dijit.layout.AccordionContainer"]["_transition"] = 
function(/*dijit._Widget?*/ newWidget, /*dijit._Widget?*/ oldWidget, /*Boolean*/ animate){
	// Overrides StackContainer._transition() to provide sliding of title bars etc.
	var isMobileNavigator;
	try {
		isMobileNavigator = mobileNavigator;
	} catch(e) {
		isMobileNavigator = false;
	}
	if (isMobileNavigator) {
		try {
			if (getFacade && getFacade() && getFacade().deshabilitarRedimensionMapa) {
				getFacade().deshabilitarRedimensionMapa();
			}
		} catch(e) {}
	}
	if (dojo.isIE < 8){
		// workaround animation bugs by not animating; not worth supporting animation for IE6 & 7
		animate = false;
	}

	if(this._animation){
		// there's an in-progress animation.  speedily end it so we can do the newly requested one
		this._animation.stop(true);
		delete this._animation;
	}

	var self = this;

	if(newWidget){
		newWidget._wrapperWidget.set("selected", true);
		
		if(newWidget._wrapperWidget &&
		   newWidget._wrapperWidget.contentWidget && 
		   newWidget._wrapperWidget.contentWidget.collapseAll) {
			newWidget._wrapperWidget.contentWidget.collapseAll();
		}

		var d = this._showChild(newWidget);	// prepare widget to be slid in

		// Size the new widget, in case this is the first time it's being shown,
		// or I have been resized since the last time it was shown.
		// Note that page must be visible for resizing to work.
		if(this.doLayout && newWidget.resize){
			newWidget.resize(this._containerContentBox);
		}
	}
	
	if(oldWidget){
		oldWidget._wrapperWidget.set("selected", false);
		if(!animate){
			this._hideChild(oldWidget);
		}
	}

	if(animate){
		var newContents = newWidget._wrapperWidget.containerNode,
			oldContents = oldWidget._wrapperWidget.containerNode;

		// During the animation we will be showing two dijitAccordionChildWrapper nodes at once,
		// which on claro takes up 4px extra space (compared to stable AccordionContainer).
		// Have to compensate for that by immediately shrinking the pane being closed.
		var wrapperContainerNode = newWidget._wrapperWidget.containerNode,
			wrapperContainerNodeMargin = dojo._getMarginExtents(wrapperContainerNode),
			wrapperContainerNodePadBorder = dojo._getPadBorderExtents(wrapperContainerNode),
			animationHeightOverhead = wrapperContainerNodeMargin.h + wrapperContainerNodePadBorder.h;

		oldContents.style.height = (self._verticalSpace - animationHeightOverhead) + "px";
		
		this._animation = new dojo.Animation({
			node: newContents,
			duration: this.duration,
			curve: [1, this._verticalSpace - animationHeightOverhead - 1],
			onAnimate: function(value){
				value = Math.floor(value);	// avoid fractional values
				newContents.style.height = value + "px";
				oldContents.style.height = (self._verticalSpace - animationHeightOverhead - value) + "px";
			},
			onEnd: function(){
				delete self._animation;
				if (isMobileNavigator) {
					try {
						if (navigator.appName.toLowerCase().indexOf("opera") != -1) {
							oldWidget._wrapperWidget.containerNode.style.display = "none";
							self._hideChild(oldWidget);
							try{javascript:recalcularAlturasArbol();}catch(ex){javascript:parent.recalcularAlturasArbol();}
						} else {
							newContents.style.height = "auto";
							oldWidget._wrapperWidget.containerNode.style.display = "none";
							oldContents.style.height = "auto";
							self._hideChild(oldWidget);
							/*
								iaaa: 08-03-2013
								Modificaciones para mejorar la representaciÃ³n del arbol
								se corrigen problemas de altura y anchura
							*/
							var alt = obtenerAlturaContenedor();
							if(parseInt(newContents.clientHeight) > alt) {
								try{
									javascript:inicializarAlturasArbol(alt);
								}catch(ex){
									javascript:parent.inicializarAlturasArbol(alt);
								}
							}
						}
					} catch(e) {
						newContents.style.height = "auto";
						oldWidget._wrapperWidget.containerNode.style.display = "none";
						oldContents.style.height = "auto";
						self._hideChild(oldWidget);
					}
					try {
						if (getFacade && getFacade() && getFacade().habilitarRedimensionMapa) {
			    			getFacade().habilitarRedimensionMapa();
			    		}
					} catch(e) {}
				} else {
					newContents.style.height = "auto";
					oldWidget._wrapperWidget.containerNode.style.display = "none";
					oldContents.style.height = "auto";
					self._hideChild(oldWidget);
					/*
						iaaa: 08-03-2013
						Modificaciones para mejorar la representaciÃ³n del arbol
						se corrigen problemas de altura y anchura
					*/
					var alt = obtenerAlturaContenedor();
					if(parseInt(newContents.clientHeight) > alt) {
						try{
							javascript:inicializarAlturasArbol(alt);
						}catch(ex){
							javascript:parent.inicializarAlturasArbol(alt);
						}
					}
				}
				if (newContents.children && newContents.children.length && 
				   (newContents.children[0].id.toLowerCase().indexOf("layout_accordionpane") != -1)) {
					var node = newContents.children[0];
					try{
						actualizarAnchuraContenedor(newContents, node);
					} catch(e) {
						try{
							parent.actualizarAnchuraContenedor(newContents, node);
						}catch(ex){}
					}
				}
			}
		});
		this._animation.onStop = this._animation.onEnd;
		this._animation.play();
	}

	return d;	// If child has an href, promise that fires when the widget has finished loading
};

/*
	iaaa: 08-03-2013
	Modificaciones para poder actualizar el icono de capa visible de nodos hijos de nodos expandibles
*/
dojoOverwriteFunctions["dijit._TreeNode"] = {};
dojoOverwriteFunctions["dijit._TreeNode"]["expand"] = function(){
	// summary:
	//		Show my children
	// returns:
	//		Deferred that fires when expansion is complete

	// If there's already an expand in progress or we are already expanded, just return
	if(this._expandDeferred){
		return this._expandDeferred;		// dojo.Deferred
	}

	// cancel in progress collapse operation
	this._wipeOut && this._wipeOut.stop();

	// All the state information for when a node is expanded, maybe this should be
	// set when the animation completes instead
	this.isExpanded = true;
	dijit.setWaiState(this.labelNode, "expanded", "true");
	if(this.tree.showRoot || this !== this.tree.rootNode){
		dijit.setWaiRole(this.containerNode, "group");
	}
	dojo.addClass(this.contentNode,'dijitTreeContentExpanded');
	this._setExpando();
	this._updateItemClasses(this.item);
	if(this == this.tree.rootNode){
		dijit.setWaiState(this.tree.domNode, "expanded", "true");
	}

	var pointer = this;
	var def,
		wipeIn = dojo.fx.wipeIn({
			node: this.containerNode, duration: dijit.defaultDuration,
			onBegin: function(){
				def.callback(true);
				var children = [];
				if (pointer && pointer.item && pointer.item.children && pointer.item.children.length) {
					children = pointer.item.children;
				}
				for (var i=0, len=pointer.item.children.length; i<len; i++) {
					var item = pointer.item.children[i];
					if (item.id) {
						var nodeId = item.id[0];
						if (pointer.tree && pointer.tree._itemNodesMap && pointer.tree._itemNodesMap[nodeId]) {
							var img = pointer.tree._itemNodesMap[nodeId][0].contentNode.children[0];
							var visibility = (item.clase == 'visible');
							if (visibility) {
								img.style.filter = 'alpha(opacity=100)';
								img.style.opacity = 1;
							} else {
								img.style.filter = 'alpha(opacity=20)';
								img.style.opacity = 0.2;
							}
						}
					}
				}
			}
		});

	// Deferred that fires when expand is complete
	def = (this._expandDeferred = new dojo.Deferred(function(){
		// Canceller
		wipeIn.stop();
	}));

	wipeIn.play();

	return def;		// dojo.Deferred
}

if(navigator.appName == "Microsoft Internet Explorer"){
	//Resolver un problema de posicionamiento en nodos expandibles
	dojoOverwriteFunctions["dijit._TreeNode"] = {};
	dojoOverwriteFunctions["dijit._TreeNode"]["_setIndentAttr"] = function(indent){
		// summary:
		//		Tell this node how many levels it should be indented
		// description:
		//		0 for top level nodes, 1 for their children, 2 for their
		//		grandchildren, etc.
	
		// Math.max() is to prevent negative padding on hidden root node (when indent == -1)
		var pixels = (Math.max(indent, 0) * this.tree._nodePixelIndent) + "px";
		
		if(parseInt(pixels) == 1900) {
			pixels = "19px";
		}
		
		dojo.style(this.domNode, "backgroundPosition",	pixels + " 0px");
		dojo.style(this.rowNode, this.isLeftToRight() ? "paddingLeft" : "paddingRight", pixels);
	
		dojo.forEach(this.getChildren(), function(child){
			child.set("indent", indent+1);
		});
		
		this._set("indent", indent);
	};
}

//Redefinir funciones de dojo
function redefinirFuncionesDojo() {
	var object;
	for (var i in dojoOverwriteFunctions) {
		try {
			object = eval(i);
			for (var j in dojoOverwriteFunctions[i]) {
				if (object) {
					object.prototype[j] = dojoOverwriteFunctions[i][j];
				}
			}
		} catch (e) {
		}
	}
}

/*************************************************************************************/
/**Funciones de configuracion e inicializacion del arbol							**/
/*************************************************************************************/
//Definir categorÃ­as del Ã¡rbol, se emplea la lista de categorÃ­as para calcular la altura del Ã¡rbol
function definirCategoriasArbol(categories) {
	try {
		if(categories) {
			treeCategories = categories;
			for (var i=0, iLen = treeCategories.length; i<iLen; i++){
				if (treeCategories[i] && treeCategories[i].id && 
					(treeCategories[i].id.toLowerCase().indexOf("tree")!=-1)) {
					treeCategories[i].collapseAll = collapseAll;
					treeCategories[i].expandAll = expandAll;
				}
			}
		}
	} catch (e) {
	}
}

/*
	iaaa: 08-03-2013
	Modificaciones para redimensionar el arbol correctamente
*/
//Abrir/cerrar el Ã¡rbol al iniciarlo
function abrirArbolAlInicializar(valor){
	try {
			colapsar();
			resizeElements(true);
			if (getFacade && getFacade().redimensionarArbol) {
				getFacade().redimensionarArbol();
			}
			if(valor) {
				//Para que el Ã¡rbol salga correctamente abierto (todos sus elementos colocados
				//y redimensionados correctamente se cierra y se vuelve a abrir)
				colapsar();
			}
	} catch(ex) {
			try {
				parent.colapsar();
				parent.resizeElements(true);
				if (parent.getFacade && parent.getFacade().redimensionarArbol) {
					parent.getFacade().redimensionarArbol();
				}
				if (valor) {
					//Para que el Ã¡rbol salga correctamente abierto (todos sus elementos colocados
					//y redimensionados correctamente se cierra y se vuelve a abrir)
					parent.colapsar();
				}
			} catch(ex1){
				//no se ecuentra la funcion
			}
	}
}

//Mostrar el boede del Ã¡rbol redondeado
var treeParentNodeId = null;
function mostrarBordeRedondeado(altura, parentNodeId, borderNodeId, borderFillId, borderUpId, borderDownId){
	if (parentNodeId && borderNodeId && borderFillId && borderUpId && borderDownId) {
		treeParentNodeId = parentNodeId;
		var treeNodeRound = document.createElement('div');
		treeNodeRound.setAttribute('id', borderNodeId);
		var treeNodeRoundImgUp = document.createElement('img');
		treeNodeRoundImgUp.setAttribute('id', borderUpId);
		treeNodeRoundImgUp.src = directorioRecursos + "/tree_corner_up.gif";
		treeNodeRound.appendChild(treeNodeRoundImgUp);
		var treeNodeRoundInner = document.createElement('div');
		treeNodeRoundInner.setAttribute('id', borderFillId);
		//Internet explorer requiere que se defina el ancho del div
		treeNodeRoundInner.style.width = "13px";
		treeNodeRound.appendChild(treeNodeRoundInner);
		var treeNodeRoundImgDown = document.createElement('img');
		treeNodeRoundImgDown.setAttribute('id', borderDownId);
		treeNodeRoundImgDown.src = directorioRecursos + "/tree_corner_down.gif";
		treeNodeRound.appendChild(treeNodeRoundImgDown);
		if (parentNodeId) {
			document.getElementById(parentNodeId).appendChild(treeNodeRound);
		} else {
			document.body.appendChild(treeNodeRound);
		}
		treeNodeRound.style.height=altura+"px";
		treeNodeRoundInner.style.height=(altura-26)+"px";
	}
}
/*
	iaaa: 08-03-2013
	Variables que almacenan informaciÃ³n para el cÃ¡lculo de altura del Ã¡rbol
*/
var treeNodesNumber = 0;
var treeNodesSize = 0;
var treeMargin = 0;
var treeMarginTop = 0;
var treeMarginBottom = 0;
//Inicializar la altura de cada uno de los contenedores de los nodos del arbol
function inicializarAlturaNodosArbol(altura, numHijos, tamHijo, margen){
	altura = ((altura != undefined) && (altura != null))?altura:10;
	numHijos = ((numHijos != undefined) && (numHijos != null))?numHijos:0;
	tamHijo = ((numHijos != undefined) && (numHijos != null))?tamHijo:0;
	margen = ((margen != undefined) && (margen != null))?margen:0;
	
	treeNodesNumber = numHijos;
	treeNodesSize = tamHijo;
	if ((typeof margen).toLowerCase() == "number") {
		treeMarginTop = margen;
		treeMarginBottom = margen;
	} else {
		treeMarginTop = margen.top;
		treeMarginBottom = margen.bottom;
	}
	
	var alt = altura - (numHijos*tamHijo + safeMargin) - margen - margen;

	if (alt < 0) {
		alt = 3;
	}
		
	/*
		iaaa 21-2-2013:
		Se fuerza el tamaÃ±o de los contenedores de las listas de capas 
	*/
	try {
		inicializarAlturasArbol(alt);
	} catch(ex) {
		try {
			parent.inicializarAlturasArbol(alt);
		} catch(ex1) {
		}
	}
}

//Inicializar el Ã¡rbol
function inicializarArbol(abierto, categorias, altura, tamCat, margen) {
	try {
		categorias = (categorias?categorias:[]);
		/*
		iaaa: 08-03-2013
			Informa de que el arbol se ha cargado
		*/
		try{
			setArbolCargado(true);
		} catch(ex) {
			try {
				parent.setArbolCargado(true);
			} catch(ex1) {
			}
		}
		abrirArbolAlInicializar(abierto);
		definirCategoriasArbol(categorias);
		inicializarAlturaNodosArbol(altura, categorias.length, tamCat, margen);
	} catch(ex2) {
		//alert("Error al cargar el arbol de capas: \n" + ex2);
	}
}

/*
	iaaa: 08-03-2013
	Funcion para obtener la actura actual del arbol
*/
function obtenerAlturaContenedor(){
	try {
		var altura = document.getElementById(treeParentNodeId).clientHeight;
		var alt = altura - (treeNodesNumber*treeNodesSize + safeMargin) - treeMarginTop - treeMarginBottom - 5;
		return alt;
	} catch(e) {
		return 0;
	}
}

/*************************************************************************************/
/**Funciones de interaccion con el Ã¡rbol 					    **/
/*************************************************************************************/
//Cerrar todos los nodos hijos del nodo seleccionado
function collapseSelectedTree(){
	try {
		for (var i = 0, iLen = treeCategories.length; i<iLen; i++) {
			if(treeCategories[i] && treeCategories[i].selected && treeCategories[i].collapseAll){
				treeCategories[i].collapseAll();
			}	
		}
	} catch(e) {
		//
	}
}
