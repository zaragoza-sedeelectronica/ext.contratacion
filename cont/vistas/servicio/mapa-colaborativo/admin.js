var markers;

//Definici&oacute;n de UTM30N ED50 (Empleada en la informaci&oacute;n vectorial del fichero zona-satrada.geojson)
var crsED50 = L.CRS.proj4js('EPSG:23030', '+proj=utm +zone=30 +ellps=intl +units=m +no_defs');

var creando = window.location.href.indexOf('/new')>0;

var featureGroup;


var iconos = [
{"id": "Acceso accesible", "uri": "http://www.zaragoza.es/contenidos/iconos/acceso_accesible.png"},
{"id": "Acceso peatones", "uri": "http://www.zaragoza.es/contenidos/iconos/acceso_peatones.png"},
{"id": "Acceso veh&iacute;culos", "uri": "http://www.zaragoza.es/contenidos/iconos/acceso_vehiculos.png"},
{"id": "Administraci&oacute;n p&uacute;blica", "uri": "http://www.zaragoza.es/contenidos/iconos/administracionPublica.png"},
{"id": "Afecciones al tr&aacute;fico", "uri": "http://www.zaragoza.es/contenidos/iconos/afecciones.png"},
{"id": "Agenda", "uri": "http://www.zaragoza.es/contenidos/iconos/agenda.png"},
{"id": "Agua", "uri": "http://www.zaragoza.es/contenidos/iconos/cortesdeagua.png"},
{"id": "Alojamiento", "uri": "http://www.zaragoza.es/contenidos/iconos/alojamiento2.png"},
{"id": "Animal", "uri": "http://www.zaragoza.es/contenidos/iconos/zonaEsparcimientoCanino.png"},
{"id": "Aparcabici", "uri": "http://www.zaragoza.es/contenidos/iconos/aparcabici.png"},
{"id": "Aparcamiento", "uri": "http://www.zaragoza.es/contenidos/iconos/aparcamientos.png"},
{"id": "Arte", "uri": "http://www.zaragoza.es/contenidos/iconos/arte.png"},
{"id": "Aseos", "uri": "http://www.zaragoza.es/contenidos/iconos/aseosPublicos.png"},
{"id": "Atenci&oacute;n ciudadana", "uri": "http://www.zaragoza.es/contenidos/iconos/atencionCiudadana.png"},
{"id": "Autob&uacute;s", "uri": "http://www.zaragoza.es/contenidos/iconos/paradasdeBus.png"},
{"id": "Aviso 1", "uri": "http://www.zaragoza.es/contenidos/iconos/avisos.png"},
{"id": "Aviso 2", "uri": "http://www.zaragoza.es/contenidos/iconos/avisos_negro.png"},
{"id": "Ayuntamiento", "uri": "http://www.zaragoza.es/contenidos/iconos/ayuntamiento.png"},
{"id": "Bar", "uri": "http://www.zaragoza.es/contenidos/iconos/barmarcha.png"},
{"id": "Biblioteca", "uri": "http://www.zaragoza.es/contenidos/iconos/bibliotecas_documentacion20.png"},
{"id": "Biceberg", "uri": "http://www.zaragoza.es/contenidos/iconos/biceberg.png"},
{"id": "Bizi", "uri": "http://www.zaragoza.es/contenidos/iconos/estacionesBizi2.png"},
{"id": "Cafe", "uri": "http://www.zaragoza.es/contenidos/iconos/quioscoBar.png"},
{"id": "Campo de f&uacute;tbol", "uri": "http://www.zaragoza.es/contenidos/iconos/campos_futbol20.png"},
{"id": "Canal", "uri": "http://www.zaragoza.es/contenidos/iconos/canalImperial.png"},
{"id": "Casa", "uri": "http://www.zaragoza.es/contenidos/iconos/casa0.png"},
{"id": "Centro deportivo", "uri": "http://www.zaragoza.es/contenidos/iconos/centros_deportivos20.png"},
{"id": "Cine", "uri": "http://www.zaragoza.es/contenidos/iconos/cine.png"},
{"id": "Circuito", "uri": "http://www.zaragoza.es/contenidos/iconos/circuitoVita.png"},
{"id": "Compras", "uri": "http://www.zaragoza.es/contenidos/iconos/compras.png"},
{"id": "Congesti&oacute;n tr&aacute;fico", "uri": "http://www.zaragoza.es/contenidos/iconos/estadodetrafico.png"},
{"id": "Congreso", "uri": "http://www.zaragoza.es/contenidos/iconos/congreso.png"},
{"id": "Coraz&oacute;n", "uri": "http://www.zaragoza.es/contenidos/iconos/corazon.png"},
{"id": "Cultura y ocio", "uri": "http://www.zaragoza.es/contenidos/iconos/culturaYocio.png"},
{"id": "Deporte", "uri": "http://www.zaragoza.es/contenidos/iconos/deporte.png"},
{"id": "Destacado", "uri": "http://www.zaragoza.es/contenidos/iconos/novedadesDestacados.png"},
{"id": "Ebro", "uri": "http://www.zaragoza.es/contenidos/iconos/rioEbro.png"},
{"id": "Educaci&oacute;n", "uri": "http://www.zaragoza.es/contenidos/iconos/educacion.png"},
{"id": "Empleo", "uri": "http://www.zaragoza.es/contenidos/iconos/empleo.png"},
{"id": "Empresa", "uri": "http://www.zaragoza.es/contenidos/iconos/empresas.png"},
{"id": "Estacionamiento motos", "uri": "http://www.zaragoza.es/contenidos/iconos/estacionamiento_motos.png"},
{"id": "Estacionamiento personas con discapacidad", "uri": "http://www.zaragoza.es/contenidos/iconos/estacionamiento_minus.png"},
{"id": "Farmacia", "uri": "http://www.zaragoza.es/contenidos/iconos/farmaciahorarioespecial.png"},
{"id": "Fiestas", "uri": "http://www.zaragoza.es/contenidos/iconos/fiestasPopulares.png"},
{"id": "Fuente", "uri": "http://www.zaragoza.es/contenidos/iconos/fuenteAguaPotable.png"},
{"id": "Fuente ornamental", "uri": "http://www.zaragoza.es/contenidos/iconos/fuenteOrnamental.png"},
{"id": "Gasolinera", "uri": "http://www.zaragoza.es/contenidos/iconos/estacionServicio.png"},
{"id": "Gimnasio", "uri": "http://www.zaragoza.es/contenidos/iconos/gimnasioCalle.png"},
{"id": "Hosteler&iacute;a", "uri": "http://www.zaragoza.es/contenidos/iconos/hosteleria.png"},
{"id": "Icono gen&eacute;rico", "uri": "http://www.zaragoza.es/contenidos/iconos/estaSemana.png"},
{"id": "Icono gen&eacute;rico (color negro)", "uri": "http://www.zaragoza.es/contenidos/iconos/estaSemana_negro.png"},
{"id": "Informaci&oacute;n", "uri": "http://www.zaragoza.es/contenidos/iconos/informacion.png"},
{"id": "Innovaci&oacute;n", "uri": "http://www.zaragoza.es/contenidos/iconos/innovacion.png"},
{"id": "Juegos 1", "uri": "http://www.zaragoza.es/contenidos/iconos/juegosDeMayores.png"},
{"id": "Juegos 2", "uri": "http://www.zaragoza.es/contenidos/iconos/juegosDeMayores2.png"},
{"id": "Juegos 3", "uri": "http://www.zaragoza.es/contenidos/iconos/juegosInfantiles.png"},
{"id": "Mano", "uri": "http://www.zaragoza.es/contenidos/iconos/cortesdetrafico.png"},
{"id": "Medio ambiente", "uri": "http://www.zaragoza.es/contenidos/iconos/medioAmbiente.png"},
{"id": "Musica", "uri": "http://www.zaragoza.es/contenidos/iconos/quioscoMusica.png"},
{"id": "P&aacute;jaro", "uri": "http://www.zaragoza.es/contenidos/iconos/cotorraargentinaazul.png"},
{"id": "Parqu&iacute;metro", "uri": "http://www.zaragoza.es/contenidos/iconos/parquimetro_ESRO.png"},
{"id": "Parquing", "uri": "http://www.zaragoza.es/contenidos/iconos/parking.png"},
{"id": "Participaci&oacute;n", "uri": "http://www.zaragoza.es/contenidos/iconos/participacionCiudadana.png"},
{"id": "Protecci&oacute;n ciudadana", "uri": "http://www.zaragoza.es/contenidos/iconos/proteccionCiudadana.png"},
{"id": "Punto Limpio", "uri": "http://www.zaragoza.es/contenidos/iconos/puntosLimpios.png"},
{"id": "Restaurante", "uri": "http://www.zaragoza.es/contenidos/iconos/restaurante20.png"},
{"id": "Sanidad", "uri": "http://www.zaragoza.es/contenidos/iconos/sanidad.png"},
{"id": "Semana santa", "uri": "http://www.zaragoza.es/contenidos/iconos/santa1.png"},
{"id": "Servicios sociales", "uri": "http://www.zaragoza.es/contenidos/iconos/serviciosSociales.png"},
{"id": "Servicios urbanos", "uri": "http://www.zaragoza.es/contenidos/iconos/serviciosUrbanos.png"},
{"id": "Taxi", "uri": "http://www.zaragoza.es/contenidos/iconos/paradasdeTaxi.png"},
{"id": "Tranv&iacute;a", "uri": "http://www.zaragoza.es/contenidos/iconos/tranvia.png"},
{"id": "Wizi", "uri": "http://www.zaragoza.es/contenidos/iconos/wizi.png"},
{"id": "Zonas verdes", "uri": "http://www.zaragoza.es/contenidos/iconos/zonasVerdes.png"}
];


