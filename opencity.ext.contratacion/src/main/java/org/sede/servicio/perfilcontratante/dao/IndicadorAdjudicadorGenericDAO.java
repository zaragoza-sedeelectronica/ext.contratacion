package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.IndicadorAjudicatario;
import org.sede.servicio.perfilcontratante.entity.IndicadoresTipo;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorAdjudicadorGenericDAO extends GenericDAO<IndicadorAjudicatario,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);


}
