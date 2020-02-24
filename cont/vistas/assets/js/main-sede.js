$(function() {

    // Botón de búsqueda móvil
    $('.btn-search.visible-xs-inline').click(function(e) {
        $('.search-mobile').slideToggle('fast');
    });

    // Botón cierre de búsqueda móvil
    $('.search-mobile .btn-close').click(function(e) {
        $('.search-mobile').slideToggle('fast');
    });

    // Añadimos el margin correspondiente al ancho de la etiqueta de búsqueda en portal
    $('[id^="input-search-"]').css('padding-left', $('.search-tag').outerWidth() + 10 + 'px');

    // Búsqueda en portal
    $('.search-tag button.close').click(function() {
        $('#input-search-tag').remove();
        $('.search-tag').remove();
        // Eliminamos el padding de la etiqueta de búsqueda de portal (#input-search-desktop e #input-search-mobile)
        $('[id^="input-search-"]').removeAttr('style');
    });

    // Cambiar action del formulario de búsqueda si existe etiqueta (filtro) de portal
    $("#gs").submit(function(e) {
        //e.preventDefault();
        if($('.search-tag').length > 0){
            if($('input[name=path]').length > 0){
                $('#gs').attr("action", $('input[name=path]').val());
                $("input[name=path]").remove();
            } else {
                $('#gs').attr("action","./list");    
            };
        };
        //$('#gs').submit();
    });

    // Botón de agrupación de portales
    $('.btn-sitemap').click(function() {
        $('.navegacion-portales').slideToggle('slow');
        //console.log($(window).height() + '-' + 57 + '-' + $('.social-networks').outerHeight());
        $('.navegacion-portales .row').addClass('overflow-y').css('height', $(window).height() - 57 - $('.social-networks').outerHeight());
    });

    // Solo actua si existe navegación de portal. En caso contrario la navegación es estática.
    //if($('.navbar-portal').length){
        // Linea para hacer accesible la navegación si javascript está deshabilitado
    //    $('.navbar-portal').removeClass('navbar-static-top').addClass('navbar-fixed-top')
        // Añadimos margin-top a la barra del portal (.navbar-portal) y al menú lateral (#sidebar-wrapper)
    //    $('.navbar-fixed-top').css('margin-top', '56px');
        // Añadimos padding-top al contenido para evitar que se solape a .navbar-fixed-top.navbar-portal
    //    $('#wrapper').css('padding-top', '56px'); // TOFIX: Ver si integrar por css (ver con javascript disabled)
        // Detectamos el scroll de la página para ocultar barra de navegación principal
    //    $(window).scroll(function () {
            // Si la distancia del scroll del top de la página es > 5px y el mapaweb no está abierto
    //        if ($(this).scrollTop() > 5 && $('.navegacion-portales').css('display') !== 'block') { 
    //            $('.navbar-static-top').hide();
    //            $('.navbar-fixed-top').css('margin-top', '0px');
    //            $('body').css('padding-top', '56px');
    //        } else {
    //            $('.navbar-static-top').show();
    //            $('.navbar-fixed-top').css('margin-top', '56px');
    //            $('body').css('padding-top', '0px');
    //        };
    //    });
    //};

    // Botón de limpieza de inputs
    // $('.has-feedback .form-control').keyup(function() {
    //     if ($(this).next().is('a')) $('.form-control-clear').unwrap();
    //     console.log($(this).next('span').toggle(Boolean($(this).val())));
    //     $(this).next('span').toggle(Boolean($(this).val()));
    // });
    $('.form-control-clear').toggle(Boolean($('.has-feedback .form-control').val()));
    $('.form-control-clear').click(function() {
        console.log($(this).prev());
        $(this).prev().val('').focus();
        $(this).hide();
    });

    // Tabs
    // =============================================
    // Si javascript está habilitado
    var hash = window.location.hash;
    // Evitamos error en gestiones AngularJS con ngRoute (#/)
    hash = hash.indexOf('#/') !== -1 ? undefined : hash;
    function showTab(hash) {
        $('.tabs a[href="' + hash + '"]').tab('show');
        // set timeout onDomReady
        setTimeout(function(){ // Descontamos del offset 57px del .navbar-portal
            var offset = $(':target').offset();
            if(offset){
                var scrollto = offset.top - 57; // minus fixed header height
                $('html, body').animate({scrollTop:scrollto}, 10);
            };
        }, 100);
    };
    // 1. Ocultamos <h2> dentro de .tab-content
    //$('.tab-content h2').addClass("oculto");
    $('.tab-content .tab-title').addClass("oculto");
    // 2. Mostramos las tabs (por defecto ocultas vía style="display:none;")
    $('.tabs').show();
    // Marcamos primera tab como activa
    //$('#tabs a:first').tab('show');
    $('.tabs').each(function(index,tabs){
        $(tabs).find('a:first').tab('show');
    });
    // 3. Eliminamos la clase activa de cada bloque de contenido excepto el primero
    //$('.tab-pane').not(":first").removeClass('active');
    $('.tab-content').each(function(index,content){
        $(content).find('.tab-pane').not(":first").removeClass('active');
    });
    // 4. Abrir tab mediante url hash
    if(hash){
        showTab(hash);
    };
    // 5. Al hacer click sobre una tab establecemos manualmente el hash de la tab (por si el usuario vuelve a la página anterior)
    // http://lea.verou.me/2011/05/change-url-hash-without-page-jump/
    $('.tabs a').click(function (e) {
        // e.preventDefault();
        if(history.pushState) {
            history.pushState(null, null, $(this).attr('href'));
        } else {
            location.hash = $(this).attr('href');
        };
    })
    // 6. Abrir tab en la misma página (sin recargar) a través del hash
    $('a[href^="#"]').on('click', function() {
        showTab($(this).attr('href'));
    });

    // Esta clase solo existe en móvil (sede:class="tab-pane" se transforma en class="collapse-pane")
    if($('.collapse-pane').length > 0){
        $('.collapse-pane').collapse();    
    };
    
    // ==============================================

    // Collapse manual de las facetas en /list
    if($('div[id^="collapse-facet"].collapse').length > 0){
        $('div[id^="collapse-facet"].collapse').collapse();    
    };

    // Collapse por defecto del bloque de comentarios en detalle
    if($('#form-comment').length > 0){
        $('#form-comment').collapse();    
    };

    // Collapse accesible por teclado
    $('[data-toggle="collapse"]').keypress(function(e) { 
        if (e.keyCode == 13) { //13 es el id de la tecla 'enter' 
            var id = $(this).attr('data-target');
            $(id).collapse('toggle');
        } 
    });

    // ReadSpeaker en documentos PDF <a class="pdf">
    $('a.pdf').each(function() {
        if($(this).attr('class') == 'pdf'){
            var oldHref = $(this).attr('href');
            //oldHref.replace('http://www.zaragoza.es','').replace('https://www.zaragoza.es','');
            // Generamos tooltip con tamaño del pdf (class="oculto")
            $(this).tooltip().attr('data-original-title', $(this).children('.oculto').text());
            // Eliminamos la clase oculto
            $(this).children('.oculto').remove();
            // Regeneramos el ancla con readspeaker
            $(this).after('<a href="http://docreader.readspeaker.com/docreader/?cid=bpegq&amp;lang=es_es&amp;url=' + oldHref + '" onclick="window.open(this.href, dcrwin); return false;" title="Escuchar este documento pdf" style="text-decoration: none;margin-left: 6px; color: #3333aa; font-weight: bold;"><img src="//zaragoza.es/cont/paginas/img/listen_pdf.png" style="border-style: none; vertical-align: text-bottom;width:auto;height:auto;" alt="Escuchar este documento usando ReadSpeaker docReader"></a>');
            // $(this).html($(this).text() + '<a href="http://docreader.readspeaker.com/docreader/?cid=bpegq&amp;lang=es_es&amp;url=http://www.zaragoza.es' + oldHref + '" onclick="window.open(this.href, dcrwin); return false;" title="Escuchar este documento pdf" style="text-decoration: none;margin-left: 6px; color: #3333aa; font-weight: bold;"><img src="//zaragoza.es/cont/paginas/img/listen_pdf.png" style="border-style: none; vertical-align: text-bottom;width:auto;height:auto;" alt="Escuchar este documento usando ReadSpeaker docReader"></a>');
        };
    });

    $('.popover-manual').each(function() {
        console.log($(this).children('.oculto').html());
        // Generamos popover
        $(this).popover({
            'content': $(this).children('.oculto').html(),
            'placement': 'bottom',
            'html': true
        })
        // Eliminamos la clase .oculto
        $(this).children('.oculto').remove();
        // Regeneramos el popover sin el contenido de .oculto
        $(this).html($(this).html());
    });

    // Si un header tiene un icono FA con la clase .rotate gira al hacer click
    $(":header,.panel-heading").click(function() {
        if($(this).children('.rotate').length){
            $(this).children('.rotate').toggleClass("down");
        };
    });
});

$(document).ready(function() {
    var trigger = $('.hamburger'),
        overlay = $('#wrapper > .overlay'),
        isClosed = true;
        //h1 = $('h1'),
        //breadcrumbs = $('.breadcrumb');

    trigger.click(function() {
        hamburger_cross();
    });

    function hamburger_cross() {

        if (isClosed == true) {
            overlay.hide();
            trigger.removeClass('is-open');
            trigger.addClass('is-closed');
            isClosed = false;
            //h1.removeClass('vertical-text');
            //breadcrumbs.show();
        } else {
            overlay.show();
            trigger.removeClass('is-closed');
            trigger.addClass('is-open');
            isClosed = true;
            //h1.addClass('vertical-text');
            //breadcrumbs.hide();
        }
    }

    $('[data-toggle="offcanvas"]').click(function() {
        $('#wrapper').toggleClass('toggled');
    });

    if($('#socialShare [data-toggle="tooltip"]').length > 0){
        $('#socialShare [data-toggle="tooltip"]').tooltip();     
    };

});