var selectIcons = '<select class="form-control" name="icon" id="icon"><option value="">Icono</option>';
            for (var contador = 0; contador < iconos.length; contador++) {
                var sel = '';
                if (iconos[contador].uri == $('#icon').val()) {
                    sel = ' selected="selected"';
                }
                selectIcons = selectIcons.concat('<option' + sel + ' value="' + iconos[contador].uri + '" style="padding-left:3em;height:2em;background-repeat:no-repeat;background-image:url(' + iconos[contador].uri + ')">' + iconos[contador].id + '</option>');
            }
            selectIcons = selectIcons + '</select>';
$("#icon").replaceWith(selectIcons);


function cargaEnMapa(datos) {
    featureGroup = L.featureGroup().addTo(map)
    var recursos = L.Proj.geoJson(datos, {
        onEachFeature: function(feature, layer) {
            if (feature.geometry.type==='Point') {

                L.marker([feature.geometry.coordinates[1],feature.geometry.coordinates[0]], {
                // new L.Marker(new L.LatLng(feature.geometry.coordinates[0], feature.geometry.coordinates[1]), {
                    icon: L.icon({
                            iconUrl: feature.properties.icon || 'http://www.zaragoza.es/contenidos/iconos/generico.png',
                            iconSize: [22, 20],
                            iconAnchor: [22, 20],
                            popupAnchor: [-10, -20],
                    }),
                    draggable: (feature.properties.updateable ==='true' ? true:false),
                    properties: feature.properties})
                    .bindPopup(content(feature))
                    .addTo(featureGroup);
            } else if (feature.geometry.type==='LineString') {
                var linea = L.polyline(reverseCoordsLinea(feature.geometry.coordinates), 
                        {
                            color: feature.properties.style.strokeColor || "#c33",
                            weight: feature.properties.style["strokeWidth"] || "3",
                            opacity: feature.properties.style["strokeOpacity"] || "0.7",
                            properties: feature.properties
                        });
                    linea.bindPopup(content(feature))
                    linea.addTo(featureGroup);
            } else if (feature.geometry.type==='Polygon') {
                var poli = L.polygon(reverseCoordsPoligono(feature.geometry.coordinates), 
                        {
                            color: feature.properties.style.strokeColor || "#c33",
                            weight: feature.properties.style["strokeWidth"] || "3",
                            opacity: feature.properties.style["strokeOpacity"] || "0.7",
                            fillColor: feature.properties.style["fillColor"] || "#c33",
                            fillOpacity: feature.properties.style["fillOpacity"] || "0.7",
                            properties: feature.properties
                        });
                poli.bindPopup(content(feature))
                poli.addTo(featureGroup);
            }   
        }
    
    });
    if (featureGroup.getLayers().length > 0) {
        map.fitBounds(featureGroup.getBounds());
    }
}

