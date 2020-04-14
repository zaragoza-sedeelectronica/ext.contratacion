package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.Indicador;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorGenericDAO extends GenericDAO<Indicador,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public BigDecimal consultaTotalGanados(BigDecimal idEmpresa,String year, String idPortal);

}
