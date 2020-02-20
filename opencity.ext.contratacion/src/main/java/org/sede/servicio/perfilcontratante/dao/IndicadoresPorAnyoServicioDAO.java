package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.IndicadoresPorAnyoServicioGestor;

import com.googlecode.genericdao.dao.jpa.GenericDAO;


public interface IndicadoresPorAnyoServicioDAO extends GenericDAO<IndicadoresPorAnyoServicioGestor, BigDecimal> {
    public Set<ConstraintViolation<Object>> validar(Object registro);
}

