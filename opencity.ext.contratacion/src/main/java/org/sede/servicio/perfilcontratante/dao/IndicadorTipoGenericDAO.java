package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.DatosIndicadoresProcedimiento;
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimiento;
import org.sede.servicio.perfilcontratante.entity.IndicadoresTipo;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorTipoGenericDAO extends GenericDAO<IndicadoresTipo,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<IndicadoresTipo> contratoMasParticipantes(BigDecimal idPortal, String anyo);
    public SearchResult<BigDecimal> totaleContratoPorAnyoPortal(BigDecimal idPortal, String anyo);


}
