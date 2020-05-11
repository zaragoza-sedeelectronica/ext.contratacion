package org.sede.servicio.perfilcontratante;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.MimeTypes;
import org.sede.core.rest.Peticion;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Contrato;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Utils {
	private Utils() {
		super();
	}
    public static final String CONTRATOSELLADO = "contratoSellado";

	@Description("Ordenacion de Maps para visualizacion de datos de Licitados y ganados")
    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order){
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
            if (order){
                return o1.getKey().compareTo(o2.getKey());
            }else{
                return o2.getKey().compareTo(o1.getKey());
            }
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	
	public static boolean necesitaSellado(Anuncio registro){
		boolean retorno = false;
		if (registro.getType().getId().intValue() == 2 
				|| registro.getType().getId().intValue() == 5 
				|| registro.getType().getId().intValue() == 6 
				|| registro.getType().getId().intValue() == 20) {
			retorno = true;
		}
		return retorno;
	}

	public static String getSino(Boolean dat) {
		if (dat == null) {
			return "";
		} else if (dat) {
			return "Si";
		} else {
			return "No";
		}
	}

	public static String getUrgente(String urgente) {
		if (urgente == null) {
			return "";
		} else if ("S".equals(urgente)) {
			return "Urgente";
		} else if ("E".equals(urgente)) {
			return "Excepcional";	
		} else {
			return "Ordinaria";
		}
	}
	
	public static final String PROPENTIDAD = "contrato_entidad";
	public static BigDecimal obtenerEntidad(Peticion peticion) {
		if (peticion.getCredenciales() != null && peticion.getCredenciales().getUsuario().getPropiedades() != null
				&& peticion.getCredenciales().getUsuario().getPropiedades().containsKey(PROPENTIDAD)) {
			return new BigDecimal(peticion.getCredenciales().getUsuario().getPropiedades().get(PROPENTIDAD));
		} else {
			return null;
		}
	}
	public static final String PROPPORTAL = "contrato_portal";
	public static BigDecimal obtenerPortal(Peticion peticion) {
		if (peticion.getCredenciales() != null && peticion.getCredenciales().getUsuario().getPropiedades() != null
				&& peticion.getCredenciales().getUsuario().getPropiedades().containsKey(PROPPORTAL)) {
			return new BigDecimal(peticion.getCredenciales().getUsuario().getPropiedades().get(PROPPORTAL));
		} else {
			return null;
		}
	}

	public static boolean existeEnVirtuoso(String expediente) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode actualObj = mapper.readTree(VirtuosoDataManagement.query(
					"SELECT ?y WHERE {<http://www.zaragoza.es/api/recurso/sector-publico/contrato/" + expediente + "> ?y <http://contsem.unizar.es/def/sector-publico/pproc#Contract>. }", Contrato.class.getAnnotation(Grafo.class).value(), MimeTypes.SPARQL_JSON));
			if (((ArrayNode)actualObj.get("results").get("bindings")).size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
