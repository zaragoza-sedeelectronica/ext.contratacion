var actividadesDataset = new Bloodhound({
    datumTokenizer: function(countries) {
        return Bloodhound.tokenizers.whitespace(result.value);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
        // Esta consulta se filtra por el código de usuario establecido en la propiedad de gcz usr_agenda
        url: '/sede/servicio/cultura/evento/admin/list?rows=10&q=registration.url==*zaragoza.es/sede/servicio/cultura/evento/*&q=title==%QUERY*',
        filter: function(response) {
            return response.result;
        },
        wildcard: '%QUERY',
    }
});
$('#programa_title').typeahead(null, {
    name: 'actos',
    displayKey: function(results) {
        $('#program').val('');
        return results.title;
    },
    source: actividadesDataset
});
$('#programa_title').bind('typeahead:select', function(ev, suggestion) {
    console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;
    $('#programa_id').val(item.id);
    $('.visible-act').attr("hidden", false);
    if (item.program != null) {
        $('#program').val(item.program.title);
        $('#program').attr("disabled", true);
    } else {
        $('#program').attr("disabled", false);
        var programasDataset = new Bloodhound({
            datumTokenizer: function(countries) {
                return Bloodhound.tokenizers.whitespace(result.value);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                url: '/sede/servicio/cultura/evento/program?rows=10&q=title==%QUERY*&fl=id,title',
                
                filter: function(response) {
                    return response.result;
                },
                wildcard: '%QUERY',
            }
        });
        $('#program').typeahead(null, {
            name: 'programas',
            displayKey: function(results) {
                return results.title;
            },
            source: programasDataset
        });
        $('#program').bind('typeahead:select', function(ev2, suggestion2) {
            console.log('Selection: ' + JSON.stringify(suggestion2));
            var item2 = suggestion2;
            $('#program_id').val(item2.id);
        });
    }
});
$('#programa_title').bind('typeahead:select', function(ev, suggestion) {
    console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;

    $('#programa_id').val(item.id);
    $('#info-actividad').html('<div class="fnd-gris-claro padding-a1em">'
            + '<strong>Información de la actividad</strong>'
            + '<ul><br/>'
            + '<li><a href=/sede/servicio/cultura/evento/' + item.id + '>Detalle de la actividad</a></li>'
            + '</ul>'
            + '</div>'
        );

    /*
    var idActo = item.id;
    var data = item.subEvent;

    var startD = new Date(data.startDate);
    var startDate = ("0" + startD.getDate()).slice(-2) 
                    + "/" + ("0" + (startD.getMonth() + 1)).slice(-2) 
                    + "/" + startD.getFullYear();
    var endD = new Date(data.endDate);
    var endDate = ("0" + endD.getDate()).slice(-2) 
                    + "/" + ("0" + (endD.getMonth() + 1)).slice(-2) 
                    + "/" + endD.getFullYear();
    var openingHours = "<br/>";
    for (var i in data.openingHours) {
        openingHours = openingHours + data.openingHours[i].dayOfWeek + " " 
            + data.openingHours[i].startTime + "h - " 
            + data.openingHours[i].endTime + 'h';
    }
    
    console.log('acto: ' + JSON.stringify(item));
    console.log('subEvent: ' + JSON.stringify(data));

    $('#programa_id').val(item.id);
    if (data != undefined && data.location != undefined) {
        $('#info-actividad').html('<div class="fnd-gris-claro padding-a1em">'
            + '<strong>Información de la actividad</strong>'
            + '<ul><br/>'
            + '<li><strong>Lugar:</strong> ' + data.location.title + '</li>'
            + '<li><strong>Dirección:</strong> ' + data.location.streetAddress 
            + ' (' + data.location.addressLocality + ')' + '</li>'
            + '<li><strong>Duración:</strong> ' + data.subevento.startDate 
            + ' - ' + data.subevento.endDate + '</li>'
            + '<li><strong>Horario:</strong> ' + openingHours + '</li>'
            + '<li><a href="/sede/servicio/cultura/evento/">Detalle de la actividad</a></li>'
            + '</ul>'
            + '</div>');
    }*/

    /*var url = '/sede/servicio/cultura/evento/'+idActo+'/subevents';
    var data = {
      fl: 'id,location,startDate,endDate,openingHours'
    };
    $.getJSON(url, data, function (data, status) {
        if (status == "success") {
            var startD = new Date(data.startDate);
            var startDate = ("0" + startD.getDate()).slice(-2) 
                            + "/" + ("0" + (startD.getMonth() + 1)).slice(-2) 
                            + "/" + startD.getFullYear();
            var endD = new Date(data.endDate);
            var endDate = ("0" + endD.getDate()).slice(-2) 
                            + "/" + ("0" + (endD.getMonth() + 1)).slice(-2) 
                            + "/" + endD.getFullYear();
            var openingHours = "<br/>";
            for (var i in data.openingHours) {
                openingHours = openingHours + data.openingHours[i].dayOfWeek + " " 
                    + data.openingHours[i].startTime + "h - " 
                    + data.openingHours[i].endTime + 'h';
            }
            
            $('#programa_id').val(item.id);
            if (data != "undefined" && data.location != "undefined") {
                $('#info-actividad').html('<div class="fnd-gris-claro padding-a1em">'
                    + '<strong>InformaciÃ³n de la actividad</strong>'
                    + '<ul>'
                    + '<li><strong>Lugar:</strong> ' + data.location.title + '</li>'
                    + '<li><strong>DirecciÃ³n:</strong> ' + data.location.streetAddress 
                    + ' (' + data.location.addressLocality + ')' + '</li>'
                    + '<li><strong>DuraciÃ³n:</strong> ' + startDate 
                    + ' - ' + endDate + '</li>'
                    + '<li><strong>Horario:</strong> ' + openingHours + '</li>'
                    + '</ul>'
                    + '</div>');
            }
        }
    });*/
});

