function loadFacade(ready, clientName){
	var idReco = document.getElementById("idreco").value;
	var estructuraRecorridos = '{"procesiones":[{"id":"' + idReco + '","url":"\/\/www.zaragoza.es\/contenidos\/semanasanta\/' + idReco + '.json"}]}';
	parent.frames["visualizador_visor"].getFacade().definirRecorridosProcesiones(estructuraRecorridos);
	parent.frames["visualizador_visor"].getFacade().pintarRecorridoProcesion(idReco, true);
}

function getFacade() {
	return {
		visualizadorCargado: function() {
			loadFacade();
		}
	};
}
getFacade();
