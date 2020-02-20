var IDCAPATRAFICO = 500;
var treeControlActualidad;
var treeControlMovilidad;
var treeControlEquipamientos;
var esTactil = "ontouchstart" in document.documentElement;
var defined = false;
var collapsed = false;
parent.getFacade = getFacade;
try {
	if (dataActualidad) {
	}
} catch (e) {
	dataActualidad = ""
}
try {
	if (dataMovilidad) {
	}
} catch (e) {
	dataMovilidad = ""
}
try {
	if (dataTrafico) {
	}
} catch (e) {
	dataTrafico = ""
}
try {
	if (dataEquipamientos) {
	}
} catch (e) {
	dataEquipamientos = ""
}
var content;
try {
	if (treeOrderDataList) {
		content = "{'layers':[";
		var dataType;
		var lastChar = content.slice(content.length - 1, content.length);
		for ( var i = 0, iLen = treeOrderDataList.length; i < iLen; i++) {
			switch (treeOrderDataList[i]) {
			case "actualidad":
				dataType = dataActualidad;
				break;
			case "movilidad":
				dataType = dataMovilidad;
				break;
			case "equipamientos":
				dataType = dataEquipamientos;
				break;
			default:
				break
			}
			if (dataType != "") {
				content += (((i != 0) && (lastChar != "[")) ? "," : "")
						+ dataType;
				lastChar = content.slice(content.length - 1, content.length)
			}
		}
		content += "]}"
	}
} catch (e1) {
	try {
		content = "{'layers':["
				+ dataActualidad
				+ ((dataActualidad != "") ? "," : "")
				+ dataMovilidad
				+ (((dataActualidad != "") || (dataMovilidad != "")) ? "," : "")
				+ dataEquipamientos + "]}"
	} catch (e) {
		content = ""
	}
}
var userAgentInfo = navigator.userAgent.toLowerCase();
var mobileNavigator = (userAgentInfo
		&& (userAgentInfo.indexOf("android") != -1)
		|| (userAgentInfo.indexOf("iphone") != -1)
		|| (userAgentInfo.indexOf("ipod") != -1) || (userAgentInfo
		.indexOf("ipad") != -1));
var appleDevice = (userAgentInfo.indexOf("iphone") != -1)
		|| (userAgentInfo.indexOf("ipod") != -1)
		|| (userAgentInfo.indexOf("ipad") != -1);
var deviceType;
if (mobileNavigator) {
	if (((screen.width > 560) && (screen.width <= screen.height))
			|| ((screen.width > screen.height) && (screen.height > 560))) {
		deviceType = "tablet"
	} else {
		deviceType = "handheld"
	}
} else {
	deviceType = "screen"
}
var androidVersion = null;
var navigatorName = "unknown";
if (/firefox/.test(userAgentInfo)) {
	navigatorName = "firefox"
} else {
	if (/msie/.test(userAgentInfo)) {
		navigatorName = "msie"
	} else {
		if (/opera/.test(userAgentInfo)) {
			navigatorName = "opera"
		} else {
			if (/chrome/.test(userAgentInfo)) {
				navigatorName = "chrome"
			} else {
				if (/webkit/.test(userAgentInfo)
						&& !/chrome/.test(userAgentInfo)) {
					if (!/android/.test(userAgentInfo)) {
						navigatorName = "safari"
					} else {
						navigatorName = "android";
						var v = (navigator.appVersion)
								.match(/Android (\d+(?:\.\d+){1,2});/);
						if (v) {
							androidVersion = (parseInt(v[1], 10))
						}
					}
				}
			}
		}
	}
}
var supportsOrientationChange = "onorientationchange" in window, orientationEvent = supportsOrientationChange ? "orientationchange"
		: "resize";
