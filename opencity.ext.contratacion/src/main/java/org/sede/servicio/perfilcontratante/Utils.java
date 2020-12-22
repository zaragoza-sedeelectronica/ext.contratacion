package org.sede.servicio.perfilcontratante;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.MimeTypes;
import org.sede.core.rest.Peticion;
import org.sede.core.utils.Anonimizar;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.dao.EmpresaGenericDAO;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Contrato;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.Oferta;
import org.springframework.beans.factory.annotation.Autowired;


import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;


public class Utils {

	private Utils() {
		super();
	}

	public static final String CONTRATOSELLADO = "contratoSellado";

	@Description("Ordenacion de Maps para visualizacion de datos de Licitados y ganados")
	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
							   Map.Entry<String, Integer> o2) {
				if (order) {
					return o1.getKey().compareTo(o2.getKey());
				} else {
					return o2.getKey().compareTo(o1.getKey());
				}
			}
		});
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static boolean necesitaSellado(Anuncio registro) {
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
			if (((ArrayNode) actualObj.get("results").get("bindings")).size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static byte[] decodeFromString(String txtBase64) {
		if (txtBase64.indexOf("data:") >= 0) {
			txtBase64 = txtBase64.substring(txtBase64.indexOf(","), txtBase64.length());
		}
		return Base64.getDecoder().decode(txtBase64);
	}

	public static Oferta crearOferta(JsonNode objc, boolean ganador,EmpresaGenericDAO daoEmpresa) throws Exception {
		Oferta oferta = new Oferta();
		Empresa licitador = new Empresa();
		try {
			JsonNode actualObj = objc;
			if (actualObj != null) {
				if (actualObj.has("pproc:awardDate")&& ganador) {
					oferta.setFechaAdjudicacion(ConvertDate.string2Date(actualObj.get("pproc:awardDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
					oferta.setGanador(true);
					oferta.setFechaFormalizacion(ConvertDate.string2Date(actualObj.get("pproc:awardDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
				} else {
					oferta.setGanador(false);
				}

				oferta.setCanon(false);
				oferta.setTieneUte(false);
				oferta.setAhorroVisible(true);
				if (actualObj.has("pc:supplier")) {
					if (actualObj.get("pc:supplier").has("org:identifier")) {
						licitador.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());
						Search busqueEmpresa = new Search(Empresa.class);
						busqueEmpresa.addFilterLike("nif", actualObj.get("pc:supplier").get("org:identifier").asText());

						SearchResult<Empresa> licitador2 = daoEmpresa.searchAndCount(busqueEmpresa);
						if (licitador2.getResult().size() > 0) {
							licitador = licitador2.getResult().get(0);
							for (Empresa item : licitador2.getResult()) {
								licitador = item;
								break;

							}
						}
					} else {
						licitador.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());
						licitador.setUte("N");
						licitador.setNacionalidad("es");
						if (actualObj.get("pc:supplier").has("s:name")) {
							licitador.setNombre(actualObj.get("pc:supplier").get("s:name").asText());
						}
						if (actualObj.get("pc:supplier").has("org:identifier")) {
							licitador.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());

							if (Anonimizar.anoniCierto(licitador.getNif())) {
								licitador.setAutonomo("S");
							} else {
								licitador.setAutonomo("N");
							}
						}
					}
				}


					oferta.setEmpresa(licitador);

				}
				if (actualObj.has("pc:offeredPrice")) {
					Iterator<JsonNode> iterator = actualObj.get("pc:offeredPrice").elements();
					while (iterator.hasNext()) {
						JsonNode valor = iterator.next();
						String conIva = valor.get("gr:valueAddedTaxIncluded").asText();
						if ("true".equals(conIva)) {
							oferta.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
						} else {
							oferta.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
						}
					}
				}



			return oferta;


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
