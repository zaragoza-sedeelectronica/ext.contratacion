$('.datepicker').datepicker();

function addLugarRealizacion(infoCentro) {
    // Si no tiene un equipamiento seleccionado en el autocompletado salimos del evento
    if($('#equipamiento_title').val() === '') {
        return;
    };
    // Construimos la tabla de los lugares de realiación
    var num =  $('#listado-lugares tr').length - 1;
    var row = '<tr id="addlug' + num + '">'
        + '<td>' 
        + '<input class="form-control" name="se[' + num + '].location.title" type="text" readonly="readonly" id="se' + num + '.location.title" value="' + $('#equipamiento_title').val() + '" />'
        + '<input class="form-control" name="equipid" type="hidden" id="se' + num + '.location.id" value="' + $('#equipamiento_id').val() + '" />'
        + '<span class="text-primary" id="popover-centro-' + num + '">Inf. del lugar</span>'
        + '</td>'
        + '<td>' + '<input class="form-control datepicker" placeholder="Formato: dd/mm/yyyy" data-date-format="dd/mm/yyyy" data-date-language="es" data-provide="datepicker" class="form-control" name="se[' + num + '].startDate" type="text" id="se[' + num + '].startDate"/>' + '</td>'
        + '<td>' + '<input class="form-control datepicker" placeholder="Formato: dd/mm/yyyy" data-date-format="dd/mm/yyyy" data-date-language="es" data-provide="datepicker" class="form-control" name="se[' + num + '].endDate" type="text" id="se[' + num + '].endDate" required="required"/>' + '</td>'
        + '<td>' + '<input placeholder="Ej: De lunes a viernes, de 09:00 a 21:00h" class="form-control" name="se[' + num + '].horario" type="text" id="se[' + num + '].horario"/>' + '</td>'
        + '<td>' + '<input placeholder="Información adicional al lugar de realización" class="form-control" name="se[' + num + '].comment" type="text" id="se[' + num + '].comment"/>' + '</td>'
        + '<td>' + '<button class="btn btn-default" type="button" onclick="borrarLugar('+ num +');">Borrar</button>' + '</td>'
        + '</tr>';
    // Añadimos la fila a la tabla de lugares de realización
    $('#listado-lugares').append(row);
    // Obtenemos la info del centro y generamos popover en la fila de la tabla correspondiente
    $('#popover-centro-' + num).popover({
        html: true,
        trigger: 'hover',
        content: function() {;
            return infoCentro;
        }
    }).blur(function(e) {
        $(this).popover('toggle');
    });
    // Borramos la selección del autocompletado de equipamiento
    setTimeout(function(){ 
        $('#equipamiento_title').val('');
    }, 50);
    // SOLO CIPAJ: Asociamos las fechas a nivel de actividad
    if($('.datepicker#startDate').val()){
        $('[id="se[0].startDate"]').datepicker();
        $('[id="se[0].startDate"]').val($('.datepicker#startDate').val());
    };
    if($('.datepicker#endDate').val()){
        $('[id="se[0].endDate"]').datepicker();
        $('[id="se[0].endDate"]').val($('.datepicker#endDate').val());
    };
    if($('#horario').val()){
        var horario = $('#horario').val();
        $('[id="se[0].horario"]').val(horario);
    };
};

function borrarLugar(num){
    $('#listado-lugares #addlug' + num).remove();
};

