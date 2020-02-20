package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sede.core.anotaciones.Esquema;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.entity.Tipocontrato;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
@Repository
@Transactional(ConfigPerfilContratante.TM)
public class TipoContratoGenericDAOImpl extends GenericDAOImpl <Tipocontrato, BigDecimal> implements TipoContratoGenericDAO {
	
	@PersistenceContext(unitName=ConfigPerfilContratante.ESQUEMA)
	public void setEntityManager(EntityManager entityManager) {
		this.setEm(entityManager);
	}
	public Tipocontrato obtenerTipo(JsonNode actualObj) {
		Iterator<JsonNode> iterator = actualObj.get("@type").elements();
		Tipocontrato tipo = new Tipocontrato();
		while (iterator.hasNext()) {
			String valor = iterator.next().asText();
			if (!"pproc:Contract".equals(valor)) {
				tipo.setType(valor.substring(valor.indexOf(':') + 1, valor.length()));
				break;
			}
		}
		Search search = new Search();
		search.addFilterAnd(Filter.equal("type", tipo.getType()));
		tipo = this.searchUnique(search);
		if (tipo == null) {
			tipo = new Tipocontrato();
			tipo.setId(new BigDecimal(4));
			
		}
		return tipo;
	}
}