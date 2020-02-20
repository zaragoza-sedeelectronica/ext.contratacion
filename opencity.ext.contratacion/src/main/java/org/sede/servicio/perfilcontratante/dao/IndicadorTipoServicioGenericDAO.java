package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimientoServicioGestor;
import org.sede.servicio.perfilcontratante.entity.IndicadoresTipoServicioGestor;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorTipoServicioGenericDAO extends GenericDAO<IndicadoresTipoServicioGestor,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<IndicadoresTipoServicioGestor> contratoMasParticipantes(BigDecimal idServicio, String anyo);
    public SearchResult<BigDecimal> totaleContratoPorAnyoPorServicio(BigDecimal IdServicio, String anyo);


}
