function generarQueryString (puntoSinRestangular, original, service_code, agency_responsible_code, colaborador, filteredExpectedDate) {
	var queryString='';
    
    if (puntoSinRestangular.title!==original.title) {
    	queryString = queryString + "&title=" + puntoSinRestangular.title;
    }
    if (puntoSinRestangular.description!==original.description) {
    	queryString = queryString + "&description=" + puntoSinRestangular.description;
    }
    if (service_code != null && service_code!==original.service_code) {
    	queryString = queryString + "&service_code=" + service_code;
    }
    if (agency_responsible_code != null && agency_responsible_code!==original.agency_responsible_code && colaborador) {
    	queryString = queryString + "&agency_responsible_code=" + agency_responsible_code;
    }
    if (puntoSinRestangular.type!==original.type) {
    	queryString = queryString + "&type=" + puntoSinRestangular.type;
    }
    /*if (puntoSinRestangular.status!==original.status) {
    	queryString = queryString + "&status=" + puntoSinRestangular.status;
    }*/
    
    
    if (puntoSinRestangular.first_name!==original.first_name) {
       	queryString = queryString + "&first_name=" + puntoSinRestangular.first_name;
    }
    if (puntoSinRestangular.email!==original.email) {
       	queryString = queryString + "&email=" + puntoSinRestangular.email;
    }
    if (puntoSinRestangular.phone!==original.phone) {
       	queryString = queryString + "&phone=" + puntoSinRestangular.phone;
    }
    if (puntoSinRestangular.user_address!==original.user_address) {
       	queryString = queryString + "&user_address=" + puntoSinRestangular.user_address;
    }
    
    if (puntoSinRestangular.priority!==original.priority) {
    	queryString = queryString + "&priority=" + puntoSinRestangular.priority;
    }
    if (puntoSinRestangular.origin!==original.origin) {
    	queryString = queryString + "&origin=" + puntoSinRestangular.origin;
    }
    if (puntoSinRestangular.lat!==original.lat) {
       	queryString = queryString + "&lat=" + puntoSinRestangular.lat;
    }
    if (puntoSinRestangular.long!==original.long) {
       	queryString = queryString + "&long=" + puntoSinRestangular.long;
    }
    if (puntoSinRestangular.address_id!==original.address_id) {
       	queryString = queryString + "&address_id=" + puntoSinRestangular.address_id;
    }
    if (puntoSinRestangular.address_string!==original.address_string) {
       	queryString = queryString + "&address_string=" + puntoSinRestangular.address_string;
    }
    if (puntoSinRestangular.answer_requested!==original.answer_requested) {
       	queryString = queryString + "&answer_requested=" + puntoSinRestangular.answer_requested;
    }
    if (puntoSinRestangular.zona_inspeccion!==original.zona_inspeccion) {
       	queryString = queryString + "&zona_inspeccion=" + puntoSinRestangular.zona_inspeccion;
    }
    if (puntoSinRestangular.expected_resolution_datetime!==original.expected_resolution_datetime) {
       	queryString = queryString + "&expected_resolution_datetime=" + filteredExpectedDate;
    }
    if (puntoSinRestangular.id_cat_sip!==original.id_cat_sip) {
       	queryString = queryString + "&id_cat_sip=" + puntoSinRestangular.id_cat_sip;
    }
    return queryString;
	
}
function generarQueryStringCrear (puntoSinRestangular, service_code, agency_responsible_code, colaborador,filteredExpectedDate) {
	var queryString='';
    
    if (!angular.isUndefined(puntoSinRestangular.title)) {
    	queryString = queryString + "&title=" + puntoSinRestangular.title;
    }
    if (!angular.isUndefined(puntoSinRestangular.description)) {
    	queryString = queryString + "&description=" + puntoSinRestangular.description;
    }
    if (service_code != null) {
    	queryString = queryString + "&service_code=" + service_code;
    }
    if (agency_responsible_code != null && colaborador) {
    	queryString = queryString + "&agency_responsible_code=" + agency_responsible_code;
    }
    if (!angular.isUndefined(puntoSinRestangular.type)) {
    	queryString = queryString + "&type=" + puntoSinRestangular.type;
    }
    /*if (puntoSinRestangular.status!==original.status) {
    	queryString = queryString + "&status=" + puntoSinRestangular.status;
    }*/
    
    if (!angular.isUndefined(puntoSinRestangular.first_name)) {
       	queryString = queryString + "&first_name=" + puntoSinRestangular.first_name;
    }
    if (!angular.isUndefined(puntoSinRestangular.email)) {
       	queryString = queryString + "&email=" + puntoSinRestangular.email;
    }
    if (!angular.isUndefined(puntoSinRestangular.phone)) {
       	queryString = queryString + "&phone=" + puntoSinRestangular.phone;
    }
    if (!angular.isUndefined(puntoSinRestangular.user_address)) {
       	queryString = queryString + "&user_address=" + puntoSinRestangular.user_address;
    }
    
    
    if (!angular.isUndefined(puntoSinRestangular.priority)) {
    	queryString = queryString + "&priority=" + puntoSinRestangular.priority;
    }
    if (!angular.isUndefined(puntoSinRestangular.origin)) {
    	queryString = queryString + "&origin=" + puntoSinRestangular.origin;
    }
    if (!angular.isUndefined(puntoSinRestangular.lat)) {
    	queryString = queryString + "&lat=" + puntoSinRestangular.lat;
    }
    if (!angular.isUndefined(puntoSinRestangular.long)) {
    	queryString = queryString + "&long=" + puntoSinRestangular.long;
    }
    if (!angular.isUndefined(puntoSinRestangular.address_id)) {
    	queryString = queryString + "&address_id=" + puntoSinRestangular.address_id;
    }
    if (!angular.isUndefined(puntoSinRestangular.address_string)) {
    	queryString = queryString + "&address_string=" + puntoSinRestangular.address_string;
    }
    if (!angular.isUndefined(puntoSinRestangular.answer_requested)) {
    	queryString = queryString + "&answer_requested=" + puntoSinRestangular.answer_requested;
    }
    if (!angular.isUndefined(puntoSinRestangular.zona_inspeccion)) {
    	queryString = queryString + "&zona_inspeccion=" + puntoSinRestangular.zona_inspeccion;
    }
    if (!angular.isUndefined(puntoSinRestangular.expected_resolution_datetime)) {
    	queryString = queryString + "&expected_resolution_datetime=" + filteredExpectedDate;
    }
    if (!angular.isUndefined(puntoSinRestangular.public)) {
    	queryString = queryString + "&public=" + puntoSinRestangular.public;
    }
    
//    if (portaleroSeleccion != null) {
//    	queryString = queryString + "&x=" + portaleroSeleccion.x + "&y=" + portaleroSeleccion.y + "&address_id=" + portaleroSeleccion.id + "&address_string=" + portaleroSeleccion.calle.title;
//    }
    return queryString;
	
}



function toBinaryString(data) {
	return btoa(unescape(encodeURIComponent(data)));
}

