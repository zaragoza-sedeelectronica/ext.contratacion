package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.servicio.perfilcontratante.entity.Indicador;
import org.springframework.http.ResponseEntity;

import com.googlecode.genericdao.dao.jpa.GenericDAO;

public interface IndicadorGenericDAO extends GenericDAO<Indicador,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public BigDecimal consultaTotalGanados(BigDecimal idEmpresa,String year);

}
