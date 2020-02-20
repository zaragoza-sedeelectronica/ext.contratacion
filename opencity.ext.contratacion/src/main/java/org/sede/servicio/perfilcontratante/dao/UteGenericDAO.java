package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.perfilcontratante.entity.Ute;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;
public interface  UteGenericDAO extends GenericDAO<Ute,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<?>findUte();
	public ResponseEntity<?> asociarAEmpresa(BigDecimal idEmpresa, BigDecimal idEmpresa2, BigDecimal participacion);
	public ResponseEntity<?> eliminarAsociacion(BigDecimal id, BigDecimal id2);
}
