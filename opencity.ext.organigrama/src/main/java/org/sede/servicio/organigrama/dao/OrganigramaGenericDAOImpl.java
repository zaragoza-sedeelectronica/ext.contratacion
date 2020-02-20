package org.sede.servicio.organigrama.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sede.core.anotaciones.Esquema;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.rest.Rest;
import org.sede.core.rest.json.JSONArray;
import org.sede.core.rest.json.JSONObject;
import org.sede.servicio.ModelAttr;
import org.sede.servicio.organigrama.ConfigOrganigrama;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.spring4.context.SpringWebContext;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

@Repository
@Transactional(ConfigOrganigrama.TM)
public class OrganigramaGenericDAOImpl extends GenericDAOImpl <EstructuraOrganizativa, BigDecimal> implements OrganigramaGenericDAO {
	private static final Logger logger = LoggerFactory.getLogger(OrganigramaGenericDAOImpl.class);
	@PersistenceContext(unitName=ConfigOrganigrama.ESQUEMA)
	public void setEntityManager(EntityManager entityManager) {
		this.setEm(entityManager);
	}
	private static final String ID = "id";
	private static final String NOMBRE = "nombre";
	private static final String IDSUPERIOR = "idsuperior";
	
	public List<EstructuraOrganizativa> listado(Integer id) {
		String query = "PREFIX org: <http://datos.gob.es/voc/sector-publico/org#> "
				+ "PREFIX w3org: <http://www.w3.org/ns/org#> "
				+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> "
				+ "SELECT DISTINCT '" + id + "' as ?" + ID + " ?uri ?" + NOMBRE + " ?" + IDSUPERIOR + " ?nombrepadre ?purpose ?cp ?address ?fax ?email ?tel "
				+ "WHERE { "
				+ "	?uri a <http://vocab.linkeddata.es/kos/sector-publico/organismo>. "
				+ "	?uri dcterms:identifier '" + id + "'. "
				+ "	?uri rdfs:label ?" + NOMBRE + ". "
				+ (id == 1 ? "	OPTIONAL {" : "")  
					+ " ?uri w3org:unitOf ?superior."
					+ " ?superior rdfs:label ?nombrepadre."
					+ " ?superior dcterms:identifier ?" + IDSUPERIOR + "."
				+ (id == 1 ? " } " : "")
				+ "	OPTIONAL {?uri w3org:purpose ?purpose.} "
				+ "	OPTIONAL {?uri vcard:hasPostalCode ?cp.} "
				+ "	OPTIONAL {?uri vcard:address/vcard:street-adr ?address.} "
				+ "	OPTIONAL {?uri vcard:faxNumber ?fax.} "
				+ "	OPTIONAL {?uri vcard:hasEmail ?email.} "
				+ "	OPTIONAL {?uri vcard:tel ?tel.} "
				+ "} ";
		try {
			String resp = Rest.getInstance().query(query, EstructuraOrganizativa.class.getAnnotation(Grafo.class).value());
			if (resp != null) {
				return this.asList(new JSONObject(resp).getJSONObject("results").getJSONArray("bindings"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	public List<EstructuraOrganizativa> listadoHijos(Integer id) {
		String query = "PREFIX org: <http://datos.gob.es/voc/sector-publico/org#> "
				+ "PREFIX w3org: <http://www.w3.org/ns/org#> "
				+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> "
				+ "SELECT DISTINCT ?uri ?" + ID + " ?" + NOMBRE + " ?cp ?address ?fax ?email ?tel "
				+ "WHERE { "
				+ "     ?uri a <http://vocab.linkeddata.es/kos/sector-publico/organismo>. "
				+ "     ?uri dcterms:identifier ?" + ID + ". "
				+ "     ?uri rdfs:label ?" + NOMBRE + ". "
				+ "		?uri w3org:unitOf <http://www.zaragoza.es/api/recurso/sector-publico/organismo/" + id + ">. "
				+ "		OPTIONAL {?uri vcard:hasPostalCode ?cp.} "
				+ "		OPTIONAL {?uri vcard:address/vcard:street-adr ?address.} "
				+ "		OPTIONAL {?uri vcard:faxNumber ?fax.} "
				+ "		OPTIONAL {?uri vcard:hasEmail ?email.} "
				+ "		OPTIONAL {?uri vcard:tel ?tel.} "
				+ "}";
		
		try {
			String resp = Rest.getInstance().query(query, EstructuraOrganizativa.class.getAnnotation(Grafo.class).value());
			if (resp != null) {
				return this.asList(new JSONObject(resp).getJSONObject("results").getJSONArray("bindings"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public List<EstructuraOrganizativa> query(String consulta) {	
		StringBuilder q = new StringBuilder();
		
		if (consulta.contains(" ")) {
			String[] palabras = consulta.split(" ");
			for (String palabra : palabras) {
				q.append("(?=.*" + palabra + ")");
			}
		} else {
			q.append(consulta);
		}
		String query = "SELECT DISTINCT ?" + ID + " ?uri ?" + NOMBRE + " " 
		        + "WHERE { "
		        + " ?uri ?s  ?p. "
		        + " ?uri dcterms:identifier ?" + ID + ". "
		        + " ?uri rdfs:label ?" + NOMBRE + ". "
		        + " FILTER (REGEX(STR(?p), \"" + q.toString() + "\", \"i\")) "
		        + "}";
		
		try {
			String resp = Rest.getInstance().query(query, EstructuraOrganizativa.class.getAnnotation(Grafo.class).value());
			if (resp != null) {
				return this.asList(new JSONObject(resp).getJSONObject("results").getJSONArray("bindings"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private List<EstructuraOrganizativa> asList(JSONArray jsonArray) {
		List<EstructuraOrganizativa> list = new ArrayList<EstructuraOrganizativa>();
		for (int i = 0; i < jsonArray.length(); i++) {
			EstructuraOrganizativa estructura = new EstructuraOrganizativa();
			JSONObject entidad = jsonArray.getJSONObject(i); 
			estructura.setId(entidad.has(ID) ? entidad.getJSONObject(ID).getBigDecimal("value") : null);
			estructura.setAddress_interno(entidad.has("address") ? entidad.getJSONObject("address").getString("value") : null);
			estructura.setEmail(entidad.has("email") ? entidad.getJSONObject("email").getString("value") : null);
			estructura.setFax(entidad.has("fax") ? entidad.getJSONObject("fax").getString("value") : null);
			estructura.setPhone(entidad.has("tel") ? entidad.getJSONObject("tel").getString("value") : null);
			estructura.setPostal_code(entidad.has("cp") ? entidad.getJSONObject("cp").getString("value") : null);
			estructura.setPurpose(entidad.has("purpose") ? entidad.getJSONObject("purpose").getString("value") : null);
			estructura.setTitle(entidad.has(NOMBRE) ? entidad.getJSONObject(NOMBRE).getString("value") : null);
			estructura.setUnidadRaiz(entidad.has("address") ? entidad.getJSONObject("address").getString("value") : null);
			estructura.setUnidadSuperior(entidad.has(IDSUPERIOR) ? entidad.getJSONObject(IDSUPERIOR).getString("value") : null);
			estructura.setNombreUnidadSuperior(entidad.has("nombrepadre") ? entidad.getJSONObject("nombrepadre").getString("value") : null);
// FIXME			estructura.setX(entidad.has("address") ? entidad.getString("address") : null);
// FIXME		estructura.setY(entidad.has("address") ? entidad.getString("address") : null);

		    list.add(estructura);
		}
		return list;
	}

	public String obtenerNombreUnidadSuperior(EstructuraOrganizativa registro) {
		String query = "PREFIX org: <http://datos.gob.es/voc/sector-publico/org#> "
				+ "PREFIX w3org: <http://www.w3.org/ns/org#> "
				+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> "
				+ "SELECT DISTINCT ?nombrepadre "
				+ "WHERE { "
				+ "	?uri a <http://vocab.linkeddata.es/kos/sector-publico/organismo>. "
				+ "	?uri dcterms:identifier '" + registro.getId() + "'. "
				+ "	OPTIONAL {"  
					+ " ?uri w3org:unitOf ?superior."
					+ " ?superior rdfs:label ?nombrepadre."
					+ " ?superior dcterms:identifier ?" + IDSUPERIOR + "."
				+ " } "
				+ "} ";
		try {
			String resp = Rest.getInstance().query(query, EstructuraOrganizativa.class.getAnnotation(Grafo.class).value());
			if (resp != null) {
				
				JSONArray ob = new JSONObject(resp).getJSONObject("results").getJSONArray("bindings");
				if (ob.length() > 0) {
					JSONObject entidad = ob.getJSONObject(0);
					return entidad.has("nombrepadre") ? entidad.getJSONObject("nombrepadre").getString("value") : null;
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public ProcessorResult getForTag(Arguments arguments, Node node) {
		Element nodo = ((Element)node);

		String fragmento = nodo.getAttributeValue("fragment") == null ? "fragmentos/organigrama/detalle" : nodo.getAttributeValue("fragment");
		String[] ocultos = nodo.getAttributeValue("ocultos") == null ? new String[0] : nodo.getAttributeValue("ocultos").split(",");

		BigDecimal id = nodo.getAttributeValue("id") != null ? new BigDecimal(nodo.getAttributeValue("id")) : null;
		Integer idCen = nodo.getAttributeValue("idCen") != null ? Integer.parseInt(nodo.getAttributeValue("idcen")) : null;

		EstructuraOrganizativa resultado = null;
		if(idCen != null){
			Search busqueda = new Search().setMaxResults(1).setFirstResult(0);
			busqueda.addFilter(Filter.equal("id_recurso", idCen));
			resultado = this.searchUnique(busqueda);
		} else {
			resultado = this.find(id);
		}

		((SpringWebContext)arguments.getContext()).getVariables().put(ModelAttr.REGISTRO, resultado);
		((SpringWebContext)arguments.getContext()).getVariables().put("ocultos", ocultos);
		Template template = arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
				arguments.getConfiguration(), fragmento, arguments.getContext()));
		node.getParent().insertBefore(node, template.getDocument());

		node.getParent().removeChild(node);
		return ProcessorResult.OK;

	}
}