if(typeof uriMapa == 'undefined') {
    console.log('Variable uriMapa no definida');
    mapaVacio();
} else {
    $.getJSON(uriMapa + ".geojson?srsname=wgs84", function(json) {
        delete json.crs;
        cargaEnMapa(json);
        var drawControl = new L.Control.Draw({
            draw:{
                polyline:true,
                polygon:true,
                rectangle:true,
                circle:false,
                marker:true
            },
            edit: {
              featureGroup: featureGroup
            }
          }).addTo(map);
    }).error(function() {
        mapaVacio();

    });
}

function mapaVacio() {
    cargaEnMapa({properties:'',features:[]});
    var drawControl = new L.Control.Draw({
    draw:{
        polyline:true,
        polygon:true,
        rectangle:true,
        circle:false,
        marker:true
    },
    edit: {
      featureGroup: featureGroup
    }
  }).addTo(map);
}

function reverseCoordsLinea(coords) {
    var retorno = [];    
    for (var i = 0; i < coords.length; i++) {
        retorno.push([coords[i][1],coords[i][0]]);
    }
    return retorno
}

function reverseCoordsPoligono(coords) {
    var retorno = [];
    retorno[0] = []   
    for (var i = 0; i < coords[0].length; i++) {
        retorno[0].push([coords[0][i][1],coords[0][i][0]]);
    }
    return retorno
}
function guardarInformacionColaborativa() {
    $("#jsoncontent").val(JSON.stringify(obtenerJson()));
    return true; 
}
function content(l) {
   if (!l.properties.style) {
       l.properties.style={};
   }
    if (l.properties.updateable === "true") {
        var content = '<div style="width:300px"><input type="hidden" value="' + (l.properties.id || '') + '" name="form_m_id" id="form_m_id"/>'
            + '<input type="text" class="form-control" value="' + (l.properties.title || '') + '" name="form_m_title" id="form_m_title" placeholder="Nombre"/><br/>' 
            + '<textarea name="form_m_description" class="form-control" id="form_m_description" placeholder="Descripci&oacute;n">' + (l.properties.description || '') + '</textarea><br/>';

        if (l.geometry.type==='Point') {
            content = content + '<select class="form-control" name="form_m_icon" id="form_m_icon"><option value="">Icono</option>';
            for (var contador = 0; contador < iconos.length; contador++) {
                var sel = '';
                if (iconos[contador].uri == l.properties.icon) {
                    sel = ' selected="selected"';
                }
                content = content.concat('<option' + sel + ' value="' + iconos[contador].uri + '" style="padding-left:3em;height:2em;background-repeat:no-repeat;background-image:url(' + iconos[contador].uri + ')">' + iconos[contador].id + '</option>');
            }
            content = content + '</select><br/>';
        } else if (l.geometry.type==='LineString') {
            content = content + 
                    '<div class="row">'
                    + '<div class="col-md-4">'
                        + 'Color <input class="form-control" type="color" value="' + (l.properties.style.strokeColor || '') + '" name="form_m_stroke" id="form_m_stroke" placeholder="Color"/><br/>'
                    + '</div>'
                    + '<div class="col-md-4">'
                        + 'Ancho: <input class="form-control" type="number" min="0" max="10" step="1" value="' + (l.properties.style["strokeWidth"] || '') + '" name="form_m_stroke-width" id="form_m_stroke-width" placeholder="Ancho L&iacute;nea"/>' 
                    + '</div>'
                    + '<div class="col-md-4">'
                        + 'Opacidad: <input class="form-control" type="number" min="0" max="1" step="0.1" value="' + (l.properties.style["strokeOpacity"] || '') + '" name="form_m_stroke-opacity" id="form_m_stroke-opacity" placeholder="Opacidad L&iacute;nea"/>' 
                    + '</div>' 
                + '</div>';
        } else if (l.geometry.type==='Polygon') {
            content = content + 
                    '<div class="row">'
                    + '<div class="col-md-4">'
                        + 'Color Borde <input class="form-control" type="color" value="' + (l.properties.style.strokeColor || '') + '" name="form_m_stroke" id="form_m_stroke" placeholder="Color"/><br/>'
                    + '</div>'
                    + '<div class="col-md-4">'
                        + 'Ancho: <input class="form-control" type="number" min="0" max="10" step="1" value="' + (l.properties.style["strokeWidth"] || '') + '" name="form_m_stroke-width" id="form_m_stroke-width" placeholder="Ancho L&iacute;nea"/>' 
                    + '</div>'
                    + '<div class="col-md-4">'
                        + 'Opacidad: <input class="form-control" type="number" min="0" max="1" step="0.1" value="' + (l.properties.style["strokeOpacity"] || '') + '" name="form_m_stroke-opacity" id="form_m_stroke-opacity" placeholder="Opacidad L&iacute;nea"/>' 
                    + '</div>' 
                + '</div>';
            content = content 
                + '<div class="row">'
                    + '<div class="col-md-4">'
                        + 'Color relleno <input class="form-control" type="color" value="' + (l.properties.style.fillColor || '') + '" name="form_m_fill" id="form_m_fill" placeholder="Color del relleno"/><br/>'
                    + '</div>'
                    + '<div class="col-md-4">'
                        + 'Opacidad <input class="form-control" type="number" min="0" max="1" step="0.1" value="' + (l.properties.style["fillOpacity"] || '') + '" name="form_m_fill-opacity" id="form_m_fill-opacity" placeholder="Opacidad del relleno"/><br/>'
                    + '</div>' 
                + '</div>';
        }

        content = content + '</div><button onclick="guardar()" class="btn btn-primary">Guardar</button>';
    } else {
        content = '<strong>' + l.properties.title + '</strong><br/>' + l.properties.description;

    }
    
    return content;
    
}