///////////////////////////////////////

var usuariosDataset = new Bloodhound({
    datumTokenizer: function(countries) {
        return Bloodhound.tokenizers.whitespace(result.value);
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
        url: '/sede/servicio/zona-personal/usuarios?rows=10&q=email==%QUERY*',
        
        filter: function(response) {
            return response.result;
        },
        wildcard: '%QUERY',
    }
});
$('#usuario_name').typeahead(null, {
    name: 'users',
    displayKey: function(results) {
        return results.email;
    },
    source: usuariosDataset
});
$('#usuario_name').bind('typeahead:select', function(ev, suggestion) {
    console.log('Selection: ' + JSON.stringify(suggestion));
    var item = suggestion;
    $('#usuario_id').val(item.id);

    var idUsuario = $('#usuario_id').val();

    var url = '/sede/servicio/cultura/evento/admin/inscripcion/usuario/'+idUsuario;
    var data = {
      fl: 'id,fechaNacimiento,genero,codigoPostal,penalizado'
    };
    
    $('.visible').attr("hidden", false);
    $('.visible-info').attr("hidden", false);
    $('#fecha_nacimiento').val('');
    $('#genero').val('');
    $('#codigo_postal').val('');
    $('#penalizado').val('');

    var d = new Date(item.perfil.fechaNacimiento);
    var fechaNacimiento = ("0" + d.getDate()).slice(-2) + "/" + ("0" + (d.getMonth() + 1)).slice(-2) + "/" + d.getFullYear();

    $('#info-usuario').html('<div class="fnd-gris-claro padding-a1em">'
        + '<strong>Informaci&oacute;n del usuario/a</strong>'
        + '<ul>'
        + '<li><strong>Nombre:</strong> ' + item.name + '</li>'
        + '<li><strong>Email:</strong> ' + item.email + '</li>'
        + '<li><strong>Fecha Nacimiento:</strong> ' + fechaNacimiento + '</li>'
        + '<li><strong>Genero:</strong> ' + item.perfil.genero + '</li>'
        + '</ul>'
        + '</div>');

/*$.getJSON(url, data, function (data, status) {
        if (status == "success") {
            var d = new Date(data.fechaNacimiento);
            var fechaNacimiento = ("0" + d.getDate()).slice(-2) + "/" + ("0" + (d.getMonth() + 1)).slice(-2) + "/" + d.getFullYear();
            var penalizado;
            if (data.penalizado == 'S') {
                penalizado = 'Sí';
            } else {
                penalizado = 'No';
            }

            $('.visible').attr("hidden", true);
            $('.visible-info').attr("hidden", true);
            $('#fecha_nacimiento').val(fechaNacimiento);
            $("#selectGenero option[value=" + data.genero + "]").attr('selected', 'selected'); 
            $('#codigo_postal').val(data.codigoPostal);
            $("#selectPenalizado option[value=" + data.penalizado + "]").attr('selected', 'selected'); 
            $('#info-usuario').html('<div class="fnd-gris-claro padding-a1em">'
                + '<strong>Información del usuario/a</strong>'
                + '<ul>'
                + '<li><strong>Nombre:</strong> ' + item.name + '</li>'
                + '<li><strong>Email:</strong> ' + item.email + '</li>'
                + '<li><strong>Fecha de nacimiento:</strong> ' + fechaNacimiento + '</li>'
                + '<li><strong>Género:</strong> ' + data.genero + '</li>'
                + '<li><strong>Código Postal:</strong> ' + data.codigoPostal + '</li>'
                + '<li><strong>Penalizado/a:</strong> ' + penalizado + '</li>'
                + '<br/>'
                + '<span><a href=/sede/servicio/cultura/evento/admin/inscripcion/usuario/' + item.id + '/edit>Editar datos usuario</a></span>'
                + '</ul>'
                + '</div>');
        }
    });*/    
});

function editarDatosUsuario() {
    if ($('.visible').attr('hidden') == 'hidden' || $('.visible').attr('hidden') == 'true') {
        $('.visible').attr("hidden", false);
    } else {
        $('.visible').attr("hidden", true);
    }
}