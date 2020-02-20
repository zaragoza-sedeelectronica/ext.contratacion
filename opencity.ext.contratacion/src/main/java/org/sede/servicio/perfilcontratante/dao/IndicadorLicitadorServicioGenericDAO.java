package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.IndicadorLicitadorServicio;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorLicitadorServicioGenericDAO extends GenericDAO<IndicadorLicitadorServicio,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public ResponseEntity<IndicadorLicitadorServicio> getIlicitadoresGanadoresIndicadores(Integer idEntidad, String year);
    public BigDecimal consultaTotalLicitaciones(BigDecimal idEmpresa,BigDecimal servicio,String year);

}
