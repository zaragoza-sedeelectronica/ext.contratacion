package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.DatosIndicadoresProcedimiento;
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimiento;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
public interface IndicadorProcedimientoGenericDAO extends GenericDAO<IndicadoresProcedimiento,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<DatosIndicadoresProcedimiento> contratoMasParticipantes(BigDecimal idPortal, String anyo);
    public SearchResult<BigDecimal> totaleContratoPorAnyoPortal(BigDecimal idPortal,String anyo);


}
