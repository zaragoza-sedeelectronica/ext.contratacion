package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.ContratosPorAnyoServicioGestor;

import com.googlecode.genericdao.dao.jpa.GenericDAO;


public interface ContratosPorAnyoServicioDAO extends GenericDAO<ContratosPorAnyoServicioGestor, BigDecimal> {
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public BigDecimal consultaTotalLicitadore(BigDecimal servicio,String year);

}

