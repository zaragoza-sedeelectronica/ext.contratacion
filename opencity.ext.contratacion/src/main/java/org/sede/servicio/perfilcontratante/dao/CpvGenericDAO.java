package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.Cpv;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;


public interface CpvGenericDAO extends GenericDAO<Cpv,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<?>findCpv();
}
