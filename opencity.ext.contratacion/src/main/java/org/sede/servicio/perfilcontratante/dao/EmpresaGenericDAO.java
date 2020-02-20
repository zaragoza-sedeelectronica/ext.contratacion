package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.EmpresaConParticipacion;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface EmpresaGenericDAO extends GenericDAO<Empresa,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);

	public SearchResult<Empresa> findAdjudicadores();

	public List<Contrato> findContratosAdjudicados(BigDecimal id);

	public SearchResult<Empresa> findEmpresaUte();

	public List<Empresa> findEmpresaUte(BigDecimal id);
	public SearchResult<Empresa> findEmpresaPertneceUte(BigDecimal id);

	public List<EmpresaConParticipacion> findEmpresaEnUte(BigDecimal id);
}