var programaDataset = new Bloodhound({
    datumTokenizer: function(countries) {
        return Bloodhound.tokenizers.whitespace(result.value);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: {
        url: '/sede/servicio/cultura/evento/programa?rows=10&q=title==%QUERY*&fl=id,title',
        // filter: function(x) {
        //     return $.map(x.result, function(item) {
        //         // console.log(item)
        //         return { item: item };
        //     });
        // },
        filter: function(response) {      
            return response.result;
        },
        wildcard: '%QUERY',
        // prepare: function(query, settings){
        //     var a = encodeURIComponent(query);
        //     console.log(a);
        // }
    }
});
$('#programa_title').typeahead(null, {
    name: 'programa',
    limit: 'Infinity',
    // display: 'item.title',
    displayKey: function(results) {
        return results.title;        
    },
    source: programaDataset
});
$('#programa_title').bind('typeahead:select', function(ev, suggestion) {
    //console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;
    $('#programa_id').val(item.id);
});


// TODO: NO DEVUELVE REGISTROS CON ACENTOS
var equipamientosDataset = new Bloodhound({
    datumTokenizer: function(countries) {
        return Bloodhound.tokenizers.whitespace(result.value);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: {
        url: '/sede/servicio/equipamiento/catalog?rows=10&q=title==%QUERY*&fl=id,title,calle,tel,email,url,portal.junta',
        filter: function(response) {      
            return response.result;
        },
        wildcard: '%QUERY',
    }
});
$('#equipamiento_title').typeahead(null, {
    name: 'equipamientos',
    limit: 'Infinity',
    // display: 'item.title',
    displayKey: function(results) {
        return results.title;        
    },
    source: equipamientosDataset,
    templates: {
        empty: [
            '<div class="empty-message">',
            'Lugar de realización no encontrado',
            '</div>'
        ].join('\n'),
        suggestion: function(data) {
            return '<p><strong>' + data.title + '</strong><br/><small>' + data.calle + '</small></p>';
        }
    }
});
$('#equipamiento_title').bind('typeahead:select', function(ev, suggestion) {
    //console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;
    // Campos adicionales de lugar de realización
    $('.datos-lugar').slideDown('slow');
    $('#equipamiento_id').val(item.id);
    var infoCentro = '<div class="fnd-gris-claro">'
        // + '<strong>Información del centro</strong>'
        + '<ul>'
        + (item.tel ? '<li><strong>Dirección:</strong> ' + item.calle + '</li>' : '')
        + (item.portal ? '<li><strong>Barrio:</strong> ' + item.portal.junta.title.replace('Junta Municipal ', '').replace('Junta Vecinal ', '') + '</li>' : '')
        + (item.tel && item.tel.tel ? '<li><strong>Teléfono:</strong> ' + item.tel.tel + '</li>' : '')
        + (item.email ? '<li><strong>Email:</strong> ' + item.email + '</li>' : '')
        + '</ul>'
        + '</div>';
    addLugarRealizacion(infoCentro);
});

$('#lugar_inscrip').typeahead(null, {
    name: 'equipamientos',
    limit: 'Infinity',
    // display: 'item.title',
    displayKey: function(results) {
        return results.title;        
    },
    source: equipamientosDataset
});
$('#lugar_inscrip').bind('typeahead:select', function(ev, suggestion) {
    var item = suggestion;
    // Autocompleta campos de inscripción con información de Centros (abiertos)
    if(item.calle) { $('#direccion_inscrip').val(item.calle); };
    if(item.tel && item.tel.tel) { $('#telefono_inscrip').val(item.tel.tel); };
    if(item.tel && item.tel.fax) { $('#fax_inscrip').val(item.tel.fax); };
    if(item.url) { $('#web_inscrip').val(item.url); };
    if(item.email) { $('#email_inscrip').val(item.email); };
});

var entidadesDataset = new Bloodhound({
    datumTokenizer: function(countries) {
        return Bloodhound.tokenizers.whitespace(result.value);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
        url: '/sede/servicio/cultura/evento/entidad?validation=true&rows=10&q=title==%QUERY*&fl=id,title,streetAddress,telephone,email,openingHours&sort=title asc',
        filter: function(response) {
        	$("#entidad_num_res").text(response.totalCount);
        	console.log($("#entidad_num_res").text());
            return response.result;
        },
        wildcard: '%QUERY'
    }
});

$('#entidad_title').typeahead(null, {
    name: 'entidades',
    limit: 'Infinity',
    templates: {
  	  footer: function (context) {
  		  var number = parseInt($("#entidad_num_res").text());
  		  if (number > 10) {
  			  return '<p class="destacado">Encontradas: ' + number + ' entidades puedes acotar más la consulta</p>';
  		  } else {
  			  return '';
  		  }
      }
    },    
    displayKey: function(results) {
        return results.title;        
    },
    source: entidadesDataset
});
$('#entidad_title').bind('typeahead:select', function(ev, suggestion) {
    //console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;
    // Campos adicionales de la entidad
    $('.datos-entidad').slideDown('slow');
    $('#entidad_id').val(item.id);
    var infoEntidad = '<div class="fnd-gris-claro">'
        // + '<strong>Información de la entidad organizadora</strong>'
        + '<ul>'
        + (item.streetAddress ? '<li><strong>Dirección:</strong> ' + item.streetAddress + '</li>' : '')
        + (item.telephone ? '<li><strong>Teléfono:</strong> ' + item.telephone + '</li>' : '')
        + (item.email ? '<li><strong>Email:</strong> ' + item.email + '</li>' : '')
        + (item.openingHours ? '<li><strong>Horario:</strong> ' + item.openingHours + '</li>' : '')
        + '</ul>'
        + '</div>';
    addEntidadOrganizadora(infoEntidad);
});

function addEntidadOrganizadora(infoEntidad) {
    // Si no tiene un equipamiento seleccionado en el autocompletado salimos del evento
    if($('#entidad_title').val() === '') {
        return;
    };
    // Construimos la tabla de las entidades
    var num =  $('#listado-entidades tr').length - 1;
    var row = '<tr id="entidad-' + num + '">'
        + '<td>' 
        + '<input class="form-control" name="org[' + num + '].title" type="text" readonly="readonly" id="org' + num + '.title" value="' + $('#entidad_title').val() + '" />'
        + '<input class="form-control" name="orgid" type="hidden" id="org' + num + '.id" value="' + $('#entidad_id').val() + '" />'
        + '<span class="text-primary" id="popover-entidad-' + num + '">Inf. de la entidad</span>'
        + '</td>'
        + '<td>' + '<button class="btn btn-default" type="button" onclick="borrarEntidad('+ num +');">Borrar</button>' + '</td>'
        + '</tr>';
    // Añadimos la fila a la tabla de entidades organizadoras
    $('#listado-entidades').append(row);
    // Obtenemos la info de la entidad y generamos popover en la fila de la tabla correspondiente
    $('#popover-entidad-' + num).popover({
        html: true,
        trigger: 'hover',
        content: function() {;
            return infoEntidad;
        }
    }).blur(function(e) {
        $(this).popover('toggle');
    });
    // Borramos la selección del autocompletado de equipamiento
    setTimeout(function(){ 
        $('#entidad_title').val('');
    }, 50);
}

function borrarEntidad(num){
    $('#listado-entidades #entidad-' + num).remove();
}


jQuery.validator.addMethod('entidadrule', function() {
	if (location.pathname.indexOf('juvenil') !== -1) {
		return ($('#listado-entidades tr').length > 1) || $("#entidad_titleNew").val();
	} else {
		return true;
	}

}, 'Please input a reason' );


$('#newForm').validate({
    rules: {
        title: {
            required: true
        },
        category: {
            required: true
        },
        type: {
            required: true
        },
        endDate: {
            required: function() {
                return $('#se0.endDate').length === 0 ? true : $('#se0.endDate').val(); 
            }
        },
        entidad_title: {
        	entidadrule : true
        },
        nombre_contacto: {
            required: true
        },
        telefono_contacto: {
            required: true,
            digits : true,
        }
    },messages: {
    	entidad_title: "Debes añadir una entidad organizadora seleccionándola de las opciones mostradas"
    },
    errorElement: 'span',
    errorClass: 'help-block',
    highlight: function (element, errorClass, validClass) {
        $(element).closest('.form-group').addClass('has-error');
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element).closest('.form-group').removeClass('has-error');
    },
    errorPlacement: function (error, element) {
        if (element.parent('.input-group').length || element.prop('type') === 'checkbox' || element.prop('type') === 'radio') {
            error.insertAfter(element.parent());
        } else {
            error.insertAfter(element);
        }
    },
    submitHandler: function(form) {
//        if($('#equipamiento_title').val() != '') {
//            $('#descripcion').val($('#descripcion').val() + '<p><strong>Lugar: </strong>' + $('#equipamiento_title').val() + '</p>');
//        }
        form.submit();
    }
});

File.prototype.convertToBase64 = function(callback){
    var reader = new FileReader();
    reader.onload = function(e) {
         callback(e.target.result)
    };
    reader.onerror = function(e) {
         callback(null);
    };
    reader.readAsDataURL(this);
};

$("#anexo-documento").on('change',function(){
    var selectedFile = this.files[0];
    //console.log(selectedFile);
    if(selectedFile.type === 'application/pdf' || selectedFile.type === 'text/html' || 
            selectedFile.type === 'application/vnd.oasis.opendocument.text' || 
            selectedFile.type === 'application/vnd.oasis.opendocument.spreadsheet' ||
            selectedFile.type === 'application/vnd.oasis.opendocument.presentation' ||
            selectedFile.type === 'application/vnd.ms-excel' || 
            selectedFile.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
            selectedFile.type === 'application/vnd.ms-powerpoint' ||
            selectedFile.type === 'image/jpeg' ||
            selectedFile.type === 'image/jpg' ||
            selectedFile.type === 'application/msword' || selectedFile.type === 'text/xml' ||
            selectedFile.type === 'text/plain' || selectedFile.type === '.csv' ||
            selectedFile.type === 'application/pdf' || selectedFile.type === 'application/zip' ||
            selectedFile.type === 'application/vnd.ms-excel' || selectedFile.type === 'vnd.openxmlformats-officedocument.spreadsheetml.sheet'){
        if((selectedFile.size / 1024 / 1024) > 3 ) {
            $(this).val('');
            showalert('#anexo-documento', 'El tamaño máximo de la imagen no puede exceder los 3 MB.', 'danger');
        } else {
            selectedFile.convertToBase64(function(base64){});
        };
    } else {
        $(this).val('');
        showalert('#anexo-documento', 'Formato de documento no soportado. Formatos permitidos: pdf, html, xls, ppt, csv, xml, txt, etc.', 'danger');
    };
});

$("#anexo-imagen").on('change',function(){
    var selectedFile = this.files[0];
    //console.log(selectedFile);
    if(selectedFile.type === 'image/jpeg' || selectedFile.type === 'image/png' || selectedFile.type === 'image/gif'){
        if((selectedFile.size / 1024 / 1024) > 3 ) {
            $(this).val('');
            showalert('#anexo-imagen', 'El tamaño máximo de la imagen no puede exceder los 3 MB.', 'danger');
        } else {
            selectedFile.convertToBase64(function(base64){});
        };
    } else {
        $(this).val('');
        showalert('#anexo-imagen', 'Formato de programa no soportado. Formatos permitidos: jpeg, png o gif', 'danger');
    };
});

$("#anexo-sonido").on('change',function(){
    var selectedFile = this.files[0];
    //console.log(selectedFile);
    if(selectedFile.type === 'audio/wav' || selectedFile.type === 'audio/mp4'){
        if((selectedFile.size / 1024 / 1024) > 3 ) {
            $(this).val('');
            showalert('#anexo-sonido', 'El tamaño máximo de la imagen no puede exceder los 3 MB.', 'danger');
        } else {
            selectedFile.convertToBase64(function(base64){});
        };
    } else {
        $(this).val('');
        showalert('#anexo-sonido', 'Formato de audio no soportado. Formatos permitidos: wav o mp4', 'danger');
    };
});

function showalert(elementId, message, alerttype) {
    var id = 'alert-' + elementId.split('#').pop();
    $(elementId).after('<div id="'+ id +'" role="alert" class="alert alert-' + alerttype + '">'
        + '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>'
        + '<span>' + message + '</span>'
        + '</div>');
    setTimeout(function() {
        $('#' + id).alert('close');
    }, 3000);
};

// Realiza animación scroll hasta posicionarse en la descripción
$('a.descripcion-link').click(function(){
    $('html, body').animate({
        scrollTop: $($(this).attr('href')).offset().top - 100
    }, 200);
});