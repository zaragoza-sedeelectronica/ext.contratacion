/*
Aviso de licencia
-----------------
El código fuente contenido en este fichero es propiedad
intelectual de la empresa GeoSpatiumLab S. L. (GeosLab)
(http://geoslab.com/).


Este código fuente se proporciona únicamente como
mecanismo para posibilitar la ejecución de la aplicación
que lo contiene, por razones técnicas, y se licencia
para ese uso exclusivamente. El hecho de estar leyendo
este aviso de licencia ya implica, por tanto, un acceso no
autorizado a este código.


Los dueños de la propiedad intelectual de este código
fuente prohiben expresamente cualquier uso del
mismo distinto al enunciado en el párrafo anterior,
incluyendo su lectura, análisis, copia a cualquier formato,
incluyendo papel, transmisión por cualquier medio, modificación
y su utilización (tal cual o tras modificarlo) en aplicaciones
distintas a aquella para la que el código ha sido licenciado.
*/
(function() {
	/*
	 * Leaflet.BaseLayerSelector
	 *
	 * Layers control with map configuration change according to the base layer selected.
	 *
	 */
	L.Control.BaseLayerSelector = L.Control.Layers.extend({

		initialize: function (baseLayers, overlays, options) {
			L.setOptions(this, options);

			this._layers = {};
			this._lastZIndex = 0;
			this._handlingClick = false;

			this._baseLayers = baseLayers;
			this._overlayLayers = overlays;

			for (var i in baseLayers) {
				baseLayers[i].baseLayerName = i;
				this._addLayer(baseLayers[i], i);
			}

			for (i in overlays) {
				overlays[i].overlayLayerName = i;
				this._addLayer(overlays[i], i, true);
			}
		},

		// Cambiar la configuracion del mapa en funcion de la capa base a visualizar
		changeMapProperties: function(e) {
			if (e) {
				var layer = e.layer;
				if (layer) {
					var map = layer._map;
					if (map) {
						var center = map.getCenter();
						var layerCrs = layer._crs;
						if (!layerCrs) {
							layerCrs = L.CRS.EPSG3857;
						}
						map.options.crs = layerCrs;

						var mapMinZoom = 0;
						if (map.options && 
							(map.options.minZoom != undefined) && 
							(map.options.minZoom != null)) {
							mapMinZoom = map.options.minZoom;
						}
						var mapMaxZoom = 20;
						if (map.options && 
							(map.options.maxZoom != undefined) && 
							(map.options.maxZoom != null)) {
							mapMaxZoom = map.options.maxZoom;
						}

						var layerMinZoom = 0;
						if (layer.options && 
							(layer.options.minZoom != undefined) && 
							(layer.options.minZoom != null)) {
							layerMinZoom = layer.options.minZoom;
						}
						var layerMaxZoom = 20;
						if (layer.options && 
							(layer.options.maxZoom != undefined) && 
							(layer.options.maxZoom != null)) {
							layerMaxZoom = layer.options.maxZoom;
						}

						if ((mapMinZoom > layerMinZoom) && (mapMaxZoom > layerMaxZoom)) {
							//Ajustar el nivel de zoom actual
							map._zoom -= map.options.minZoom;
						}

						map.options.minZoom = layerMinZoom;
						map.options.maxZoom = layerMaxZoom;

						if ((mapMinZoom < layerMinZoom) && (mapMaxZoom < layerMaxZoom)) {
							//Ajustar el nivel de zoom actual
							map._zoom += map.options.minZoom;
						}

						if (map._zoom < layerMinZoom) {
							map._zoom = layerMinZoom;
						} else if (map._zoom > layerMaxZoom) {
							map._zoom = layerMaxZoom;
						}
						
						var overlayLayer;
						for (var i in this._overlayLayers) {
							overlayLayer = this._overlayLayers[i];
							//Cambiar el crs de la capa del catastro para que se ajuste correctamente a 
							//la capa base
							if (overlayLayer) {
								overlayLayer._crs = layerCrs;
								overlayLayer.options.crs = layerCrs;
								overlayLayer.setParams({srs: layerCrs.code}, false);								
							}
						}
						
						var layerCluster;
						for (var i in map._layers) {
							layerCluster = map._layers[i];
							if (L.FixedMarkerClusterGroup && layerCluster instanceof L.FixedMarkerClusterGroup) {
								//Resetear clusterizado y asignar el nivel de zoom correcto
								layerCluster._zoom = map._zoom;
								layerCluster.resetCluster();
							} else if (L.MarkerClusterGroup && layerCluster instanceof L.MarkerClusterGroup) {
								//Resetear clusterizado y asignar el nivel de zoom correcto
								var features = layerCluster.getLayers();
								layerCluster._zoom = map._zoom;
								layerCluster.clearLayers();
								layerCluster.addLayers(features);
								layerCluster._mergeSplitClusters();
							}
						}

						//Recargar el mapa
						map._resetView(center, map._zoom);
					}
				}
			}
		},

		addBaseLayer: function (layer, name) {
			this._baseLayers[name] = layer;
			layer.baseLayerName = name;
			this._addLayer(layer, name);
			this._update();
			return this;
		},

		addOverlay: function (layer, name) {
			this._overlayLayers[name] = layer;
			layer.overlayLayerName = name;
			this._addLayer(layer, name, true);
			this._update();
			return this;
		},

		removeLayer: function (layer) {
			if (this.baseLayers && layer.baseLayerName) {
				delete this.baseLayers[layer.baseLayerName];
				delete layer.baseLayerName;
			}
			if (this.overlayLayers && layer.overlayLayerName) {
				delete this.overlayLayers[layer.overlayLayerName];
				delete layer.overlayLayerName;
			}
			var id = L.stamp(layer);
			delete this._layers[id];
			this._update();
			return this;
		},

		onAdd: function (map) {
			var container = L.Control.Layers.prototype.onAdd.call(this, map);

			// Capturar el cambio de capa base y cambiar la configuración del mapa en función 
			//de la capa a visualizar
			map.on("baselayerchange", this.changeMapProperties);

			return container;
		},
	});
	
	L.control.baselayerselector = function (baseLayers, overlays, options) {
		if (!options) {
			options = {};
		}
		if (!options.position) {
			options.position = 'topright';
			options.collapsed = true;
			options.autoZIndex = true;
		}
		return new L.Control.BaseLayerSelector(baseLayers, overlays, options);
	};
})();
