/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sede.core.anotaciones.Esquema;
import org.sede.servicio.perfilcontratante.entity.Tipocontrato;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class TipoContratoGenericDAOImpl extends GenericDAOImpl <Tipocontrato, BigDecimal> implements TipoContratoGenericDAO {
	
	@PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
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