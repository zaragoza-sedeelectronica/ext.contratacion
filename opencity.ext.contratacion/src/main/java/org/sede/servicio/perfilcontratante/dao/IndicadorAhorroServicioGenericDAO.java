package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.IndicadorAhorroServicio;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorAhorroServicioGenericDAO extends GenericDAO<IndicadorAhorroServicio,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
}