function guardar() {

    var propiedades = {};
    propiedades.style = {};
    propiedades.id = $("#form_m_id").val();
    
    propiedades.updateable = "true";

    propiedades.title = $("#form_m_title").val();
    propiedades.description = $("#form_m_description").val();
    propiedades.icon = $("#form_m_icon").val();

    propiedades.style["strokeColor"] = $("#form_m_stroke").val();
    propiedades.style["strokeWidth"] = $("#form_m_stroke-width").val();
    propiedades.style["strokeOpacity"] = $("#form_m_stroke-opacity").val();
    propiedades.style["fillColor"] = $("#form_m_fill").val();
    propiedades.style["fillOpacity"] = $("#form_m_fill-opacity").val();

    var capas = obtenerGeoJson(propiedades);
    
    map.removeLayer(featureGroup);
    cargaEnMapa(capas);
    if (currentPopup != null)currentPopup._source.closePopup();
}

function obtenerGeoJson(propiedades) {
    var capas = {};
    capas.type = "FeatureCollection";
    capas.properties = {};
    capas.features = [];
    for (var i = 0; i < featureGroup.getLayers().length; i++) {
        // var feat = featureGroup.getLayers()[i];
        var feat = {};
        if (propiedades && (!featureGroup.getLayers()[i].options.properties
            || featureGroup.getLayers()[i].options.properties.id === propiedades.id)) {
            // featureGroup.getLayers()[i].options.properties = propiedades;
            feat.type = "Feature";
            feat.geometry = featureGroup.getLayers()[i].toGeoJSON().geometry;
            feat.properties = propiedades;
        } else {
            feat.type = "Feature";
            feat.geometry = featureGroup.getLayers()[i].toGeoJSON().geometry;
            feat.properties = featureGroup.getLayers()[i].options.properties;
        }
        capas.features.push(feat);
    }
    return capas;
}

