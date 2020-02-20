package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.DatosLicitador;

import com.googlecode.genericdao.dao.jpa.GenericDAO;

public interface DatosLicitadorGenericDAO extends GenericDAO<DatosLicitador, BigDecimal> {
    public Set<ConstraintViolation<Object>> validar(Object registro);
}