var botonesHabilitados = true;
var deviceScreenWidth = window.outerWidth;
var deviceScreenHeight = window.outerHeight;
var initialScreenWidth = deviceScreenWidth;
var initialScreenHeight = deviceScreenHeight;
var deviceOrientation = null;
var widthMarginRange = 10;
if (mobileNavigator) {
	deviceOrientation = (deviceScreenWidth > deviceScreenHeight) ? "landscape"
			: "portrait";
	if (appleDevice) {
		if ((deviceScreenWidth >= (screen.width - widthMarginRange))
				&& (deviceScreenWidth <= (screen.width + widthMarginRange))) {
			deviceOrientation = "portrait"
		} else {
			deviceOrientation = "landscape"
		}
	} else {
		if (deviceScreenWidth < deviceScreenHeight) {
			deviceOrientation = "portrait"
		} else {
			deviceOrientation = "landscape"
		}
	}
}
var fullScreenModeState = {
	active : false,
	orientation : null
};
var elementosPendientes = [];
var externalToolBarOriginalPosition = null;
var oldHomeHeight = null;
var oldHomeWidth = null;
var oldMapHeight = null;
var oldMapWidth = null;
var oldMapMargin = null;
var oldMarkupHeight = null;
var oldExpandedPopupDivHeight = null;
var oldBackgroundTreeDivHeight = null;
var oldBotcolapseHeight = null;
var oldMarkuproundHeight = null;
var oldMarkuproundinnerHeight = null;
var oldBotcolapseTop = null;
var arbolClassName = "claro";
var nodoPadreArbolId = "eti";
var nodoArbolId = "markup";
var botonArbolId = "botcolapse";
var fondoArbolDivId = "backgroundTreeDiv";
var bordeArbolDivId = "markupround";
var bordeArbolRellenoDivId = "markuproundinner";
var bordeArbolEsquinaSupDivId = "markuproundup";
var bordeArbolEsquinaInfDivId = "markuprounddown";
var cabeceraDivId = "cabecera";
var mapaVisorId = "map";
var mapaArbolCabeceraId = "viewerTreeHeader";
var mapaToolbarId = "viewerToolBarContainer";
var mapaToolbarMargin = 25;
var mapaContenedorWebId = "mapa";
var mapaMaxiContenedorWebId = "maxiMapa";
var mapaMaxiIframeContenedorId = "iframeContainer";
var botonBorrarPoiId = "borrarPOI";
var iframeVisorId = "mapahome";
var iframeVisorMaxiId = "mapahomeMaxi";
var externalToolBarContainerId = "externalToolBarContainer";
var externalToolButtonsContainerId = "externalToolButtonsContainer";
var externalToolButtonsTableId = "externalToolButtonsTable";
var externalToolButtonsTableContentId = "externalToolButtonsTableContent";
var dojoArbolNodoSeleccionadoBlankImg = "//ajax.googleapis.com/ajax/libs/dojo/1.6.1/dojo/resources/blank.gif";
var idezarArbolNodoSeleccionadoImg = "//www.zaragoza.es/img/seleccionado.gif";
var dojoArbolNodoNoSeleccionadoOpacity = 0.2;
var timedUpdate = null;
var updatedOnce = false;
var maxiMapaTitulo = null;
var minimaAlturaMapa = 200;
var miniMapMode = null;
var colaborativeMode = null;
var showTreeHeader = false;
if (showTreeHeader == false) {
	mapaArbolCabeceraId = "";
}
var treeMargin = 0;
var treeMarginTop = (showTreeHeader ? (mobileNavigator ? 30 : 128) : 0);
var treeMarginBottom = 0;
var scrollPosition = 0;
var homeIP = "..";
var homeMaxiViewerIP = "//www.zaragoza.es/IDEZar_visorMiniMaxi";
var borrarPoiImg20x20 = homeIP + "/img/borrarGeometrias_20x20.png";
var borrarPoiImg24x24 = homeIP + "/img/borrarGeometrias_24x24.png";
var borrarPoiImg30x30 = homeIP + "/img/borrarGeometrias_30x30.png";
var botonArbolDeshabilitadoImg = homeIP + "/img/new2015/botonArbolCapas.png";
var botonArbolCerradoImg = homeIP + "/img/new2015/botonArbolCapas.png";
var botonArbolCerradoCargandoImg = homeIP + "/img/new2015/botonArbolCapas.png";
var botonArbolAbiertoImg = homeIP + "/img/new2015/botonArbolCapas.png";
var scriptGeocoderServlet = "//www.zaragoza.es/geocoderIDEZar/dwr/interface/GeorefServlet.js";
var scriptGeocoderEngine = "//www.zaragoza.es/geocoderIDEZar/dwr/engine.js";
var scriptGeocoderGUI = "//www.zaragoza.es/geocoderIDEZar/javascript/GUI.js";
var dojoFilePath = "/js/dojo.xd.js";
var scriptDojoLib = homeIP + dojoFilePath;
var treeConfigStyleFilePath = "/css/tree_config.css";
var estiloTreeConfig = homeIP + treeConfigStyleFilePath;
var treeFunctionsFilePath = "/js/tree_functions.js";
var scriptTreeFunctions = homeIP + treeFunctionsFilePath;
var treeConfigFilePath = "/js/tree_config.js";
var scriptTreeConfig = homeIP + treeConfigFilePath;
if (typeof ocultarArbolCapas == "undefined") {
	ocultarArbolCapas = true;
}
if (typeof jsonMaxi == "undefined") {
	jsonMaxi = null;
}
if (typeof jsonMini == "undefined") {
    jsonMini = null; 
}
ogob = "<a class='exte' href='//www.zaragoza.es/ciudad/gobierno-abierto/' accesskey='9'>Gobierno Abierto<span class='inv'>: </span><span class='oculto'>Datos Abiertos, Participaciï¿½n y Colaboraciï¿½n, Espacios de Participaciï¿½n, Transparencia</span></a>";
ayto = "<a href='//www.zaragoza.es/ciudad/ayto/' accesskey='3' class='exte'>El Ayuntamiento<span class='inv'>: </span><span class='oculto'>El Alcalde, Organizaciï¿½n, Normativa, Oferta de Empleo, Tablï¿½n Municipal, Perfil del Contratante, Trï¿½mites y Servicios, Agua, Hacienda, Premios y Concursos, Ayudas y Subvenciones, Policia Zaragoza, Bomberos y Protecciï¿½n Civil, Archivo, Biblioteca, Hemeroteca, Noticias, Calidad</span></a>";
ciudad = "<a href='//www.zaragoza.es/ciudad/' accesskey='4' class='exte'>La Ciudad<span class='inv'>: </span><span class='oculto'> Cifras,Cementerio, Consumo, Cï¿½mo moverse, Deporte, Educaciï¿½n, Educaciï¿½n y Bibliotecas, Medio Ambiente, IDEZar, Infraestructuras, Salud Pï¿½blica, Servicios y Vï¿½a Pï¿½blica, Urbanismo, Vistas de la Ciudad, Zaragoza Internacional, Zaragoza Sin Barreras</span></a>";
la_gente = "<a href='//www.zaragoza.es/ciudad/sectores/' accesskey='5' class='exte'>Para la Gente<span class='inv'>: </span><span class='oculto'>Blog Ciudadano, Casa de las Culturas, Centros Municipales, Cooperaciï¿½n al Desarrollo, Juventud, Mujer, Participa, Participaciï¿½n Ciudadana, Personas Mayores, Personaliza, Servicios Sociales, Tecnologia y Ciudadanï¿½a   </span></a>";
cultura = "<a href='//www.zaragoza.es/ciudad/cultura/' accesskey='8' class='exte'>Cultura<span class='inv'>: </span><span class='oculto'>Arte Pï¿½blico, Centros Municipales, Edificios y Conjuntos de InterÃ©s,Monumentos, Museos y Exposiciones, Publicaciones Municipales, Premios y Concursos, Zaragoza Cultural </span></a>";
var currentRouteOrigin, currentRouteDestination;
var geocoderRouteOrigin;
function ocultarAvisosGeo() {
	var anchors = document.getElementsByTagName("div");
	for ( var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (anchor.getAttribute("class")
				&& anchor.getAttribute("class").indexOf("georref") >= 0) {
			anchor.style.position = "absolute";
			anchor.style.left = "-5000px";
			anchor.style.width = "0";
			anchor.style.overflow = "hidden"
		}
	}
}
function carga() {
	if (document.all) {
		eval("o_gob.innerHTML=ogob");
		eval("el_ayto.innerHTML=ayto");
		eval("la_ciudad.innerHTML=ciudad");
		try {
			eval("gente.innerHTML=la_gente")
		} catch (e) {
			eval("la_gente.innerHTML=la_gente")
		}
		eval("la_cultura.innerHTML=cultura")
	} else {
		if (document.layers) {
		} else {
			document.getElementById("o_gob").innerHTML = ogob;
			document.getElementById("el_ayto").innerHTML = ayto;
			document.getElementById("la_ciudad").innerHTML = ciudad;
			try {
				document.getElementById("la_gente").innerHTML = la_gente
			} catch (e) {
				document.getElementById("gente").innerHTML = la_gente
			}
			document.getElementById("la_cultura").innerHTML = cultura
		}
	}
}
function eliminarGeo() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	try {
		if (treeControlActualidad) {
			eliminarGeoDeTree(treeControlActualidad)
		} else {
			if (parent.frames[mapId].treeControlActualidad) {
				eliminarGeoDeTree(parent.frames[mapId].treeControlActualidad)
			}
		}
		if (treeControlMovilidad) {
			eliminarGeoDeTree(treeControlMovilidad)
		} else {
			if (parent.frames[mapId].treeControlMovilidad) {
				eliminarGeoDeTree(parent.frames[mapId].treeControlMovilidad)
			}
		}
		if (treeControlEquipamientos) {
			eliminarGeoDeTree(treeControlEquipamientos)
		} else {
			if (parent.frames[mapId].treeControlEquipamientos) {
				eliminarGeoDeTree(parent.frames[mapId].treeControlEquipamientos)
			}
		}
	} catch (e) {
		if (parent.frames[mapId].treeControlActualidad) {
			eliminarGeoDeTree(parent.frames[mapId].treeControlActualidad)
		}
		if (parent.frames[mapId].treeControlMovilidad) {
			eliminarGeoDeTree(parent.frames[mapId].treeControlMovilidad)
		}
		if (parent.frames[mapId].treeControlEquipamientos) {
			eliminarGeoDeTree(parent.frames[mapId].treeControlEquipamientos)
		}
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	facade.eliminarCalle()
}
function eliminarGeoDeTree(tree) {
	for ( var itera in tree._itemNodesMap) {
		if (itera != "undefined") {
			var node = tree._itemNodesMap[itera];
			var nodo = node[0];
			if (nodo.item) {
				if (nodo.item.children) {
					for (i = 0; i < nodo.item.children.length; i++) {
						ocultarNodoInterno(nodo.item.children[i])
					}
				} else {
					ocultarNodo(nodo)
				}
			}
		}
	}
}
function ocultarNodo(nodo) {
	var estadoNodo = nodo.item.clase;
	nodo.item.clase = "oculto";
	if (nodo.rowNode.children && nodo.rowNode.children.length
			&& (nodo.rowNode.children[0].tagName.toLowerCase() == "img")) {
		nodo.contentNode.children[0].style.filter = 'alpha(opacity=' + (dojoArbolNodoNoSeleccionadoOpacity * 100) + ')';
		nodo.contentNode.children[0].style.opacity = dojoArbolNodoNoSeleccionadoOpacity;
	} else {
		nodo.contentNode.children[0].style.filter = 'alpha(opacity=100)';
		nodo.contentNode.children[0].style.opacity = 1;
	}
	if (nodo.item.id[0] == IDCAPATRAFICO) {
		if (estadoNodo == "visible") {
			actualizarVisibilidadTrafico("FALSE")
		}
	} else {
		actualizarVisibilidad(nodo.item.id[0], "FALSE")
	}
}
function ocultarNodoInterno(nodo) {
	var estadoNodo = nodo.clase;
	nodo.clase = "oculto";
	try {
		if (nodo.rowNode.children && npdo.rowNode.children.length
				&& (nodo.rowNode.children[0].tagName.toLowerCase() == "img")) {
			nodo.contentNode.children[0].style.filter = 'alpha(opacity=' + (dojoArbolNodoNoSeleccionadoOpacity * 100) + ')';
			nodo.contentNode.children[0].style.opacity = dojoArbolNodoNoSeleccionadoOpacity;
		} else {
			nodo.contentNode.children[0].style.filter = 'alpha(opacity=100)';
			nodo.contentNode.children[0].style.opacity = 1;
		}
	} catch (e) {
	}
	if (nodo.id[0] == IDCAPATRAFICO) {
		if (estadoNodo == "visible") {
			actualizarVisibilidadTrafico("FALSE")
		}
	} else {
		actualizarVisibilidad(nodo.id[0], "FALSE")
	}
}
function crearBotonEliminarGeo() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var borradoPOI = document.createElement("div");
	borradoPOI.setAttribute("id", botonBorrarPoiId);
	var enlace = document.createElement("a");
	enlace.setAttribute("href", "#");
	if (navigator.appName == "Microsoft Internet Explorer") {
		enlace.onclick = function() {
			try {
				javascript: eliminarGeo()
			} catch (e) {
				javascript: parent.eliminarGeo()
			}
			return false
		}
	} else {
		enlace
				.setAttribute(
						"onClick",
						"try{javascript:eliminarGeo();}catch(e){javascript:parent.eliminarGeo();}return false;")
	}
	var img = document.createElement("img");
	img.setAttribute("id", "eliminarGeo_img");
	img.setAttribute("alt", "Limpiar Mapa");
	img.setAttribute("title", "Limpiar Mapa");
	if (deviceType == "handheld") {
		img.setAttribute("src", borrarPoiImg24x24);
		img.setAttribute("height", "24");
		img.setAttribute("width", "24")
	} else {
		if (deviceType == "tablet") {
			img.setAttribute("src", borrarPoiImg30x30);
			img.setAttribute("height", "30");
			img.setAttribute("width", "30")
		} else {
			img.setAttribute("src", borrarPoiImg20x20);
			img.setAttribute("height", "20");
			img.setAttribute("width", "20")
		}
	}
	img.setAttribute("border", "0");
	enlace.appendChild(img);
	borradoPOI.appendChild(enlace);
	var externalToolBarContainer = document.createElement("DIV");
	externalToolBarContainer.setAttribute("id", externalToolBarContainerId);
	var externalToolButtonsContainer = document.createElement("DIV");
	externalToolButtonsContainer.setAttribute("id",
			externalToolButtonsContainerId);
	var externalToolButtonsTable = document.createElement("TABLE");
	externalToolButtonsTable.setAttribute("id", externalToolButtonsTableId);
	var externalToolButtonsTableTBody = document.createElement("TBODY");
	var externalToolButtonsTableContent = document.createElement("TR");
	externalToolButtonsTableContent.setAttribute("id",
			externalToolButtonsTableContentId);
	var borradoPOIButtonTD = document.createElement("TD");
	borradoPOIButtonTD.setAttribute("id", "borradoPOIButtonTD");
	if (navigator.appName == "Microsoft Internet Explorer") {
		var value;
		if (deviceType == "handheld") {
			value = 24
		} else {
			if (deviceType == "tablet") {
				value = 36
			} else {
				value = 20
			}
		}
		externalToolBarContainer.style.setAttribute("cssText",
				"position:absolute;bottom:16px;right:30px;z-index:10000;width:"
						+ value + "px");
		externalToolButtonsContainer.style.setAttribute("cssText",
				"position:relative; vertical-align:baseline; float:left;width:"
						+ value + "px");
		img.style.setAttribute("cssText", "width:" + value + "px;height:"
				+ value + "px;z-index:1;");
		borradoPOI.setAttribute("align", "center");
		externalToolButtonsContainer.appendChild(borradoPOI);
		externalToolBarContainer.appendChild(externalToolButtonsContainer)
	} else {
		externalToolBarContainer.setAttribute("style",
				"position:absolute;bottom:16px;right:30px;z-index:10000;");
		externalToolButtonsContainer.setAttribute("style",
				"position:relative; vertical-align:baseline; float:left;");
		externalToolButtonsTable.setAttribute("style",
				"border-collapse:collapse;");
		externalToolButtonsTable.setAttribute("border", "0");
		borradoPOIButtonTD.appendChild(borradoPOI);
		externalToolButtonsTableContent.appendChild(borradoPOIButtonTD);
		externalToolButtonsTableTBody
				.appendChild(externalToolButtonsTableContent);
		externalToolButtonsTable.appendChild(externalToolButtonsTableTBody);
		externalToolButtonsContainer.appendChild(externalToolButtonsTable);
		externalToolBarContainer.appendChild(externalToolButtonsContainer)
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (facade && facade.agregarElementoExterno) {
		facade.agregarElementoExterno(externalToolBarContainer)
	}
}
function obtenerElementoPorId(id) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	if (document.getElementById(id)) {
		return document.getElementById(id)
	} else {
		return parent.frames[mapId].document.getElementById(id)
	}
}
function obtenerElementosPorClase(clase) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var appVersion = navigator.appVersion.toLowerCase();
	if ((navigator.appName == "Microsoft Internet Explorer")
			&& ((navigator.appVersion.indexOf("MSIE 8") != -1)
					|| (navigator.appVersion.indexOf("MSIE 7") != -1)
					|| (navigator.appVersion.indexOf("MSIE 6") != -1) || (navigator.appVersion
					.indexOf("MSIE 5") != -1))) {
		var nodo = obtenerElementoPorId(nodoArbolId);
		var lista = [];
		if (nodo) {
			var listaDivs = nodo.getElementsByTagName("DIV");
			for ( var i = 0, iLen = listaDivs.length; i < iLen; i++) {
				if ((listaDivs[i].getAttribute("class") == clase)
						|| (listaDivs[i].className && (listaDivs[i].className == clase))) {
					lista.push(listaDivs[i])
				}
			}
		}
		return lista
	} else {
		var lista = document.getElementsByClassName(clase);
		if (lista && lista.length) {
			return lista
		} else {
			return parent.frames[mapId].document.getElementsByClassName(clase)
		}
	}
}
function resizeElements(forceResize, forceHeight) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	var mapContainerId;
	if (!maxiMapaVisible()) {
		mapContainerId = mapaContenedorWebId
	} else {
		mapContainerId = mapaMaxiContenedorWebId
	}
	var orientationChange = false;
	if (!miniMapMode && !colaborativeMode) {
		if ((deviceScreenWidth != window.outerWidth)
				&& ((Math.abs(deviceScreenWidth - window.outerHeight) == Math
						.abs(deviceScreenHeight - window.outerWidth))
						|| ((deviceScreenWidth == initialScreenWidth) && (window.outerWidth != initialScreenHeight)) || ((deviceScreenWidth != initialScreenWidth) && (window.outerWidth == initialScreenWidth)))) {
			if (mobileNavigator && facade && facade.ocultarMapa) {
				facade.ocultarMapa()
			}
			orientationChange = true
		}
	} else {
		if (deviceScreenWidth != window.outerWidth) {
			if (mobileNavigator && facade && facade.ocultarMapa) {
				facade.ocultarMapa()
			}
			orientationChange = true
		}
	}
	deviceScreenWidth = window.outerWidth;
	deviceScreenHeight = window.outerHeight;
	if (mobileNavigator && orientationChange) {
		deviceOrientation = (deviceScreenWidth > deviceScreenHeight) ? "landscape"
				: "portrait";
		if (appleDevice) {
			if ((deviceScreenWidth >= (screen.width - widthMarginRange))
					&& (deviceScreenWidth <= (screen.width + widthMarginRange))) {
				deviceOrientation = "portrait"
			} else {
				deviceOrientation = "landscape"
			}
		} else {
			if (deviceScreenWidth < deviceScreenHeight) {
				deviceOrientation = "portrait"
			} else {
				deviceOrientation = "landscape"
			}
		}
	}
	if ((forceResize == null) || (forceResize == undefined)) {
		forceResize = false
	}
	if ((!mobileNavigator) || (miniMapMode) || (colaborativeMode)) {
		if (!mobileNavigator && miniMapMode && maxiMapaVisible()) {
		} else {
			if (mobileNavigator) {
				if (orientationChange) {
					cerrarPopupExpandido()
				}
				if (orientationChange && maxiMapaVisible()) {
					getFacade().maximizaMapa(false, true);
					if (timedUpdate) {
						window.clearTimeout(timedUpdate);
						timedUpdate = null
					}
					timedUpdate = window.setTimeout(
							"actualizarDimensionesMaxiMapaRetardado()", 250)
				} else {
					if (orientationChange
							|| (maxiMapaVisible() && !updatedOnce)) {
						if (timedUpdate) {
							window.clearTimeout(timedUpdate);
							timedUpdate = null
						}
						timedUpdate = window.setTimeout(
								"actualizarDimensionesMapaRetardado({orientationChange:"
										+ orientationChange + "})", 100);
						updatedOnce = true
					} else {
						if (!updatedOnce) {
							updatedOnce = true
						}
					}
				}
			} else {
				if (colaborativeMode) {
					if (timedUpdate) {
						window.clearTimeout(timedUpdate);
						timedUpdate = null
					}
					timedUpdate = window.setTimeout(
							"actualizarDimensionesMapaRetardado()", 100);
					updatedOnce = true
				}
			}
		}
		if (!mobileNavigator) {
			if (!collapsed) {
				colapsar()
			}
		} else {
			if (colaborativeMode && orientationChange && fullScreenModeState
					&& fullScreenModeState.active) {
				if (!collapsed) {
					colapsar()
				}
			}
		}
		return
	}
	var mapaDiv = document.getElementById(mapContainerId);
	var homeDiv = document.getElementById(mapId);
	var node;
	if (mobileNavigator) {
		if (orientationChange) {
			cerrarPopupExpandido()
		}
		if (!orientationChange && !forceResize) {
			return
		}
		if (fullScreenModeState.active && !forceResize) {
			if (!collapsed) {
				colapsar()
			}
			var height = ((userAgentInfo.indexOf("iphone") == -1) ? (window.outerHeight / window.devicePixelRatio)
					: parseInt(window.innerHeight))
					+ "px";
			var nodo = obtenerElementoPorId(nodoArbolId);
			if (nodo) {
				nodo.style.height = ((parseInt(height)) - treeMarginTop - treeMarginBottom)
						+ "px"
				nodo.style.top = treeMarginTop;
				var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
				if (nodoAux) {
					nodoAux.style.height = nodo.style.top;
				}
			}
			nodo = obtenerElementoPorId(bordeArbolDivId);
			if (nodo) {
				nodo.style.height = ((parseInt(height)) - treeMarginTop - treeMarginBottom)
						+ "px"
			}
			nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
			if (nodo) {
				nodo.style.height = ((parseInt(height) - 26) - treeMarginTop - treeMarginBottom)
						+ "px"
			}
			if (homeDiv) {
				if (userAgentInfo.indexOf("iphone") != -1) {
					homeDiv.style.height = height
				} else {
					homeDiv.style.height = "100%"
				}
				homeDiv.style.width = "100%"
			}
			if (mapaDiv) {
				mapaDiv.style.height = height;
				mapaDiv.style.width = "100%";
				mapaDiv.style.margin = "0"
			}
			var arbolContenedorAltura = ((parseInt(height)) - treeMarginTop - treeMarginBottom) - 150;
			if (arbolContenedorAltura < 0) {
				arbolContenedorAltura = 3
			}
			inicializarAlturasArbol(arbolContenedorAltura);
			window.scrollTo(0, 0);
			window.scrollTo(0, 1);
			window.scrollTo(0, 80);
			return
		}
	}
	var altura = calcularAltura();
	var arbolContenedorAltura = (altura - treeMarginTop - treeMarginBottom) - 150;
	if (arbolContenedorAltura < 0) {
		arbolContenedorAltura = 3
	}
	inicializarAlturasArbol(arbolContenedorAltura);
	if (mobileNavigator) {
		if (!collapsed) {
			colapsar()
		}
		if (facade && facade.actualizarDimensionesMapa) {
			facade.actualizarDimensionesMapa()
		}
		window.focus(mapaDiv)
	} else {
		if (!collapsed) {
			colapsar()
		}
	}
}
function actualizarDimensionesMapaRetardado(options) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (getFacade && getFacade().arbolVisible && !getFacade().arbolVisible()) {
		if (colaborativeMode && options && options.orientationChange) {
			if (fullScreenModeState && fullScreenModeState.active) {
				if (mobileNavigator && appleDevice) {
					window.scrollTo(0, 0)
				}
				var ifrm = document.getElementById(mapId);
				if (ifrm) {
					ifrm.style.height = window.innerHeight + "px";
					ifrm.style.width = document.body.clientWidth + "px"
				}
			} else {
				if (appleDevice && mobileNavigator) {
					var oldValueHeight;
					var oldValueWidth;
					var mapContainerStyle = null;
					var ifrm = document.getElementById(mapId);
					if (ifrm) {
						oldValueHeight = ifrm.style.height;
						if (node.style && node.style.height) {
							ifrm.style.height = node.style.height
						} else {
							mapContainerStyle = window.getComputedStyle(node,
									null);
							ifrm.style.height = mapContainerStyle.height
						}
						oldValueWidth = ifrm.style.width;
						if (node.style && node.style.width) {
							ifrm.style.width = node.style.width
						} else {
							if (mapContainerStyle == null) {
								mapContainerStyle = window.getComputedStyle(
										node, null)
							}
							ifrm.style.width = mapContainerStyle.width
						}
						var facade;
						if (parent.frames[mapId].getFacade) {
							facade = parent.frames[mapId].getFacade()
									|| parent.frames[mapId].facade
						} else {
							facade = parent.frames[mapId].facade
						}
						if (facade && facade.actualizarDimensionesMapa) {
							facade.actualizarDimensionesMapa()
						}
						ifrm.style.height = oldValueHeight;
						ifrm.style.width = oldValueWidth
					}
				}
			}
		}
		if (facade && facade.actualizarDimensionesMapa) {
			facade.actualizarDimensionesMapa()
		}
		focusToMap()
	}
	try {
		window.clearTimeout(timedUpdate);
		timedUpdate = null
	} catch (e) {
	}
}
function focusToMap() {
	var mapContainerId;
	if (!maxiMapaVisible()) {
		mapContainerId = mapaContenedorWebId
	} else {
		mapContainerId = mapaMaxiContenedorWebId
	}
	var mapaDiv = document.getElementById(mapContainerId);
	if (mapaDiv) {
		window.focus(mapaDiv)
	}
}
function actualizarDimensionesMaxiMapaRetardado() {
	var mapId = iframeVisorMaxiId;
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (facade && facade.actualizarDimensionesMapa) {
		facade.actualizarDimensionesMapa()
	}
	var timedCode = "getFacade().maximizaMapa(true);getFacade().redimensionarArbol();focusToMap();try{window.clearTimeout(timedUpdate); timedUpdate=null;}catch(e){}";
	if (timedUpdate) {
		window.clearTimeout(timedUpdate);
		timedUpdate = null
	}
	timedUpdate = setTimeout(timedCode, 250)
}
function asignarAlturaACabecera() {
	var nodo = document.getElementById(cabeceraDivId);
	var valor = 0;
	if (nodo && nodo.children) {
		for ( var i = 0, iLen = nodo.children.length; i < iLen; i++) {
			valor += parseInt(nodo.children[i].clientHeight)
		}
	}
	if (nodo) {
		if (valor) {
			nodo.style.height = valor + "px"
		} else {
			nodo.style.height = ""
		}
	}
}
function calcularAltura(height) {
	var altura = 380;
	var nuevaAltura;
	if (document.body && document.body.offsetWidth) {
		altura = document.body.offsetHeight
	}
	if (document.compatMode == "CSS1Compat" && document.documentElement
			&& document.documentElement.offsetWidth) {
		altura = document.documentElement.offsetHeight
	}
	if (window.innerWidth && window.innerHeight) {
		altura = window.innerHeight
	}
	if (height) {
		altura = height
	}
	altura = parseInt(altura);
	var mapId;
	var maxiMapaExpandido = false;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId;
		maxiMapaExpandido = true
	}
	var mapContainerId;
	if (!maxiMapaVisible()) {
		mapContainerId = mapaContenedorWebId
	} else {
		mapContainerId = mapaMaxiContenedorWebId
	}
	if (maxiMapaExpandido || miniMapMode || colaborativeMode) {
		var facade;
		if (parent.frames[mapId].getFacade) {
			facade = parent.frames[mapId].getFacade()
					|| parent.frames[mapId].facade
		} else {
			facade = parent.frames[mapId].facade
		}
		if (facade && facade.obtenerAlturaContenedorMapa) {
			nuevaAltura = facade.obtenerAlturaContenedorMapa()
		}
		if (nuevaAltura == 0) {
			nuevaAltura = altura
		}
		var nodo = obtenerElementoPorId(nodoArbolId);
		if (nodo) {
			nodo.style.height = ((nuevaAltura) - treeMarginTop - treeMarginBottom)
					+ "px"
			nodo.style.top = treeMarginTop;
			var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
			if (nodoAux) {
				nodoAux.style.height = nodo.style.top;
			}
		}
		nodo = obtenerElementoPorId(bordeArbolDivId);
		if (nodo) {
			nodo.style.height = ((nuevaAltura) - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
		if (nodo) {
			nodo.style.height = ((nuevaAltura - 26) - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		return nuevaAltura
	}
	if (altura > 820) {
		altura = altura - 350;
		nuevaAltura = altura - 22;
		var nodo = obtenerElementoPorId(mapContainerId);
		if (nodo) {
			nodo.style.height = nuevaAltura + "px"
		}
		nodo = obtenerElementoPorId(mapId);
		if (nodo) {
			nodo.style.height = nuevaAltura + "px"
		}
		nodo = obtenerElementoPorId(nodoArbolId);
		if (nodo) {
			nodo.style.height = (nuevaAltura - treeMarginTop - treeMarginBottom)
					+ "px"
			nodo.style.top = treeMarginTop;
			var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
			if (nodoAux) {
				nodoAux.style.height = nodo.style.top;
			}
		}
		nodo = obtenerElementoPorId(bordeArbolDivId);
		if (nodo) {
			nodo.style.height = (nuevaAltura - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
		if (nodo) {
			nodo.style.height = ((nuevaAltura - 26) - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		asignarAlturaACabecera();
		return nuevaAltura
	} else {
		var factor = 0.65;
		if (height) {
			factor = 1
		} else {
			if (mobileNavigator) {
				factor = 0.7
			}
		}
		nuevaAltura = parseInt(Math.floor(altura * factor));
		if (mobileNavigator) {
			if (nuevaAltura < minimaAlturaMapa) {
				nuevaAltura = minimaAlturaMapa
			}
		}
		var nodo = obtenerElementoPorId(mapContainerId);
		if (nodo) {
			nodo.style.height = nuevaAltura + "px"
		}
		nodo = obtenerElementoPorId(mapId);
		if (nodo) {
			nodo.style.height = nuevaAltura + "px"
		}
		nodo = obtenerElementoPorId(nodoArbolId);
		if (nodo) {
			nodo.style.height = (nuevaAltura - treeMarginTop - treeMarginBottom)
					+ "px"
			nodo.style.top = treeMarginTop;
			var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
			if (nodoAux) {
				nodoAux.style.height = nodo.style.top;
			}
		}
		nodo = obtenerElementoPorId(bordeArbolDivId);
		if (nodo) {
			nodo.style.height = (nuevaAltura - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
		if (nodo) {
			nodo.style.height = ((nuevaAltura - 26) - treeMarginTop - treeMarginBottom)
					+ "px"
		}
		asignarAlturaACabecera();
		return parseInt(Math.floor(nuevaAltura))
	}
}
function recalcularAlturasArbol() {
	if (!mobileNavigator) {
	}
	var lista1 = obtenerElementosPorClase("dijitAccordionChildWrapper");
	recalcularAlturaLista(lista1, false);
	var lista2 = obtenerElementosPorClase("dijitTreeContainer");
	recalcularAlturaLista(lista2, true);
	var lista3 = obtenerElementosPorClase("dijitContentPane dijitAccordionContainer-child dijitAccordionContainer-dijitContentPane");
	var altura = recalcularAlturaLista(lista3);
	var nodo = obtenerElementoPorId("resultados");
	if (nodo && (deviceType == "handheld")) {
		if (altura <= 16) {
			nodo.setAttribute("size", "1")
		} else {
			nodo.setAttribute("size", "5")
		}
	}
}
function inicializarAlturasArbol(altura) {
	if (!mobileNavigator) {
	}
	var lista = obtenerElementosPorClase("dijitAccordionChildWrapper");
	var alturaNodo;
	var innerHTMLCode = null;
	var indexNode = null;
	var nodeName = null;
	var indexNodeLast = null;
	var nodeToEvalue = null;
	for ( var i = 0, iLen = lista.length; i < iLen; i++) {
		if (lista[i].children
				&& lista[i].children[0]
				&& ((lista[i].children[0].id.indexOf("dijit_Tree_") != -1) || (lista[i].children[0].id
						.indexOf("dijit_layout_AccordionPane_") != -1))) {
			if (mobileNavigator && (navigatorName == "android")
					&& (androidVersion > 3)) {
				alturaNodo = 0;
				innerHTMLCode = null;
				indexNode = null;
				nodeName = null;
				indexNodeLast = null;
				nodeToEvalue = null;
				if (lista[i].children[0].id
						.indexOf("dijit_layout_AccordionPane_") != -1) {
					alturaNodo = parseInt(lista[i].children[0].clientHeight)
				} else {
					innerHTMLCode = lista[i].children[0].innerHTML;
					indexNode = innerHTMLCode.indexOf("dijit__TreeNode_");
					nodeName = null;
					indexNodeLast = null;
					if (indexNode) {
						indexNodeLast = innerHTMLCode.substring(indexNode)
								.indexOf('"');
						nodeName = innerHTMLCode.substring(indexNode, indexNode
								+ indexNodeLast)
					}
					if (nodeName) {
						nodeToEvalue = obtenerElementoPorId(nodeName);
						if (nodeToEvalue) {
							alturaNodo = parseInt(nodeToEvalue.clientHeight)
						}
					}
				}
				if (!isNaN(alturaNodo) && (alturaNodo != 0)
						&& (alturaNodo < altura)) {
					lista[i].children[0].style.height = alturaNodo + "px"
				} else {
					if (navigator.appName == "Microsoft Internet Explorer") {
						lista[i].children[0].style.setAttribute("cssText",
								"height: " + altura + "px")
					} else {
						if (mobileNavigator && (navigator.appName == "Opera")) {
							lista[i].children[0].style.height = 50 + "px"
						} else {
							lista[i].children[0].style.height = altura + "px"
						}
					}
				}
			} else {
				if (navigator.appName == "Microsoft Internet Explorer") {
					lista[i].children[0].style.setAttribute("cssText",
							"height: " + altura + "px")
				} else {
					if (mobileNavigator && (navigator.appName == "Opera")) {
						lista[i].children[0].style.height = 50 + "px"
					} else {
						lista[i].children[0].style.height = altura + "px"
					}
				}
			}
		}
	}
}
function actualizarAnchuraContenedor(padre, nodo) {
	margen = 20;
	if (nodo
			&& nodo.style
			&& nodo.style.width
			&& padre
			&& padre.clientWidth
			&& ((parseInt(nodo.style.width) < (parseInt(padre.clientWidth) - margen)) || (parseInt(nodo.style.width) > (parseInt(padre.clientWidth) - margen)))) {
		nodo.style.width = (parseInt(padre.clientWidth) - margen) + "px"
	}
}
function actualizarAnchuraArbol() {
	if (!mobileNavigator) {
	}
	var lista = obtenerElementosPorClase("dijitAccordionChildWrapper");
	for ( var i = 0, iLen = lista.length; i < iLen; i++) {
		if (lista[i].children
				&& lista[i].children[0]
				&& (lista[i].children[0].id
						.indexOf("dijit_layout_AccordionPane_") != -1)) {
			actualizarAnchuraContenedor(lista[i], lista[i].children[0])
		}
	}
}
function recalcularAlturaLista(lista, padre) {
	var parentNode;
	var listaHijos;
	var contHijos;
	var height;
	var maxHeight = 0;
	var nodo = obtenerElementoPorId(nodoArbolId);
	if (nodo) {
		maxHeight = parseInt(nodo.style.height) - 150
	}
	if (maxHeight < 0) {
		maxHeight = 3
	}
	for ( var i = 0, iLen = lista.length; i < iLen; i++) {
		lista[i].style.height = maxHeight + "px";
		lista[i].style.maxHeight = maxHeight + "px"
	}
	return maxHeight
}
function definirCapas(jsonContent) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (!ocultarArbolCapas) {
		var nodeEti = document.createElement("DIV");
		if (nodeEti) {
			nodeEti.setAttribute("id", nodoPadreArbolId);
			nodeEti.setAttribute("class", arbolClassName);
			nodeEti.className = arbolClassName;
			facade.agregarElementoExterno(nodeEti);
			//crearBotonEliminarGeo();
			crearBotonArbol();
			if (mobileNavigator) {
				crearFondoArbol()
			}
		}
	}
	jsonContent = jsonContent.replace(/title:/g, "'title':");
	jsonContent = jsonContent.replace(/claseIcono:/g, "'claseIcono':");
	jsonContent = jsonContent.replace(/clase:/g, "'clase':");
	jsonContent = jsonContent.replace(/id:/g, "'id':");
	jsonContent = jsonContent.replace(/url:/g, "'url':");
	jsonContent = jsonContent.replace(/children/g, "'childs'");
	if (jsonContent == "") {
		content = null
	} else {
		content = jsonContent
	}
	facade.definirCapas(content, !ocultarArbolCapas);
	if (!defined) {
		addSelectLayerIDs();
		defined = true
	}
}
function obtenerVisibilidad(layerId) {
	var contents = eval("(" + content + ")");
	if (layerId.indexOf("_") > 0) {
		var ids = layerId.split("_");
		if (contents.layers[ids[0]].childs[ids[1]].clase == "visible") {
			return "true"
		} else {
			return "false"
		}
	}
	if (contents.layers[layerId].clase == "visible") {
		return "true"
	} else {
		return "false"
	}
}
function maxiMapaVisible() {
	try {
		var node = document.getElementById(mapaMaxiContenedorWebId);
		return (node && (node.style.display == "block"))
	} catch (e) {
		return false
	}
}
function maxiMapaCargado() {
	if (maxiMapaVisible()) {
		var node = document.getElementById(iframeVisorMaxiId);
		return (node && frames[iframeVisorMaxiId].loaded)
	}
	return false
}
function addSelectLayerIDs() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var selector = document.getElementById("selectLayerID");
	var map = parent.frames[mapId].getMap();
	for ( var i = 0, l = map.layers.length; i < l; i++) {
		var layer = map.layers[i];
		if ((layer.CLASS_NAME == "OpenLayers.Layer.IAAA_GenericVector")
				&& (layer.layerId != null)) {
			actualizarVisibilidad(layer.layerId, obtenerVisibilidad(""
					+ layer.layerId), "TRUE", true)
		}
	}
}
function cambiarVisibilidad(item, node) {
	if (item != null && item.clase == "visible") {
		try {
			ga('send', 'event', 'ocultarcapa', item.title[0],
					window.location.pathname);
		} catch (e) {
			;
		}
		item.clase = "oculto";
		if (node.rowNode.children && node.rowNode.children.length
				&& (node.rowNode.children[0].tagName.toLowerCase() == "img")) {
			node.contentNode.children[0].style.filter = 'alpha(opacity=' + (dojoArbolNodoNoSeleccionadoOpacity * 100) + ')';
			node.contentNode.children[0].style.opacity = dojoArbolNodoNoSeleccionadoOpacity;
		} else {
			node.contentNode.children[0].style.filter = 'alpha(opacity=100)';
			node.contentNode.children[0].style.opacity = 1;
		}
		if (item.id == IDCAPATRAFICO) {
			return actualizarVisibilidadTrafico("FALSE")
		} else {
			return actualizarVisibilidad(item.id, "FALSE")
		}
	} else {
		try {
			ga('send', 'event', 'mostrarcapa', item.title[0],
					window.location.pathname);
		} catch (e) {
			;
		}
		item.clase = "visible";
		node.contentNode.children[0].style.filter = 'alpha(opacity=100)';
		node.contentNode.children[0].style.opacity = 1;
		if (item.id == IDCAPATRAFICO) {
			return actualizarVisibilidadTrafico("TRUE")
		} else {
			return actualizarVisibilidad(item.id, "TRUE")
		}
	}
}
function actualizarVisibilidad(layerIndex, visibility) {
	return actualizarVisibilidad(layerIndex, visibility, false)
}
function actualizarVisibilidad(layerIndex, visibility, centrarEngeometrias) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if ((layerIndex == undefined) || (layerIndex == null)) {
		ind = null
	} else {
		ind = layerIndex;
		if ((visibility == null) || (visibility == undefined)) {
			visibility = true
		} else {
			visibility = (visibility.toUpperCase() == "TRUE")
		}
	}
	facade.actualizarVisibilidad(ind, visibility, centrarEngeometrias)
}
function actualizarVisibilidadTrafico(visibility) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if ((visibility == null) || (visibility == undefined)) {
		visibility = true
	} else {
		visibility = (visibility.toUpperCase() == "TRUE")
	}
	facade.actualizarVisibilidadTrafico(visibility)
}
function seleccion2visualizador(calle, x, y, srs) {
	srs = ((srs != undefined) && (srs != null))?srs:"EPSG:23030";
	georreferenciarCalle(calle, x, y, srs);
	if (getFacade && getFacade().arbolVisible && getFacade().arbolVisible()) {
		if (!collapsed) {
			colapsar()
		}
	}
	var nodo = obtenerElementoPorId("direccion");
	if (nodo) {
		nodo.blur()
	}
	focusToMap()
}
function localizarAviso(x, y) {
	centrarMapa(x, y, null, null, 5000, "EPSG:23030")
}
function centrarMapa(xMin, yMin, xMax, yMax, scale, srs) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (xMin == "") {
		xMin = null
	} else {
		xMin = parseFloat(xMin)
	}
	if (yMin == "") {
		yMin = null
	} else {
		yMin = parseFloat(yMin)
	}
	if (xMax == "") {
		xMax = null
	} else {
		xMax = parseFloat(xMax)
	}
	if (yMax == "") {
		yMax = null
	} else {
		yMax = parseFloat(yMax)
	}
	if (srs == "") {
		srs = null
	}
	if (scale == "") {
		scale = null
	} else {
		scale = parseFloat(scale)
	}
	facade.centrarMapa(xMin, yMin, xMax, yMax, srs, scale)
}
function georreferenciarCalle(nombreCalle, x, y, crs) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	facade.georreferenciarCalle(nombreCalle, x, y, crs)
}
function loadFacade() {
	var facade, facadeMaxi;
	if (miniMapMode) {
		if (frames[iframeVisorId]) {
			if (frames[iframeVisorId].getFacade) {
				facade = frames[iframeVisorId].getFacade()
						|| parent.frames[iframeVisorId].facade
			} else {
				facade = frames[iframeVisorId].facade
			}
			if (!frames[iframeVisorId].loaded) {
				facade.definirCapaJSON("situacionElemento", jsonMini);
				frames[iframeVisorId].loaded = true
			}
			if (frames[iframeVisorMaxiId] && !frames[iframeVisorMaxiId].loaded) {
				if (frames[iframeVisorMaxiId].getFacade) {
					facadeMaxi = frames[iframeVisorMaxiId].getFacade()
							|| parent.frames[iframeVisorMaxiId].facade
				} else {
					facadeMaxi = frames[iframeVisorMaxiId].facade
				}
				try {
					definirCapas(content)
				} catch (e) {
				}
				facadeMaxi.definirCapaJSON("situacionElemento", jsonMaxi,
						false, maxiMapaTitulo);
				frames[iframeVisorMaxiId].loaded = true;
				if (facade) {
					if ((facade.calleBuscadaNombre != undefined) && (facade.calleBuscadaNombre != null)) {
						facadeMaxi.georreferenciarCalle(facade.calleBuscadaNombre, facade.calleBuscadaX, facade.calleBuscadaY, facade.calleBuscadaCRS);
					}
					var centro = facade.obtenerCentroMapa();
					var escala = facade.obtenerEscalaMapa();
					if (centro) {
						facadeMaxi.centrarMapa(centro.centro.lon,
								centro.centro.lat, null, null, centro.srs,
								escala)
					}
				}
			}
		}
	} else {
		if (colaborativeMode) {
			if (frames[iframeVisorId]) {
				if (frames[iframeVisorId].getFacade) {
					facade = frames[iframeVisorId].getFacade()
							|| parent.frames[iframeVisorId].facade
				} else {
					facade = frames[iframeVisorId].facade
				}
				definirCapas(content);
				var jsonToLoad = null;
				try {
					jsonToLoad = jsonColaborativo
				} catch (e) {
				}
				if (jsonToLoad) {
					facade.definirCapaJSON("userDefined", jsonToLoad, null,
							null, null, true)
				}
				frames[iframeVisorId].loaded = true
			}
		} else {
			definirCapas(content)
		}
	}
}
function deshabilitarBotones() {
	var node = obtenerElementoPorId(botonBorrarPoiId);
	if (node) {
		node.style.display = "none"
	}
	node = obtenerElementoPorId(botonArbolId);
	if (node) {
		node.style.backgroundImage = "url(" + botonArbolDeshabilitadoImg + ")"
	}
	botonesHabilitados = false
}
function habilitarBotones() {
	var node = obtenerElementoPorId(botonBorrarPoiId);
	if (node) {
		node.style.display = "block"
	}
	if (mobileNavigator) {
		node = obtenerElementoPorId(botonArbolId + "_img");
		if (node) {
			node.setAttribute("src", botonArbolCerradoImg)
		}
	} else {
		node = obtenerElementoPorId(botonArbolId);
		if (node) {
			node.style.backgroundImage = "url(" + botonArbolCerradoImg + ")"
		}
	}
	botonesHabilitados = true
}
var oldMapContainerTop = null;
var oldMapContainerLeft = null;
var oldMapContainerHeight = null;
var oldMapContainerWidth = null;
var oldMapContainerPosition = null;
var oldMapContainerZIndex = null;
var oldMapContainerClassName = null;
var oldDocumentOverflow = null;
var oldIframeHeight = null;
var oldIframeWidth = null;
var oldPageScrollPosition = null;
function fullScreen(mostrar) {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var mapContainerId;
	if (!maxiMapaVisible()) {
		mapContainerId = mapaContenedorWebId
	} else {
		mapContainerId = mapaMaxiContenedorWebId
	}
	var mapaDiv = document.getElementById(mapContainerId);
	var homeDiv = document.getElementById(mapId);
	var arbolContenedorAltura;
	if (!collapsed) {
		colapsar()
	}
	if (colaborativeMode) {
		var node = mapaDiv;
		if (mostrar) {
			try {
				document.body.addEventListener("touchstart", preventDefault,
						false);
				document.body.addEventListener("touchmove", preventDefault,
						false)
			} catch (e) {
			}
			if (!collapsed) {
				colapsar()
			}
			if ((window.pageYOffset != undefined)
					&& (window.pageYOffset != null)) {
				oldMapContainerTop = node.style.top;
				oldMapContainerLeft = node.style.left;
				node.style.top = "";
				node.style.left = ""
			}
			try {
				oldPageScrollPosition = window.document.documentElement.scrollTop
			} catch (e) {
			}
			if (!oldPageScrollPosition) {
				try {
					oldPageScrollPosition = window.document.body.scrollTop
				} catch (e) {
				}
			}
			if (mobileNavigator && !appleDevice) {
				window.scrollTo(0, 1)
			} else {
				window.scrollTo(0, 0)
			}
			oldMapContainerHeight = node.style.height;
			oldMapContainerWidth = node.style.width;
			node.style.height = "";
			node.style.width = "";
			oldMapContainerPosition = node.style.position;
			node.style.position = "";
			oldMapContainerZIndex = node.style.zIndex;
			node.style.zIndex = "";
			oldMapContainerClassName = "";
			if (node.className) {
				oldMapContainerClassName = node.className;
				node.className = node.className + " mapaMaximizado"
			} else {
				node.className = "mapaMaximizado"
			}
			if (mobileNavigator) {
				var ifrm = document.getElementById(mapId);
				if (ifrm) {
					oldIframeHeight = ifrm.style.height;
					ifrm.style.height = window.innerHeight + "px";
					oldIframeWidth = ifrm.style.width;
					ifrm.style.width = document.body.clientWidth + "px"
				}
			}
			oldDocumentOverflow = document.body.style.overflow;
			document.body.style.overflow = "hidden";
			if (mobileNavigator && (navigatorName == "firefox")) {
				window.scrollTo(0, 1)
			}
		} else {
			try {
				document.body.removeEventListener("touchstart", preventDefault);
				document.body.removeEventListener("touchmove", preventDefault)
			} catch (e) {
			}
			if (!collapsed) {
				colapsar()
			}
			if (oldMapContainerTop != null) {
				node.style.top = oldMapContainerTop
			}
			if (oldMapContainerLeft != null) {
				node.style.left = oldMapContainerLeft
			}
			if (oldMapContainerHeight != null) {
				node.style.height = oldMapContainerHeight
			}
			if (oldMapContainerWidth != null) {
				node.style.width = oldMapContainerWidth
			}
			if (oldMapContainerPosition != null) {
				node.style.position = oldMapContainerPosition
			}
			if (oldMapContainerZIndex != null) {
				node.style.zIndex = oldMapContainerZIndex
			}
			if (oldMapContainerClassName != null) {
				node.className = oldMapContainerClassName
			}
			if (oldDocumentOverflow != null) {
				document.body.style.overflow = oldDocumentOverflow
			}
			var forceHeightValue = false;
			if (mobileNavigator
					&& ((oldIframeHeight != null) || (oldIframeWidth != null))) {
				var ifrm = document.getElementById(mapId);
				if (ifrm) {
					if (oldIframeHeight != null) {
						ifrm.style.height = oldIframeHeight;
						if ((!ifrm.style.height || (ifrm.style.height == ""))
								&& appleDevice) {
							if (node.style && node.style.height) {
								ifrm.style.height = node.style.height
							} else {
								var mapContainerStyle = window
										.getComputedStyle(node, null);
								ifrm.style.height = mapContainerStyle.height
							}
							forceHeightValue = true
						}
					}
					if (oldIframeWidth != null) {
						ifrm.style.width = oldIframeWidth
					}
				}
			}
			if (oldPageScrollPosition && oldPageScrollPosition) {
				window.scrollTo(0, oldPageScrollPosition)
			}
			if (fullScreenModeState.active
					&& (fullScreenModeState.orientation != null)
					&& (fullScreenModeState.orientation != deviceOrientation)) {
			}
			if (forceHeightValue) {
				var facade;
				if (parent.frames[mapId].getFacade) {
					facade = parent.frames[mapId].getFacade()
							|| parent.frames[mapId].facade
				} else {
					facade = parent.frames[mapId].facade
				}
				if (facade && facade.actualizarDimensionesMapa) {
					facade.actualizarDimensionesMapa()
				}
				if (mobileNavigator
						&& ((oldIframeHeight != null) || (oldIframeWidth != null))) {
					var ifrm = document.getElementById(mapId);
					if (ifrm) {
						ifrm.style.height = oldIframeHeight
					}
				}
			}
			oldMapContainerTop = null;
			oldMapContainerLeft = null;
			oldMapContainerHeight = null;
			oldMapContainerWidth = null;
			oldMapContainerPosition = null;
			oldMapContainerZIndex = null;
			oldMapContainerClassName = null;
			oldDocumentOverflow = null;
			oldIframeHeight = null;
			oldIframeWidth = null;
			oldPageScrollPosition = null
		}
	} else {
		if (mostrar) {
			var scrollX = window.scrollX;
			var scrollY = window.scrollY;
			window.scrollTo(0, 1);
			var height = ((userAgentInfo.indexOf("iphone") == -1) ? ((deviceType == "handheld") ? (window.outerHeight / window.devicePixelRatio)
					: ((deviceType == "tablet") ? parseInt(window.outerHeight)
							: parseInt(window.innerHeight)))
					: parseInt(window.innerHeight))
					+ "px";
			var nodo = obtenerElementoPorId(nodoArbolId);
			if (nodo) {
				oldMarkupHeight = nodo.style.height;
				nodo.style.height = (parseInt(height) - treeMarginTop - treeMarginBottom)
						+ "px"
				nodo.style.top = treeMarginTop;
				var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
				if (nodoAux) {
					nodoAux.style.height = nodo.style.top;
				}
			}
			nodo = obtenerElementoPorId(bordeArbolDivId);
			if (nodo) {
				oldMarkuproundHeight = nodo.style.height;
				nodo.style.height = (parseInt(height) - treeMarginTop - treeMarginBottom)
						+ "px"
			}
			nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
			if (nodo) {
				oldMarkuproundinnerHeight = nodo.style.height;
				nodo.style.height = (parseInt(height) - treeMarginTop
						- treeMarginBottom - 26)
						+ "px"
			}
			if (homeDiv) {
				oldHomeHeight = homeDiv.style.height;
				if (mobileNavigator && (oldHomeHeight == "")) {
					oldHomeHeight = homeDiv.clientHeight
				}
				oldHomeWidth = homeDiv.style.width;
				if (userAgentInfo.indexOf("iphone") != -1) {
					homeDiv.style.height = height
				} else {
					homeDiv.style.height = "100%"
				}
				homeDiv.style.width = "100%"
			}
			if (mapaDiv) {
				oldMapHeight = mapaDiv.style.height;
				if (mobileNavigator && (oldMapHeight == "")) {
					oldMapHeight = mapaDiv.clientHeight
				}
				oldMapWidth = mapaDiv.style.width;
				oldMapMargin = mapaDiv.style.margin;
				mapaDiv.style.height = height;
				mapaDiv.style.width = "100%";
				mapaDiv.style.margin = "0";
				window.scrollTo(0, 80);
				if (miniMapMode || colaborativeMode) {
					window.scrollTo(scrollX, scrollY)
				}
			}
			arbolContenedorAltura = parseInt(height) - 150 - treeMarginTop
					- treeMarginBottom;
			if (arbolContenedorAltura < 0) {
				arbolContenedorAltura = 3
			}
			inicializarAlturasArbol(arbolContenedorAltura)
		} else {
			var scrollX = window.scrollX;
			var scrollY = window.scrollY;
			window.scrollTo(0, 1);
			if (mapaDiv && (oldMapHeight != null)) {
				mapaDiv.style.height = oldMapHeight
			}
			if (mapaDiv && (oldMapWidth != null)) {
				mapaDiv.style.width = oldMapWidth
			}
			if (mapaDiv && (oldMapMargin != null)) {
				mapaDiv.style.margin = oldMapMargin
			}
			if (homeDiv && (oldHomeHeight != null)) {
				homeDiv.style.height = oldHomeHeight
			}
			if (homeDiv && (oldHomeWidth != null)) {
				homeDiv.style.width = oldHomeWidth
			}
			var nodo = obtenerElementoPorId(nodoArbolId);
			if (nodo && (oldMarkupHeight != null)) {
				nodo.style.height = oldMarkupHeight
			}
			nodo = obtenerElementoPorId(bordeArbolDivId);
			if (nodo && (oldMarkuproundHeight != null)) {
				nodo.style.height = oldMarkuproundHeight
			}
			nodo = obtenerElementoPorId(bordeArbolRellenoDivId);
			if (nodo && (oldMarkuproundinnerHeight != null)) {
				nodo.style.height = oldMarkuproundinnerHeight
			}
			if (fullScreenModeState.active
					&& (fullScreenModeState.orientation != null)
					&& (fullScreenModeState.orientation != deviceOrientation)) {
				resizeElements(true)
			} else {
				arbolContenedorAltura = parseInt(oldMarkupHeight) - 150;
				if (arbolContenedorAltura < 0) {
					arbolContenedorAltura = 3
				}
				inicializarAlturasArbol(arbolContenedorAltura)
			}
			if (mobileNavigator) {
				resizeElements(true, oldMapHeight)
			}
			oldHomeHeight = null;
			oldHomeWidth = null;
			oldMapHeight = null;
			oldMapWidth = null;
			oldMapMargin = null;
			oldMarkupHeight = null;
			oldExpandedPopupDivHeight = null;
			oldBackgroundTreeDivHeight = null;
			oldBotcolapseHeight = null;
			oldMarkuproundHeight = null;
			oldMarkuproundinnerHeight = null;
			oldBotcolapseTop = null;
			if (!miniMapMode && !colaborativeMode) {
				window.scrollTo(0, 1)
			} else {
				window.scrollTo(scrollX, scrollY)
			}
		}
	}
	window.focus(mapaDiv);
	fullScreenModeState = {
		active : mostrar,
		orientation : deviceOrientation
	};
	return true
}
function preventDefault(e) {
	var node = e.srcElement || e.target;
	if ((node.id == "maxiMapaBackground")
			|| (node.id == "maxiMapaLoadingBackground")) {
		if (e && e.preventDefault) {
			e.preventDefault()
		}
		e.returnValue = false
	}
}
function maximizeMap(maximizar, mantenerFondo) {
	var node = document.getElementById(mapaMaxiContenedorWebId);
	if (node) {
		var bckgNode;
		if (maximizar || mantenerFondo) {
			bckgNode = document.getElementById("maxiMapaBackground");
			if (bckgNode) {
				if (navigator.appName == "Microsoft Internet Explorer") {
					bckgNode.style.display = "block";
					bckgNode.style.visibility = "visible";
					bckgNode.style.height = document.body.clientHeight + "px"
				} else {
					bckgNode.style.display = "block";
					bckgNode.style.visibility = "visible"
				}
			}
			bckgNode = document.getElementById("maxiMapaLoadingBackground");
			if (bckgNode) {
				if (navigator.appName == "Microsoft Internet Explorer") {
					bckgNode.style.display = "block";
					bckgNode.style.visibility = "visible";
					bckgNode.style.height = document.body.clientHeight + "px"
				} else {
					bckgNode.style.display = "block";
					bckgNode.style.visibility = "visible";
					bckgNode.style.height = window.innerHeight + "px";
					bckgNode.style.width = document.body.clientWidth + "px"
				}
			}
		} else {
			bckgNode = document.getElementById("maxiMapaBackground");
			if (bckgNode) {
				bckgNode.style.display = "none";
				bckgNode.style.visibility = "hidden"
			}
			bckgNode = document.getElementById("maxiMapaLoadingBackground");
			if (bckgNode) {
				bckgNode.style.display = "none";
				bckgNode.style.visibility = "hidden"
			}
		}
		if (maximizar) {
			try {
				document.body.addEventListener("touchstart", preventDefault,
						false);
				document.body.addEventListener("touchmove", preventDefault,
						false)
			} catch (e) {
			}
			if ((window.pageYOffset != undefined)
					&& (window.pageYOffset != null)) {
				node.style.top = "0px"
			}
			node.style.backgroundColor = "white";
			node.style.display = "block";
			node.style.visibility = "visible";
			if ((navigator.appName == "Microsoft Internet Explorer")
					|| mobileNavigator) {
				try {
					scrollPosition = window.document.documentElement.scrollTop
				} catch (e) {
				}
				if (!scrollPosition) {
					try {
						scrollPosition = window.document.body.scrollTop
					} catch (e) {
					}
				}
				if (mobileNavigator && !appleDevice) {
					window.scrollTo(0, 1)
				} else {
					window.scrollTo(0, 0)
				}
			}
			if (mobileNavigator) {
				var ifrm = document.getElementById(iframeVisorMaxiId);
				if (ifrm) {
					if (appleDevice) {
						ifrm.style.height = ((deviceOrientation == "landscape") ? screen.availWidth
								: screen.availHeight)
								+ "px"
					} else {
						ifrm.style.height = window.innerHeight + "px"
					}
					ifrm.style.width = document.body.clientWidth + "px"
				}
			}
			if (!document.getElementById(mapaMaxiIframeContenedorId)) {
				var ifrmContainer = document.createElement("div");
				ifrmContainer.setAttribute("id", mapaMaxiIframeContenedorId);
				var ifrm = document.createElement("iframe");
				ifrm.setAttribute("name", iframeVisorMaxiId);
				ifrm.setAttribute("id", iframeVisorMaxiId);
				ifrm.setAttribute("title", "La Ciudad al Instante");
				ifrm.setAttribute("scrolling", "no");
				if (mobileNavigator) {
					if (appleDevice) {
						ifrm.style.height = ((deviceOrientation == "landscape") ? screen.availWidth
								: screen.availHeight)
								+ "px"
					} else {
						ifrm.style.height = window.innerHeight + "px"
					}
					ifrm.style.width = document.body.clientWidth + "px"
				}
				ifrm.setAttribute("src", homeMaxiViewerIP
						+ "/index.html?SIZE=MAXI");
				ifrm.setAttribute("frameBorder", "0");
				ifrm.setAttribute("ALLOWTRANSPARENCY", "true");
				ifrmContainer.appendChild(ifrm);
				node.appendChild(ifrmContainer);
				node.style.backgroundColor = ""
			} else {
				node.style.backgroundColor = "";
				var ifrmContainer = document
						.getElementById(mapaMaxiIframeContenedorId);
				var centro, escala, facadeMini, facadeMaxi;
				if (parent.frames[iframeVisorMaxiId].getFacade) {
					facadeMaxi = parent.frames[iframeVisorMaxiId].getFacade()
							|| parent.frames[iframeVisorMaxiId].facade
				} else {
					facadeMaxi = parent.frames[iframeVisorMaxiId].facade
				}
				if (parent.frames[iframeVisorId].getFacade) {
					facadeMini = parent.frames[iframeVisorId].getFacade()
							|| parent.frames[iframeVisorId].facade
				} else {
					facadeMini = parent.frames[iframeVisorId].facade
				}
				if (facadeMini && facadeMini.obtenerCentroMapa) {
					centro = facadeMini.obtenerCentroMapa()
				}
				if (facadeMini && facadeMini.obtenerEscalaMapa) {
					escala = facadeMini.obtenerEscalaMapa()
				}
				if (facadeMini && ((facadeMini.calleBuscadaNombre != undefined) && (facadeMini.calleBuscadaNombre != null)) && facadeMaxi) {
					facadeMaxi.georreferenciarCalle(facadeMini.calleBuscadaNombre, facadeMini.calleBuscadaX, facadeMini.calleBuscadaY, facadeMini.calleBuscadaCRS);
				}
				if (facadeMaxi && facadeMaxi.centrarMapa && centro) {
					facadeMaxi.actualizarDimensionesMapa();
					facadeMaxi.centrarMapa(centro.centro.lon,
							centro.centro.lat, null, null, centro.srs, escala)
				}
			}
			document.body.style.overflow = "hidden";
			node.scrollIntoView();
		} else {
			try {
				document.body.removeEventListener("touchstart", preventDefault);
				document.body.removeEventListener("touchmove", preventDefault)
			} catch (e) {
			}
			if (!collapsed) {
				colapsar()
			}
			node.style.display = "none";
			node.style.visibility = "hidden";
			document.body.style.overflow = "";
			if ((navigator.appName == "Microsoft Internet Explorer")
					|| mobileNavigator) {
				window.scrollTo(0, scrollPosition)
			}
			var centro, escala, facadeMini, facadeMaxi;
			if (parent.frames[iframeVisorMaxiId].getFacade) {
				facadeMaxi = parent.frames[iframeVisorMaxiId].getFacade()
						|| parent.frames[iframeVisorMaxiId].facade
			} else {
				facadeMaxi = parent.frames[iframeVisorMaxiId].facade
			}
			if (facadeMaxi && facadeMaxi.obtenerCentroMapa) {
				centro = facadeMaxi.obtenerCentroMapa()
			}
			if (facadeMaxi && facadeMaxi.obtenerEscalaMapa) {
				escala = facadeMaxi.obtenerEscalaMapa()
			}
			if (parent.frames[iframeVisorId].getFacade) {
				facadeMini = parent.frames[iframeVisorId].getFacade()
						|| parent.frames[iframeVisorId].facade
			} else {
				facadeMini = parent.frames[iframeVisorId].facade
			}
			if (facadeMaxi && ((facadeMaxi.calleBuscadaNombre != undefined) && (facadeMaxi.calleBuscadaNombre != null)) && facadeMini) {
				facadeMini.georreferenciarCalle(facadeMaxi.calleBuscadaNombre, facadeMaxi.calleBuscadaX, facadeMaxi.calleBuscadaY, facadeMaxi.calleBuscadaCRS);
			}
			if (facadeMini && facadeMini.centrarMapa && centro) {
				facadeMini.actualizarDimensionesMapa();
				facadeMini.centrarMapa(centro.centro.lon, centro.centro.lat,
						null, null, centro.srs, escala)
			}
		}
	}
	return true
}
function getElementYPosition(id) {
	var currentNode = document.getElementById(id);
	if (currentNode) {
		var node = currentNode.parentNode;
		var offset = 0;
		if (node) {
			while (node.tagName.toLowerCase() != "html") {
				for ( var i = 0, iLen = node.children.length; i < iLen; i++) {
					if (node.children[i] != currentNode) {
						if (node.children[i].clientHeight
								&& (node.children[i].id != "menu")) {
							offset += parseInt(node.children[i].clientHeight)
						}
					} else {
						break
					}
				}
				currentNode = node;
				node = currentNode.parentNode
			}
			return offset + 5
		}
	}
	return null
}
function cerrarPopupExpandido() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (facade && facade.closeExpandedPopup) {
		facade.closeExpandedPopup()
	}
	return false
}
function cargarArbolRetardado() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	if (frames[mapId] && frames[mapId].cargaArbol) {
		var treeMargins = {
			top : treeMarginTop,
			bottom : treeMarginBottom
		};
		frames[mapId].cargaArbol(treeMargins, calcularAltura(),
				nodoPadreArbolId, nodoArbolId, bordeArbolDivId,
				bordeArbolRellenoDivId, bordeArbolEsquinaSupDivId,
				bordeArbolEsquinaInfDivId, false);
	} else {
		setTimeout("cargarArbolRetardado()", 250)
	}
}
function calculateCurrentRoute() {
	if ((currentRouteOrigin == null) || (currentRouteDestination == null)) {
		return;
	}
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (facade && facade.borrarRutas) {
		facade.borrarRutas();
	}
	if (facade && facade.calcularRuta) {
		var origenX = currentRouteOrigin[0];
		var origenY = currentRouteOrigin[1];
		var destinoX = currentRouteDestination[0];
		var destinoY = currentRouteDestination[1];
		var transit_modes = [];
		var tte_bus_check = parent.frames[mapId].document.getElementById("tte_bus");
		if (tte_bus_check && tte_bus_check.checked) {
			transit_modes.push("bus");
		}
		var tte_tram_check = parent.frames[mapId].document.getElementById("tte_tram");
		if (tte_tram_check && tte_tram_check.checked) {
			transit_modes.push("tram");
		}
		var tte_rural_bus_check = parent.frames[mapId].document.getElementById("tte_rural_bus");
		if (tte_rural_bus_check && tte_rural_bus_check.checked) {
			transit_modes.push("rural_bus");
		}
		var opcionesRuta = {
			transit_modes: transit_modes,
			originName: currentRouteOrigin[2]
		};
		facade.calcularRuta(origenX, origenY, destinoX, destinoY, opcionesRuta);
	}
	colapsar();
}
function getFacade() {
	return {
		visualizadorCargado : function(value) {
			if (value) {
				loadFacade()
			}
			if (typeof(loadData) == "function") {
				loadData();
			}
		},
		deshabilitaBotones : function() {
			deshabilitarBotones()
		},
		habilitaBotones : function() {
			habilitarBotones()
		},
		arbolVisible : function() {
			var node = obtenerElementoPorId(nodoArbolId);
			return (node && (node.style.width != "0") && (node.style.width != "0px"))
		},
		arbolCargado : function() {
			var node = obtenerElementoPorId(nodoArbolId);
			return (node)
		},
		modoPantallaCompleta : function(mostrar) {
			return fullScreen(mostrar)
		},
		cargarArbol : function() {
			var mapId;
			if (!maxiMapaVisible()) {
				mapId = iframeVisorId
			} else {
				mapId = iframeVisorMaxiId
			}
			var facade;
			if (frames[mapId].getFacade) {
				facade = frames[mapId].getFacade()
						|| parent.frames[mapId].facade
			} else {
				facade = frames[mapId].facade
			}
			if (facade && facade.cargarScript) {
				facade.cargarScript("script", scriptGeocoderServlet);
				facade.cargarScript("script", scriptGeocoderEngine);
				facade.cargarScript("script", scriptGeocoderGUI);
				facade.cargarScript("script", scriptDojoLib);
				facade.cargarScript("css", estiloTreeConfig);
				facade.cargarScript("script", scriptTreeFunctions);
				facade.cargarScript("script", scriptTreeConfig);
				cargarArbolRetardado()
			}
		},
		maximizaMapa : function(maximizar, mantenerFondo) {
			return maximizeMap(maximizar, mantenerFondo)
		},
		redimensionarArbol : function() {
			try {
				var altura = calcularAltura();
				var arbolContenedorAltura = (altura - treeMarginTop - treeMarginBottom) - 150;
				if (arbolContenedorAltura < 0) {
					arbolContenedorAltura = 3
				}
				inicializarAlturasArbol(arbolContenedorAltura)
			} catch (e) {
			}
		},
		obtenerContenedorMapa : function(modo) {
			var nodoId;
			if ((modo == "MAXI") && miniMapMode) {
				nodoId = iframeVisorMaxiId
			} else {
				if (colaborativeMode && appleDevice) {
					if (updatedOnce) {
						nodoId = iframeVisorId
					} else {
						nodoId = mapaContenedorWebId
					}
				} else {
					nodoId = mapaContenedorWebId
				}
			}
			return document.getElementById(nodoId)
		},
		guardarInformacionMapaColaborativo : function() {
			guardarInformacionColaborativa()
		},
		obtenerInformacionMapaColaborativo : function() {
			var mapId = iframeVisorId;
			var facade;
			if (frames[mapId].getFacade) {
				facade = frames[mapId].getFacade()
						|| parent.frames[mapId].facade
			} else {
				facade = frames[mapId].facade
			}
			if (facade && facade.obtenerInformacionUsuario) {
				return facade.obtenerInformacionUsuario()
			}
			return null
		},
		obtenerInformacionTextualMapaColaborativo : function() {
			var mapId = iframeVisorId;
			var facade;
			if (frames[mapId].getFacade) {
				facade = frames[mapId].getFacade()
						|| parent.frames[mapId].facade
			} else {
				facade = frames[mapId].facade
			}
			if (facade && facade.obtenerInformacionUsuarioTexto) {
				return facade.obtenerInformacionUsuarioTexto()
			}
			return ""
		},
		comoLlegar : function(x, y, nombre) {
			// Guardar atributos del destino de la ruta
			currentRouteDestination = [x, y, nombre];
			// Mostrar panel de "C�mo llegar"
			var mapId = iframeVisorId;
			if (frames[mapId].showRoutesPanel) {
				frames[mapId].showRoutesPanel();
			}
			if (frames[mapId].openRoutesPanel) {
				frames[mapId].openRoutesPanel();
			}
			// Abrir �rbol
			if (collapsed == true) {
				colapsar();
			} else {
				actualizarAnchuraArbol();
			}
			// Focalizar en geocoder y limpiar b�squeda previa
			var input = frames[mapId].document.getElementById("rutas_direccion");
			if (input) {
				input.focus();
				input.value = "";
			}
			var select = frames[mapId].document.getElementById("rutas_resultados");
			if (select) {
				select.innerHTML = "";
			}
			currentRouteOrigin = null;
			geocoderRouteOrigin = null;
		},
		pintarGeoJSON : function(geojson, centrarMapa) {
			var mapId = iframeVisorId;
			var facade;
			if (frames[mapId].getFacade) {
				facade = frames[mapId].getFacade()
						|| parent.frames[mapId].facade
			} else {
				facade = frames[mapId].facade
			}
			if (facade && facade.pintarGeoJSON) {
				facade.pintarGeoJSON(geojson, centrarMapa);
				return true;
			}
			return false;
		}
	}
}
var arbolCargado = false;
function getArbolCargado() {
	return arbolCargado
}
function setArbolCargado(value) {
	arbolCargado = value;
	var node;
	if (mobileNavigator) {
		node = obtenerElementoPorId(botonArbolId + "_img");
		if (node) {
			node.setAttribute("src", botonArbolCerradoImg)
		}
	} else {
		node = obtenerElementoPorId(botonArbolId);
		if (node) {
			node.style.backgroundImage = "url(" + botonArbolCerradoImg + ")"
		}
	}
}
function colapsar() {
	var mapId;
	var mapContainerId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId;
		mapContainerId = mapaContenedorWebId
	} else {
		mapId = iframeVisorMaxiId;
		mapContainerId = mapaMaxiContenedorWebId
	}
	var node;
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (!getArbolCargado()) {
		return
	}
	if (botonesHabilitados) {
		if (collapsed == true) {
			if (navigator.appName.toLowerCase().indexOf("opera") != -1) {
				javascript: recalcularAlturasArbol()
			}
			node = obtenerElementoPorId(nodoPadreArbolId);
			if (node) {
				node.style.display = "block";
				node.style.visibility = "visible"
			}
			collapsed = false;
			if (parseInt(screen.width) > 480) {
				node = obtenerElementoPorId(nodoPadreArbolId);
				if (node) {
					node.style.width = "24%";
					if (parseInt(node.clientWidth) < 300) {
						node.style.width = "300px"
					}
					var node2 = obtenerElementoPorId(nodoArbolId);
					if (node2) {
						node2.style.display = "block";
						node2.style.visibility = "visible";
						node2.style.width = (parseInt(node.clientWidth) - 13)
								+ "px";
						var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
						if (nodoAux) {
							nodoAux.style.width = node2.style.width;
							if (mobileNavigator && nodoAux.firstChild) {
								nodoAux.firstChild.style.width = node2.style.width;
							}
						}
						nodoAux = obtenerElementoPorId(mapaToolbarId);
						if (!mobileNavigator && nodoAux) {
							nodoAux.style.display = "block";
						}
					}
				}
				node = obtenerElementoPorId(nodoArbolId);
				node2 = obtenerElementoPorId(mapaVisorId);
				if (node && node2) {
					var buttonSize;
					if (deviceType == "handheld") {
						buttonSize = 24
					} else {
						if (deviceType == "tablet") {
							buttonSize = 30
						} else {
							buttonSize = 20
						}
					}
					var desplazarVertical = ((parseInt(node2.clientWidth) < 546) && !((deviceType == "handheld") && (navigator.appName == "Opera")));
					moverBarraHerramientasExterna(
							((desplazarVertical) ? (16 + buttonSize + 5) : null),
							null, null,
							(parseInt(node.clientWidth) + buttonSize))
				}
			} else {
				node = obtenerElementoPorId(nodoPadreArbolId);
				if (node) {
					node.style.width = "75%";
					var node2 = obtenerElementoPorId(nodoArbolId);
					if (node2) {
						node2.style.display = "block";
						node2.style.visibility = "visible";
						node2.style.width = (parseInt(node.clientWidth) - 13)
								+ "px";
						var nodoAux = obtenerElementoPorId(mapaArbolCabeceraId);
						if (nodoAux) {
							nodoAux.style.width = node2.style.width;
							if (mobileNavigator && nodoAux.firstChild) {
								nodoAux.firstChild.style.width = node2.style.width;
							}
						}
						nodoAux = obtenerElementoPorId(mapaToolbarId);
						if (!mobileNavigator && nodoAux) {
							nodoAux.style.display = "block";
						}
					}
				}
				node = obtenerElementoPorId(fondoArbolDivId);
				if (node) {
					node.style.display = "block";
					node.style.visibility = "visible"
				}
				moverBarraHerramientasExterna(null, null, null, 25)
			}
			if (mobileNavigator) {
				if (miniMapMode || colaborativeMode) {
					node = obtenerElementoPorId(botonArbolId + "_img");
					if (node) {
						node.setAttribute("src", botonArbolAbiertoImg)
					}
				} else {
					node = obtenerElementoPorId(botonArbolId);
					if (node) {
						node.style.backgroundImage = "url("
								+ botonArbolAbiertoImg + ")"
					}
				}
			} else {
				node = obtenerElementoPorId(botonArbolId);
				if (node) {
					node.style.backgroundImage = "url(" + botonArbolAbiertoImg
							+ ")";
				}
			}
			node = obtenerElementoPorId(bordeArbolDivId);
			if (node) {
				node.style.display = "block";
				node.style.visibility = "visible";
				node.style.right = (parseInt(obtenerElementoPorId(nodoArbolId).clientWidth))
							+ "px"
			}
			node = document.getElementById(mapContainerId);
			if (node) {
				window.focus(node)
			}
			actualizarAnchuraArbol();
			if (mobileNavigator && (miniMapMode || colaborativeMode)) {
				getFacade().redimensionarArbol()
			}
		} else {
			collapsed = true;
			restaurarBarraHerramientasExterna();
			var node = obtenerElementoPorId(fondoArbolDivId);
			if (node) {
				node.style.display = "none";
				node.style.visibility = "hidden"
			}
			node = obtenerElementoPorId(nodoPadreArbolId);
			if (node) {
				node.style.width = "0px"
			}
			node = obtenerElementoPorId(nodoArbolId);
			if (node) {
				node.style.width = "0px";
				node.style.display = "none";
				node.style.visibility = "hidden"
			}
			if (mobileNavigator) {
				if (miniMapMode || colaborativeMode) {
					node = obtenerElementoPorId(botonArbolId + "_img");
					if (node) {
						node.setAttribute("src", botonArbolCerradoImg)
					}
				} else {
					node = obtenerElementoPorId(botonArbolId);
					if (node) {
						node.style.backgroundImage = "url("
								+ botonArbolCerradoImg + ")"
					}
				}
			} else {
				node = obtenerElementoPorId(botonArbolId);
				if (node) {
					node.style.backgroundImage = "url(" + botonArbolCerradoImg
							+ ")";
				}
			}
			node = obtenerElementoPorId(bordeArbolDivId);
			if (node) {
				node.style.display = "none";
				node.style.visibility = "hidden"
			}
			node = obtenerElementoPorId(nodoPadreArbolId);
			if (node) {
				node.style.display = "none";
				node.style.visibility = "hidden"
			}
			try {
				collapseSelectedTree()
			} catch (e) {
				if (frames[mapId] && frames[mapId].collapseSelectedTree) {
					frames[mapId].collapseSelectedTree()
				}
			}
		}
		node = obtenerElementoPorId(botonArbolId);
		if (node && node.children && node.children[0]) {
			node.children[0].blur()
		}
	}
}
function restaurarBarraHerramientasExterna() {
	var node = obtenerElementoPorId(externalToolBarContainerId);
	if (node) {
		if (externalToolBarOriginalPosition) {
			if (externalToolBarOriginalPosition.top) {
				node.style.top = externalToolBarOriginalPosition.top
			} else {
				node.style.top = ""
			}
			if (externalToolBarOriginalPosition.left) {
				node.style.left = externalToolBarOriginalPosition.left
			} else {
				node.style.left = ""
			}
			if (externalToolBarOriginalPosition.bottom) {
				node.style.bottom = externalToolBarOriginalPosition.bottom
			} else {
				node.style.bottom = ""
			}
			if (externalToolBarOriginalPosition.right) {
				node.style.right = externalToolBarOriginalPosition.right
			} else {
				node.style.right = ""
			}
		}
		externalToolBarOriginalPosition = null
	}
}
function moverBarraHerramientasExterna(top, left, bottom, right) {
	var node = obtenerElementoPorId(externalToolBarContainerId);
	if (node) {
		if (externalToolBarOriginalPosition == null) {
			externalToolBarOriginalPosition = {};
			externalToolBarOriginalPosition.top = node.style.top;
			externalToolBarOriginalPosition.left = node.style.left;
			externalToolBarOriginalPosition.bottom = node.style.bottom;
			externalToolBarOriginalPosition.right = node.style.right
		}
		if (top) {
			node.style.top = top + "px"
		}
		if (left) {
			node.style.left = left + "px"
		}
		if (bottom) {
			node.style.bottom = bottom + "px"
		}
		if (right) {
			node.style.right = right + "px"
		}
	}
}
function crearBotonArbol() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	if (!mobileNavigator) {
		var botColapse = document.createElement("div");
		botColapse.setAttribute("id", botonArbolId);
		var enlace = document.createElement("a");
		enlace.setAttribute("id", botonArbolId + "Ref");
		enlace.setAttribute("href", "#");
		if (navigator.appName == "Microsoft Internet Explorer") {
			enlace.onclick = function() {
				try {
					javascript: colapsar()
				} catch (e) {
					javascript: parent.colapsar()
				}
				return false
			}
		} else {
			enlace
					.setAttribute(
							"onClick",
							"try{javascript:colapsar();}catch(e){javascript:parent.colapsar();}return false;")
		}
		if (navigator.appName == "Microsoft Internet Explorer") {
			enlace.style
					.setAttribute("cssText",
							"color:#000000; text-decoration:none; display:block; height:100%; width:100%;")
		} else {
			enlace
					.setAttribute(
							"style",
							"color:#000000; text-decoration:none; display:block; height:100%; width:100%;background-color:100%")
		}
		botColapse.appendChild(enlace);
		if (navigator.appName == "Microsoft Internet Explorer") {
			botColapse.style
					.setAttribute(
							"cssText",
							"background-image:url("
									+ botonArbolCerradoCargandoImg
									+ "); background-repeat:no-repeat;background-position:right center;color:transparent;width:81px;right:0px;top:" + ((mapaArbolCabeceraId == "" ? 0 : 90)) + "px;position:absolute;z-index:10011;height:81px;")
		} else {
			botColapse
					.setAttribute(
							"style",
							"background-image:url("
									+ botonArbolCerradoCargandoImg
									+ "); background-repeat:no-repeat;background-position:right center;color:transparent;width:81px;right:0px;top:" + ((mapaArbolCabeceraId == "" ? 0 : 90)) + "px;position:absolute;z-index:10011;height:81px;")
		}
		facade.agregarElementoExterno(botColapse)
	} else {
		if (miniMapMode || colaborativeMode) {
			var functionality = null;
			var botColapse = document.createElement("div");
			botColapse.setAttribute("id", botonArbolId);
			botColapseImg = document.createElement("IMG");
			botColapseImg.setAttribute("id", botonArbolId + "_img");
			botColapseImg.setAttribute("src", botonArbolCerradoCargandoImg);
			botColapseImg.setAttribute("align", "middle");
			botColapseImg.setAttribute("border", "0");
			botColapseImg.setAttribute("style", "width:81px;height:81px;right:0px;top:0px;");
			botColapse.appendChild(botColapseImg);
			botColapse.setAttribute("style",
					"color:transparent;height:81px;width:81px;right:0px;top:10px;");
			functionality = {
				elementNode : botColapseImg,
				typeEvent : (esTactil) ? "touchstart" : "click",
				functionOnEvent : function() {
					try {
						javascript: colapsar()
					} catch (e) {
						javascript: parent.colapsar()
					}
					return false
				}
			};
			var botColapseTable = document.createElement("table");
			botColapseTable.setAttribute("id", botonArbolId + "_table");
			var botColapseTableBody = document.createElement("tbody");
			botColapseTableBody.setAttribute("id", botonArbolId + "_tableBody");
			var botColapseTableTR = document.createElement("tr");
			botColapseTableTR.setAttribute("id", botonArbolId + "_tableTR");
			var botColapseTableTD = document.createElement("td");
			botColapseTableTD.setAttribute("id", botonArbolId + "_tableTD");
			botColapseTableTD
					.setAttribute("style",
							"height:100%; width:100%; padding:0px; border:0; margin:0; text-align:center;");
			botColapseTableTD.setAttribute("valign", "middle");
			botColapseTableTD.appendChild(botColapse);
			botColapseTableTR.setAttribute("style",
					"height:100%; width:100%; padding:0px; border:0; margin:0");
			botColapseTableTR.appendChild(botColapseTableTD);
			botColapseTableBody.setAttribute("style",
					"height:100%; width:100%; padding:0px; border:0; margin:0");
			botColapseTableBody.appendChild(botColapseTableTR);
			botColapseTable
					.setAttribute(
							"style",
							"height:81px; width:81px; padding:0px; margin:0; position:absolute; right:0px; top:10px; z-index:10011;");
			botColapseTable.setAttribute("border", "0");
			botColapseTable.setAttribute("cellspacing", "0");
			botColapseTable.appendChild(botColapseTableBody);
			facade.agregarElementoExterno(botColapseTable, functionality)
		} else {
			var mapId;
			if (!maxiMapaVisible()) {
				mapId = iframeVisorId
			} else {
				mapId = iframeVisorMaxiId
			}
			var facade;
			if (parent.frames[mapId].getFacade) {
				facade = parent.frames[mapId].getFacade()
						|| parent.frames[mapId].facade
			} else {
				facade = parent.frames[mapId].facade
			}
			var botColapse = document.createElement("div");
			botColapse.setAttribute("id", botonArbolId);
			var enlace = document.createElement("a");
			enlace.setAttribute("id", botonArbolId + "Ref");
			enlace.setAttribute("href", "#");
			enlace
					.setAttribute(
							"onClick",
							"try{javascript:colapsar();}catch(e){javascript:parent.colapsar();}return false;");
			enlace
					.setAttribute("style",
							"color:#000000; text-decoration:none; display:block; height:100%; width:100%;");
			botColapse.appendChild(enlace);
			var rightValue = 0;
			if (!mobileNavigator) {
				if (!miniMapMode && !colaborativeMode) {
					if (navigator.appName == "Netscape") {
						rightValue = 5
					} else {
						if (navigator.appName == "Opera") {
							rightValue = 10
						}
					}
				} else {
					rightValue = 5
				}
			}
			botColapse
					.setAttribute(
							"style",
							"background-image:url("
									+ botonArbolCerradoImg
									+ "); background-repeat:no-repeat;background-position:right center;color:transparent;height:81px;width:81px;right:0px;top:10px;position:absolute;z-index:10011;");
			facade.agregarElementoExterno(botColapse)
		}
	}
}
function crearFondoArbol() {
	var mapId;
	if (!maxiMapaVisible()) {
		mapId = iframeVisorId
	} else {
		mapId = iframeVisorMaxiId
	}
	var facade;
	if (parent.frames[mapId].getFacade) {
		facade = parent.frames[mapId].getFacade()
				|| parent.frames[mapId].facade
	} else {
		facade = parent.frames[mapId].facade
	}
	var treeNodeBackground = document.createElement("div");
	treeNodeBackground.setAttribute("id", fondoArbolDivId);
	if (navigator.appName == "Microsoft Internet Explorer") {
		treeNodeBackground.style
				.setAttribute(
						"cssText",
						"display:none; height:100%; overflow:none;width:100%;float:right;position:absolute;z-index:10001;position:absolute;opacity:0.7;background-color: #3b3b39;top:0;")
	} else {
		treeNodeBackground
				.setAttribute(
						"style",
						"display:none; height:100%; overflow:none;width:100%;float:right;position:absolute;z-index:10001;position:absolute;opacity:0.7;background-color: #3b3b39;top:0;")
	}
	treeNodeBackground.setAttribute("onClick", "return false;");
	facade.agregarElementoExterno(treeNodeBackground)
}
function onScrollPage() {
	if (maxiMapaCargado() && !collapsed) {
		window.scrollTo(0, ((appleDevice) ? 0 : 1))
	}
}
function setMaxiMapaTitle(title) {
	maxiMapaTitulo = title
}
function setMiniMapMode(value) {
	miniMapMode = value
}
function setColaborativeMode(value) {
	colaborativeMode = value
}
function initializeInterface() {
	miniMapMode = ((miniMapMode == null) ? (document
			.getElementById(mapaMaxiContenedorWebId) != null) : miniMapMode);
	if (miniMapMode || colaborativeMode) {
		if ((orientationEvent == "orientationchange")
				&& (navigatorName == "android") && (androidVersion > 3)
				&& (deviceType == "handheld")) {
			window
					.addEventListener(
							orientationEvent,
							function() {
								try {
									var metaList = document
											.getElementsByTagName("meta");
									var found = false;
									for ( var i in metaList) {
										if (metaList[i].name == "viewport") {
											if (metaList[i].parentNode) {
												metaList[i].parentNode
														.removeChild(metaList[i]);
												found = true;
												break
											}
										}
									}
									if (found) {
										if (document
												.getElementsByTagName("head")) {
											var node = document
													.createElement("meta");
											node.setAttribute("name",
													"viewport");
											node
													.setAttribute("content",
															"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0");
											document
													.getElementsByTagName("head")[0]
													.appendChild(node)
										}
									}
								} catch (e) {
								}
							}, false)
		}
	}
	//treeMarginTop = (!miniMapMode && !colaborativeMode) ? 5 : 5;
	treeMarginBottom = (!miniMapMode && !(colaborativeMode)) ? 5 : 30;
	if (obtenerElementoPorId("contint")) {
		obtenerElementoPorId("contint").style.marginTop = "0"
	}
	// Paginas NO HTML5
	if(!document.doctype === null){
		carga();
	}
	var ocultarArbol;
	try {
		ocultarArbol = ocultarArbolCapas
	} catch (e) {
		ocultarArbol = false
	}
	if (!ocultarArbol) {
		if (obtenerElementoPorId("q1")) {
			obtenerElementoPorId("q1").setAttribute("onFocus",
					'javascript:this.value=""')
		}
		ocultarAvisosGeo()
	} else {
		calcularAltura()
	}
}
function guardarInformacionColaborativa() {
	var formularioGuardarInformacion = document
			.getElementById("form_guardar_informacion");
	if (formularioGuardarInformacion) {
		if (colaborativeMode) {
			var node = document.getElementById("jsoncontent");
			if (node) {
				node.value = getFacade()
						.obtenerInformacionTextualMapaColaborativo()
			}
		}
		formularioGuardarInformacion.submit()
	}
}
function setMapaVisorId(value) {
	mapaVisorId = value
}
function setMapaContenedorWebId(value) {
	mapaContenedorWebId = value
}
function setMapaMaxiContenedorWebId(value) {
	mapaMaxiContenedorWebId = value
}
function setMapaMaxiIframeContenedorId(value) {
	mapaMaxiIframeContenedorId = value
}
function setIframeVisorId(value) {
	iframeVisorId = value
}
function setIframeVisorMaxiId(value) {
	iframeVisorMaxiId = value
}
function setHomeIP(value) {
	homeIP = value;
	borrarPoiImg20x20 = homeIP + "/img/borrarGeometrias_20x20.png";
	borrarPoiImg24x24 = homeIP + "/img/borrarGeometrias_24x24.png";
	borrarPoiImg30x30 = homeIP + "/img/borrarGeometrias_30x30.png";
	botonArbolDeshabilitadoImg = homeIP + "/img/new2015/botonArbolCapas.png";
	botonArbolCerradoImg = homeIP + "/img/new2015/botonArbolCapas.png";
	botonArbolCerradoCargandoImg = homeIP + "/img/new2015/botonArbolCapas.png";
	botonArbolAbiertoImg = homeIP + "/img/new2015/botonArbolCapas.png";
	scriptDojoLib = homeIP + dojoFilePath;
	estiloTreeConfig = homeIP + treeConfigStyleFilePath;
	scriptTreeFunctions = homeIP + treeFunctionsFilePath;
	scriptTreeConfig = homeIP + treeConfigFilePath
}
function setHomeMaxiViewerIP(value) {
	homeMaxiViewerIP = value
};
