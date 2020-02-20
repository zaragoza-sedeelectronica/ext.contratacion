package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimientoServicioGestor;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorProcedimientoServicioGenericDAO extends GenericDAO<IndicadoresProcedimientoServicioGestor,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<IndicadoresProcedimientoServicioGestor> contratoMasParticipantes(BigDecimal idServicio, String anyo);
    public SearchResult<BigDecimal> totaleContratoPorAnyoPorServicio(BigDecimal IdServicio, String anyo);


}
