package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.ContratosPorAnyoIdPortal;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by documentacionweb on 6/11/18.
 */
public interface ContratosPorAnyoIdPortalDAO extends GenericDAO<ContratosPorAnyoIdPortal, BigDecimal> {
    public Set<ConstraintViolation<Object>> validar(Object registro);
}

