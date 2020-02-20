function procesarPagina() {
	var link = window.location.href;
	link = removeParam('refresh', link);
	link = removeParam('debug', link);
	$('#ind_uri').val(link);	
	$('#ind_title').val(document.title.replace('. Ayuntamiento de Zaragoza',''));	
	var metas = document.getElementsByTagName('meta');
	for (var i = 0; i < metas.length; i++) {
		$('#ind_' + metas[i].getAttribute("name")).val(metas[i].getAttribute("content"));
	}
	var a = document.getElementById("main").getElementsByTagName("a");
	var links = '';
	for (var i = 0; i < a.length; i++) {
		if (i > 0) {
			links = links + ',';
		}
		links = links + a[i].href;
	}
	$('#ind_links').val(links);
	$('#ind_text').val(document.getElementById("main").innerHTML.replace(/(<([^>]+)>)/ig, ""));
}


function removeParam(key, sourceURL) {
    var rtn = sourceURL.split("?")[0],
        param,
        params_arr = [],
        queryString = (sourceURL.indexOf("?") !== -1) ? sourceURL.split("?")[1] : "";
    if (queryString !== "") {
        params_arr = queryString.split("&");
        for (var i = params_arr.length - 1; i >= 0; i -= 1) {
            param = params_arr[i].split("=")[0];
            if (param === key) {
                params_arr.splice(i, 1);
            }
        }
        rtn = rtn + "?" + params_arr.join("&");
    }
    return rtn;
}

function indizar() {
	$("#indizarForm").submit(function(e) {
	    var url = "/sede/servicio/contenido/index-solr"; // the script where you handle the form input.

	    $.ajax({
	           type: "POST",
	           url: url,
	           data: $("#indizarForm").serialize(), // serializes the form's elements.
	           success: function(data)
	           {
	               $("#respuesta").html(data);
                	window.setTimeout(function() {
                    	$(".alert").alert('close');
                	}, 2000);
	           }
	         });
	    e.preventDefault(); // avoid to execute the actual submit of the form.
	});
}