function obtenerJson(propiedades) {
    var capas = {};
    capas.title = $("#nombre").val();
    capas.type = $("#gczPublicado").val();
    capas.icon = $("#icon").val();

    capas.categories = [];
    categorias = $("#propiedadescategory").val(); 
    if (categorias != null) {
        for (var i = 0; i < categorias.length; i++) {
            var cat = {};
            cat.id = categorias[i];
            capas.categories.push(cat);
        }
    }

    capas.pois = [];
    for (var i = 0; i < featureGroup.getLayers().length; i++) {
        // var feat = featureGroup.getLayers()[i];
        var feat = {};
        if (propiedades && (!featureGroup.getLayers()[i].options.properties
            || featureGroup.getLayers()[i].options.properties.id === propiedades.id)) {
            feat = propiedades;
            // feat.type = "Feature";
            feat.geometry = featureGroup.getLayers()[i].toGeoJSON().geometry;
        } else {
            feat = featureGroup.getLayers()[i].options.properties;
            // feat.type = "Feature";
            feat.geometry = featureGroup.getLayers()[i].toGeoJSON().geometry;
            
        }

        capas.pois.push(feat);
    }
    return capas;
}


  map.on("popupopen", function(evt){currentPopup = evt.popup});

  map.on('draw:created', function(e) {

        var obj = e.layer.toGeoJSON();
        obj.properties = {}
        obj.properties.id = 'nuevo-' + (featureGroup.getLayers().length + 7);
        obj.properties.updateable = "true";
        if (obj.geometry.type==='Point') {
            L.marker(e.layer.getLatLng(),
                {
                    properties: obj.properties
                })
            .bindPopup(content(obj))
            .addTo(featureGroup);
        } else if (obj.geometry.type==='LineString') {
            var linea = L.polyline(e.layer.getLatLngs(), {
                    color: "#c33",
                    weight: "3",
                    opacity: "0.7",
                    properties: obj.properties
                });
            linea.bindPopup(content(obj))
            linea.addTo(featureGroup);
        } else if (obj.geometry.type==='Polygon') {
            var poli = L.polygon(e.layer.getLatLngs(), {
                    color: "#c33",
                    weight: "3",
                    opacity: "0.7",
                    fillColor: "#c33",
                    fillOpacity: "0.3",
                    properties: obj.properties
                });
            poli.bindPopup(content(obj))
            poli.addTo(featureGroup);
        } else {
            featureGroup.addLayer(e.layer);
        }

  });

