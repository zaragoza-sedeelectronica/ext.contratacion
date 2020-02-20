package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;

import org.sede.servicio.perfilcontratante.entity.Tipocontrato;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.genericdao.dao.jpa.GenericDAO;

public interface TipoContratoGenericDAO extends GenericDAO<Tipocontrato, BigDecimal> {
	public Tipocontrato obtenerTipo(JsonNode actualObj);
}
