package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.IndicadorAhorro;

import com.googlecode.genericdao.dao.jpa.GenericDAO;

public interface IndicadorAhorroEntidadGenericDAO extends GenericDAO<IndicadorAhorro,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
}