var num_results = 7;
var customControl = L.Control.extend({

  options: {
    position: 'topright' 
  },

  onAdd: function (map) {
    var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');

    if (!L.Browser.touch) {
        L.DomEvent.disableClickPropagation(container);
        L.DomEvent.on(container, 'mousewheel', L.DomEvent.stopPropagation);
    } else {
        L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);
    }
 
    container.style.backgroundColor = 'white';
    container.style.width = '30px';
    container.style.height = '30px';
 

    var mostrar = L.DomUtil.create('button', 'fa fa-search fa-2x');
    mostrar.id='mostrar';
    mostrar.style.border = '0';
    mostrar.style.width = '30px';
    mostrar.style.height = '30px';
    mostrar.style.backgroundColor = 'transparent';

    mostrar.onclick = function(e){

      container.style.width='auto';
      // if($('#resultado').css('display') == 'none'){
      //   $('#resultado').show();
      // };
      if (!$('#resultados').length) {
        $("#resultados").show();
          mostrar.style.display = 'none';
          var input = L.DomUtil.create('input', '');
          input.type='text';
          input.name='resultados';
          input.id='resultados';
          input.class='form-control';
          input.autocomplete='off';
          input.style.width='250px';
          input.placeholder='Buscar un elemento';

          var cerrar = L.DomUtil.create('button', '');
          cerrar.id='cerrar';
          cerrar.style.border = '0';
          cerrar.style.width = '30px';
          cerrar.style.backgroundColor = 'transparent';
          cerrar.innerHTML = 'X';
          cerrar.onclick = function(e) {
            //$("#resultados").typeahead('destroy');
            $("#resultados").remove();
            // $("#resultados").hide();
            $("#cerrar").remove();
            mostrar.style.display = 'block';
            e.preventDefault();
          }
          container.appendChild(input);
          container.appendChild(cerrar);

          var resultadosDataset = new Bloodhound({
                datumTokenizer: function(countries) {
                    return Bloodhound.tokenizers.whitespace(result.value);
                },
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                remote: {
                    url: '//www.zaragoza.es/sede/servicio/puntos-interes?rows=10&srsname=wgs84&q=title:%QUERY*&fl=id,title,description,etiqueta,geometry',
                    filter: function(data) {
                        return data.result;
                    },
                    wildcard: '%QUERY',
                }
            });

        $('#resultados').typeahead({
            hint: true,
            highlight: true,
            minLength: 1
        },
        {
        	limit: 'Infinity',
            name: 'resultados',
            displayKey: function(results) {
                return results.title;        
            },
            source: resultadosDataset
        })


        $('#resultados').bind('typeahead:select', function(ev, item) {
            //item = JSON.parse(item)
            feature = {};
            feature.properties = {};
            feature.properties.id = item.id;
            feature.properties.title = item.title;
            feature.properties.description = item.description;
            feature.properties.updateable = "true";
            feature.geometry = {}
            feature.geometry.type='Point';
            L.marker([item.geometry.coordinates[1],item.geometry.coordinates[0]], {
                icon: L.icon({
                        iconUrl: 'http://www.zaragoza.es/contenidos/iconos/generico.png',
                        iconSize: [22, 20],
                        iconAnchor: [22, 20],
                        popupAnchor: [-10, -20],
                }),
                draggable: false,
                properties: feature.properties})
                .bindPopup(content(feature))
                .addTo(featureGroup);
            map.fitBounds(featureGroup.getBounds());
            $('#resultados').typeahead('val', '');
            return item.name;
            
        });
          
      }
      e.preventDefault();
    }
    container.appendChild(mostrar);
    return container;
  }

});

map.addControl(new customControl());

$("#envioGeojson").click(function(event) {
    $('#mensajeMapa').text('');
    $('#mensajeMapa').removeClass('alert');
    $('#mensajeMapa').removeClass('alert-info');
    $('#mensajeMapa').removeClass('alert-error');
    var datosGeoJson = obtenerJson();
    if (datosGeoJson.pois.length > 0) {
        $.ajax({
            url: uriMapa,
            data: JSON.stringify(datosGeoJson),
            dataType: "json",
            type: "PUT",
            contentType: "application/json; charset=utf-8",
            success: function(data) {
                $('#mensajeMapa').text('Guardado correctamente');
                $('#mensajeMapa').addClass('alert alert-info');
                $('#envioGeojsonForm').submit();
            },
            fail: function(data) {
                $('#mensajeMapa').text('Fallo al guardar:' + JSON.stringify(data));
                $('#mensajeMapa').addClass('alert alert-error');
            },
            error: function(data) {
                $('#mensajeMapa').text('Error al guardar:' + JSON.stringify(data));
                $('#mensajeMapa').addClass('alert alert-error'); 
            }
        });
    } else {
        console.log('No hay pois');
        $('#envioGeojsonForm').submit();
    }
    return false;
});