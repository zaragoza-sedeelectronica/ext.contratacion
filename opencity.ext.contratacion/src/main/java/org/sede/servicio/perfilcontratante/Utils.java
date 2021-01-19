package org.sede.servicio.perfilcontratante;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.Interno;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.MimeTypes;
import org.sede.core.rest.Peticion;
import org.sede.core.utils.Anonimizar;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.perfilcontratante.dao.AnuncioGenericDAO;
import org.sede.servicio.perfilcontratante.dao.EmpresaGenericDAO;
import org.sede.servicio.perfilcontratante.dao.TipoContratoGenericDAO;
import org.sede.servicio.perfilcontratante.entity.*;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import sun.text.Normalizer;

import org.apache.commons.codec.binary.Base64;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;


public class Utils {
	//region Contructors
	private Utils() {}
	//endregion
	//region Atributtes
	public static final String CONTRATOSELLADO = "contratoSellado";
	public static final BigDecimal TIPO_ANUNCIO_LICITACION = new BigDecimal(2.0);
	public static final BigDecimal TIPO_ANUNCIO_PLIEGOS_TECNICOS = new BigDecimal(3.0);
	public static final BigDecimal TIPO_ANUNCIO_PLIEGOS_ADMINISTRATIVOS = new BigDecimal(4.0);
	public static final BigDecimal TIPO_ANUNCIO_ADJUDICACION = new BigDecimal(6.0);
	public static final BigDecimal TIPO_ANUNCIO_FECHA_PRESENTACION = new BigDecimal(10.0);
	public static final BigDecimal TIPO_ANUNCIO_PRESUPUESTO = new BigDecimal(11.0);
	public static final BigDecimal TIPO_ANUNCIO_OTRAS_INFORMACIONES = new BigDecimal(9.0);
	public static final String PROPENTIDAD = "contrato_entidad";
	public static final String PROPPORTAL = "contrato_portal";
	//endregion
	//region Methods

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
	public static byte[] decodeFromString(String txtBase64) {
		if (txtBase64.indexOf("data:") >= 0) {
			txtBase64 = txtBase64.substring(txtBase64.indexOf(","), txtBase64.length());
		}
		return Base64.decodeBase64(txtBase64);
	}
	public static String normalizar(String text) {

		String temp = Normalizer.normalize(text, java.text.Normalizer.Form.NFC, 0);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("&", "_").replaceAll(" ", "_").replaceAll("�", "_").replaceAll("�", "_");
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

	public static BigDecimal obtenerEntidad(Peticion peticion) {
		if (peticion.getCredenciales() != null && peticion.getCredenciales().getUsuario().getPropiedades() != null
				&& peticion.getCredenciales().getUsuario().getPropiedades().containsKey(PROPENTIDAD)) {
			return new BigDecimal(peticion.getCredenciales().getUsuario().getPropiedades().get(PROPENTIDAD));
		} else {
			return null;
		}
	}

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
